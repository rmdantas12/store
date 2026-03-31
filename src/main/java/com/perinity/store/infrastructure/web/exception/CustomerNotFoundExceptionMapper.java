package com.perinity.store.infrastructure.web.exception;

import com.perinity.store.domain.exception.CustomerNotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CustomerNotFoundExceptionMapper implements BaseExceptionMapper<CustomerNotFoundException> {

  @Override
  public Response.Status getStatus() {
    return Response.Status.NOT_FOUND;
  }

}
