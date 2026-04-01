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

/**
 * Serviço de aplicação responsável pelo ciclo de vida de {@link Customer}.
 *
 * <p>Implementa o caso de uso {@link CustomerUseCase}, aplicando regras de negócio como:
 * unicidade de CPF/e-mail, atualização parcial e validação de existência antes de exclusão.</p>
 */
@RequiredArgsConstructor
@ApplicationScoped
public class CustomerService implements CustomerUseCase {

  private final CustomerRepositoryPort repository;

  /**
   * Cria um novo cliente.
   *
   * <p>Valida unicidade de CPF e e-mail (quando informados) antes de persistir.</p>
   *
   * @param customer cliente a ser criado
   * @return cliente persistido
   * @throws DuplicateCpfException se o CPF informado já existir
   * @throws DuplicateEmailException se o e-mail informado já existir
   */
  @Override
  @Transactional
  public Customer create(final Customer customer) {
    checkUniqueFields(customer);
    return repository.save(customer);
  }

  /**
   * Atualiza um cliente existente, realizando atualização parcial.
   *
   * <p>Somente campos não nulos no {@code updatedCustomer} sobrescrevem o cliente existente.
   * A validação de unicidade é aplicada ao e-mail quando informado.</p>
   *
   * @param code identificador do cliente
   * @param updatedCustomer dados para atualização (parcial)
   * @return cliente atualizado
   * @throws CustomerNotFoundException se o cliente não existir
   * @throws DuplicateEmailException se o e-mail informado já existir
   */
  @Override
  @Transactional
  public Customer update(final UUID code, final Customer updatedCustomer) {
    final Customer existing = repository.findByCode(code)
        .orElseThrow(CustomerNotFoundException::new);

    checkUniqueEmail(updatedCustomer);

    Optional.ofNullable(updatedCustomer.getFullName())
        .ifPresent(existing::setFullName);

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

  /**
   * Exclui um cliente pelo código.
   *
   * @param code identificador do cliente
   * @throws CustomerNotFoundException se o cliente não existir
   */
  @Override
  @Transactional
  public void delete(UUID code) {
    if (!repository.existsByCode(code)) {
      throw new CustomerNotFoundException();
    }

    repository.deleteByCode(code);
  }

  /**
   * Busca um cliente pelo código.
   *
   * @param code identificador do cliente
   * @return cliente encontrado
   * @throws CustomerNotFoundException se o cliente não existir
   */
  @Override
  public Customer findByCode(UUID code) {
    return repository.findByCode(code)
        .orElseThrow(CustomerNotFoundException::new);
  }

  /**
   * Lista todos os clientes cadastrados.
   *
   * @return lista de clientes
   */
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