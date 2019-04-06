package com.prayasb.tools.cryptotaxcalculator.transactions;

import com.prayasb.tools.cryptotaxcalculator.types.TransactionType;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.ZonedDateTime;
import java.util.UUID;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Sell extends Trade {
    public Sell(UUID id, ZonedDateTime date, TransactionType transactionType, AmountInCurrency baseAmount, String notes, String exchange, AmountInCurrency quoteAmount, AmountInCurrency fee, AmountInCurrency totalCost) {
        super(id, date, transactionType, baseAmount, notes, exchange, quoteAmount, fee, totalCost);
    }
}
