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
 * Caso de uso de vendas ({@link SaleUseCase}).
 *
 * <p>Calcula totais (imposto fixo 9%) e valida a forma de pagamento.</p>
 */
@RequiredArgsConstructor
@ApplicationScoped
public class SaleService implements SaleUseCase {

  private static final BigDecimal TAX_RATE = new BigDecimal("0.09");

  private final SaleRepositoryPort saleRepository;

  private final CustomerRepositoryPort customerRepository;

  private final ProductRepositoryPort productRepository;

  /**
   * Cria uma venda: valida cliente/produtos, congela preço unitário, calcula totais e valida pagamento.
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
   * Atualiza uma venda (parcial): recalcula totais quando itens/pagamento mudam.
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
   * Exclui uma venda sem afetar cliente/produto.
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
   * Busca uma venda por código.
   */
  @Override
  public Sale findByCode(final UUID code) {
    return saleRepository.findByCode(code)
        .orElseThrow(SaleNotFoundException::new);
  }

  /**
   * Lista as vendas.
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

