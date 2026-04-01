package com.perinity.store.infrastructure.web.product;

import com.perinity.store.infrastructure.persistence.product.ProductEntity;
import com.perinity.store.infrastructure.persistence.product.ProductRepositoryJpa;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
@TestSecurity(user = "seller", roles = {"seller"})
class ProductQueryResourceTest {

  @Inject
  ProductRepositoryJpa productRepositoryJpa;

  @BeforeEach
  void setUp() {
    QuarkusTransaction.requiringNew().run(() -> {
      productRepositoryJpa.deleteAll();
      productRepositoryJpa.getEntityManager().flush();
    });
  }

  /**
   * Cenário: buscar um produto existente por código.
   * Expectativa: retornar 200 com os dados do produto.
   */
  @Test
  void findByCode_whenProductExists_shouldReturn200WithBody() {
    final var code = seedProduct("Produto A", "ELETRICO");

    final var response = given()
        .contentType(ContentType.JSON)
        .when()
        .get("/api/products/" + code);

    response.then()
        .statusCode(200)
        .body("code", equalTo(code.toString()))
        .body("name", equalTo("Produto A"))
        .body("type", equalTo("ELETRICO"))
        .body("createdAt", notNullValue());
  }

  /**
   * Cenário: buscar um produto inexistente por código.
   * Expectativa: retornar 404 com corpo de erro.
   */
  @Test
  void findByCode_whenProductDoesNotExist_shouldReturn404() {
    final var response = given()
        .contentType(ContentType.JSON)
        .when()
        .get("/api/products/" + UUID.randomUUID());

    response.then()
        .statusCode(404)
        .body("message", equalTo("Product not found"));
  }

  /**
   * Cenário: listar produtos quando não há registros.
   * Expectativa: retornar 200 com lista vazia.
   */
  @Test
  void findAll_whenNoProducts_shouldReturn200WithEmptyList() {
    final var response = given()
        .contentType(ContentType.JSON)
        .when()
        .get("/api/products");

    response.then()
        .statusCode(200)
        .body("", hasSize(0));
  }

  /**
   * Cenário: listar produtos quando existem registros.
   * Expectativa: retornar 200 com lista contendo os itens cadastrados.
   */
  @Test
  void findAll_whenProductsExist_shouldReturn200WithList() {
    seedProduct("Produto A", "ELETRICO");
    seedProduct("Produto B", "BANCO");

    final var response = given()
        .contentType(ContentType.JSON)
        .when()
        .get("/api/products");

    response.then()
        .statusCode(200)
        .body("", hasSize(2));
  }

  private UUID seedProduct(final String name, final String type) {
    final var code = UUID.randomUUID();

    QuarkusTransaction.requiringNew().run(() -> {
      final var entity = ProductEntity.builder()
          .code(code)
          .name(name)
          .type(type)
          .details("Carro A")
          .heightCm(new BigDecimal("10.00"))
          .widthCm(new BigDecimal("20.00"))
          .depthCm(new BigDecimal("30.00"))
          .weightKg(new BigDecimal("2.500"))
          .purchasePrice(new BigDecimal("100.00"))
          .salePrice(new BigDecimal("150.00"))
          .build();
      productRepositoryJpa.persist(entity);
      productRepositoryJpa.getEntityManager().flush();
    });

    return code;
  }
}

