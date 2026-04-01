package com.perinity.store.domain.exception;

import java.io.Serial;

public class DuplicateCpfException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = -4863109167205818879L;

  public static final String DEFAULT_MESSAGE = "CPF already exists";

  public DuplicateCpfException() {
    super(DEFAULT_MESSAGE);
  }

}
