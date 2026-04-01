package com.perinity.store.infrastructure.persistence.product;

import com.perinity.store.domain.model.Product;
import com.perinity.store.domain.ports.outgoing.ProductRepositoryPort;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adaptador de persistência do agregado {@link Product}.
 *
 * <p>Responsável por traduzir o modelo de domínio para entidade JPA e vice-versa, delegando
 * as operações CRUD para o repositório Panache ({@link ProductRepositoryJpa}).</p>
 */
@RequiredArgsConstructor
@ApplicationScoped
public class ProductRepositoryAdapter implements ProductRepositoryPort {

  private final ProductRepositoryJpa repository;

  private final ProductPersistenceMapper productPersistenceMapper;

  /**
   * Persiste um novo produto.
   *
   * @param product produto no modelo de domínio
   * @return produto persistido no modelo de domínio
   */
  @Override
  public Product save(final Product product) {
    final var entity = productPersistenceMapper.toEntity(product);
    repository.persist(entity);
    return productPersistenceMapper.toDomain(entity);
  }

  /**
   * Atualiza um produto existente.
   *
   * @param product produto no modelo de domínio
   * @return produto atualizado no modelo de domínio
   */
  @Override
  public Product update(final Product product) {
    final var entity = productPersistenceMapper.toEntity(product);
    repository.getEntityManager().merge(entity);
    return productPersistenceMapper.toDomain(entity);
  }

  /**
   * Busca um produto pelo código (UUID).
   *
   * @param code identificador do produto
   * @return {@link Optional} com o produto encontrado, se existir
   */
  @Override
  public Optional<Product> findByCode(final UUID code) {
    return repository.findByCode(code)
        .map(productPersistenceMapper::toDomain);
  }

  /**
   * Lista todos os produtos persistidos.
   *
   * @return lista de produtos no modelo de domínio
   */
  @Override
  public List<Product> findAll() {
    return repository.listAll()
        .stream()
        .map(productPersistenceMapper::toDomain)
        .toList();
  }

  /**
   * Exclui um produto pelo código (UUID).
   *
   * @param code identificador do produto
   */
  @Override
  public void deleteByCode(final UUID code) {
    repository.delete("code", code);
  }

  /**
   * Verifica se existe produto com o código informado.
   *
   * @param code identificador do produto
   * @return {@code true} se existir, caso contrário {@code false}
   */
  @Override
  public boolean existsByCode(final UUID code) {
    return repository.existsByCode(code);
  }
}

