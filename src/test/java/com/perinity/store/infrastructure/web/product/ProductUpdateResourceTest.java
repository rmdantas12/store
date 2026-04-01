package com.perinity.store.infrastructure.web.product;

import com.perinity.store.infrastructure.persistence.product.ProductEntity;
import com.perinity.store.infrastructure.persistence.product.ProductRepositoryJpa;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
class ProductUpdateResourceTest {

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
   * Cenário: atualizar parcialmente um produto existente.
   * Expectativa: retornar 200 e refletir as alterações no corpo.
   */
  @Test
  void update_whenPartialPayload_shouldReturn200WithUpdatedBody() {
    final var code = seedProduct();

    final var request = Map.of(
        "name", "Novo nome",
        "salePrice", new BigDecimal("199.90")
    );

    final var response = given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .put("/api/products/" + code);

    response.then()
        .statusCode(200)
        .body("code", equalTo(code.toString()))
        .body("name", equalTo("Novo nome"))
        .body("salePrice", equalTo(199.90F));
  }

  /**
   * Cenário: atualizar um produto inexistente.
   * Expectativa: retornar 404 com corpo de erro.
   */
  @Test
  void update_whenProductDoesNotExist_shouldReturn404() {
    final var request = Map.of("name", "Novo nome");

    final var response = given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .put("/api/products/" + UUID.randomUUID());

    response.then()
        .statusCode(404)
        .body("message", equalTo("Product not found"));
  }

  /**
   * Cenário: atualizar um produto com valor inválido.
   * Expectativa: retornar 400 e o corpo do erro deve mencionar o campo inválido.
   */
  @Test
  void update_whenSalePriceIsInvalid_shouldReturn400WithFieldNameInErrorBody() {
    final var code = seedProduct();

    final var request = Map.of("salePrice", new BigDecimal("-1.00"));

    final var response = given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .put("/api/products/" + code);

    response.then()
        .statusCode(400)
        .body("message", equalTo("Validation failed"))
        .body("details[0]", org.hamcrest.Matchers.containsString("salePrice"));
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

