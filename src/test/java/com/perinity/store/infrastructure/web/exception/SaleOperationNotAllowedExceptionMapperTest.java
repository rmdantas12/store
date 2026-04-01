package com.perinity.store.infrastructure.web.exception;

import com.perinity.store.domain.exception.SaleOperationNotAllowedException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SaleOperationNotAllowedExceptionMapperTest {

  @Test
  void toResponse_shouldReturn403() {
    final var mapper = new SaleOperationNotAllowedExceptionMapper();
    assertEquals(Response.Status.FORBIDDEN, mapper.getStatus());
    assertEquals(Response.Status.FORBIDDEN.getStatusCode(), mapper.getStatus().getStatusCode());
  }

  @Test
  void toResponse_shouldHandleExceptionType() {
    final var mapper = new SaleOperationNotAllowedExceptionMapper();
    final var status = mapper.getStatus();
    final var exception = new SaleOperationNotAllowedException("x");
    assertEquals(Response.Status.FORBIDDEN, status);
    assertEquals("x", exception.getMessage());
  }

}
