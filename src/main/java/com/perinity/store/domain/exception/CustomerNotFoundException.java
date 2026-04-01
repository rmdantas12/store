package com.perinity.store.domain.exception;

import java.io.Serial;

public class CustomerNotFoundException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 1970914683445570560L;

  public static final String DEFAULT_MESSAGE = "Customer not found";

  public CustomerNotFoundException() {
    super(DEFAULT_MESSAGE);
  }

}