package com.perinity.store.domain.ports.outgoing;

import com.perinity.store.domain.model.Sale;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SaleRepositoryPort {

  Sale save(Sale sale);

  Sale update(Sale sale);

  Optional<Sale> findByCode(UUID code);

  List<Sale> findAll();

  void deleteByCode(UUID code);

  boolean existsByCode(UUID code);

}
