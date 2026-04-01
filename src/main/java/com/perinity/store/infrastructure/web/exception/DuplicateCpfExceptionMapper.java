package com.perinity.store.infrastructure.web.exception;

import com.perinity.store.domain.exception.DuplicateCpfException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@Provider
public class DuplicateCpfExceptionMapper implements BaseExceptionMapper<DuplicateCpfException> {

  @Override
  public Response.Status getStatus() {
    return Response.Status.CONFLICT;
  }

}
