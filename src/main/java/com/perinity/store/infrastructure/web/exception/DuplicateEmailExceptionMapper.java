package com.perinity.store.infrastructure.web.exception;

import com.perinity.store.domain.exception.DuplicateEmailException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@Provider
public class DuplicateEmailExceptionMapper implements BaseExceptionMapper<DuplicateEmailException> {

  @Override
  public Response.Status getStatus() {
    return Response.Status.CONFLICT;
  }

}
