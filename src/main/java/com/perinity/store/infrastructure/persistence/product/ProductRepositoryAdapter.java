package com.perinity.store.infrastructure.persistence.product;

import com.perinity.store.domain.model.Product;
import com.perinity.store.domain.ports.outgoing.ProductRepositoryPort;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adaptador JPA/Panache do agregado {@link Product}.
 */
@RequiredArgsConstructor
@ApplicationScoped
public class ProductRepositoryAdapter implements ProductRepositoryPort {

  private final ProductRepositoryJpa repository;

  private final ProductPersistenceMapper productPersistenceMapper;

  /**
   * Persiste um novo produto.
   */
  @Override
  public Product save(final Product product) {
    final var entity = productPersistenceMapper.toEntity(product);
    repository.persist(entity);
    return productPersistenceMapper.toDomain(entity);
  }

  /**
   * Atualiza um produto existente.
   */
  @Override
  public Product update(final Product product) {
    final var entity = productPersistenceMapper.toEntity(product);
    repository.getEntityManager().merge(entity);
    return productPersistenceMapper.toDomain(entity);
  }

  /**
   * Busca um produto por código.
   */
  @Override
  public Optional<Product> findByCode(final UUID code) {
    return repository.findByCode(code)
        .map(productPersistenceMapper::toDomain);
  }

  /**
   * Lista produtos (ordenado por nome).
   */
  @Override
  public List<Product> findAll() {
    return repository.listAll(Sort.by("name"))
        .stream()
        .map(productPersistenceMapper::toDomain)
        .toList();
  }

  /**
   * Exclui um produto por código.
   */
  @Override
  public void deleteByCode(final UUID code) {
    repository.delete("code", code);
  }

  /**
   * Verifica existência por código.
   */
  @Override
  public boolean existsByCode(final UUID code) {
    return repository.existsByCode(code);
  }
}

