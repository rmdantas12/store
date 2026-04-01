package com.perinity.store.infrastructure.web.exception;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public record ErrorResponse(
    String message,
    List<String> details,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime timestamp
) {

  public ErrorResponse(final Throwable cause) {
    this(Objects.requireNonNull(cause).getMessage());
  }

  public ErrorResponse(final String message) {
    this(message, List.of(), LocalDateTime.now());
  }

  public ErrorResponse(final String message, final List<String> details) {
    this(message, details, LocalDateTime.now());
  }

}
