package com.perinity.store.infrastructure.persistence.product;

import com.perinity.store.domain.model.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface ProductPersistenceMapper {

  ProductEntity toEntity(Product product);

  Product toDomain(ProductEntity entity);
}

