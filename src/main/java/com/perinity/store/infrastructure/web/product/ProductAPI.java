package com.perinity.store.infrastructure.web.product;

import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.UUID;

@Path("/api/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ProductAPI {

  @POST
  Response create(@Valid ProductRequest request);

  @PUT
  @Path("/{code}")
  Response update(@PathParam("code") UUID code, @Valid ProductUpdateRequest request);

  @DELETE
  @Path("/{code}")
  Response delete(@PathParam("code") UUID code);

  @GET
  @Path("/{code}")
  ProductResponse findByCode(@PathParam("code") UUID code);

  @GET
  List<ProductResponse> findAll();
}

