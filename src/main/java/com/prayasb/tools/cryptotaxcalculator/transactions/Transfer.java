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
public class Transfer extends Transaction implements TransferableTransaction {
    private final String from;
    private final String to;

    public Transfer(UUID id, ZonedDateTime date, TransactionType transactionType, AmountInCurrency baseAmount,
                    String notes, String from, String to) {
        super(id, date, transactionType, baseAmount, notes);
        this.from = from;
        this.to = to;
    }
}
