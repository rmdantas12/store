package com.perinity.store.domain.ports.incoming;

import com.perinity.store.domain.model.Product;

import java.util.List;
import java.util.UUID;

public interface ProductUseCase {

  Product create(Product product);

  Product update(UUID code, Product product);

  void delete(UUID code);

  Product findByCode(UUID code);

  List<Product> findAll();
}

