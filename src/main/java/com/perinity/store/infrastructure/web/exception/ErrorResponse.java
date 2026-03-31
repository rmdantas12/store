package com.perinity.store.infrastructure.web.exception;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record ErrorResponse(
    String message,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime timestamp
) {

  public ErrorResponse(final Throwable cause) {
    this(cause.getMessage());
  }

  public ErrorResponse(final String message) {
    this(message, LocalDateTime.now());
  }

}
