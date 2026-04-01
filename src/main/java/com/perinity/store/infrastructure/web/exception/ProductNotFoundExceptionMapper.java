package com.perinity.store.infrastructure.web.exception;

import com.perinity.store.domain.exception.ProductNotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

/**
 * Mapper de {@link ProductNotFoundException} para HTTP 404.
 */
@Provider
public class ProductNotFoundExceptionMapper implements BaseExceptionMapper<ProductNotFoundException> {

  @Override
  public Response.Status getStatus() {
    return Response.Status.NOT_FOUND;
  }
}

