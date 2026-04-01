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
 * Serviço de aplicação responsável pelo ciclo de vida de {@link Product}.
 *
 * <p>Implementa o caso de uso {@link ProductUseCase}, aplicando atualização parcial e validação de existência
 * antes de operações que dependem do registro.</p>
 */
@RequiredArgsConstructor
@ApplicationScoped
public class ProductService implements ProductUseCase {

  private final ProductRepositoryPort repository;

  /**
   * Cria um novo produto.
   *
   * @param product produto a ser criado
   * @return produto persistido
   */
  @Override
  @Transactional
  public Product create(final Product product) {
    return repository.save(product);
  }

  /**
   * Atualiza um produto existente, realizando atualização parcial.
   *
   * <p>Somente campos não nulos no {@code updatedProduct} sobrescrevem o produto existente.</p>
   *
   * @param code identificador do produto
   * @param updatedProduct dados para atualização (parcial)
   * @return produto atualizado
   * @throws ProductNotFoundException se o produto não existir
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
   * Exclui um produto pelo código.
   *
   * @param code identificador do produto
   * @throws ProductNotFoundException se o produto não existir
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
   * Busca um produto pelo código.
   *
   * @param code identificador do produto
   * @return produto encontrado
   * @throws ProductNotFoundException se o produto não existir
   */
  @Override
  public Product findByCode(final UUID code) {
    return repository.findByCode(code)
        .orElseThrow(ProductNotFoundException::new);
  }

  /**
   * Lista todos os produtos cadastrados.
   *
   * @return lista de produtos
   */
  @Override
  public List<Product> findAll() {
    return repository.findAll();
  }
}

