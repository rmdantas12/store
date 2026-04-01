package com.perinity.store.domain.exception;

import java.io.Serial;

public class InvalidSalePaymentException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 3872450326200007170L;

  public InvalidSalePaymentException(final String message) {
    super(message);
  }

}
