package com.perinity.store.domain.exception;

import java.io.Serial;

public class DuplicateEmailException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 8355075511108666940L;

  public static final String DEFAULT_MESSAGE = "Email already exists";

  public DuplicateEmailException() {
    super(DEFAULT_MESSAGE);
  }

}
