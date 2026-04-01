package com.perinity.store.infrastructure.persistence.product;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositório JPA/Panache para {@link ProductEntity}.
 *
 * <p>Concentra as consultas específicas usadas pela camada de persistência.</p>
 */
@ApplicationScoped
public class ProductRepositoryJpa implements PanacheRepositoryBase<ProductEntity, UUID> {

  /**
   * Busca a entidade de produto pelo código (UUID).
   *
   * @param code identificador do produto
   * @return {@link Optional} com a entidade encontrada, se existir
   */
  public Optional<ProductEntity> findByCode(final UUID code) {
    return find("code", code).firstResultOptional();
  }

  /**
   * Verifica se existe uma entidade de produto com o código informado.
   *
   * @param code identificador do produto
   * @return {@code true} se existir, caso contrário {@code false}
   */
  public boolean existsByCode(final UUID code) {
    return count("code", code) > 0;
  }
}

