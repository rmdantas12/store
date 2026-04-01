package com.perinity.store.domain.exception;

import java.io.Serial;

public class ProductNotFoundException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = -7567589807354428020L;

  public static final String DEFAULT_MESSAGE = "Product not found";

  public ProductNotFoundException() {
    super(DEFAULT_MESSAGE);
  }
}

