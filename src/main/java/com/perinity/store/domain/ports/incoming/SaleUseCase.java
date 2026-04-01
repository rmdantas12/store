package com.perinity.store.domain.ports.incoming;

import com.perinity.store.domain.model.Sale;

import java.util.List;
import java.util.UUID;

public interface SaleUseCase {

  Sale create(Sale sale);

  Sale update(UUID code, Sale sale);

  void delete(UUID code);

  Sale findByCode(UUID code);

  List<Sale> findAll();

}
