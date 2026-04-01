package com.perinity.store.infrastructure.web.exception;

import com.perinity.store.domain.exception.SaleNotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@Provider
public class SaleNotFoundExceptionMapper implements BaseExceptionMapper<SaleNotFoundException> {

  @Override
  public Response.Status getStatus() {
    return Response.Status.NOT_FOUND;
  }
  
}
