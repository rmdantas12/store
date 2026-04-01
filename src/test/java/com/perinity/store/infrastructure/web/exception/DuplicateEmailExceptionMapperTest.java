package com.perinity.store.infrastructure.web.exception;

import com.perinity.store.domain.exception.DuplicateEmailException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class DuplicateEmailExceptionMapperTest {

  /**
   * Cenário: mapear uma {@link DuplicateEmailException} para uma resposta HTTP.
   * Expectativa: o status da resposta deve ser 409 (CONFLICT) e o corpo deve ser um {@link ErrorResponse}
   * contendo a mensagem original da exceção.
   */
  @Test
  void toResponse_whenDuplicateEmailException_shouldReturnConflictWithErrorResponse() {
    final var mapper = new DuplicateEmailExceptionMapper();
    final var ex = new DuplicateEmailException();

    try (Response response = mapper.toResponse(ex)) {
      assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
      final var entity = response.getEntity();
      assertInstanceOf(ErrorResponse.class, entity);
      assertEquals(DuplicateEmailException.DEFAULT_MESSAGE, ((ErrorResponse) entity).message());
    }
  }

}

