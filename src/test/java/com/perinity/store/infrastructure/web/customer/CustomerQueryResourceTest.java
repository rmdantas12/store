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
class CustomerQueryResourceTest {

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
   * Cenário: buscar um cliente por código já existente.
   * Expectativa: retornar 200 e os dados do cliente persistido.
   */
  @Test
  void findByCode_whenCustomerExists_shouldReturn200() {
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

    final var response = given()
        .when()
        .get("/api/customers/{code}", code);

    response.then()
        .statusCode(200)
        .body("code", equalTo(code.toString()))
        .body("cpf", equalTo("98765432100"))
        .body("email", equalTo("ana.silva@acme.com"));
  }

  /**
   * Cenário: buscar um cliente por um código inexistente.
   * Expectativa: retornar 404 e o corpo de erro no padrão {@code ErrorResponse}.
   */
  @Test
  void findByCode_whenCustomerDoesNotExist_shouldReturn404() {
    final var code = UUID.randomUUID();

    final var response = given()
        .when()
        .get("/api/customers/{code}", code);

    response.then()
        .statusCode(404)
        .body("message", equalTo("Customer not found"))
        .body("details", hasSize(0))
        .body("timestamp", notNullValue());
  }

  /**
   * Cenário: listar clientes quando não existe nenhum cadastrado.
   * Expectativa: retornar 200 com lista vazia.
   */
  @Test
  void findAll_whenNoCustomers_shouldReturn200WithEmptyList() {
    final var response = given()
        .when()
        .get("/api/customers");

    response.then()
        .statusCode(200)
        .body("", hasSize(0));
  }

  /**
   * Cenário: listar clientes quando existem registros.
   * Expectativa: retornar 200 com lista contendo os clientes.
   */
  @Test
  void findAll_whenCustomersExist_shouldReturn200WithList() {
    seedCustomer(
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

    final var response = given()
        .when()
        .get("/api/customers");

    response.then()
        .statusCode(200)
        .body("", hasSize(2));
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

