package com.perinity.store.infrastructure.persistence.customer;

import com.perinity.store.domain.model.Customer;
import com.perinity.store.domain.ports.outgoing.CustomerRepositoryPort;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@ApplicationScoped
public class CustomerRepositoryAdapter implements CustomerRepositoryPort {

  private final CustomerRepositoryJpa repository;

  private final CustomerPersistenceMapper customerPersistenceMapper;

  @Override
  public Customer save(final Customer customer) {
    final var entity = customerPersistenceMapper.toEntity(customer);
    repository.persist(entity);
    return customerPersistenceMapper.toDomain(entity);
  }

  @Override
  public Customer update(final Customer customer) {
    final var entity = customerPersistenceMapper.toEntity(customer);
    repository.getEntityManager().merge(entity);
    return customerPersistenceMapper.toDomain(entity);
  }

  @Override
  public Optional<Customer> findByCode(UUID code) {
    return repository.findByCode(code)
        .map(customerPersistenceMapper::toDomain);
  }

  @Override
  public List<Customer> findAll() {
    return repository.listAll()
        .stream()
        .map(customerPersistenceMapper::toDomain)
        .toList();
  }

  @Override
  public void deleteByCode(final UUID code) {
    repository.delete("code", code);
  }

  @Override
  public boolean existsByCode(final UUID code) {
    return repository.existsByCode(code);
  }

  @Override
  public boolean existsByCpf(String cpf) {
    return repository.existsByCpf(cpf);
  }

  @Override
  public boolean existsByEmail(String email) {
    return repository.existsByEmail(email);
  }

}