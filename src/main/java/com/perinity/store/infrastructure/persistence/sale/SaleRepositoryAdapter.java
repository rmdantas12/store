package com.perinity.store.infrastructure.persistence.sale;

import com.perinity.store.domain.model.Sale;
import com.perinity.store.domain.ports.outgoing.SaleRepositoryPort;
import com.perinity.store.infrastructure.persistence.customer.CustomerRepositoryJpa;
import com.perinity.store.infrastructure.persistence.product.ProductRepositoryJpa;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adaptador JPA/Panache do agregado {@link Sale}.
 */
@RequiredArgsConstructor
@ApplicationScoped
public class SaleRepositoryAdapter implements SaleRepositoryPort {

  private final SaleRepositoryJpa saleRepositoryJpa;
  private final SaleItemRepositoryJpa saleItemRepositoryJpa;
  private final CustomerRepositoryJpa customerRepositoryJpa;
  private final ProductRepositoryJpa productRepositoryJpa;
  private final SalePersistenceMapper salePersistenceMapper;

  /**
   * Persiste uma nova venda.
   */
  @Override
  public Sale save(final Sale sale) {
    final var entity = salePersistenceMapper.toEntity(sale, customerRepositoryJpa, productRepositoryJpa);
    saleRepositoryJpa.persist(entity);
    saleRepositoryJpa.getEntityManager().flush();
    return salePersistenceMapper.toDomain(entity);
  }

  /**
   * Atualiza uma venda existente.
   */
  @Override
  public Sale update(final Sale sale) {
    final var entity = salePersistenceMapper.toEntity(sale, customerRepositoryJpa, productRepositoryJpa);
    saleRepositoryJpa.getEntityManager().merge(entity);
    saleRepositoryJpa.getEntityManager().flush();
    return salePersistenceMapper.toDomain(entity);
  }

  /**
   * Busca uma venda por código, carregando cliente e itens.
   */
  @Override
  public Optional<Sale> findByCode(final UUID code) {
    return saleRepositoryJpa.findByCodeWithDetails(code)
        .map(salePersistenceMapper::toDomain);
  }

  /**
   * Lista vendas, carregando cliente e itens (mais recentes primeiro).
   */
  @Override
  public List<Sale> findAll() {
    final var query = saleRepositoryJpa.find("select distinct s from SaleEntity s " +
            "join fetch s.customer " +
            "left join fetch s.items i " +
            "left join fetch i.product " +
            "order by s.createdAt desc"
    );

    return query.list()
        .stream()
        .map(salePersistenceMapper::toDomain)
        .toList();
  }

  /**
   * Exclui uma venda e seus itens.
   */
  @Override
  public void deleteByCode(final UUID code) {
    saleItemRepositoryJpa.deleteBySaleCode(code);
    saleRepositoryJpa.delete("code", code);
  }

  /**
   * Verifica existência por código.
   */
  @Override
  public boolean existsByCode(final UUID code) {
    return saleRepositoryJpa.count("code", code) > 0;
  }

}

