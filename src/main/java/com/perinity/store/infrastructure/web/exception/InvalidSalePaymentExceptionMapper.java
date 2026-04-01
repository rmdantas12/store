package com.perinity.store.infrastructure.web.exception;

import com.perinity.store.domain.exception.InvalidSalePaymentException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@Provider
public class InvalidSalePaymentExceptionMapper implements BaseExceptionMapper<InvalidSalePaymentException> {

  @Override
  public Response.Status getStatus() {
    return Response.Status.BAD_REQUEST;
  }

}
