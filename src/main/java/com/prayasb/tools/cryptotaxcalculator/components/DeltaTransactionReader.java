package com.prayasb.tools.cryptotaxcalculator.components;

import com.google.common.base.Strings;
import com.prayasb.tools.cryptotaxcalculator.config.headers.Delta;
import com.prayasb.tools.cryptotaxcalculator.exceptions.SourceFileReadException;
import com.prayasb.tools.cryptotaxcalculator.transactions.Transaction;
import com.prayasb.tools.cryptotaxcalculator.utils.ValueUtil;
import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class DeltaTransactionReader {
  private final String sourceFile;

  private Map<String, Integer> titleToIndexMap = new HashMap<>();

  public List<Transaction> read() {
    File file = new File(sourceFile);

    if (!file.canRead()) {
      throw new SourceFileReadException("Cannot read file @ " + sourceFile);
    }

    List<Transaction> transactions = new ArrayList<>();

    try (
      BufferedReader reader = Files.newBufferedReader(file.toPath(), Charset.defaultCharset());
    ) {
      String header = reader.readLine();

      if (!Strings.isNullOrEmpty(header)) {
        String[] headers = header.split(",", -1);
        for (int i = 0; i < headers.length; i++) {
          titleToIndexMap.put(ValueUtil.stripQuotes(headers[i]), i);
        }

        if (titleToIndexMap.isEmpty()) {
          throw new RuntimeException("No headers could be parsed from file: " + sourceFile);
        }

        String line;
        while (!Strings.isNullOrEmpty(line = reader.readLine())) {
          transactions.add(Delta.parse(titleToIndexMap, line.split(",", -1)));
        }
      }

    } catch (IOException e) {
      throw new SourceFileReadException("Unable to read CSV", e);
    }

    return transactions;
  }
}
