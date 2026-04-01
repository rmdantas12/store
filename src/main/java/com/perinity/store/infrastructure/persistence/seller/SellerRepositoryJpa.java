package com.perinity.store.infrastructure.persistence.seller;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class SellerRepositoryJpa implements PanacheRepository<SellerEntity> {

  public Optional<SellerEntity> findByCode(final String code) {
    return find("code", code).firstResultOptional();
  }

}
