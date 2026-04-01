package com.perinity.store.infrastructure.web.exception;

import com.perinity.store.domain.exception.ProductNotFoundException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class ProductNotFoundExceptionMapperTest {

  /**
   * Cenário: mapear uma {@link ProductNotFoundException} para uma resposta HTTP.
   * Expectativa: o status da resposta deve ser 404 (NOT_FOUND) e o corpo deve ser um {@link ErrorResponse}
   * contendo a mensagem original da exceção.
   */
  @Test
  void toResponse_whenProductNotFoundException_shouldReturnNotFoundWithErrorResponse() {
    final var mapper = new ProductNotFoundExceptionMapper();
    final var ex = new ProductNotFoundException();

    try (Response response = mapper.toResponse(ex)) {
      assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
      final var entity = response.getEntity();

      if (!(entity instanceof ErrorResponse)) {
        fail("Expected entity to be ErrorResponse but was: " + entity);
      }

      final var error = (ErrorResponse) entity;
      assertEquals(ProductNotFoundException.DEFAULT_MESSAGE, error.message());
      assertEquals(0, error.details().size());
    }
  }
}

