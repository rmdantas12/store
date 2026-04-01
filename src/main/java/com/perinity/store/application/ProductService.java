package com.perinity.store.application;

import com.perinity.store.domain.exception.ProductNotFoundException;
import com.perinity.store.domain.model.Product;
import com.perinity.store.domain.ports.incoming.ProductUseCase;
import com.perinity.store.domain.ports.outgoing.ProductRepositoryPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Caso de uso de produtos ({@link ProductUseCase}).
 */
@RequiredArgsConstructor
@ApplicationScoped
public class ProductService implements ProductUseCase {

  private final ProductRepositoryPort repository;

  /**
   * Cria um produto.
   */
  @Override
  @Transactional
  public Product create(final Product product) {
    return repository.save(product);
  }

  /**
   * Atualiza um produto (parcial).
   */
  @Override
  @Transactional
  public Product update(final UUID code, final Product updatedProduct) {
    final var existing = repository.findByCode(code)
        .orElseThrow(ProductNotFoundException::new);

    Optional.ofNullable(updatedProduct.getName())
        .ifPresent(existing::setName);
    Optional.ofNullable(updatedProduct.getType())
        .ifPresent(existing::setType);
    Optional.ofNullable(updatedProduct.getDetails())
        .ifPresent(existing::setDetails);

    Optional.ofNullable(updatedProduct.getHeightCm())
        .ifPresent(existing::setHeightCm);
    Optional.ofNullable(updatedProduct.getWidthCm())
        .ifPresent(existing::setWidthCm);
    Optional.ofNullable(updatedProduct.getDepthCm())
        .ifPresent(existing::setDepthCm);
    Optional.ofNullable(updatedProduct.getWeightKg())
        .ifPresent(existing::setWeightKg);

    Optional.ofNullable(updatedProduct.getPurchasePrice())
        .ifPresent(existing::setPurchasePrice);
    Optional.ofNullable(updatedProduct.getSalePrice())
        .ifPresent(existing::setSalePrice);

    return repository.update(existing);
  }

  /**
   * Exclui um produto por código.
   */
  @Override
  @Transactional
  public void delete(final UUID code) {
    if (!repository.existsByCode(code)) {
      throw new ProductNotFoundException();
    }
    repository.deleteByCode(code);
  }

  /**
   * Busca um produto por código.
   */
  @Override
  public Product findByCode(final UUID code) {
    return repository.findByCode(code)
        .orElseThrow(ProductNotFoundException::new);
  }

  /**
   * Lista produtos.
   */
  @Override
  public List<Product> findAll() {
    return repository.findAll();
  }
}

