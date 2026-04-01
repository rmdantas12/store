package com.perinity.store.domain.model;

/**
 * Forma de pagamento utilizada em uma venda.
 */
public enum PaymentMethod {

  /**
   * Pagamento em dinheiro.
   *
   * <p>Requer o valor pago ({@code cashPaidAmount}).</p>
   */
  CASH,

  /**
   * Pagamento com cartão de crédito.
   *
   * <p>Requer o número do cartão ({@code cardNumber}).</p>
   */
  CREDIT_CARD
}

