package com.perinity.store.infrastructure.persistence.customer;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositório JPA/Panache para {@link CustomerEntity}.
 *
 * <p>Concentra as consultas específicas (por código, CPF e e-mail) usadas pela camada de
 * persistência.</p>
 */
@ApplicationScoped
public class CustomerRepositoryJpa implements PanacheRepositoryBase<CustomerEntity, UUID> {

  /**
   * Busca a entidade de cliente pelo código (UUID).
   *
   * @param code identificador do cliente
   * @return {@link Optional} com a entidade encontrada, se existir
   */
  public Optional<CustomerEntity> findByCode(final UUID code) {
    return find("code", code).firstResultOptional();
  }

  /**
   * Verifica se existe uma entidade de cliente com o código informado.
   *
   * @param code identificador do cliente
   * @return {@code true} se existir, caso contrário {@code false}
   */
  public boolean existsByCode(final UUID code) {
    return count("code", code) > 0;
  }

  /**
   * Verifica se existe uma entidade de cliente com o CPF informado.
   *
   * @param cpf CPF do cliente
   * @return {@code true} se existir, caso contrário {@code false}
   */
  public boolean existsByCpf(final String cpf) {
    return count("cpf", cpf) > 0;
  }

  /**
   * Verifica se existe uma entidade de cliente com o e-mail informado.
   *
   * @param email e-mail do cliente
   * @return {@code true} se existir, caso contrário {@code false}
   */
  public boolean existsByEmail(final String email) {
    return count("email", email) > 0;
  }

}