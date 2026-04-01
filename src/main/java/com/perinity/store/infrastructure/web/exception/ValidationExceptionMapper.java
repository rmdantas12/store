package com.perinity.store.infrastructure.web.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Provider
public class ValidationExceptionMapper implements BaseExceptionMapper<ConstraintViolationException> {

  @Override
  public Response.Status getStatus() {
    return Response.Status.BAD_REQUEST;
  }

  @Override
  public Response toResponse(final ConstraintViolationException exception) {
    final List<String> details = Optional.ofNullable(exception)
        .map(ConstraintViolationException::getConstraintViolations)
        .orElseGet(java.util.Set::of)
        .stream()
        .map(this::toDetail)
        .sorted(Comparator.naturalOrder())
        .toList();

    return Response.status(getStatus())
        .entity(new ErrorResponse("Validation failed", details))
        .build();
  }

  private String toDetail(final ConstraintViolation<?> violation) {
    final String field = Optional.ofNullable(violation)
        .map(ConstraintViolation::getPropertyPath)
        .map(Object::toString)
        .map(this::lastPathNode)
        .orElse("unknown");

    final String message = Optional.ofNullable(violation)
        .map(ConstraintViolation::getMessage)
        .orElse("invalid");

    return field + ": " + message;
  }

  private String lastPathNode(final String path) {
    final int idx = path.lastIndexOf('.');
    return idx >= 0 ? path.substring(idx + 1) : path;
  }
}

