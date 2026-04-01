package com.perinity.store.infrastructure.persistence.seller;

import com.perinity.store.domain.model.Seller;
import com.perinity.store.domain.ports.outgoing.SellerRepositoryPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
@ApplicationScoped
public class SellerRepositoryAdapter implements SellerRepositoryPort {

  private final SellerRepositoryJpa repositoryJpa;

  @Override
  public Optional<Seller> findByCode(final String code) {
    return repositoryJpa.findByCode(code)
        .map(it ->
            Seller.builder()
                .code(it.getCode())
                .name(it.getName())
                .build()
        );
  }

  @Override
  @Transactional
  public void saveIfNotExists(final Seller seller) {
    if (repositoryJpa.findByCode(seller.getCode()).isPresent()) {
      return;
    }

    final var entity = SellerEntity.builder()
        .code(seller.getCode())
        .name(seller.getName())
        .build();

    repositoryJpa.persist(entity);
  }

}
