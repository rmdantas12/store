package com.perinity.store.application;

import com.perinity.store.domain.exception.CustomerNotFoundException;
import com.perinity.store.domain.exception.InvalidSalePaymentException;
import com.perinity.store.domain.exception.ProductNotFoundException;
import com.perinity.store.domain.exception.SaleNotFoundException;
import com.perinity.store.domain.model.PaymentMethod;
import com.perinity.store.domain.model.Sale;
import com.perinity.store.domain.model.SaleItem;
import com.perinity.store.domain.ports.incoming.SaleUseCase;
import com.perinity.store.domain.ports.outgoing.CustomerRepositoryPort;
import com.perinity.store.domain.ports.outgoing.ProductRepositoryPort;
import com.perinity.store.domain.ports.outgoing.SaleRepositoryPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Serviço de aplicação responsável pelo ciclo de vida de {@link Sale}.
 *
 * <p>Implementa o caso de uso {@link SaleUseCase} para criação, atualização, exclusão e consulta de vendas.</p>
 *
 * <p>Regras aplicadas:</p>
 * <ul>
 *   <li>Valida existência do cliente informado.</li>
 *   <li>Valida existência dos produtos informados nos itens.</li>
 *   <li>Enriquece itens com nome do produto e "congela" o valor unitário com base no preço de venda do produto.</li>
 *   <li>Calcula total dos produtos, imposto fixo de 9% e total final da venda.</li>
 *   <li>Valida forma de pagamento e seus dados pertinentes (dinheiro ou cartão de crédito).</li>
 * </ul>
 */
@RequiredArgsConstructor
@ApplicationScoped
public class SaleService implements SaleUseCase {

  private static final BigDecimal TAX_RATE = new BigDecimal("0.09");

  private final SaleRepositoryPort saleRepository;

  private final CustomerRepositoryPort customerRepository;

  private final ProductRepositoryPort productRepository;

  /**
   * Cria uma nova venda.
   *
   * <p>Valida o cliente e os produtos, enriquece os itens com dados do produto (nome/preço de venda),
   * calcula totais (produtos + imposto) e valida os dados da forma de pagamento.</p>
   *
   * @param sale venda a ser criada
   * @return venda persistida com totais calculados
   * @throws CustomerNotFoundException se o cliente informado não existir
   * @throws ProductNotFoundException se algum produto informado nos itens não existir
   * @throws InvalidSalePaymentException se os dados da forma de pagamento forem inválidos
   */
  @Override
  @Transactional
  public Sale create(final Sale sale) {
    final var customer = customerRepository.findByCode(sale.getCustomerCode())
        .orElseThrow(CustomerNotFoundException::new);

    final var items = sale.getItems().stream()
        .map(this::enrichItemWithProduct)
        .toList();

    final var productsTotal = calculateProductsTotal(items);
    final var tax = calculateTax(productsTotal);
    final var saleTotal = productsTotal.add(tax);

    validatePayment(sale.getPaymentMethod(), sale.getCashPaidAmount(), sale.getCardNumber(), saleTotal);

    sale.setCustomerName(customer.getFullName());
    sale.setCreatedAt(LocalDateTime.now());
    sale.setItems(items);
    sale.setProductsTotal(productsTotal);
    sale.setTaxAmount(tax);
    sale.setSaleTotal(saleTotal);

    return saleRepository.save(sale);
  }

