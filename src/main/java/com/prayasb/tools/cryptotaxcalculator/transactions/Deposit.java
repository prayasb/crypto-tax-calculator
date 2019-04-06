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
public class Deposit extends Transaction implements TransferableTransaction, ExchangeTransaction {
    private final String exchange;
    private final String from;
    private final String to;

    public Deposit(UUID id, ZonedDateTime date, TransactionType transactionType, AmountInCurrency baseAmount,
                   String notes, String exchange, String from, String to) {
        super(id, date, transactionType, baseAmount, notes);
        this.exchange = exchange;
        this.from = from;
        this.to = to;
    }
}
