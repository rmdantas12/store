package com.perinity.store.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Produto cadastrado no sistema.
 */
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

  /**
   * Código único do produto (gerado na persistência).
   */
  @EqualsAndHashCode.Include
  private UUID code;

  /**
   * Nome do produto.
   */
  private String name;

  /**
   * Tipo do produto (ex.: acabamento, amortecedor, banco, elétrico).
   */
  private String type;

  /**
   * Detalhes do produto (ex.: compatibilidade).
   */
  private String details;

  /**
   * Altura do produto em centímetros.
   */
  private BigDecimal heightCm;

  /**
   * Largura do produto em centímetros.
   */
  private BigDecimal widthCm;

  /**
   * Profundidade do produto em centímetros.
   */
  private BigDecimal depthCm;

  /**
   * Peso do produto em quilogramas.
   */
  private BigDecimal weightKg;

  /**
   * Preço de compra (custo).
   */
  private BigDecimal purchasePrice;

  /**
   * Preço de venda.
   */
  private BigDecimal salePrice;

  /**
   * Data/hora de cadastro (definida na persistência).
   */
  private LocalDateTime createdAt;

}