  /**
   * Atualiza uma venda existente (atualização parcial).
   *
   * <p>Quando itens são informados, eles são novamente enriquecidos e os totais são recalculados.
   * Quando a forma de pagamento é informada/alterada, os dados pertinentes são revalidados.</p>
   *
   * @param code identificador da venda
   * @param updatedSale dados para atualização (parcial)
   * @return venda atualizada
   * @throws SaleNotFoundException se a venda não existir
   * @throws CustomerNotFoundException se o cliente informado não existir (quando alterado)
   * @throws ProductNotFoundException se algum produto informado nos itens não existir
   * @throws InvalidSalePaymentException se os dados da forma de pagamento forem inválidos
   */
  @Override
  @Transactional
  public Sale update(final UUID code, final Sale updatedSale) {
    final var existing = saleRepository.findByCode(code)
        .orElseThrow(SaleNotFoundException::new);

    final var customerCode = updatedSale.getCustomerCode();

    if (Objects.nonNull(customerCode) && !Objects.equals(customerCode, existing.getCustomerCode())) {
      final var customer = customerRepository.findByCode(updatedSale.getCustomerCode())
          .orElseThrow(CustomerNotFoundException::new);

      existing.setCustomerCode(updatedSale.getCustomerCode());
      existing.setCustomerName(customer.getFullName());
    }

    Optional.ofNullable(updatedSale.getSellerCode())
        .ifPresent(existing::setSellerCode);

    if (updatedSale.getItems() != null) {
      final var items = updatedSale.getItems().stream()
          .map(this::enrichItemWithProduct)
          .toList();

      existing.setItems(items);
    }

    Optional.ofNullable(updatedSale.getPaymentMethod())
        .ifPresent(existing::setPaymentMethod);

    Optional.ofNullable(updatedSale.getCashPaidAmount())
        .ifPresent(existing::setCashPaidAmount);

    Optional.ofNullable(updatedSale.getCardNumber())
        .ifPresent(existing::setCardNumber);

    final var productsTotal = calculateProductsTotal(existing.getItems());
    final var tax = calculateTax(productsTotal);
    final var saleTotal = productsTotal.add(tax);

    validatePayment(existing.getPaymentMethod(), existing.getCashPaidAmount(), existing.getCardNumber(), saleTotal);

    existing.setProductsTotal(productsTotal);
    existing.setTaxAmount(tax);
    existing.setSaleTotal(saleTotal);

    return saleRepository.update(existing);
  }

  /**
   * Exclui uma venda pelo código.
   *
   * <p>A exclusão da venda não afeta os cadastros de produtos e clientes.</p>
   *
   * @param code identificador da venda
   * @throws SaleNotFoundException se a venda não existir
   */
  @Override
  @Transactional
  public void delete(final UUID code) {
    if (!saleRepository.existsByCode(code)) {
      throw new SaleNotFoundException();
    }
    saleRepository.deleteByCode(code);
  }

  /**
   * Busca uma venda pelo código.
   *
   * @param code identificador da venda
   * @return venda encontrada
   * @throws SaleNotFoundException se a venda não existir
   */
  @Override
  public Sale findByCode(final UUID code) {
    return saleRepository.findByCode(code)
        .orElseThrow(SaleNotFoundException::new);
  }

  /**
   * Lista todas as vendas cadastradas.
   *
   * @return lista de vendas
   */
  @Override
  public List<Sale> findAll() {
    return saleRepository.findAll();
  }

  private SaleItem enrichItemWithProduct(final SaleItem item) {
    final var product = productRepository.findByCode(item.getProductCode())
        .orElseThrow(ProductNotFoundException::new);

    return SaleItem.builder()
        .productCode(product.getCode())
        .productName(product.getName())
        .quantity(item.getQuantity())
        .unitPrice(product.getSalePrice())
        .build();
  }

  private BigDecimal calculateProductsTotal(final List<SaleItem> items) {
    return items.stream()
        .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
        .reduce(BigDecimal.ZERO, BigDecimal::add)
        .setScale(2, RoundingMode.HALF_UP);
  }

  private BigDecimal calculateTax(final BigDecimal productsTotal) {
    return productsTotal.multiply(TAX_RATE)
        .setScale(2, RoundingMode.HALF_UP);
  }

  private void validatePayment(
      final PaymentMethod method,
      final BigDecimal cashPaidAmount,
      final String cardNumber,
      final BigDecimal saleTotal
  ) {
    if (method == PaymentMethod.CASH) {
      if (cashPaidAmount == null) {
        throw new InvalidSalePaymentException("cashPaidAmount is required for CASH payments");
      }
      if (cashPaidAmount.compareTo(saleTotal) < 0) {
        throw new InvalidSalePaymentException("cashPaidAmount must be >= saleTotal");
      }
    }

    if (method == PaymentMethod.CREDIT_CARD) {
      if (cardNumber == null || cardNumber.isBlank()) {
        throw new InvalidSalePaymentException("cardNumber is required for CREDIT_CARD payments");
      }
    }
  }
}

