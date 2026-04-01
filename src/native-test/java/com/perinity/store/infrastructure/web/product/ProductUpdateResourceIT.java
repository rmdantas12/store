package com.perinity.store.infrastructure.web.product;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusIntegrationTest
class ProductUpdateResourceIT {

  /**
   * Cenário: atualizar um produto após criação via API.
   * Expectativa: retornar 200 e refletir alterações no corpo.
   */
  @Test
  void update_whenValidRequest_shouldReturn200() {
    final var createRequest = Map.of(
        "name", "Produto A",
        "type", "ELETRICO",
        "details", "Carro A",
        "heightCm", 10.00,
        "widthCm", 20.00,
        "depthCm", 30.00,
        "weightKg", 2.500,
        "purchasePrice", 100.00,
        "salePrice", 150.00
    );

    final Response createResponse = given()
        .contentType(ContentType.JSON)
        .body(createRequest)
        .when()
        .post("/api/products");

    createResponse.then()
        .statusCode(201)
        .body("code", notNullValue());

    final var code = createResponse.jsonPath().getString("code");

    final var updateRequest = Map.of(
        "name", "Produto A - Atualizado",
        "salePrice", 199.90
    );

    final var updateResponse = given()
        .contentType(ContentType.JSON)
        .body(updateRequest)
        .when()
        .put("/api/products/" + code);

    updateResponse.then()
        .statusCode(200)
        .body("code", equalTo(code))
        .body("name", equalTo("Produto A - Atualizado"));
  }
}

