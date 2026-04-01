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
class SaleQueryResourceIT {

  private static String generateCpf() {
    final long n = Math.floorMod(System.nanoTime(), 1_000_000_00000L);
    return String.format("%011d", n);
  }

  /**
   * Cenário: buscar uma venda por código após criação via API.
   * Expectativa: retornar 200 e corpo com totais e itens.
   */
  @Test
  void findByCode_whenCreatedViaApi_shouldReturn200() {
    final var cpf = generateCpf();

    final var customerRequest = Map.of(
        "fullName", "John Doe",
        "motherName", "Jane Doe",
        "fullAddress", "Street 1",
        "zipCode", "12345-678",
        "cpf", cpf,
        "rg", "MG-123",
        "birthDate", LocalDate.parse("1990-01-01").toString(),
        "cellPhone", "31999999999",
        "email", "john.doe@acme.com"
    );

    final Response customerResponse = given()
        .contentType(ContentType.JSON)
        .body(customerRequest)
        .when()
        .post("/api/customers");

    final var customerCode = customerResponse.then()
        .statusCode(201)
        .extract()
        .jsonPath()
        .getString("code");

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

    final var productCode = productResponse.then()
        .statusCode(201)
        .extract()
        .jsonPath()
        .getString("code");

    final var saleRequest = Map.of(
        "customerCode", customerCode,
        "paymentMethod", "CASH",
        "cashPaidAmount", 1000.00,
        "items", List.of(
            Map.of("productCode", productCode, "quantity", 2)
        )
    );

    final Response saleResponse = given()
        .contentType(ContentType.JSON)
        .body(saleRequest)
        .when()
        .post("/api/sales");

    final var saleCode = saleResponse.then()
        .statusCode(201)
        .body("code", notNullValue())
        .extract()
        .jsonPath()
        .getString("code");

    final var getResponse = given()
        .contentType(ContentType.JSON)
        .when()
        .get("/api/sales/" + saleCode);

    getResponse.then()
        .statusCode(200)
        .body("code", equalTo(saleCode))
        .body("productsTotal", equalTo(200.00F))
        .body("saleTotal", equalTo(218.00F));
  }
}

