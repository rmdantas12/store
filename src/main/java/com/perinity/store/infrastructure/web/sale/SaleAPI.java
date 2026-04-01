package com.perinity.store.infrastructure.web.sale;

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
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.UUID;

@Path("/api/sales")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Sales", description = "Sale management operations")
@SecurityRequirement(name = "bearerAuth")
public interface SaleAPI {

  @POST
  @Operation(summary = "Create a new sale", description = "Creates a new sale and returns the created resource")
  @APIResponses(
      {
          @APIResponse(
              responseCode = "201", description = "Sale created",
              content = @Content(schema = @Schema(implementation = SaleResponse.class))
          ),
          @APIResponse(responseCode = "400", description = "Invalid input / invalid payment"),
          @APIResponse(responseCode = "404", description = "Customer or product not found")
      }
  )
  Response create(@Valid SaleRequest request);

  @PUT
  @Path("/{code}")
  @Operation(summary = "Update an existing sale", description = "Updates a sale by its code")
  @APIResponses(
      {
          @APIResponse(
              responseCode = "200", description = "Sale updated",
              content = @Content(schema = @Schema(implementation = SaleResponse.class))
          ),
          @APIResponse(responseCode = "400", description = "Invalid input / invalid payment"),
          @APIResponse(responseCode = "404", description = "Sale not found (or customer/product not found)")
      }
  )
  Response update(@PathParam("code") UUID code, @Valid SaleUpdateRequest request);

  @DELETE
  @Path("/{code}")
  @Operation(summary = "Delete a sale", description = "Deletes a sale by its code")
  @APIResponses(
      {
          @APIResponse(responseCode = "204", description = "Sale deleted"),
          @APIResponse(responseCode = "404", description = "Sale not found")
      }
  )
  Response delete(@PathParam("code") UUID code);

  @GET
  @Path("/{code}")
  @Operation(summary = "Get a sale by code", description = "Returns a single sale")
  @APIResponses(
      {
          @APIResponse(
              responseCode = "200", description = "Sale found",
              content = @Content(schema = @Schema(implementation = SaleResponse.class))
          ),
          @APIResponse(responseCode = "404", description = "Sale not found")
      }
  )
  Response findByCode(@PathParam("code") UUID code);

  @GET
  @Operation(summary = "List all sales", description = "Returns all sales")
  @APIResponse(
      responseCode = "200", description = "List of sales",
      content = @Content(schema = @Schema(implementation = SaleResponse.class, type = SchemaType.ARRAY))
  )
  Response findAll();
}

