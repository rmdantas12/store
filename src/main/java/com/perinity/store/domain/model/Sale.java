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
 * Entidade de domínio que representa uma venda cadastrada no sistema.
 *
 * <p>Uma venda é composta por um cliente, itens (produtos + quantidades) e uma forma de pagamento. No momento
 * do cadastro, o valor unitário de cada item é armazenado no próprio item para manter histórico.</p>
 */
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Sale {

  /**
   * Código único da venda.
   *
   * <p>Identificador do registro no sistema. É autogerado na persistência quando não informado.</p>
   */
  @EqualsAndHashCode.Include
  private UUID code;

  /**
   * Código do cliente associado à venda.
   */
  private UUID customerCode;

  /**
   * Nome do cliente no momento da venda.
   *
   * <p>Usado para exibição em consultas/listagens. Pode ser preenchido no cadastro (enriquecimento).</p>
   */
  private String customerName;

  /**
   * Código do vendedor responsável pela venda.
   *
   * <p>Como o cadastro de vendedor não faz parte do escopo por enquanto, este campo pode ser opcional.</p>
   */
  private String sellerCode;

  /**
   * Itens vendidos.
   *
   * <p>Cada item contém produto, quantidade e valor unitário.</p>
   */
  private List<SaleItem> items;

  /**
   * Forma de pagamento selecionada.
   */
  private PaymentMethod paymentMethod;

  /**
   * Valor pago em dinheiro (quando {@link #paymentMethod} = {@link PaymentMethod#CASH}).
   */
  private BigDecimal cashPaidAmount;

  /**
   * Número do cartão (quando {@link #paymentMethod} = {@link PaymentMethod#CREDIT_CARD}).
   */
  private String cardNumber;

  /**
   * Imposto fixo sobre a venda, calculado em 9% do total de produtos.
   */
  private BigDecimal taxAmount;

  /**
   * Total dos produtos.
   *
   * <p>Calculado como a soma de (quantidade * valor unitário) de cada item.</p>
   *
   * <p>Exemplo: item A (2 * 10.00) + item B (1 * 5.00) = 25.00.</p>
   */
  private BigDecimal productsTotal;

  /**
   * Total final da venda (total dos produtos + imposto).
   */
  private BigDecimal saleTotal;

  /**
   * Data e hora de cadastro da venda.
   *
   * <p>Representa o momento em que a venda foi registrada no sistema. Quando não informada,
   * é definida no cadastro.</p>
   */
  private LocalDateTime createdAt;

  /**
   * Data e hora da última atualização da venda.
   *
   * <p>É atualizada automaticamente na persistência a cada alteração do registro.</p>
   */
  private LocalDateTime updatedAt;
}

