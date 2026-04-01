package com.perinity.store.infrastructure.persistence.product;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositório JPA/Panache de {@link ProductEntity}.
 */
@ApplicationScoped
public class ProductRepositoryJpa implements PanacheRepositoryBase<ProductEntity, UUID> {

  /**
   * Busca por código.
   */
  public Optional<ProductEntity> findByCode(final UUID code) {
    return find("code", code).firstResultOptional();
  }

  /**
   * Verifica existência por código.
   */
  public boolean existsByCode(final UUID code) {
    return count("code", code) > 0;
  }
}

