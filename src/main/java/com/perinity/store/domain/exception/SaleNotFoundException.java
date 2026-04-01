package com.perinity.store.domain.exception;

import java.io.Serial;

public class SaleNotFoundException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 6236324896181523278L;

  public static final String DEFAULT_MESSAGE = "Sale not found";

  public SaleNotFoundException() {
    super(DEFAULT_MESSAGE);
  }

}
