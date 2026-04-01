package com.perinity.store.infrastructure.web.customer;

import com.perinity.store.infrastructure.persistence.customer.CustomerEntity;
import com.perinity.store.infrastructure.persistence.customer.CustomerRepositoryJpa;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;

@QuarkusTest
class CustomerUpdateResourceTest {

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
   * Cenário: atualizar um cliente existente com payload parcial válido.
   * Expectativa: retornar 200 e refletir a alteração realizada.
   */
  @Test
  void update_whenCustomerExistsAndPayloadIsValid_shouldReturn200() {
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

    final var request = Map.of(
        "fullName", "Ana Silva Updated"
    );

    final var response = putUpdateCustomer(code, request);

    response.then()
        .statusCode(200)
        .body("code", equalTo(code.toString()))
        .body("fullName", equalTo("Ana Silva Updated"))
        .body("cpf", equalTo("98765432100"))
        .body("email", equalTo("ana.silva@acme.com"));
  }

  /**
   * Cenário: atualizar um cliente inexistente.
   * Expectativa: retornar 404 e o corpo de erro no padrão {@code ErrorResponse}.
   */
  @Test
  void update_whenCustomerDoesNotExist_shouldReturn404() {
    final var code = UUID.randomUUID();

    final var request = Map.of(
        "fullName", "Does Not Matter"
    );

    final var response = putUpdateCustomer(code, request);

    response.then()
        .statusCode(404)
        .body("message", equalTo("Customer not found"))
        .body("details", hasSize(0))
        .body("timestamp", notNullValue());
  }

  /**
   * Cenário: atualizar um cliente com e-mail inválido.
   * Expectativa: retornar 400 e listar o erro de validação em {@code details}.
   */
  @Test
  void update_whenEmailIsInvalid_shouldReturn400() {
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

    final var request = Map.of(
        "email", "invalid-email"
    );

    final var response = putUpdateCustomer(code, request);

    response.then()
        .statusCode(400)
        .body("message", equalTo("Validation failed"))
        .body("details", hasItem(startsWith("email:")));
  }

  /**
   * Cenário: atualizar um cliente para um e-mail já existente em outro cliente.
   * Expectativa: retornar 409 com a mensagem "Email already exists".
   */
  @Test
  void update_whenEmailAlreadyExists_shouldReturn409() {
    final var codeA = seedCustomer(
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
    seedCustomer(
        "John Doe",
        "Jane Doe",
        "Street 1",
        "12345-678",
        "12345678901",
        "MG-123",
        LocalDate.parse("1990-01-01"),
        "31999999999",
        "john.doe@acme.com"
    );

    final var request = Map.of(
        "email", "john.doe@acme.com"
    );

    final var response = putUpdateCustomer(codeA, request);

    response.then()
        .statusCode(409)
        .body("message", equalTo("Email already exists"))
        .body("details", hasSize(0))
        .body("timestamp", notNullValue());
  }

  private Response putUpdateCustomer(final UUID code, final Map<String, ?> request) {
    return given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .put("/api/customers/{code}", code);
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

