package com.perinity.store.infrastructure.web.product;

import com.perinity.store.infrastructure.persistence.product.ProductRepositoryJpa;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;

@QuarkusTest
class ProductCreateResourceTest {

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
   * Cenário: criar um produto válido.
   * Expectativa: retornar 201, com `Location` preenchido e corpo contendo o `code` do produto criado.
   */
  @Test
  void create_whenValidRequest_shouldReturn201WithLocationAndBody() {
    final var request = Map.of(
        "name", "Amortecedor traseiro",
        "type", "AMORTECEDOR",
        "details", "Carro A",
        "heightCm", new BigDecimal("10.00"),
        "widthCm", new BigDecimal("20.00"),
        "depthCm", new BigDecimal("30.00"),
        "weightKg", new BigDecimal("2.500"),
        "purchasePrice", new BigDecimal("100.00"),
        "salePrice", new BigDecimal("150.00")
    );

    final var response = given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/api/products");

    response.then()
        .statusCode(201)
        .header("Location", notNullValue())
        .body("code", notNullValue())
        .body("name", equalTo("Amortecedor traseiro"))
        .body("type", equalTo("AMORTECEDOR"))
        .body("details", equalTo("Carro A"));
  }

  /**
   * Cenário: tentar criar produto com `name` inválido.
   * Expectativa: retornar 400 e o corpo do erro deve mencionar `name`.
   */
  @ParameterizedTest(name = "[{index}] name inválido")
  @CsvSource("'   ',AMORTECEDOR,Carro A,10.00,20.00,30.00,2.500,100.00,150.00")
  void create_whenNameIsInvalid_shouldReturn400WithFieldNameInErrorBody(
      final String name,
      final String type,
      final String details,
      final BigDecimal heightCm,
      final BigDecimal widthCm,
      final BigDecimal depthCm,
      final BigDecimal weightKg,
      final BigDecimal purchasePrice,
      final BigDecimal salePrice
  ) {
    final var request = Map.of(
        "name", name,
        "type", type,
        "details", details,
        "heightCm", heightCm,
        "widthCm", widthCm,
        "depthCm", depthCm,
        "weightKg", weightKg,
        "purchasePrice", purchasePrice,
        "salePrice", salePrice
    );

    final var response = postCreateProduct(request);

    response.then()
        .statusCode(400)
        .body("message", equalTo("Validation failed"))
        .body("details", hasItem(startsWith("name:")));
  }

  /**
   * Cenário: tentar criar produto com `type` inválido.
   * Expectativa: retornar 400 e o corpo do erro deve mencionar `type`.
   */
  @ParameterizedTest(name = "[{index}] type inválido")
  @CsvSource("Amortecedor,'   ',Carro A,10.00,20.00,30.00,2.500,100.00,150.00")
  void create_whenTypeIsInvalid_shouldReturn400WithFieldNameInErrorBody(
      final String name,
      final String type,
      final String details,
      final BigDecimal heightCm,
      final BigDecimal widthCm,
      final BigDecimal depthCm,
      final BigDecimal weightKg,
      final BigDecimal purchasePrice,
      final BigDecimal salePrice
  ) {
    final var request = Map.of(
        "name", name,
        "type", type,
        "details", details,
        "heightCm", heightCm,
        "widthCm", widthCm,
        "depthCm", depthCm,
        "weightKg", weightKg,
        "purchasePrice", purchasePrice,
        "salePrice", salePrice
    );

    final var response = postCreateProduct(request);

    response.then()
        .statusCode(400)
        .body("message", equalTo("Validation failed"))
        .body("details", hasItem(startsWith("type:")));
  }

  /**
   * Cenário: tentar criar produto com `details` inválido.
   * Expectativa: retornar 400 e o corpo do erro deve mencionar `details`.
   */
  @ParameterizedTest(name = "[{index}] details inválido")
  @CsvSource("Amortecedor,AMORTECEDOR,'   ',10.00,20.00,30.00,2.500,100.00,150.00")
  void create_whenDetailsIsInvalid_shouldReturn400WithFieldNameInErrorBody(
      final String name,
      final String type,
      final String details,
      final BigDecimal heightCm,
      final BigDecimal widthCm,
      final BigDecimal depthCm,
      final BigDecimal weightKg,
      final BigDecimal purchasePrice,
      final BigDecimal salePrice
  ) {
    final var request = Map.of(
        "name", name,
        "type", type,
        "details", details,
        "heightCm", heightCm,
        "widthCm", widthCm,
        "depthCm", depthCm,
        "weightKg", weightKg,
        "purchasePrice", purchasePrice,
        "salePrice", salePrice
    );

    final var response = postCreateProduct(request);

    response.then()
        .statusCode(400)
        .body("message", equalTo("Validation failed"))
        .body("details", hasItem(startsWith("details:")));
  }

  /**
   * Cenário: tentar criar produto com `heightCm` inválido (<= 0).
   * Expectativa: retornar 400 e o corpo do erro deve mencionar `heightCm`.
   */
  @ParameterizedTest(name = "[{index}] heightCm inválido")
  @CsvSource("Amortecedor,AMORTECEDOR,Carro A,-1.00,20.00,30.00,2.500,100.00,150.00")
  void create_whenHeightIsInvalid_shouldReturn400WithFieldNameInErrorBody(
      final String name,
      final String type,
      final String details,
      final BigDecimal heightCm,
      final BigDecimal widthCm,
      final BigDecimal depthCm,
      final BigDecimal weightKg,
      final BigDecimal purchasePrice,
      final BigDecimal salePrice
  ) {
    final var request = Map.of(
        "name", name,
        "type", type,
        "details", details,
        "heightCm", heightCm,
        "widthCm", widthCm,
        "depthCm", depthCm,
        "weightKg", weightKg,
        "purchasePrice", purchasePrice,
        "salePrice", salePrice
    );

    final var response = postCreateProduct(request);

    response.then()
        .statusCode(400)
        .body("message", equalTo("Validation failed"))
        .body("details", hasItem(startsWith("heightCm:")));
  }

  /**
   * Cenário: tentar criar produto com `salePrice` inválido (<= 0).
   * Expectativa: retornar 400 e o corpo do erro deve mencionar `salePrice`.
   */
  @ParameterizedTest(name = "[{index}] salePrice inválido")
  @CsvSource("Amortecedor,AMORTECEDOR,Carro A,10.00,20.00,30.00,2.500,100.00,0.00")
  void create_whenSalePriceIsInvalid_shouldReturn400WithFieldNameInErrorBody(
      final String name,
      final String type,
      final String details,
      final BigDecimal heightCm,
      final BigDecimal widthCm,
      final BigDecimal depthCm,
      final BigDecimal weightKg,
      final BigDecimal purchasePrice,
      final BigDecimal salePrice
  ) {
    final var request = Map.of(
        "name", name,
        "type", type,
        "details", details,
        "heightCm", heightCm,
        "widthCm", widthCm,
        "depthCm", depthCm,
        "weightKg", weightKg,
        "purchasePrice", purchasePrice,
        "salePrice", salePrice
    );

    final var response = postCreateProduct(request);

    response.then()
        .statusCode(400)
        .body("message", equalTo("Validation failed"))
        .body("details", hasItem(startsWith("salePrice:")));
  }

  private static Response postCreateProduct(final Map<String, ?> request) {
    return given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/api/products");
  }
}

