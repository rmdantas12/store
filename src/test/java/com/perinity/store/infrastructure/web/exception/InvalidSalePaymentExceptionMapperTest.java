package com.perinity.store.infrastructure.web.exception;

import com.perinity.store.domain.exception.InvalidSalePaymentException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class InvalidSalePaymentExceptionMapperTest {

  /**
   * Cenário: mapear uma {@link InvalidSalePaymentException} para uma resposta HTTP.
   * Expectativa: o status da resposta deve ser 400 (BAD_REQUEST) e o corpo deve ser um {@link ErrorResponse}.
   */
  @Test
  void toResponse_whenInvalidSalePaymentException_shouldReturnBadRequestWithErrorResponse() {
    final var mapper = new InvalidSalePaymentExceptionMapper();
    final var ex = new InvalidSalePaymentException("invalid payment");

    try (Response response = mapper.toResponse(ex)) {
      assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
      final var entity = response.getEntity();

      if (!(entity instanceof ErrorResponse)) {
        fail("Expected entity to be ErrorResponse but was: " + entity);
      }

      final var error = (ErrorResponse) entity;
      assertEquals("invalid payment", error.message());
      assertEquals(0, error.details().size());
    }
  }
}

