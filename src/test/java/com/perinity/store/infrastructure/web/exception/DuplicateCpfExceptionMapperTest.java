package com.perinity.store.infrastructure.web.exception;

import com.perinity.store.domain.exception.DuplicateCpfException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class DuplicateCpfExceptionMapperTest {

  /**
   * Cenário: mapear uma {@link DuplicateCpfException} para uma resposta HTTP.
   * Expectativa: o status da resposta deve ser 409 (CONFLICT) e o corpo deve ser um {@link ErrorResponse}
   * contendo a mensagem original da exceção.
   */
  @Test
  void toResponse_whenDuplicateCpfException_shouldReturnConflictWithErrorResponse() {
    final var mapper = new DuplicateCpfExceptionMapper();
    final var ex = new DuplicateCpfException();

    try (Response response = mapper.toResponse(ex)) {
      assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
      final var entity = response.getEntity();

      if (!(entity instanceof ErrorResponse)) {
        fail("Expected entity to be ErrorResponse but was: " + entity);
      }

      final var error = (ErrorResponse) entity;
      assertEquals(DuplicateCpfException.DEFAULT_MESSAGE, error.message());
      assertEquals(0, error.details().size());
    }
  }

}

