package com.perinity.store.infrastructure.persistence.customer;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class CustomerRepositoryJpa implements PanacheRepositoryBase<CustomerEntity, UUID> {

  public Optional<CustomerEntity> findByCode(final UUID code) {
    return find("code", code).firstResultOptional();
  }

  public boolean existsByCode(final UUID code) {
    return count("code", code) > 0;
  }

  public boolean existsByCpf(final String cpf) {
    return count("cpf", cpf) > 0;
  }

  public boolean existsByEmail(final String email) {
    return count("email", email) > 0;
  }

}