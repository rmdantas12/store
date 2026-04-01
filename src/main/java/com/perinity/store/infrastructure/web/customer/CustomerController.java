package com.perinity.store.infrastructure.web.customer;

import com.perinity.store.domain.ports.incoming.CustomerUseCase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@ApplicationScoped
public class CustomerController implements CustomerAPI {

  private final CustomerUseCase customerService;

  private final CustomerWebMapper customerWebMapper;

  @Context
  private UriInfo uriInfo;

  @Override
  public Response create(final CustomerRequest request) {
    final var customer = customerWebMapper.toDomain(request);
    final var created = customerService.create(customer);

    final var location = uriInfo.getAbsolutePathBuilder()
        .path(created.getCode().toString())
        .build();

    final var body = customerWebMapper.toResponse(created);

    return Response.created(location)
        .entity(body)
        .build();
  }

  @Override
  public Response update(final UUID code, final CustomerUpdateRequest request) {
    final var customer = customerWebMapper.toDomain(request);
    final var updated = customerService.update(code, customer);
    final var body = customerWebMapper.toResponse(updated);

    return Response.ok(body)
        .build();
  }

  @Override
  public Response delete(final UUID code) {
    customerService.delete(code);

    return Response.noContent()
        .build();
  }

  @Override
  public Response findByCode(final UUID code) {
    final var customer = customerService.findByCode(code);
    final var body = customerWebMapper.toResponse(customer);

    return Response.ok(body)
        .build();
  }

  @Override
  public Response findAll() {
    final var body = customerService.findAll().stream()
        .map(customerWebMapper::toResponse)
        .toList();

    return Response.ok(body)
        .build();
  }

}