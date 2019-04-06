package com.prayasb.tools.cryptotaxcalculator.transactions;

import com.prayasb.tools.cryptotaxcalculator.types.TransactionType;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
public abstract class Transaction {
    private final UUID id;
    private final ZonedDateTime date;
    private final TransactionType transactionType;
    private final AmountInCurrency baseAmount;
    private final String notes;
}
