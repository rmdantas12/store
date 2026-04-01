package com.perinity.store.infrastructure.web.customer;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;

@QuarkusIntegrationTest
class CustomerCreateResourceIT {

  private static String generateCpf() {
    final long n = Math.floorMod(System.nanoTime(), 1_000_000_00000L);
    return String.format("%011d", n);
  }

  /**
   * Cenário: criar um cliente válido.
   * Expectativa: retornar 201, com `Location` preenchido e corpo contendo o `code` do cliente criado.
   */
  @Test
  void create_whenValidRequest_shouldReturn201WithLocationAndBody() {
    final var cpf = generateCpf();
    final var email = "john.doe+" + cpf + "@acme.com";
    final var request = Map.of(
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

    final var response = given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/api/customers");

    response.then()
        .statusCode(201)
        .header("Location", notNullValue())
        .body("code", notNullValue())
        .body("cpf", equalTo(cpf))
        .body("email", equalTo(email));
  }

  /**
   * Cenário: tentar criar cliente com `fullName` inválido.
   * Expectativa: retornar 400 e o corpo do erro deve mencionar `fullName`.
   */
  @Test
  void create_whenFullNameIsInvalid_shouldReturn400WithFieldNameInErrorBody() {
    final var request = Map.of(
        "fullName", "   ",
        "motherName", "Jane Doe",
        "fullAddress", "Street 1",
        "zipCode", "12345-678",
        "cpf", "12345678901",
        "rg", "MG-123",
        "birthDate", LocalDate.parse("1990-01-01").toString(),
        "cellPhone", "31999999999",
        "email", "john.doe@acme.com"
    );

    final var response = given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/api/customers");

    response.then()
        .statusCode(400)
        .body("message", equalTo("Validation failed"))
        .body("details", hasItem(startsWith("fullName:")));
  }
}

