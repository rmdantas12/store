package com.perinity.store.infrastructure.web.customer;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@QuarkusIntegrationTest
class CustomerQueryResourceIT {

  private static String generateCpf() {
    final long n = Math.floorMod(System.nanoTime(), 1_000_000_00000L);
    return String.format("%011d", n);
  }

  /**
   * Cenário: criar um cliente e depois consultar pelo código.
   * Expectativa: retornar 200 e os dados do cliente criado.
   */
  @Test
  void findByCode_whenCustomerExists_shouldReturn200() {
    final var cpf = generateCpf();
    final var email = "ana.silva+" + cpf + "@acme.com";
    final var request = Map.of(
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
        .body(request)
        .when()
        .post("/api/customers");

    final var code = created.then()
        .statusCode(201)
        .extract()
        .path("code");

    final var response = given()
        .when()
        .get("/api/customers/{code}", code);

    response.then()
        .statusCode(200)
        .body("code", equalTo(code))
        .body("cpf", equalTo(cpf))
        .body("email", equalTo(email));
  }
}

