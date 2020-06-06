package com.prayasb.tools.cryptotaxcalculator.config.headers;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.prayasb.tools.cryptotaxcalculator.transactions.*;
import com.prayasb.tools.cryptotaxcalculator.types.TransactionType;
import com.prayasb.tools.cryptotaxcalculator.utils.ValueUtil;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public class Delta {
  public static final String USD = "USD";
  public static final String USDC = "USDC"; // assuming USD ~ USDC

  private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");

  public static Transaction parse(Map<String, Integer> titleToIndexMap, String[] line) {
    Transaction entry;

    ZonedDateTime date = extractValue(titleToIndexMap, line, "Date", Delta::parseDate);
    TransactionType type = extractValue(titleToIndexMap, line, "Type", TransactionType::valueOf);
    if (type == null) {
      throw new RuntimeException("Invalid type in line: " + Joiner.on(",").join(line));
    }

    AmountInCurrency baseAmount =
        extractAmountInCurrency(titleToIndexMap, line, "Base amount", "Base currency");
    String notes = extractValue(titleToIndexMap, line, "Notes");
    String exchange = extractValue(titleToIndexMap, line, "Exchange");
    AmountInCurrency quoteAmount =
        extractAmountInCurrency(titleToIndexMap, line, "Quote amount", "Quote currency");
    AmountInCurrency fee = extractAmountInCurrency(titleToIndexMap, line, "Fee", "Fee currency");
    AmountInCurrency totalCost =
        extractAmountInCurrency(titleToIndexMap, line, "Costs/Proceeds", "Costs/Proceeds currency");
    String from = extractValue(titleToIndexMap, line, "Sent/Received from");
    String to = extractValue(titleToIndexMap, line, "Sent to");
    UUID id = UUID.randomUUID();
    switch (type) {
      case BUY:
        entry = new Buy(id, date, type, baseAmount, notes, exchange, quoteAmount, fee, totalCost);
        break;
      case SELL:
        entry = new Sell(id, date, type, baseAmount, notes, exchange, quoteAmount, fee, totalCost);
        break;
      case DEPOSIT:
        entry = new Deposit(id, date, type, baseAmount, notes, exchange, from, to);
        break;
      case TRANSFER:
        entry = new Transfer(id, date, type, baseAmount, notes, from, to);
        break;
      default:
        throw new IllegalStateException("Unexpected value: " + type);
    }

    return entry;
  }

  private static <T> T extractValue(
      Map<String, Integer> titleToIndexMap,
      String[] line,
      String header,
      Function<String, T> valueParser) {
    int index = titleToIndexMap.get(header);
    String value = ValueUtil.stripQuotes(line[index]);

    if (!Strings.isNullOrEmpty(value)) {
        return valueParser.apply(value);
    } else {
        return null;
    }
  }

  private static String extractValue(Map<String, Integer> titleToIndexMap, String[] line, String header) {
    int index = titleToIndexMap.get(header);
    return ValueUtil.stripQuotes(line[index]);
  }

  private static AmountInCurrency extractAmountInCurrency(
      Map<String, Integer> titleToIndexMap,
      String[] line,
      String amountHeader,
      String currencyHeader) {
    BigDecimal amount = extractValue(titleToIndexMap, line, amountHeader, BigDecimal::new);
    String currency = extractValue(titleToIndexMap, line, currencyHeader);

    return new AmountInCurrency(amount, currency);
  }

  private static ZonedDateTime parseDate(String date) {
      return ZonedDateTime.parse(date, DATE_FORMAT);
  }
}
