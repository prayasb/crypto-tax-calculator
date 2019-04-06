package com.prayasb.tools.cryptotaxcalculator.transactions;

import com.prayasb.tools.cryptotaxcalculator.types.TransactionType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public abstract class Trade extends Transaction implements ExchangeTransaction {
    private final String exchange;
    private final AmountInCurrency quoteAmount;
    private final AmountInCurrency fee;
    private final AmountInCurrency totalCost;


    public Trade(UUID id, ZonedDateTime date, TransactionType transactionType, AmountInCurrency baseAmount, String notes,
                 String exchange, AmountInCurrency quoteAmount, AmountInCurrency fee, AmountInCurrency totalCost) {
        super(id, date, transactionType, baseAmount, notes);
        this.exchange = exchange;
        this.quoteAmount = quoteAmount;
        this.fee = fee;
        this.totalCost = totalCost;
    }
}


