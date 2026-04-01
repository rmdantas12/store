package com.perinity.store.infrastructure.web.customer;

import com.perinity.store.infrastructure.persistence.customer.CustomerEntity;
import com.perinity.store.infrastructure.persistence.customer.CustomerRepositoryJpa;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
class CustomerDeleteResourceTest {

  @Inject
  CustomerRepositoryJpa customerRepositoryJpa;

  @BeforeEach
  void setUp() {
    QuarkusTransaction.requiringNew().run(() -> {
      customerRepositoryJpa.deleteAll();
      customerRepositoryJpa.getEntityManager().flush();
    });
  }

  /**
   * Cenário: excluir um cliente existente.
   * Expectativa: retornar 204 e, ao consultar depois, retornar 404.
   */
  @Test
  void delete_whenCustomerExists_shouldReturn204AndRemoveCustomer() {
    final var code = seedCustomer(
        "Ana Silva",
        "Maria Silva",
        "Street 2",
        "12345678",
        "98765432100",
        "SP-999",
        LocalDate.parse("1995-05-05"),
        "11999999999",
        "ana.silva@acme.com"
    );

    final var deleteResponse = given()
        .when()
        .delete("/api/customers/{code}", code);

    deleteResponse.then()
        .statusCode(204);

    final var getResponse = given()
        .when()
        .get("/api/customers/{code}", code);

    getResponse.then()
        .statusCode(404)
        .body("message", equalTo("Customer not found"))
        .body("details", hasSize(0))
        .body("timestamp", notNullValue());
  }

  /**
   * Cenário: excluir um cliente inexistente.
   * Expectativa: retornar 404 e o corpo de erro no padrão {@code ErrorResponse}.
   */
  @Test
  void delete_whenCustomerDoesNotExist_shouldReturn404() {
    final var code = UUID.randomUUID();

    final var response = given()
        .when()
        .delete("/api/customers/{code}", code);

    response.then()
        .statusCode(404)
        .body("message", equalTo("Customer not found"))
        .body("details", hasSize(0))
        .body("timestamp", notNullValue());
  }

  private UUID seedCustomer(
      final String fullName,
      final String motherName,
      final String fullAddress,
      final String zipCode,
      final String cpf,
      final String rg,
      final LocalDate birthDate,
      final String cellPhone,
      final String email
  ) {
    return QuarkusTransaction.requiringNew().call(() -> {
      final var entity = CustomerEntity.builder()
          .fullName(fullName)
          .motherName(motherName)
          .fullAddress(fullAddress)
          .zipCode(zipCode)
          .cpf(cpf)
          .rg(rg)
          .birthDate(birthDate)
          .cellPhone(cellPhone)
          .email(email)
          .build();

      customerRepositoryJpa.persist(entity);
      customerRepositoryJpa.getEntityManager().flush();

      return entity.getCode();
    });
  }
}

