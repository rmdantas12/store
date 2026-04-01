package com.perinity.store.infrastructure.web.product;

import com.perinity.store.domain.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public interface ProductWebMapper {

  @Mapping(target = "code", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  Product toDomain(final ProductRequest request);

  @Mapping(target = "code", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  Product toDomain(final ProductUpdateRequest request);

  ProductResponse toResponse(final Product product);
}

