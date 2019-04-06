package com.prayasb.tools.cryptotaxcalculator.output;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class TaxEntry {
    private final String exchangeWhereSold;
    private final String assetName;
    private final ZonedDateTime dateAcquired;
    private final String costBasis;
    private final ZonedDateTime dateSold;
    private final String quantitySold;
    private final String salePrice;
    private final String notes;

    public String toCSV() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s", exchangeWhereSold, assetName, dateAcquired.toString(), costBasis, dateSold.toString(), quantitySold, salePrice, notes);
    }
}
