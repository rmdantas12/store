package com.perinity.store.application;

import com.perinity.store.domain.exception.CustomerNotFoundException;
import com.perinity.store.domain.exception.DuplicateCpfException;
import com.perinity.store.domain.exception.DuplicateEmailException;
import com.perinity.store.domain.model.Customer;
import com.perinity.store.domain.ports.outgoing.CustomerRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

  @Mock
  CustomerRepositoryPort repository;

  @InjectMocks
  CustomerService service;

  /**
   * Cenário: criar um cliente com CPF já cadastrado.
   * Expectativa: a operação deve ser rejeitada com {@link DuplicateCpfException}
   * e nenhum comando de persistência deve ser executado.
   */
  @Test
  void create_whenCpfAlreadyExists_shouldThrowDuplicateCpfException() {
    final var customer = Customer.builder()
        .cpf("123")
        .email("a@b.com")
        .build();

    when(repository.existsByCpf("123")).thenReturn(true);

    assertThrows(DuplicateCpfException.class, () -> service.create(customer));

    verify(repository, never()).save(customer);
  }

  /**
   * Cenário: criar um cliente com e-mail já cadastrado.
   * Expectativa: a operação deve ser rejeitada com {@link DuplicateEmailException}
   * e nenhum comando de persistência deve ser executado.
   */
  @Test
  void create_whenEmailAlreadyExists_shouldThrowDuplicateEmailException() {
    final var customer = Customer.builder()
        .cpf("123")
        .email("a@b.com")
        .build();

    when(repository.existsByCpf("123")).thenReturn(false);
    when(repository.existsByEmail("a@b.com")).thenReturn(true);

    assertThrows(DuplicateEmailException.class, () -> service.create(customer));

    verify(repository, never()).save(customer);
  }

  /**
   * Cenário: criar um cliente com CPF e e-mail únicos.
   * Expectativa: o cliente deve ser persistido e retornado como criado.
   */
  @Test
  void create_whenUniqueFields_shouldSaveAndReturnCustomer() {
    final var customer = Customer.builder()
        .cpf("123")
        .email("a@b.com")
        .build();

    final var created = Customer.builder()
        .code(UUID.randomUUID())
        .cpf("123")
        .email("a@b.com")
        .build();

    when(repository.existsByCpf("123")).thenReturn(false);
    when(repository.existsByEmail("a@b.com")).thenReturn(false);
    when(repository.save(customer)).thenReturn(created);

    final var result = service.create(customer);

    assertSame(created, result);
    verify(repository).save(customer);
  }

  /**
   * Cenário: criar um cliente com CPF e e-mail nulos.
   * Expectativa: nenhuma validação de unicidade deve ser executada e o cliente deve ser salvo.
   */
  @Test
  void create_whenCpfAndEmailAreNull_shouldSaveWithoutUniquenessChecks() {
    final var customer = Customer.builder()
        .cpf(null)
        .email(null)
        .build();

    final var created = Customer.builder()
        .code(UUID.randomUUID())
        .cpf(null)
        .email(null)
        .build();

    when(repository.save(customer)).thenReturn(created);

    final var result = service.create(customer);

    assertSame(created, result);
    verify(repository, never()).existsByCpf(anyString());
    verify(repository, never()).existsByEmail(anyString());
    verify(repository).save(customer);
  }

  /**
   * Cenário: atualizar um cliente inexistente.
   * Expectativa: a operação deve falhar com {@link CustomerNotFoundException}.
   */
  @Test
  void update_whenCustomerDoesNotExist_shouldThrowCustomerNotFoundException() {
    final var code = UUID.randomUUID();
    when(repository.findByCode(code)).thenReturn(Optional.empty());

    assertThrows(CustomerNotFoundException.class, () -> service.update(code, Customer.builder().build()));
  }

  /**
   * Cenário: atualizar um cliente informando um e-mail já existente.
   * Expectativa: a operação deve ser rejeitada com {@link DuplicateEmailException}
   * e a atualização na persistência não deve ser executada.
   */
  @Test
  void update_whenEmailAlreadyExists_shouldThrowDuplicateEmailException() {
    final var code = UUID.randomUUID();

    final var existing = Customer.builder()
        .code(code)
        .email("old@b.com")
        .build();

    final var updatedCustomer = Customer.builder()
        .email("new@b.com")
        .build();

    when(repository.findByCode(code)).thenReturn(Optional.of(existing));
    when(repository.existsByEmail("new@b.com")).thenReturn(true);

    assertThrows(DuplicateEmailException.class, () -> service.update(code, updatedCustomer));

    verify(repository, never()).update(existing);
  }

  /**
   * Cenário: atualização parcial de um cliente.
   * Expectativa: apenas campos não-nulos presentes no payload devem sobrescrever a entidade existente.
   */
  @Test
  void update_whenPartialPayload_shouldUpdateOnlyNonNullFields() {
    final var code = UUID.randomUUID();

    final var existing = Customer.builder()
        .code(code)
        .fullName("Old Name")
        .motherName("Old Mother")
        .fullAddress("Old Address")
        .zipCode("00000")
        .rg("old-rg")
        .birthDate(LocalDate.parse("1990-01-01"))
        .cellPhone("0000")
        .email("old@b.com")
        .build();

    final var updatePayload = Customer.builder()
        .fullName("New Name")
        .zipCode("11111")
        .email("new@b.com")
        .build();

    when(repository.findByCode(code)).thenReturn(Optional.of(existing));
    when(repository.existsByEmail("new@b.com")).thenReturn(false);
    when(repository.update(existing)).thenReturn(existing);

    final var result = service.update(code, updatePayload);

    assertSame(existing, result);
    assertEquals("New Name", existing.getFullName());
    assertEquals("Old Mother", existing.getMotherName());
    assertEquals("Old Address", existing.getFullAddress());
    assertEquals("11111", existing.getZipCode());
    assertEquals("old-rg", existing.getRg());
    assertEquals(LocalDate.parse("1990-01-01"), existing.getBirthDate());
    assertEquals("0000", existing.getCellPhone());
    assertEquals("new@b.com", existing.getEmail());

    verify(repository).update(existing);
  }

  /**
   * Cenário: atualizar um cliente sem informar e-mail (nulo).
   * Expectativa: a verificação de unicidade de e-mail não deve ser executada e a atualização deve prosseguir.
   */
  @Test
  void update_whenEmailIsNull_shouldNotCheckEmailUniqueness() {
    final var code = UUID.randomUUID();

    final var existing = Customer.builder()
        .code(code)
        .email("old@b.com")
        .fullName("Old Name")
        .build();

    final var updatePayload = Customer.builder()
        .email(null)
        .fullName("New Name")
        .build();

    when(repository.findByCode(code)).thenReturn(Optional.of(existing));
    when(repository.update(existing)).thenReturn(existing);

    final var result = service.update(code, updatePayload);

    assertSame(existing, result);
    assertEquals("New Name", existing.getFullName());
    assertEquals("old@b.com", existing.getEmail());
    verify(repository, never()).existsByEmail(anyString());
    verify(repository).update(existing);
  }

  /**
   * Cenário: excluir um cliente inexistente.
   * Expectativa: a operação deve falhar com {@link CustomerNotFoundException}
   * e nenhuma exclusão deve ser executada.
   */
  @Test
  void delete_whenCustomerDoesNotExist_shouldThrowCustomerNotFoundException() {
    final var code = UUID.randomUUID();
    when(repository.existsByCode(code)).thenReturn(false);

    assertThrows(CustomerNotFoundException.class, () -> service.delete(code));

    verify(repository, never()).deleteByCode(code);
  }

  /**
   * Cenário: excluir um cliente existente.
   * Expectativa: a operação de exclusão no repositório deve ser executada.
   */
  @Test
  void delete_whenCustomerExists_shouldDeleteByCode() {
    final var code = UUID.randomUUID();
    when(repository.existsByCode(code)).thenReturn(true);

    service.delete(code);

    verify(repository).deleteByCode(code);
  }

  /**
   * Cenário: buscar um cliente por código inexistente.
   * Expectativa: a operação deve falhar com {@link CustomerNotFoundException}.
   */
  @Test
  void findByCode_whenCustomerDoesNotExist_shouldThrowCustomerNotFoundException() {
    final var code = UUID.randomUUID();
    when(repository.findByCode(code)).thenReturn(Optional.empty());

    assertThrows(CustomerNotFoundException.class, () -> service.findByCode(code));
  }

  /**
   * Cenário: buscar um cliente por código existente.
   * Expectativa: o cliente deve ser retornado.
   */
  @Test
  void findByCode_whenCustomerExists_shouldReturnCustomer() {
    final var code = UUID.randomUUID();

    final var customer = Customer.builder()
        .code(code)
        .build();

    when(repository.findByCode(code)).thenReturn(Optional.of(customer));

    final var result = service.findByCode(code);

    assertSame(customer, result);
  }

  /**
   * Cenário: listar todos os clientes.
   * Expectativa: o serviço deve delegar ao repositório e retornar a mesma lista.
   */
  @Test
  void findAll_shouldReturnAllCustomers() {
    final var customerA = Customer.builder()
        .code(UUID.randomUUID())
        .build();

    final var customerB = Customer.builder()
        .code(UUID.randomUUID())
        .build();

    final var customers = List.of(customerA, customerB);

    when(repository.findAll()).thenReturn(customers);

    final var result = service.findAll();

    assertSame(customers, result);
  }
}

