package com.perinity.store.domain.ports.outgoing;

import com.perinity.store.domain.model.Customer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepositoryPort {

  Customer save(Customer customer);

  Customer update(Customer customer);

  Optional<Customer> findByCode(UUID code);

  List<Customer> findAll();

  void deleteByCode(UUID code);

  boolean existsByCode(UUID code);

  boolean existsByCpf(String cpf);

  boolean existsByEmail(String email);

}
