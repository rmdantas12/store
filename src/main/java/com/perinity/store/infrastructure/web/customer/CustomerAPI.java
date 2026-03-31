package com.perinity.store.infrastructure.web.customer;

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

import java.util.UUID;

@Path("/api/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Customers", description = "Customer management operations")
public interface CustomerAPI {

  @POST
  @Operation(summary = "Create a new customer", description = "Creates a new customer and returns the created resource")
  @APIResponses(
      {
          @APIResponse(
              responseCode = "201", description = "Customer created",
              content = @Content(schema = @Schema(implementation = CustomerResponse.class))
          ),
          @APIResponse(responseCode = "400", description = "Invalid input"),
          @APIResponse(responseCode = "409", description = "CPF or email already exists")
      }
  )
  Response create(@Valid CustomerRequest request);

  @PUT
  @Path("/{code}")
  @Operation(summary = "Update an existing customer", description = "Updates a customer by its code")
  @APIResponses(
      {
          @APIResponse(
              responseCode = "200", description = "Customer updated",
              content = @Content(schema = @Schema(implementation = CustomerResponse.class))
          ),
          @APIResponse(responseCode = "404", description = "Customer not found"),
          @APIResponse(responseCode = "400", description = "Invalid input"),
          @APIResponse(responseCode = "409", description = "CPF or email already exists")
      }
  )
  Response update(@PathParam("code") UUID code, @Valid CustomerUpdateRequest request);

  @DELETE
  @Path("/{code}")
  @Operation(summary = "Delete a customer", description = "Deletes a customer by its code")
  @APIResponses(
      {
          @APIResponse(responseCode = "204", description = "Customer deleted"),
          @APIResponse(responseCode = "404", description = "Customer not found")
      }
  )
  Response delete(@PathParam("code") UUID code);

  @GET
  @Path("/{code}")
  @Operation(summary = "Get a customer by code", description = "Returns a single customer")
  @APIResponses(
      {
          @APIResponse(
              responseCode = "200", description = "Customer found",
              content = @Content(schema = @Schema(implementation = CustomerResponse.class))
          ),
          @APIResponse(responseCode = "404", description = "Customer not found")
      }
  )
  Response findByCode(@PathParam("code") UUID code);

  @GET
  @Operation(summary = "List all customers", description = "Returns all customers")
  @APIResponse(
      responseCode = "200", description = "List of customers",
      content = @Content(schema = @Schema(implementation = CustomerResponse.class, type = SchemaType.ARRAY))
  )
  Response findAll();

}

