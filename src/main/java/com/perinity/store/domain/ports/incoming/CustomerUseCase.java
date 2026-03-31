package com.perinity.store.domain.ports.incoming;

import com.perinity.store.domain.model.Customer;

import java.util.List;
import java.util.UUID;

public interface CustomerUseCase {

  Customer create(Customer customer);

  Customer update(UUID code, Customer customer);

  void delete(UUID code);

  Customer findByCode(UUID code);

  List<Customer> findAll();

}
