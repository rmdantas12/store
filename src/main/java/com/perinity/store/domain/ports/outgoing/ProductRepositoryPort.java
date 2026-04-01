package com.perinity.store.domain.ports.outgoing;

import com.perinity.store.domain.model.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepositoryPort {

  Product save(Product product);

  Product update(Product product);

  Optional<Product> findByCode(UUID code);

  List<Product> findAll();

  void deleteByCode(UUID code);

  boolean existsByCode(UUID code);
}

