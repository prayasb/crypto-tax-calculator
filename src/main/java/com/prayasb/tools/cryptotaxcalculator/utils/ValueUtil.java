package com.prayasb.tools.cryptotaxcalculator.utils;

import com.google.common.base.Strings;

import java.math.BigDecimal;
import java.text.NumberFormat;

import static com.prayasb.tools.cryptotaxcalculator.config.headers.Delta.USD;

public class ValueUtil {
    private static final NumberFormat USD_NUMBER_FORMAT = NumberFormat.getNumberInstance();
    private static final NumberFormat CRYPTO_NUMBER_FORMAT = NumberFormat.getNumberInstance();
    static {
        USD_NUMBER_FORMAT.setMaximumFractionDigits(2);
        USD_NUMBER_FORMAT.setGroupingUsed(false);

        CRYPTO_NUMBER_FORMAT.setMaximumFractionDigits(8);
        CRYPTO_NUMBER_FORMAT.setGroupingUsed(false);
    }

    public static String stripQuotes(String value) {
        return Strings.nullToEmpty(value).replaceAll("\"", "");
    }

    public static String getAmountForDisplay(BigDecimal amount, String currency) {
        if (USD.equals(currency)) {
            return USD_NUMBER_FORMAT.format(amount);
        } else {
            return CRYPTO_NUMBER_FORMAT.format(amount);
        }
    }
}
