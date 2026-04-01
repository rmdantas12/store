package com.perinity.store.infrastructure.web.product;

import com.perinity.store.domain.ports.incoming.ProductUseCase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@ApplicationScoped
public class ProductController implements ProductAPI {

  private final ProductUseCase productUseCase;

  private final ProductWebMapper productWebMapper;

  @Context
  private UriInfo uriInfo;

  @Override
  public Response create(final ProductRequest request) {
    final var product = productWebMapper.toDomain(request);
    final var created = productUseCase.create(product);

    final URI location = uriInfo.getAbsolutePathBuilder()
        .path(created.getCode().toString())
        .build();

    return Response.created(location)
        .entity(productWebMapper.toResponse(created))
        .build();
  }

  @Override
  public Response update(final UUID code, final ProductUpdateRequest request) {
    final var updated = productUseCase.update(code, productWebMapper.toDomain(request));
    return Response.ok(productWebMapper.toResponse(updated))
        .build();
  }

  @Override
  public Response delete(final UUID code) {
    productUseCase.delete(code);
    return Response.noContent()
        .build();
  }

  @Override
  public ProductResponse findByCode(final UUID code) {
    return productWebMapper.toResponse(productUseCase.findByCode(code));
  }

  @Override
  public List<ProductResponse> findAll() {
    return productUseCase.findAll()
        .stream()
        .map(productWebMapper::toResponse)
        .toList();
  }
}

