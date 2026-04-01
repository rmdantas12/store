package com.perinity.store.infrastructure.persistence.product;

import com.perinity.store.domain.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public interface ProductPersistenceMapper {

  @Mapping(target = "updatedAt", ignore = true)
  ProductEntity toEntity(Product product);

  Product toDomain(ProductEntity entity);
}

