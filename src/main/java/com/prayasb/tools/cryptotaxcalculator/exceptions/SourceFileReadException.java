package com.prayasb.tools.cryptotaxcalculator.exceptions;

public class SourceFileReadException extends RuntimeException {
  public SourceFileReadException(String message) {
    super(message);
  }

  public SourceFileReadException(String message, Throwable cause) {
    super(message, cause);
  }
}
