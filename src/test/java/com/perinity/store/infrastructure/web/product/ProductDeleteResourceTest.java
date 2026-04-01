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

@QuarkusTest
@TestSecurity(user = "admin-user", roles = {"admin"})
class ProductDeleteResourceTest {

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
   * Cenário: excluir um produto existente.
   * Expectativa: retornar 204 e, ao buscar novamente, retornar 404.
   */
  @Test
  void delete_whenProductExists_shouldReturn204AndSubsequentGetShouldReturn404() {
    final var code = seedProduct();

    final var deleteResponse = given()
        .contentType(ContentType.JSON)
        .when()
        .delete("/api/products/" + code);

    deleteResponse.then()
        .statusCode(204);

    final var getResponse = given()
        .contentType(ContentType.JSON)
        .when()
        .get("/api/products/" + code);

    getResponse.then()
        .statusCode(404)
        .body("message", equalTo("Product not found"));
  }

  /**
   * Cenário: excluir um produto inexistente.
   * Expectativa: retornar 404 com corpo de erro.
   */
  @Test
  void delete_whenProductDoesNotExist_shouldReturn404() {
    final var response = given()
        .contentType(ContentType.JSON)
        .when()
        .delete("/api/products/" + UUID.randomUUID());

    response.then()
        .statusCode(404)
        .body("message", equalTo("Product not found"));
  }

  private UUID seedProduct() {
    final var code = UUID.randomUUID();
    QuarkusTransaction.requiringNew().run(() -> {
      final var entity = ProductEntity.builder()
          .code(code)
          .name("Produto A")
          .type("ELETRICO")
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

