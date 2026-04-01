package com.perinity.store.infrastructure.web.exception;

import com.perinity.store.domain.exception.CustomerNotFoundException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class CustomerNotFoundExceptionMapperTest {

  /**
   * Cenário: mapear uma {@link CustomerNotFoundException} para uma resposta HTTP.
   * Expectativa: o status da resposta deve ser 404 (NOT_FOUND) e o corpo deve ser um {@link ErrorResponse}
   * contendo a mensagem original da exceção.
   */
  @Test
  void toResponse_whenCustomerNotFoundException_shouldReturnNotFoundWithErrorResponse() {
    final var mapper = new CustomerNotFoundExceptionMapper();
    final var ex = new CustomerNotFoundException();

    try (Response response = mapper.toResponse(ex)) {
      assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
      final var entity = response.getEntity();

      if (!(entity instanceof ErrorResponse)) {
        fail("Expected entity to be ErrorResponse but was: " + entity);
      }

      final var error = (ErrorResponse) entity;
      assertEquals(CustomerNotFoundException.DEFAULT_MESSAGE, error.message());
      assertEquals(0, error.details().size());
    }
  }

}

