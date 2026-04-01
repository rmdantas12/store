package com.perinity.store.infrastructure.persistence.customer;

import com.perinity.store.domain.model.Customer;
import com.perinity.store.domain.ports.outgoing.CustomerRepositoryPort;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adaptador de persistência do agregado {@link Customer}.
 *
 * <p>Responsável por traduzir o modelo de domínio para entidade JPA e vice-versa, delegando
 * as operações CRUD para o repositório Panache ({@link CustomerRepositoryJpa}).</p>
 */
@RequiredArgsConstructor
@ApplicationScoped
public class CustomerRepositoryAdapter implements CustomerRepositoryPort {

  private final CustomerRepositoryJpa repository;

  private final CustomerPersistenceMapper customerPersistenceMapper;

  /**
   * Persiste um novo cliente.
   *
   * <p>Converte o {@link Customer} de domínio para {@link CustomerEntity}, persiste e retorna o
   * domínio mapeado a partir da entidade persistida.</p>
   *
   * @param customer cliente no modelo de domínio
   * @return cliente persistido no modelo de domínio
   */
  @Override
  public Customer save(final Customer customer) {
    final var entity = customerPersistenceMapper.toEntity(customer);
    repository.persist(entity);
    return customerPersistenceMapper.toDomain(entity);
  }

  /**
   * Atualiza um cliente existente.
   *
   * <p>Converte o {@link Customer} de domínio para {@link CustomerEntity}, realiza merge via
   * {@code EntityManager} e retorna o domínio mapeado.</p>
   *
   * @param customer cliente no modelo de domínio
   * @return cliente atualizado no modelo de domínio
   */
  @Override
  public Customer update(final Customer customer) {
    final var entity = customerPersistenceMapper.toEntity(customer);
    repository.getEntityManager().merge(entity);
    return customerPersistenceMapper.toDomain(entity);
  }

  /**
   * Busca um cliente pelo código (UUID).
   *
   * @param code identificador do cliente
   * @return {@link Optional} com o cliente encontrado, se existir
   */
  @Override
  public Optional<Customer> findByCode(UUID code) {
    return repository.findByCode(code)
        .map(customerPersistenceMapper::toDomain);
  }

  /**
   * Lista todos os clientes persistidos.
   *
   * @return lista de clientes no modelo de domínio
   */
  @Override
  public List<Customer> findAll() {
    return repository.listAll()
        .stream()
        .map(customerPersistenceMapper::toDomain)
        .toList();
  }

  /**
   * Exclui um cliente pelo código (UUID).
   *
   * @param code identificador do cliente
   */
  @Override
  public void deleteByCode(final UUID code) {
    repository.delete("code", code);
  }

  /**
   * Verifica se existe cliente com o código informado.
   *
   * @param code identificador do cliente
   * @return {@code true} se existir, caso contrário {@code false}
   */
  @Override
  public boolean existsByCode(final UUID code) {
    return repository.existsByCode(code);
  }

  /**
   * Verifica se existe cliente com o CPF informado.
   *
   * @param cpf CPF do cliente
   * @return {@code true} se existir, caso contrário {@code false}
   */
  @Override
  public boolean existsByCpf(String cpf) {
    return repository.existsByCpf(cpf);
  }

  /**
   * Verifica se existe cliente com o e-mail informado.
   *
   * @param email e-mail do cliente
   * @return {@code true} se existir, caso contrário {@code false}
   */
  @Override
  public boolean existsByEmail(String email) {
    return repository.existsByEmail(email);
  }

}