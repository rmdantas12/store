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
 * Entidade de domínio que representa um produto cadastrado no sistema.
 *
 * <p>Este modelo contém apenas os dados do produto e é usado pela camada de domínio/aplicação,
 * independente de detalhes de persistência e transporte (web).</p>
 */
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

  /**
   * Código único do produto.
   *
   * <p>Identificador do registro no sistema. É autogerado na persistência quando não informado.</p>
   */
  @EqualsAndHashCode.Include
  private UUID code;

  /**
   * Nome do produto.
   *
   * <p>Ex.: "Amortecedor traseiro", "Banco esportivo".</p>
   */
  private String name;

  /**
   * Tipo do produto (ex.: acabamento externo, acabamento interno, amortecedor, banco, elétrico, etc.).
   */
  private String type;

  /**
   * Detalhes do produto (ex.: a qual carro se destina).
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
   * Preço de compra do produto.
   *
   * <p>Valor pago para adquirir o item (custo).</p>
   */
  private BigDecimal purchasePrice;

  /**
   * Preço de venda do produto.
   *
   * <p>Valor praticado para venda ao cliente.</p>
   */
  private BigDecimal salePrice;

  /**
   * Data de cadastro no sistema.
   *
   * <p>Definida no momento da criação/persistência do produto.</p>
   */
  private LocalDateTime createdAt;

}

