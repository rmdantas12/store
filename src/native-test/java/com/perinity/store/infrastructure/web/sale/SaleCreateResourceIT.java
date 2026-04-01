package com.perinity.store.infrastructure.web.sale;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusIntegrationTest
class SaleCreateResourceIT {

  private static String generateCpf() {
    final long n = Math.floorMod(System.nanoTime(), 1_000_000_00000L);
    return String.format("%011d", n);
  }

  /**
   * Cenário: criar uma venda válida via API.
   * Expectativa: retornar 201 e totais calculados.
   */
  @Test
  void create_whenValidCashSale_shouldReturn201WithTotals() {
    final var cpf = generateCpf();
    final var email = "john.doe+" + cpf + "@acme.com";

    final var customerRequest = Map.of(
        "fullName", "John Doe",
        "motherName", "Jane Doe",
        "fullAddress", "Street 1",
        "zipCode", "12345-678",
        "cpf", cpf,
        "rg", "MG-123",
        "birthDate", LocalDate.parse("1990-01-01").toString(),
        "cellPhone", "31999999999",
        "email", email
    );

    final Response customerResponse = given()
        .contentType(ContentType.JSON)
        .body(customerRequest)
        .when()
        .post("/api/customers");

    customerResponse.then()
        .statusCode(201)
        .body("code", notNullValue());

    final var customerCode = customerResponse.jsonPath().getString("code");

    final var productRequest = Map.of(
        "name", "Amortecedor traseiro",
        "type", "AMORTECEDOR",
        "details", "Carro A",
        "heightCm", 10.00,
        "widthCm", 20.00,
        "depthCm", 30.00,
        "weightKg", 2.500,
        "purchasePrice", 100.00,
        "salePrice", 100.00
    );

    final Response productResponse = given()
        .contentType(ContentType.JSON)
        .body(productRequest)
        .when()
        .post("/api/products");

    productResponse.then()
        .statusCode(201)
        .body("code", notNullValue());

    final var productCode = productResponse.jsonPath().getString("code");

    final var saleRequest = Map.of(
        "customerCode", customerCode,
        "paymentMethod", "CASH",
        "cashPaidAmount", 1000.00,
        "items", List.of(
            Map.of("productCode", productCode, "quantity", 2)
        )
    );

    final var saleResponse = given()
        .contentType(ContentType.JSON)
        .body(saleRequest)
        .when()
        .post("/api/sales");

    saleResponse.then()
        .statusCode(201)
        .body("code", notNullValue())
        .body("productsTotal", equalTo(200.00F))
        .body("taxAmount", equalTo(18.00F))
        .body("saleTotal", equalTo(218.00F));
  }
}

