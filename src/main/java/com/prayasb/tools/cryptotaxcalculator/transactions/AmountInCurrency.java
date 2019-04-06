package com.prayasb.tools.cryptotaxcalculator.transactions;

import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.prayasb.tools.cryptotaxcalculator.config.headers.Delta.USD;

@Data
public class AmountInCurrency {
    private final BigDecimal amount;
    private final String currency;

    public AmountInCurrency(BigDecimal amount, String currency) {
        this.amount = scale(amount, currency);
        this.currency = currency;
    }

    private BigDecimal scale(BigDecimal amount, String currency) {
        if (amount != null && USD.equals(currency)) {
            return amount.setScale(2, RoundingMode.HALF_UP);
        } else if (amount != null) {
            return amount.setScale(8, RoundingMode.HALF_UP);
        }

        return null;
    }
}
