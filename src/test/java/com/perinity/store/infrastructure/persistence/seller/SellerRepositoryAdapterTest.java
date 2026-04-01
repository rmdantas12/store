package com.perinity.store.infrastructure.persistence.seller;

import com.perinity.store.domain.model.Seller;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SellerRepositoryAdapterTest {

  @Mock
  SellerRepositoryJpa repositoryJpa;

  @Test
  void findByCode_whenEntityExists_shouldMapToDomain() {
    final var adapter = new SellerRepositoryAdapter(repositoryJpa);

    final var entity = SellerEntity.builder()
        .code("seller")
        .name("Seller Name")
        .build();

    when(repositoryJpa.findByCode("seller")).thenReturn(Optional.of(entity));

    final var result = adapter.findByCode("seller");

    assertTrue(result.isPresent());

    final var seller = Seller.builder()
        .code("seller")
        .name("Seller Name")
        .build();

    assertEquals(seller, result.get());
  }

  @Test
  void saveIfNotExists_whenAlreadyExists_shouldNotPersist() {
    final var adapter = new SellerRepositoryAdapter(repositoryJpa);

    final var entity = SellerEntity.builder()
        .code("seller")
        .name("x")
        .build();

    when(repositoryJpa.findByCode("seller")).thenReturn(Optional.of(entity));

    final var seller = Seller.builder()
        .code("seller")
        .name("Seller Name")
        .build();

    adapter.saveIfNotExists(seller);

    verify(repositoryJpa, never()).persist(any(SellerEntity.class));
  }

  @Test
  void saveIfNotExists_whenDoesNotExist_shouldPersist() {
    final var adapter = new SellerRepositoryAdapter(repositoryJpa);

    when(repositoryJpa.findByCode("seller")).thenReturn(Optional.empty());

    final var seller = Seller.builder()
        .code("seller")
        .name("Seller Name")
        .build();

    adapter.saveIfNotExists(seller);

    verify(repositoryJpa).persist(any(SellerEntity.class));
  }

}
