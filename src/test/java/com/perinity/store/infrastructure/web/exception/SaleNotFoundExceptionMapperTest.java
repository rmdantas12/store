package com.perinity.store.infrastructure.web.exception;

import com.perinity.store.domain.exception.SaleNotFoundException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class SaleNotFoundExceptionMapperTest {

  /**
   * Cenário: mapear uma {@link SaleNotFoundException} para uma resposta HTTP.
   * Expectativa: o status da resposta deve ser 404 (NOT_FOUND) e o corpo deve ser um {@link ErrorResponse}.
   */
  @Test
  void toResponse_whenSaleNotFoundException_shouldReturnNotFoundWithErrorResponse() {
    final var mapper = new SaleNotFoundExceptionMapper();
    final var ex = new SaleNotFoundException();

    try (Response response = mapper.toResponse(ex)) {
      assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
      final var entity = response.getEntity();

      if (!(entity instanceof ErrorResponse)) {
        fail("Expected entity to be ErrorResponse but was: " + entity);
      }

      final var error = (ErrorResponse) entity;
      assertEquals(SaleNotFoundException.DEFAULT_MESSAGE, error.message());
      assertEquals(0, error.details().size());
    }
  }
}

