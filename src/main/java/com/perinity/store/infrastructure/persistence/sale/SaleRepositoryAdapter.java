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
 * Adaptador de persistência para o agregado {@link Sale}.
 *
 * <p>Responsável por implementar o {@link SaleRepositoryPort} usando Panache/JPA:
 * converte entre o modelo de domínio e as entidades JPA, executa operações de persistência
 * e realiza consultas trazendo os relacionamentos necessários (cliente e itens).</p>
 *
 * <p>A exclusão de uma venda remove seus itens associados, sem afetar os cadastros de
 * cliente e produto.</p>
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
   *
   * @param sale venda no modelo de domínio
   * @return venda persistida no modelo de domínio
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
   *
   * @param sale venda no modelo de domínio
   * @return venda atualizada no modelo de domínio
   */
  @Override
  public Sale update(final Sale sale) {
    final var entity = salePersistenceMapper.toEntity(sale, customerRepositoryJpa, productRepositoryJpa);
    saleRepositoryJpa.getEntityManager().merge(entity);
    saleRepositoryJpa.getEntityManager().flush();
    return salePersistenceMapper.toDomain(entity);
  }

  /**
   * Busca uma venda pelo código (UUID), carregando seus detalhes (cliente e itens).
   *
   * @param code identificador da venda
   * @return {@link Optional} com a venda encontrada, se existir
   */
  @Override
  public Optional<Sale> findByCode(final UUID code) {
    return saleRepositoryJpa.findByCodeWithDetails(code)
        .map(salePersistenceMapper::toDomain);
  }

  /**
   * Lista todas as vendas, carregando cliente e itens.
   *
   * <p>Ordena por {@code createdAt} decrescente (mais recentes primeiro).</p>
   *
   * @return lista de vendas no modelo de domínio
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
   * Exclui uma venda pelo código (UUID).
   *
   * <p>Remove primeiro os itens associados para evitar violação de integridade referencial.</p>
   *
   * @param code identificador da venda
   */
  @Override
  public void deleteByCode(final UUID code) {
    saleItemRepositoryJpa.deleteBySaleCode(code);
    saleRepositoryJpa.delete("code", code);
  }

  /**
   * Verifica se existe uma venda com o código informado.
   *
   * @param code identificador da venda
   * @return {@code true} se existir, caso contrário {@code false}
   */
  @Override
  public boolean existsByCode(final UUID code) {
    return saleRepositoryJpa.count("code", code) > 0;
  }

}

