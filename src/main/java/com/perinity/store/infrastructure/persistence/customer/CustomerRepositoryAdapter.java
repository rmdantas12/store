package com.perinity.store.infrastructure.persistence.customer;

import com.perinity.store.domain.model.Customer;
import com.perinity.store.domain.ports.outgoing.CustomerRepositoryPort;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adaptador JPA/Panache do agregado {@link Customer}.
 */
@RequiredArgsConstructor
@ApplicationScoped
public class CustomerRepositoryAdapter implements CustomerRepositoryPort {

  private final CustomerRepositoryJpa repository;

  private final CustomerPersistenceMapper customerPersistenceMapper;

  /**
   * Persiste um novo cliente.
   */
  @Override
  public Customer save(final Customer customer) {
    final var entity = customerPersistenceMapper.toEntity(customer);
    repository.persist(entity);
    return customerPersistenceMapper.toDomain(entity);
  }

  /**
   * Atualiza um cliente existente.
   */
  @Override
  public Customer update(final Customer customer) {
    final var entity = customerPersistenceMapper.toEntity(customer);
    repository.getEntityManager().merge(entity);
    return customerPersistenceMapper.toDomain(entity);
  }

  /**
   * Busca um cliente por código.
   */
  @Override
  public Optional<Customer> findByCode(UUID code) {
    return repository.findByCode(code)
        .map(customerPersistenceMapper::toDomain);
  }

  /**
   * Lista clientes.
   */
  @Override
  public List<Customer> findAll() {
    return repository.listAll()
        .stream()
        .map(customerPersistenceMapper::toDomain)
        .toList();
  }

  /**
   * Exclui um cliente por código.
   */
  @Override
  public void deleteByCode(final UUID code) {
    repository.delete("code", code);
  }

  /**
   * Verifica existência por código.
   */
  @Override
  public boolean existsByCode(final UUID code) {
    return repository.existsByCode(code);
  }

  /**
   * Verifica existência por CPF.
   */
  @Override
  public boolean existsByCpf(String cpf) {
    return repository.existsByCpf(cpf);
  }

  /**
   * Verifica existência por e-mail.
   */
  @Override
  public boolean existsByEmail(String email) {
    return repository.existsByEmail(email);
  }

}