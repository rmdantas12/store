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
   * Nome do produto para exibição/consulta.
   */
  private String productName;

  /**
   * Quantidade comprada do produto.
   */
  private Integer quantity;

  /**
   * Valor unitário congelado no momento da venda.
   */
  private BigDecimal unitPrice;
}

