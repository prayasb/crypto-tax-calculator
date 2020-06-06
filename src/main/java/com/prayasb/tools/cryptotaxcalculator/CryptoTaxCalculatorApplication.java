package com.prayasb.tools.cryptotaxcalculator;

import com.prayasb.tools.cryptotaxcalculator.components.DeltaTransactionReader;
import com.prayasb.tools.cryptotaxcalculator.output.TaxEntry;
import com.prayasb.tools.cryptotaxcalculator.transactions.Buy;
import com.prayasb.tools.cryptotaxcalculator.transactions.Sell;
import com.prayasb.tools.cryptotaxcalculator.transactions.Transaction;
import com.prayasb.tools.cryptotaxcalculator.types.TransactionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import static com.prayasb.tools.cryptotaxcalculator.config.headers.Delta.USD;
import static com.prayasb.tools.cryptotaxcalculator.utils.ValueUtil.getAmountForDisplay;

@SpringBootApplication
public class CryptoTaxCalculatorApplication implements CommandLineRunner {

  private static final Logger LOG = LoggerFactory.getLogger(CryptoTaxCalculatorApplication.class);

  public static void main(String[] args) {
    SpringApplication.run(CryptoTaxCalculatorApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    LOG.info("Executing tax calculator command line runner...");

    if (args.length != 1) {
      throw new RuntimeException(
          "Invalid arguments supplied. " + "Usage: /path/to/delta-export.csv");
    }

    String pathToCsv = args[0];
    DeltaTransactionReader reader = new DeltaTransactionReader(pathToCsv);
    List<Transaction> transactionList = reader.read();

    // extract all buys using USD sorted by date
    Map<String, List<Buy>> buysWithUSD =
        transactionList.stream()
            .filter(t -> t.getTransactionType().equals(TransactionType.BUY))
            .map(b -> (Buy) b)
            .filter(b -> b.getQuoteAmount().isUsdOrEquivalent())
            .sorted(dateComparator())
            .collect(Collectors.groupingBy(b -> b.getBaseAmount().getCurrency()));

    List<Sell> sells =
        transactionList.stream()
            .filter(t -> t.getTransactionType().equals(TransactionType.SELL))
            .map(s -> (Sell) s)
            .collect(Collectors.toList());

    // convert cross crypto buys to sells
    sells.addAll(
        transactionList.stream()
            .filter(t -> t.getTransactionType().equals(TransactionType.BUY))
            .map(b -> (Buy) b)
            .filter(b -> !b.getQuoteAmount().isUsdOrEquivalent())
            .map(
                b ->
                    new Sell(
                        b.getId(),
                        b.getDate(),
                        TransactionType.SELL,
                        b.getQuoteAmount(),
                        "Buy to sell conversion: "
                            + b.getQuoteAmount().getCurrency()
                            + "->"
                            + b.getBaseAmount().getCurrency()
                            + " trade",
                        b.getExchange(),
                        null,
                        null,
                        b.getTotalCost()))
            .collect(Collectors.toList()));

    sells.sort(dateComparator());

    Map<UUID, BigDecimal> idToQuantityLinked = new HashMap<>();
    List<TaxEntry> taxEntries = new ArrayList<>();
    for (int i = 0; i < sells.size(); i++) {
      Sell sell = sells.get(i);

      // link this sell to the first buy
      BigDecimal amountToLink = sell.getBaseAmount().getAmount();
      List<Buy> buys = buysWithUSD.get(sell.getBaseAmount().getCurrency());
      if (buys == null) {
        throw new RuntimeException(
            "No corresponding acquisition found for: " + sell.getBaseAmount().getCurrency());
      }

      for (int j = 0; j < buys.size(); j++) {
        BigDecimal amountThatCanBeLinked;
        Buy buy = buys.get(j);
        BigDecimal buyQuantityUsed = idToQuantityLinked.getOrDefault(buy.getId(), BigDecimal.ZERO);
        BigDecimal buyQuantityRemaining = buy.getBaseAmount().getAmount().subtract(buyQuantityUsed);
        if (buyQuantityRemaining.compareTo(BigDecimal.ZERO) > 0) {

          if (buyQuantityRemaining.compareTo(amountToLink) < 0) {
            // buy < sell, so use all buy amount
            amountThatCanBeLinked = buyQuantityRemaining;
          } else {
            // buy >= sell, so all sell can be used
            amountThatCanBeLinked = amountToLink;
          }

          // update the used map
          idToQuantityLinked.put(buy.getId(), buyQuantityUsed.add(amountThatCanBeLinked));
          // update the sell quantity remaining
          amountToLink = amountToLink.subtract(amountThatCanBeLinked);

          BigDecimal costBasis =
              buy.getTotalCost().getAmount().divide(buy.getBaseAmount().getAmount(), RoundingMode.HALF_EVEN).multiply(amountThatCanBeLinked);
          BigDecimal salePrice =
              sell.getTotalCost().getAmount().divide(sell.getBaseAmount().getAmount(), RoundingMode.HALF_EVEN).multiply(amountThatCanBeLinked);

          TaxEntry taxEntry =
              new TaxEntry(
					  sell.getExchange(), sell.getBaseAmount().getCurrency(), buy.getDate(), getAmountForDisplay(costBasis, USD), sell.getDate(), getAmountForDisplay(amountThatCanBeLinked, sell.getBaseAmount().getCurrency()), getAmountForDisplay(salePrice, USD), sell.getNotes());
          taxEntries.add(taxEntry);

          if (amountToLink.compareTo(BigDecimal.ZERO) == 0) {
            break;
          }
        }
      }
    }

    // TODO too lazy to do anything meaningful with the result -- export to csv or something in the future...
    System.out.println("Service name,Asset name,Date of purchase,Cost basis,Date of sale,Quantity sold,Sale proceeds,Notes");
    taxEntries.forEach(
        t -> {
          System.out.println(t.toCSV());
        });
    LOG.info("Done!");
  }

  private Comparator<Transaction> dateComparator() {
    return Comparator.comparing(Transaction::getDate);
  }
}
