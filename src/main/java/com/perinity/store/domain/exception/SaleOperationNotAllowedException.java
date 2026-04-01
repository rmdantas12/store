package com.perinity.store.domain.exception;

import java.io.Serial;

public class SaleOperationNotAllowedException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 2913001136155547859L;

  public SaleOperationNotAllowedException(final String message) {
    super(message);
  }
  
}

