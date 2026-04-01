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
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;
import java.util.UUID;


@Path("/api/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Products", description = "Product management operations")
public interface ProductAPI {

  @POST
  @Operation(summary = "Create a new product", description = "Creates a new product and returns the created resource")
  @APIResponses(
      {
          @APIResponse(
              responseCode = "201", description = "Product created",
              content = @Content(schema = @Schema(implementation = ProductResponse.class))
          ),
          @APIResponse(responseCode = "400", description = "Invalid input")
      }
  )
  Response create(@Valid ProductRequest request);

  @PUT
  @Path("/{code}")
  @Operation(summary = "Update an existing product", description = "Updates a product by its code")
  @APIResponses(
      {
          @APIResponse(
              responseCode = "200", description = "Product updated",
              content = @Content(schema = @Schema(implementation = ProductResponse.class))
          ),
          @APIResponse(responseCode = "404", description = "Product not found"),
          @APIResponse(responseCode = "400", description = "Invalid input")
      }
  )
  Response update(@PathParam("code") UUID code, @Valid ProductUpdateRequest request);

  @DELETE
  @Path("/{code}")
  @Operation(summary = "Delete a product", description = "Deletes a product by its code")
  @APIResponses(
      {
          @APIResponse(responseCode = "204", description = "Product deleted"),
          @APIResponse(responseCode = "404", description = "Product not found")
      }
  )
  Response delete(@PathParam("code") UUID code);

  @GET
  @Path("/{code}")
  @Operation(summary = "Get a product by code", description = "Returns a single product")
  @APIResponses(
      {
          @APIResponse(
              responseCode = "200", description = "Product found",
              content = @Content(schema = @Schema(implementation = ProductResponse.class))
          ),
          @APIResponse(responseCode = "404", description = "Product not found")
      }
  )
  ProductResponse findByCode(@PathParam("code") UUID code);

  @GET
  @Operation(summary = "List all products", description = "Returns all products (sorted by name)")
  @APIResponse(
      responseCode = "200", description = "List of products",
      content = @Content(schema = @Schema(implementation = ProductResponse.class, type = SchemaType.ARRAY))
  )
  List<ProductResponse> findAll();
}
