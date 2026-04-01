package com.perinity.store.domain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SaleOperationNotAllowedExceptionTest {

  @Test
  void constructor_whenMessageIsProvided_shouldPreserveMessage() {
    final var message = "not allowed";
    final var exception = new SaleOperationNotAllowedException(message);
    assertEquals(message, exception.getMessage());
  }

}
