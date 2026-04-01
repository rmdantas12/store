package com.perinity.store.infrastructure.web.customer;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.startsWith;

@QuarkusIntegrationTest
class CustomerUpdateResourceIT {

  private static String generateCpf() {
    final long n = Math.floorMod(System.nanoTime(), 1_000_000_00000L);
    return String.format("%011d", n);
  }

  /**
   * Cenário: criar um cliente e atualizar um campo via PUT.
   * Expectativa: retornar 200 e refletir a alteração.
   */
  @Test
  void update_whenCustomerExists_shouldReturn200() {
    final var cpf = generateCpf();
    final var email = "update+" + cpf + "@acme.com";

    final var createRequest = Map.of(
        "fullName", "Ana Silva",
        "motherName", "Maria Silva",
        "fullAddress", "Street 2",
        "zipCode", "12345678",
        "cpf", cpf,
        "rg", "SP-999",
        "birthDate", LocalDate.parse("1995-05-05").toString(),
        "cellPhone", "11999999999",
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

    final var updateRequest = Map.of(
        "fullName", "Ana Silva Updated"
    );

    final var updated = given()
        .contentType(ContentType.JSON)
        .body(updateRequest)
        .when()
        .put("/api/customers/{code}", code);

    updated.then()
        .statusCode(200)
        .body("code", equalTo(code))
        .body("fullName", equalTo("Ana Silva Updated"))
        .body("cpf", equalTo(cpf))
        .body("email", equalTo(email));
  }

  /**
   * Cenário: atualizar um cliente com e-mail inválido.
   * Expectativa: retornar 400 no padrão {@code ErrorResponse}, incluindo detalhe do campo.
   */
  @Test
  void update_whenEmailIsInvalid_shouldReturn400() {
    final var cpf = generateCpf();
    final var email = "update-invalid+" + cpf + "@acme.com";

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

    final var updateRequest = Map.of(
        "email", "invalid-email"
    );

    final var updated = given()
        .contentType(ContentType.JSON)
        .body(updateRequest)
        .when()
        .put("/api/customers/{code}", code);

    updated.then()
        .statusCode(400)
        .body("message", equalTo("Validation failed"))
        .body("details", hasItem(startsWith("email:")));
  }
}

