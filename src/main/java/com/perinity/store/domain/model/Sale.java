package com.perinity.store.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Venda registrada no sistema.
 */
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Sale {

  /**
   * Código único da venda (gerado na persistência).
   */
  @EqualsAndHashCode.Include
  private UUID code;

  /**
   * Código do cliente associado à venda.
   */
  private UUID customerCode;

  /**
   * Nome do cliente para exibição/consulta.
   */
  private String customerName;

  /**
   * Código do vendedor (associação futura).
   */
  private String sellerCode;

  /**
   * Nome do vendedor para exibição/consulta.
   */
  private String sellerName;

  /**
   * Itens vendidos (quantidade e valor unitário congelado).
   */
  private List<SaleItem> items;

  /**
   * Forma de pagamento selecionada.
   */
  private PaymentMethod paymentMethod;

  /**
   * Valor pago em dinheiro (apenas para {@link PaymentMethod#CASH}).
   */
  private BigDecimal cashPaidAmount;

  /**
   * Número do cartão (apenas para {@link PaymentMethod#CREDIT_CARD}).
   */
  private String cardNumber;

  /**
   * Imposto fixo (9% do total de produtos).
   */
  private BigDecimal taxAmount;

  /**
   * Total dos produtos (soma de quantidade * valor unitário por item).
   */
  private BigDecimal productsTotal;

  /**
   * Total final da venda (produtos + imposto).
   */
  private BigDecimal saleTotal;

  /**
   * Data/hora de cadastro (definida pelo sistema).
   */
  private LocalDateTime createdAt;

  /**
   * Data/hora da última atualização (definida na persistência).
   */
  private LocalDateTime updatedAt;
}

