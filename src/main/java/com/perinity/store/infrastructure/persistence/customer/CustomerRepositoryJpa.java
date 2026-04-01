package com.perinity.store.infrastructure.persistence.customer;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositório JPA/Panache de {@link CustomerEntity}.
 */
@ApplicationScoped
public class CustomerRepositoryJpa implements PanacheRepositoryBase<CustomerEntity, UUID> {

  /**
   * Busca por código.
   */
  public Optional<CustomerEntity> findByCode(final UUID code) {
    return find("code", code).firstResultOptional();
  }

  /**
   * Verifica existência por código.
   */
  public boolean existsByCode(final UUID code) {
    return count("code", code) > 0;
  }

  /**
   * Verifica existência por CPF.
   */
  public boolean existsByCpf(final String cpf) {
    return count("cpf", cpf) > 0;
  }

  /**
   * Verifica existência por e-mail.
   */
  public boolean existsByEmail(final String email) {
    return count("email", email) > 0;
  }

}