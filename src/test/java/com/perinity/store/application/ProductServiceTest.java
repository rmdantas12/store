package com.perinity.store.application;

import com.perinity.store.domain.exception.ProductNotFoundException;
import com.perinity.store.domain.model.Product;
import com.perinity.store.domain.ports.outgoing.ProductRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

  @Mock
  ProductRepositoryPort repository;

  @InjectMocks
  ProductService service;

  /**
   * Cenário: criar um produto válido.
   * Expectativa: o serviço deve delegar para {@link ProductRepositoryPort#save(Product)} e retornar o produto criado.
   */
  @Test
  void create_shouldSaveAndReturnProduct() {
    final var product = Product.builder()
        .name("Amortecedor")
        .build();

    final var created = Product.builder()
        .code(UUID.randomUUID())
        .name("Amortecedor")
        .build();

    when(repository.save(product)).thenReturn(created);

    final var result = service.create(product);

    assertSame(created, result);
    verify(repository).save(product);
  }

  /**
   * Cenário: atualizar um produto inexistente.
   * Expectativa: a operação deve falhar com {@link ProductNotFoundException}.
   */
  @Test
  void update_whenProductDoesNotExist_shouldThrowProductNotFoundException() {
    final var code = UUID.randomUUID();
    when(repository.findByCode(code)).thenReturn(Optional.empty());

    assertThrows(ProductNotFoundException.class, () -> service.update(code, Product.builder().build()));
  }

  /**
   * Cenário: atualização parcial de um produto.
   * Expectativa: apenas campos não-nulos presentes no payload devem sobrescrever o produto existente.
   */
  @Test
  void update_whenPartialPayload_shouldUpdateOnlyNonNullFields() {
    final var code = UUID.randomUUID();

    final var existing = Product.builder()
        .code(code)
        .name("Old Name")
        .type("ACABAMENTO_INTERNO")
        .details("Carro A")
        .heightCm(new BigDecimal("10.00"))
        .widthCm(new BigDecimal("20.00"))
        .depthCm(new BigDecimal("30.00"))
        .weightKg(new BigDecimal("2.500"))
        .purchasePrice(new BigDecimal("100.00"))
        .salePrice(new BigDecimal("150.00"))
        .build();

    final var updatePayload = Product.builder()
        .name("New Name")
        .salePrice(new BigDecimal("199.90"))
        .build();

    when(repository.findByCode(code)).thenReturn(Optional.of(existing));
    when(repository.update(existing)).thenReturn(existing);

    final var result = service.update(code, updatePayload);

    assertSame(existing, result);
    assertEquals("New Name", existing.getName());
    assertEquals("ACABAMENTO_INTERNO", existing.getType());
    assertEquals("Carro A", existing.getDetails());
    assertEquals(new BigDecimal("10.00"), existing.getHeightCm());
    assertEquals(new BigDecimal("20.00"), existing.getWidthCm());
    assertEquals(new BigDecimal("30.00"), existing.getDepthCm());
    assertEquals(new BigDecimal("2.500"), existing.getWeightKg());
    assertEquals(new BigDecimal("100.00"), existing.getPurchasePrice());
    assertEquals(new BigDecimal("199.90"), existing.getSalePrice());

    verify(repository).update(existing);
  }

  /**
   * Cenário: excluir um produto inexistente.
   * Expectativa: a operação deve falhar com {@link ProductNotFoundException}
   * e nenhuma exclusão deve ser executada.
   */
  @Test
  void delete_whenProductDoesNotExist_shouldThrowProductNotFoundException() {
    final var code = UUID.randomUUID();
    when(repository.existsByCode(code)).thenReturn(false);

    assertThrows(ProductNotFoundException.class, () -> service.delete(code));

    verify(repository, never()).deleteByCode(code);
  }

  /**
   * Cenário: excluir um produto existente.
   * Expectativa: a operação de exclusão no repositório deve ser executada.
   */
  @Test
  void delete_whenProductExists_shouldDeleteByCode() {
    final var code = UUID.randomUUID();
    when(repository.existsByCode(code)).thenReturn(true);

    service.delete(code);

    verify(repository).deleteByCode(code);
  }

  /**
   * Cenário: buscar um produto por código inexistente.
   * Expectativa: a operação deve falhar com {@link ProductNotFoundException}.
   */
  @Test
  void findByCode_whenProductDoesNotExist_shouldThrowProductNotFoundException() {
    final var code = UUID.randomUUID();
    when(repository.findByCode(code)).thenReturn(Optional.empty());

    assertThrows(ProductNotFoundException.class, () -> service.findByCode(code));
  }

  /**
   * Cenário: buscar um produto por código existente.
   * Expectativa: o produto deve ser retornado.
   */
  @Test
  void findByCode_whenProductExists_shouldReturnProduct() {
    final var code = UUID.randomUUID();
    final var product = Product.builder().code(code).build();
    when(repository.findByCode(code)).thenReturn(Optional.of(product));

    final var result = service.findByCode(code);

    assertSame(product, result);
  }

  /**
   * Cenário: listar todos os produtos.
   * Expectativa: o serviço deve delegar ao repositório e retornar a mesma lista.
   */
  @Test
  void findAll_shouldReturnAllProducts() {
    final var products = List.of(
        Product.builder().code(UUID.randomUUID()).build(),
        Product.builder().code(UUID.randomUUID()).build()
    );
    when(repository.findAll()).thenReturn(products);

    final var result = service.findAll();

    assertSame(products, result);
  }
}

