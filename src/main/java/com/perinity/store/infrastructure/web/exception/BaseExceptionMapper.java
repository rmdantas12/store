package com.perinity.store.infrastructure.web.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

import java.util.Objects;

public interface BaseExceptionMapper<T extends Throwable> extends ExceptionMapper<T> {

  Response.Status getStatus();

  @Override
  default Response toResponse(final T exception) {
    return Response.status(Objects.requireNonNull(getStatus()))
        .entity(new ErrorResponse(exception))
        .build();
  }

}
