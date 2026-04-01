package com.perinity.store.application;

import com.perinity.store.domain.exception.CustomerNotFoundException;
import com.perinity.store.domain.exception.InvalidSalePaymentException;
import com.perinity.store.domain.exception.ProductNotFoundException;
import com.perinity.store.domain.exception.SaleNotFoundException;
import com.perinity.store.domain.exception.SaleOperationNotAllowedException;
import com.perinity.store.domain.model.Customer;
import com.perinity.store.domain.model.PaymentMethod;
import com.perinity.store.domain.model.Product;
import com.perinity.store.domain.model.Sale;
import com.perinity.store.domain.model.SaleItem;
import com.perinity.store.domain.model.Seller;
import com.perinity.store.domain.ports.outgoing.CustomerRepositoryPort;
import com.perinity.store.domain.ports.outgoing.ProductRepositoryPort;
import com.perinity.store.domain.ports.outgoing.SaleRepositoryPort;
import com.perinity.store.domain.ports.outgoing.SellerRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SaleServiceTest {

  @Mock
  SaleRepositoryPort saleRepository;

  @Mock
  CustomerRepositoryPort customerRepository;

  @Mock
  ProductRepositoryPort productRepository;

  @Mock
  SellerRepositoryPort sellerRepository;

  @InjectMocks
  SaleService service;

  /**
   * Cenário: criar uma venda para um cliente inexistente.
   * Expectativa: falhar com {@link CustomerNotFoundException}.
   */
  @Test
  void create_whenCustomerDoesNotExist_shouldThrowCustomerNotFoundException() {
    final var customerCode = UUID.randomUUID();
    when(customerRepository.findByCode(customerCode)).thenReturn(Optional.empty());

    final var item = SaleItem.builder()
        .productCode(UUID.randomUUID())
        .quantity(1)
        .build();

    final var sale = Sale.builder()
        .customerCode(customerCode)
        .sellerCode("S-001")
        .paymentMethod(PaymentMethod.CASH)
        .cashPaidAmount(new BigDecimal("1000.00"))
        .items(List.of(item))
        .build();

    assertThrows(CustomerNotFoundException.class, () -> service.create(sale));
    verify(saleRepository, never()).save(any());
  }

  /**
   * Cenário: criar uma venda com produto inexistente.
   * Expectativa: falhar com {@link ProductNotFoundException}.
   */
  @Test
  void create_whenProductDoesNotExist_shouldThrowProductNotFoundException() {
    final var customerCode = UUID.randomUUID();

    final var customer = Customer.builder()
        .code(customerCode)
        .fullName("John")
        .build();

    when(customerRepository.findByCode(customerCode)).thenReturn(Optional.of(customer));

    final var productCode = UUID.randomUUID();

    when(productRepository.findByCode(productCode)).thenReturn(Optional.empty());

    final var item = SaleItem.builder()
        .productCode(productCode)
        .quantity(1)
        .build();

    final var sale = Sale.builder()
        .customerCode(customerCode)
        .paymentMethod(PaymentMethod.CASH)
        .cashPaidAmount(new BigDecimal("1000.00"))
        .items(List.of(item))
        .build();

    assertThrows(ProductNotFoundException.class, () -> service.create(sale));
  }

  /**
   * Cenário: criar venda com pagamento em dinheiro sem valor pago.
   * Expectativa: falhar com {@link InvalidSalePaymentException}.
   */
  @Test
  void create_whenCashPaymentWithoutPaidAmount_shouldThrowInvalidSalePaymentException() {
    final var customerCode = UUID.randomUUID();

    final var customer = Customer.builder()
        .code(customerCode)
        .fullName("John")
        .build();

    when(customerRepository.findByCode(customerCode)).thenReturn(Optional.of(customer));

    final var productCode = UUID.randomUUID();

    final var product = Product.builder()
        .code(productCode)
        .name("Produto")
        .salePrice(new BigDecimal("100.00"))
        .build();
    when(productRepository.findByCode(productCode)).thenReturn(Optional.of(product));

    final var item = SaleItem.builder()
        .productCode(productCode)
        .quantity(1)
        .build();

    final var sale = Sale.builder()
        .customerCode(customerCode)
        .paymentMethod(PaymentMethod.CASH)
        .cashPaidAmount(null)
        .items(List.of(item))
        .build();

    assertThrows(InvalidSalePaymentException.class, () -> service.create(sale));
  }

  /**
   * Cenário: criar venda com cartão sem número do cartão.
   * Expectativa: falhar com {@link InvalidSalePaymentException}.
   */
  @Test
  void create_whenCreditCardWithoutCardNumber_shouldThrowInvalidSalePaymentException() {
    final var customerCode = UUID.randomUUID();

    final var customer = Customer.builder()
        .code(customerCode)
        .fullName("John")
        .build();

    when(customerRepository.findByCode(customerCode)).thenReturn(Optional.of(customer));

    final var productCode = UUID.randomUUID();

    final var product = Product.builder()
        .code(productCode)
        .name("Produto")
        .salePrice(new BigDecimal("100.00"))
        .build();

    when(productRepository.findByCode(productCode)).thenReturn(Optional.of(product));

    final var item = SaleItem.builder()
        .productCode(productCode)
        .quantity(1)
        .build();

    final var sale = Sale.builder()
        .customerCode(customerCode)
        .paymentMethod(PaymentMethod.CREDIT_CARD)
        .cardNumber("   ")
        .items(List.of(item))
        .build();

    assertThrows(InvalidSalePaymentException.class, () -> service.create(sale));
  }

  /**
   * Cenário: criar venda com cartão com `cardNumber` nulo.
   * Expectativa: falhar com {@link InvalidSalePaymentException}.
   */
  @Test
  void create_whenCreditCardWithNullCardNumber_shouldThrowInvalidSalePaymentException() {
    final var customerCode = UUID.randomUUID();

    final var customer = Customer.builder()
        .code(customerCode)
        .fullName("John")
        .build();

    when(customerRepository.findByCode(customerCode)).thenReturn(Optional.of(customer));

    final var productCode = UUID.randomUUID();

    final var product = Product.builder()
        .code(productCode)
        .name("Produto")
        .salePrice(new BigDecimal("100.00"))
        .build();

    when(productRepository.findByCode(productCode)).thenReturn(Optional.of(product));

    final var item = SaleItem.builder()
        .productCode(productCode)
        .quantity(1)
        .build();

    final var sale = Sale.builder()
        .customerCode(customerCode)
        .paymentMethod(PaymentMethod.CREDIT_CARD)
        .cardNumber(null)
        .items(List.of(item))
        .build();

    assertThrows(InvalidSalePaymentException.class, () -> service.create(sale));
  }

  /**
   * Cenário: criar venda válida (dinheiro).
   * Expectativa: persistir e retornar venda com totais calculados e valor unitário congelado.
   */
  @Test
  void create_whenValidCashSale_shouldSaveAndReturnSaleWithTotals() {
    final var customerCode = UUID.randomUUID();

    final var customer = Customer.builder()
        .code(customerCode)
        .fullName("John")
        .build();

    when(customerRepository.findByCode(customerCode)).thenReturn(Optional.of(customer));

    final var productCode = UUID.randomUUID();

    final var product = Product.builder()
        .code(productCode)
        .name("Produto")
        .salePrice(new BigDecimal("100.00"))
        .build();

    when(productRepository.findByCode(productCode)).thenReturn(Optional.of(product));

    final var item = SaleItem.builder()
        .productCode(productCode)
        .quantity(2)
        .build();

    final var sale = Sale.builder()
        .customerCode(customerCode)
        .paymentMethod(PaymentMethod.CASH)
        .cashPaidAmount(new BigDecimal("1000.00"))
        .items(List.of(item))
        .build();

    when(saleRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    final var result = service.create(sale);

    assertEquals(customerCode, result.getCustomerCode());
    assertEquals("John", result.getCustomerName());
    assertEquals(new BigDecimal("200.00"), result.getProductsTotal());
    assertEquals(new BigDecimal("18.00"), result.getTaxAmount());
    assertEquals(new BigDecimal("218.00"), result.getSaleTotal());
    assertEquals(1, result.getItems().size());
    assertEquals(new BigDecimal("100.00"), result.getItems().getFirst().getUnitPrice());
    verify(saleRepository).save(any());
  }

  /**
   * Cenário: criar venda informando explicitamente `createdAt`.
   * Expectativa: o serviço deve ignorar o valor do input e usar a hora do sistema.
   */
  @Test
  void create_whenCreatedAtIsProvided_shouldIgnoreInputAndUseSystemTime() {
    final var customerCode = UUID.randomUUID();

    final var customer = Customer.builder()
        .code(customerCode)
        .fullName("John")
        .build();

    when(customerRepository.findByCode(customerCode)).thenReturn(Optional.of(customer));

    final var productCode = UUID.randomUUID();

    final var product = Product.builder()
        .code(productCode)
        .name("Produto")
        .salePrice(new BigDecimal("100.00"))
        .build();

    when(productRepository.findByCode(productCode)).thenReturn(Optional.of(product));

    final var createdAt = LocalDateTime.parse("2026-01-01T10:00:00");

    final var item = SaleItem.builder()
        .productCode(productCode)
        .quantity(1)
        .build();

    final var sale = Sale.builder()
        .customerCode(customerCode)
        .sellerCode("S-001")
        .createdAt(createdAt)
        .paymentMethod(PaymentMethod.CASH)
        .cashPaidAmount(new BigDecimal("1000.00"))
        .items(List.of(item))
        .build();

    when(saleRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    final var result = service.create(sale);

    assertNotEquals(createdAt, result.getCreatedAt());
  }

  /**
   * Cenário: criar venda válida (cartão de crédito).
   * Expectativa: persistir e retornar com totais calculados.
   */
  @Test
  void create_whenValidCreditCardSale_shouldSaveAndReturnSaleWithTotals() {
    final var customerCode = UUID.randomUUID();

    final var customer = Customer.builder()
        .code(customerCode)
        .fullName("John")
        .build();

    when(customerRepository.findByCode(customerCode)).thenReturn(Optional.of(customer));

    final var productCode = UUID.randomUUID();

    final var product = Product.builder()
        .code(productCode)
        .name("Produto")
        .salePrice(new BigDecimal("100.00"))
        .build();

    when(productRepository.findByCode(productCode)).thenReturn(Optional.of(product));

    final var item = SaleItem.builder()
        .productCode(productCode)
        .quantity(1)
        .build();

    final var sale = Sale.builder()
        .customerCode(customerCode)
        .sellerCode("S-001")
        .paymentMethod(PaymentMethod.CREDIT_CARD)
        .cardNumber("4111111111111111")
        .items(List.of(item))
        .build();

    when(saleRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    final var result = service.create(sale);

    assertEquals(new BigDecimal("100.00"), result.getProductsTotal());
    assertEquals(new BigDecimal("9.00"), result.getTaxAmount());
    assertEquals(new BigDecimal("109.00"), result.getSaleTotal());
  }

  /**
   * Cenário: criar venda em dinheiro com valor pago menor que o total.
   * Expectativa: falhar com {@link InvalidSalePaymentException}.
   */
  @Test
  void create_whenCashPaidAmountIsLessThanTotal_shouldThrowInvalidSalePaymentException() {
    final var customerCode = UUID.randomUUID();

    final var customer = Customer.builder()
        .code(customerCode)
        .fullName("John")
        .build();

    when(customerRepository.findByCode(customerCode)).thenReturn(Optional.of(customer));

    final var productCode = UUID.randomUUID();

    final var product = Product.builder()
        .code(productCode)
        .name("Produto")
        .salePrice(new BigDecimal("100.00"))
        .build();

    when(productRepository.findByCode(productCode)).thenReturn(Optional.of(product));

    final var item = SaleItem.builder()
        .productCode(productCode)
        .quantity(1)
        .build();

    final var sale = Sale.builder()
        .customerCode(customerCode)
        .paymentMethod(PaymentMethod.CASH)
        .cashPaidAmount(new BigDecimal("50.00"))
        .items(List.of(item))
        .build();

    assertThrows(InvalidSalePaymentException.class, () -> service.create(sale));
  }

  /**
   * Cenário: atualizar venda inexistente.
   * Expectativa: falhar com {@link SaleNotFoundException}.
   */
  @Test
  void update_whenSaleDoesNotExist_shouldThrowSaleNotFoundException() {
    final var code = UUID.randomUUID();
    when(saleRepository.findByCode(code)).thenReturn(Optional.empty());

    final var payload = Sale.builder()
        .build();

    assertThrows(SaleNotFoundException.class, () -> service.update(code, payload));
  }

  /**
   * Cenário: atualizar venda existente alterando o cliente.
   * Expectativa: atualizar customerCode e customerName.
   */
  @Test
  void update_whenCustomerChanges_shouldUpdateCustomerFields() {
    final var saleCode = UUID.randomUUID();
    final var oldCustomerCode = UUID.randomUUID();
    final var newCustomerCode = UUID.randomUUID();

    final var item = SaleItem.builder()
        .productCode(UUID.randomUUID())
        .quantity(1)
        .unitPrice(new BigDecimal("100.00"))
        .build();

    final var existing = Sale.builder()
        .code(saleCode)
        .customerCode(oldCustomerCode)
        .customerName("Old")
        .paymentMethod(PaymentMethod.CASH)
        .cashPaidAmount(new BigDecimal("1000.00"))
        .items(List.of(item))
        .build();

    when(saleRepository.findByCode(saleCode)).thenReturn(Optional.of(existing));

    final var newCustomer = Customer.builder()
        .code(newCustomerCode)
        .fullName("New")
        .build();

    when(customerRepository.findByCode(newCustomerCode)).thenReturn(Optional.of(newCustomer));
    when(saleRepository.update(existing)).thenAnswer(inv -> inv.getArgument(0));

    final var updatePayload = Sale.builder()
        .customerCode(newCustomerCode)
        .build();

    final var result = service.update(saleCode, updatePayload);

    assertEquals(newCustomerCode, result.getCustomerCode());
    assertEquals("New", result.getCustomerName());
  }

  /**
   * Cenário: atualizar venda existente informando novos itens.
   * Expectativa: enriquecer itens com produto (nome/preço) e recalcular totais.
   */
  @Test
  void update_whenItemsAreProvided_shouldEnrichItemsAndRecalculateTotals() {
    final var saleCode = UUID.randomUUID();
    final var customerCode = UUID.randomUUID();
    final var productCode = UUID.randomUUID();

    final var item = SaleItem.builder()
        .productCode(productCode)
        .quantity(1)
        .unitPrice(new BigDecimal("10.00"))
        .build();

    final var existing = Sale.builder()
        .code(saleCode)
        .customerCode(customerCode)
        .customerName("John")
        .paymentMethod(PaymentMethod.CASH)
        .cashPaidAmount(new BigDecimal("1000.00"))
        .items(List.of(item))
        .build();

    when(saleRepository.findByCode(saleCode)).thenReturn(Optional.of(existing));

    final var product = Product.builder()
        .code(productCode)
        .name("Produto")
        .salePrice(new BigDecimal("50.00"))
        .build();

    when(productRepository.findByCode(productCode)).thenReturn(Optional.of(product));
    when(saleRepository.update(existing)).thenAnswer(inv -> inv.getArgument(0));

    final var updatedItem = SaleItem.builder()
        .productCode(productCode)
        .quantity(3)
        .build();

    final var updatePayload = Sale.builder()
        .items(List.of(updatedItem))
        .build();

    final var result = service.update(saleCode, updatePayload);

    assertEquals(1, result.getItems().size());
    assertEquals("Produto", result.getItems().getFirst().getProductName());
    assertEquals(new BigDecimal("50.00"), result.getItems().getFirst().getUnitPrice());
    assertEquals(new BigDecimal("150.00"), result.getProductsTotal());
    assertEquals(new BigDecimal("13.50"), result.getTaxAmount());
    assertEquals(new BigDecimal("163.50"), result.getSaleTotal());
  }

  /**
   * Cenário: atualizar venda informando o mesmo customerCode.
   * Expectativa: não deve revalidar/buscar cliente novamente.
   */
  @Test
  void update_whenCustomerCodeIsSame_shouldNotLookupCustomer() {
    final var saleCode = UUID.randomUUID();
    final var customerCode = UUID.randomUUID();

    final var item = SaleItem.builder()
        .productCode(UUID.randomUUID())
        .quantity(1)
        .unitPrice(new BigDecimal("10.00"))
        .build();

    final var existing = Sale.builder()
        .code(saleCode)
        .customerCode(customerCode)
        .customerName("John")
        .paymentMethod(PaymentMethod.CASH)
        .cashPaidAmount(new BigDecimal("1000.00"))
        .items(List.of(item))
        .build();

    when(saleRepository.findByCode(saleCode)).thenReturn(Optional.of(existing));
    when(saleRepository.update(existing)).thenAnswer(inv -> inv.getArgument(0));

    final var updatePayload = Sale.builder()
        .customerCode(customerCode)
        .build();

    final var result = service.update(saleCode, updatePayload);

    assertSame(existing, result);
    verify(customerRepository, never()).findByCode(any());
  }

  /**
   * Cenário: buscar venda por código existente.
   * Expectativa: retornar venda do repositório.
   */
  @Test
  void findByCode_whenSaleExists_shouldReturnSale() {
    final var code = UUID.randomUUID();

    final var sale = Sale.builder()
        .code(code)
        .sellerCode("S-001")
        .build();

    when(saleRepository.findByCode(code)).thenReturn(Optional.of(sale));
    when(sellerRepository.findByCode("S-001")).thenReturn(Optional.of(Seller.builder().code("S-001").name("Seller Name").build()));

    final var result = service.findByCode(code);

    assertSame(sale, result);
    assertEquals("Seller Name", result.getSellerName());
  }

  @Test
  void update_whenSellerCodeIsDifferent_shouldThrowSaleOperationNotAllowedException() {
    final var code = UUID.randomUUID();

    final var existing = Sale.builder()
        .code(code)
        .sellerCode("S-001")
        .items(List.of())
        .paymentMethod(PaymentMethod.CASH)
        .cashPaidAmount(new BigDecimal("10.00"))
        .build();

    when(saleRepository.findByCode(code)).thenReturn(Optional.of(existing));

    final var updated = Sale.builder()
        .sellerCode("S-002")
        .build();

    assertThrows(SaleOperationNotAllowedException.class, () -> service.update(code, updated));
  }

  @Test
  void update_whenSellerCodeIsSame_shouldUpdateNormally() {
    final var code = UUID.randomUUID();

    final var item = SaleItem.builder()
        .productCode(UUID.randomUUID())
        .productName("Produto")
        .quantity(1)
        .unitPrice(new BigDecimal("10.00"))
        .build();

    final var existing = Sale.builder()
        .code(code)
        .sellerCode("S-001")
        .items(List.of(item))
        .paymentMethod(PaymentMethod.CASH)
        .cashPaidAmount(new BigDecimal("100.00"))
        .build();

    when(saleRepository.findByCode(code)).thenReturn(Optional.of(existing));
    when(saleRepository.update(any())).thenAnswer(inv -> inv.getArgument(0));
    when(sellerRepository.findByCode("S-001")).thenReturn(Optional.empty());

    final var updated = Sale.builder()
        .sellerCode("S-001")
        .build();

    final var result = service.update(code, updated);

    assertEquals(code, result.getCode());
  }

  @Test
  void findAll_whenSellerCodeIsNull_shouldNotTryToEnrichName() {
    final var sale = Sale.builder()
        .code(UUID.randomUUID())
        .sellerCode(null)
        .build();

    when(saleRepository.findAll()).thenReturn(List.of(sale));

    final var result = service.findAll();

    assertEquals(1, result.size());
    assertEquals(sale, result.getFirst());
  }

  @Test
  void findAll_whenRepositoryReturnsNullSale_shouldReturnNullEntry() {
    final var list = new java.util.ArrayList<Sale>();
    list.add(null);
    when(saleRepository.findAll()).thenReturn(list);

    final var result = service.findAll();

    assertEquals(1, result.size());
    assertEquals(null, result.getFirst());
  }

  /**
   * Cenário: listar todas as vendas.
   * Expectativa: delegar ao repositório e retornar a mesma lista.
   */
  @Test
  void findAll_shouldReturnAllSales() {
    final var sale = Sale.builder()
        .code(UUID.randomUUID())
        .build();

    final var sales = List.of(sale);

    when(saleRepository.findAll()).thenReturn(sales);

    final var result = service.findAll();

    assertEquals(sales, result);
  }

  /**
   * Cenário: excluir venda inexistente.
   * Expectativa: falhar com {@link SaleNotFoundException} e não deletar.
   */
  @Test
  void delete_whenSaleDoesNotExist_shouldThrowSaleNotFoundException() {
    final var code = UUID.randomUUID();
    when(saleRepository.existsByCode(code)).thenReturn(false);

    assertThrows(SaleNotFoundException.class, () -> service.delete(code));
    verify(saleRepository, never()).deleteByCode(code);
  }

  /**
   * Cenário: excluir venda existente.
   * Expectativa: deletar por código.
   */
  @Test
  void delete_whenSaleExists_shouldDeleteByCode() {
    final var code = UUID.randomUUID();
    when(saleRepository.existsByCode(code)).thenReturn(true);

    service.delete(code);
    verify(saleRepository).deleteByCode(code);
  }
}

