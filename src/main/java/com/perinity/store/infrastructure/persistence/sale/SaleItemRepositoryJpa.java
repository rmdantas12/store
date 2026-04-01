package com.perinity.store.infrastructure.persistence.sale;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class SaleItemRepositoryJpa implements PanacheRepositoryBase<SaleItemEntity, Long> {

  public long deleteBySaleCode(final UUID saleCode) {
    return delete("sale.code", saleCode);
  }
}

