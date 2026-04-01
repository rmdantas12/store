package com.perinity.store.infrastructure.web.customer;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusIntegrationTest
class CustomerDeleteResourceIT {

  private static String generateCpf() {
    final long n = Math.floorMod(System.nanoTime(), 1_000_000_00000L);
    return String.format("%011d", n);
  }

  /**
   * Cenário: criar um cliente e excluir pelo código.
   * Expectativa: retornar 204 e depois 404 ao consultar.
   */
  @Test
  void delete_whenCustomerExists_shouldReturn204() {
    final var cpf = generateCpf();
    final var email = "delete+" + cpf + "@acme.com";

    final var createRequest = Map.of(
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

    final var created = given()
        .contentType(ContentType.JSON)
        .body(createRequest)
        .when()
        .post("/api/customers");

    final var code = created.then()
        .statusCode(201)
        .extract()
        .path("code");

    final var deleted = given()
        .when()
        .delete("/api/customers/{code}", code);

    deleted.then()
        .statusCode(204);

    final var getAfterDelete = given()
        .when()
        .get("/api/customers/{code}", code);

    getAfterDelete.then()
        .statusCode(404)
        .body("message", equalTo("Customer not found"))
        .body("details", hasSize(0))
        .body("timestamp", notNullValue());
  }
}

