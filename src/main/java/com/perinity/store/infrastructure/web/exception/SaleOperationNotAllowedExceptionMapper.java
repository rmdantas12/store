package com.perinity.store.infrastructure.web.exception;

import com.perinity.store.domain.exception.SaleOperationNotAllowedException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@Provider
public class SaleOperationNotAllowedExceptionMapper implements BaseExceptionMapper<SaleOperationNotAllowedException> {

  @Override
  public Response.Status getStatus() {
    return Response.Status.FORBIDDEN;
  }

}
