package com.perinity.store.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Item de uma venda.
 *
 * <p>Representa um produto vendido dentro de uma {@link Sale}, incluindo a quantidade comprada e o valor unitário
 * praticado no momento da venda (sem controle de estoque).</p>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleItem {

  /**
   * Código do produto vendido.
   */
  private UUID productCode;

  /**
   * Nome do produto no momento da venda.
   *
   * <p>Usado para exibição em listagens/consultas. Pode ser preenchido no cadastro da venda (enriquecimento).</p>
   */
  private String productName;

  /**
   * Quantidade comprada do produto.
   */
  private Integer quantity;

  /**
   * Valor unitário do produto no momento da venda.
   *
   * <p>Esse valor é "congelado" no item para manter histórico, mesmo que o preço do produto mude futuramente.</p>
   */
  private BigDecimal unitPrice;
}

