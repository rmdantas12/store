package com.perinity.store.application;

import com.perinity.store.domain.exception.CustomerNotFoundException;
import com.perinity.store.domain.exception.DuplicateCpfException;
import com.perinity.store.domain.exception.DuplicateEmailException;
import com.perinity.store.domain.model.Customer;
import com.perinity.store.domain.ports.incoming.CustomerUseCase;
import com.perinity.store.domain.ports.outgoing.CustomerRepositoryPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@ApplicationScoped
public class CustomerService implements CustomerUseCase {

  private final CustomerRepositoryPort repository;

  @Override
  @Transactional
  public Customer create(final Customer customer) {
    checkUniqueFields(customer);
    return repository.save(customer);
  }

  @Override
  @Transactional
  public Customer update(final UUID code, final Customer updatedCustomer) {
    final Customer existing = repository.findByCode(code)
        .orElseThrow(CustomerNotFoundException::new);

    checkUniqueEmail(updatedCustomer);

    Optional.ofNullable(updatedCustomer.getFullName())
        .ifPresent(existing::setFullAddress);

    Optional.ofNullable(updatedCustomer.getMotherName())
        .ifPresent(existing::setMotherName);

    Optional.ofNullable(updatedCustomer.getFullAddress())
        .ifPresent(existing::setFullAddress);

    Optional.ofNullable(updatedCustomer.getZipCode())
        .ifPresent(existing::setZipCode);

    Optional.ofNullable(updatedCustomer.getRg())
        .ifPresent(existing::setRg);

    Optional.ofNullable(updatedCustomer.getBirthDate())
        .ifPresent(existing::setBirthDate);

    Optional.ofNullable(updatedCustomer.getCellPhone())
        .ifPresent(existing::setCellPhone);

    Optional.ofNullable(updatedCustomer.getEmail())
        .ifPresent(existing::setEmail);

    return repository.update(existing);
  }

  @Override
  @Transactional
  public void delete(UUID code) {
    if (repository.existsByCode(code)) {
      throw new CustomerNotFoundException();
    }

    repository.deleteByCode(code);
  }

  @Override
  public Customer findByCode(UUID code) {
    return repository.findByCode(code)
        .orElseThrow(CustomerNotFoundException::new);
  }

  @Override
  public List<Customer> findAll() {
    // TODO: Refatorar para consulta paginada
    return repository.findAll();
  }

  private void checkUniqueFields(final Customer customer) {
    checkUniqueCpf(customer);
    checkUniqueEmail(customer);
  }

  private void checkUniqueCpf(final Customer customer) {
    final var cpf = customer.getCpf();
    checkUniqueCpf(cpf);
  }

  private void checkUniqueCpf(final String cpf) {
    if (Objects.nonNull(cpf) && repository.existsByCpf(cpf)) {
      throw new DuplicateCpfException();
    }
  }

  private void checkUniqueEmail(final Customer customer) {
    final var email = customer.getEmail();
    checkUniqueEmail(email);
  }

  private void checkUniqueEmail(final String email) {
    if (Objects.nonNull(email) && repository.existsByEmail(email)) {
      throw new DuplicateEmailException();
    }
  }

}