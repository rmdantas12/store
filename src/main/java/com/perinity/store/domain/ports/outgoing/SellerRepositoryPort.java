package com.perinity.store.domain.ports.outgoing;

import com.perinity.store.domain.model.Seller;

import java.util.Optional;

public interface SellerRepositoryPort {

  Optional<Seller> findByCode(String code);

  void saveIfNotExists(Seller seller);

}
