package com.perinity.store.infrastructure.web.product;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;

@QuarkusIntegrationTest
class ProductCreateResourceIT {

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
        "heightCm", 10.00,
        "widthCm", 20.00,
        "depthCm", 30.00,
        "weightKg", 2.500,
        "purchasePrice", 100.00,
        "salePrice", 150.00
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
        .body("name", equalTo("Amortecedor traseiro"));
  }

  /**
   * Cenário: tentar criar produto com `name` inválido.
   * Expectativa: retornar 400 e o corpo do erro deve mencionar `name`.
   */
  @Test
  void create_whenNameIsInvalid_shouldReturn400WithFieldNameInErrorBody() {
    final var request = Map.of(
        "name", "   ",
        "type", "AMORTECEDOR",
        "details", "Carro A",
        "heightCm", 10.00,
        "widthCm", 20.00,
        "depthCm", 30.00,
        "weightKg", 2.500,
        "purchasePrice", 100.00,
        "salePrice", 150.00
    );

    final var response = given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/api/products");

    response.then()
        .statusCode(400)
        .body("message", equalTo("Validation failed"))
        .body("details", hasItem(startsWith("name:")));
  }
}

