package com.perinity.store.infrastructure.persistence.sale;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class SaleRepositoryJpa implements PanacheRepositoryBase<SaleEntity, UUID> {

  public Optional<SaleEntity> findByCodeWithDetails(final UUID code) {
    return find("select s from SaleEntity s " +
        "join fetch s.customer " +
        "left join fetch s.items i " +
        "left join fetch i.product " +
        "where s.code = ?1", code).firstResultOptional();
  }
}

