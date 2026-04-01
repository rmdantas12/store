package com.perinity.store.infrastructure.web.customer;

import com.perinity.store.infrastructure.persistence.customer.CustomerEntity;
import com.perinity.store.infrastructure.persistence.customer.CustomerRepositoryJpa;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;

@QuarkusTest
@TestSecurity(user = "admin-user", roles = {"admin"})
class CustomerCreateResourceTest {

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
   * Cenário: criar um cliente válido.
   * Expectativa: retornar 201, com `Location` preenchido e corpo contendo o `code` do cliente criado.
   */
  @Test
  void create_whenValidRequest_shouldReturn201WithLocationAndBody() {
    final var request = Map.of(
        "fullName", "John Doe",
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
        .statusCode(201)
        .header("Location", notNullValue())
        .body("code", notNullValue())
        .body("cpf", equalTo("12345678901"))
        .body("email", equalTo("john.doe@acme.com"));
  }

  /**
   * Cenário: tentar criar cliente com `fullName` inválido.
   * Expectativa: retornar 400 e o corpo do erro deve mencionar `fullName`.
   */
  @ParameterizedTest(name = "[{index}] fullName inválido")
  @CsvSource("'   ',Jane Doe,Street 1,12345-678,12345678901,MG-123,1990-01-01,31999999999,john.doe@acme.com")
  void create_whenFullNameIsInvalid_shouldReturn400WithFieldNameInErrorBody(
      final String fullName,
      final String motherName,
      final String fullAddress,
      final String zipCode,
      final String cpf,
      final String rg,
      final String birthDate,
      final String cellPhone,
      final String email
  ) {
    final var request = Map.of(
        "fullName", fullName,
        "motherName", motherName,
        "fullAddress", fullAddress,
        "zipCode", zipCode,
        "cpf", cpf,
        "rg", rg,
        "birthDate", birthDate,
        "cellPhone", cellPhone,
        "email", email
    );

    final var response = postCreateCustomer(request);

    response.then()
        .statusCode(400)
        .body("message", equalTo("Validation failed"))
        .body("details", hasItem(startsWith("fullName:")));
  }

  /**
   * Cenário: tentar criar cliente com `fullAddress` inválido.
   * Expectativa: retornar 400 e o corpo do erro deve mencionar `fullAddress`.
   */
  @ParameterizedTest(name = "[{index}] fullAddress inválido")
  @CsvSource("John Doe,Jane Doe,'   ',12345-678,12345678901,MG-123,1990-01-01,31999999999,john.doe@acme.com")
  void create_whenFullAddressIsInvalid_shouldReturn400WithFieldNameInErrorBody(
      final String fullName,
      final String motherName,
      final String fullAddress,
      final String zipCode,
      final String cpf,
      final String rg,
      final String birthDate,
      final String cellPhone,
      final String email
  ) {
    final var request = Map.of(
        "fullName", fullName,
        "motherName", motherName,
        "fullAddress", fullAddress,
        "zipCode", zipCode,
        "cpf", cpf,
        "rg", rg,
        "birthDate", birthDate,
        "cellPhone", cellPhone,
        "email", email
    );

    final var response = postCreateCustomer(request);

    response.then()
        .statusCode(400)
        .body("message", equalTo("Validation failed"))
        .body("details", hasItem(startsWith("fullAddress:")));
  }

  /**
   * Cenário: tentar criar cliente com `cpf` inválido.
   * Expectativa: retornar 400 e o corpo do erro deve mencionar `cpf`.
   */
  @ParameterizedTest(name = "[{index}] cpf inválido")
  @CsvSource(
      textBlock = """
          John Doe,Jane Doe,Street 1,12345-678,'   ',MG-123,1990-01-01,31999999999,john.doe@acme.com
          John Doe,Jane Doe,Street 1,12345-678,123,MG-123,1990-01-01,31999999999,john.doe@acme.com
          """
  )
  void create_whenCpfIsInvalid_shouldReturn400WithFieldNameInErrorBody(
      final String fullName,
      final String motherName,
      final String fullAddress,
      final String zipCode,
      final String cpf,
      final String rg,
      final String birthDate,
      final String cellPhone,
      final String email
  ) {
    final var request = Map.of(
        "fullName", fullName,
        "motherName", motherName,
        "fullAddress", fullAddress,
        "zipCode", zipCode,
        "cpf", cpf,
        "rg", rg,
        "birthDate", birthDate,
        "cellPhone", cellPhone,
        "email", email
    );

    final var response = postCreateCustomer(request);

    response.then()
        .statusCode(400)
        .body("message", equalTo("Validation failed"))
        .body("details", hasItem(startsWith("cpf:")));
  }

  /**
   * Cenário: tentar criar cliente com `zipCode` inválido.
   * Expectativa: retornar 400 e o corpo do erro deve mencionar `zipCode`.
   */
  @ParameterizedTest(name = "[{index}] zipCode inválido")
  @CsvSource("John Doe,Jane Doe,Street 1,123,12345678901,MG-123,1990-01-01,31999999999,john.doe@acme.com")
  void create_whenZipCodeIsInvalid_shouldReturn400WithFieldNameInErrorBody(
      final String fullName,
      final String motherName,
      final String fullAddress,
      final String zipCode,
      final String cpf,
      final String rg,
      final String birthDate,
      final String cellPhone,
      final String email
  ) {
    final var request = Map.of(
        "fullName", fullName,
        "motherName", motherName,
        "fullAddress", fullAddress,
        "zipCode", zipCode,
        "cpf", cpf,
        "rg", rg,
        "birthDate", birthDate,
        "cellPhone", cellPhone,
        "email", email
    );

    final var response = postCreateCustomer(request);

    response.then()
        .statusCode(400)
        .body("message", equalTo("Validation failed"))
        .body("details", hasItem(startsWith("zipCode:")));
  }

  /**
   * Cenário: tentar criar cliente com `cellPhone` inválido.
   * Expectativa: retornar 400 e o corpo do erro deve mencionar `cellPhone`.
   */
  @ParameterizedTest(name = "[{index}] cellPhone inválido")
  @CsvSource("John Doe,Jane Doe,Street 1,12345-678,12345678901,MG-123,1990-01-01,999,john.doe@acme.com")
  void create_whenCellPhoneIsInvalid_shouldReturn400WithFieldNameInErrorBody(
      final String fullName,
      final String motherName,
      final String fullAddress,
      final String zipCode,
      final String cpf,
      final String rg,
      final String birthDate,
      final String cellPhone,
      final String email
  ) {
    final var request = Map.of(
        "fullName", fullName,
        "motherName", motherName,
        "fullAddress", fullAddress,
        "zipCode", zipCode,
        "cpf", cpf,
        "rg", rg,
        "birthDate", birthDate,
        "cellPhone", cellPhone,
        "email", email
    );

    final var response = postCreateCustomer(request);

    response.then()
        .statusCode(400)
        .body("message", equalTo("Validation failed"))
        .body("details", hasItem(startsWith("cellPhone:")));
  }

  /**
   * Cenário: tentar criar cliente com `email` inválido.
   * Expectativa: retornar 400 e o corpo do erro deve mencionar `email`.
   */
  @ParameterizedTest(name = "[{index}] email inválido")
  @CsvSource("John Doe,Jane Doe,Street 1,12345-678,12345678901,MG-123,1990-01-01,31999999999,invalid-email")
  void create_whenEmailIsInvalid_shouldReturn400WithFieldNameInErrorBody(
      final String fullName,
      final String motherName,
      final String fullAddress,
      final String zipCode,
      final String cpf,
      final String rg,
      final String birthDate,
      final String cellPhone,
      final String email
  ) {
    final var request = Map.of(
        "fullName", fullName,
        "motherName", motherName,
        "fullAddress", fullAddress,
        "zipCode", zipCode,
        "cpf", cpf,
        "rg", rg,
        "birthDate", birthDate,
        "cellPhone", cellPhone,
        "email", email
    );

    final var response = postCreateCustomer(request);

    response.then()
        .statusCode(400)
        .body("message", equalTo("Validation failed"))
        .body("details", hasItem(startsWith("email:")));
  }

  /**
   * Cenário: tentar criar cliente com `birthDate` inválido.
   * Expectativa: retornar 400 e o corpo do erro deve mencionar `birthDate`.
   */
  @ParameterizedTest(name = "[{index}] birthDate inválido")
  @CsvSource("John Doe,Jane Doe,Street 1,12345-678,12345678901,MG-123,2099-01-01,31999999999,john.doe@acme.com")
  void create_whenBirthDateIsInvalid_shouldReturn400WithFieldNameInErrorBody(
      final String fullName,
      final String motherName,
      final String fullAddress,
      final String zipCode,
      final String cpf,
      final String rg,
      final String birthDate,
      final String cellPhone,
      final String email
  ) {
    final var request = Map.of(
        "fullName", fullName,
        "motherName", motherName,
        "fullAddress", fullAddress,
        "zipCode", zipCode,
        "cpf", cpf,
        "rg", rg,
        "birthDate", birthDate,
        "cellPhone", cellPhone,
        "email", email
    );

    final var response = postCreateCustomer(request);

    response.then()
        .statusCode(400)
        .body("message", equalTo("Validation failed"))
        .body("details", hasItem(startsWith("birthDate:")));
  }

  /**
   * Cenário: criar um cliente com CPF já existente.
   * Expectativa: retornar 409 e corpo de erro com mensagem "CPF already exists".
   */
  @Test
  void create_whenCpfAlreadyExists_shouldReturn409() {
    QuarkusTransaction.requiringNew().run(() -> {
      final var existing = CustomerEntity.builder()
          .fullName("Existing")
          .motherName("Mother")
          .fullAddress("Address")
          .zipCode("12345678")
          .cpf("12345678901")
          .rg("RG")
          .birthDate(LocalDate.parse("1990-01-01"))
          .cellPhone("11999999999")
          .email("existing@acme.com")
          .build();
      customerRepositoryJpa.persist(existing);
      customerRepositoryJpa.getEntityManager().flush();
    });

    final var request = Map.of(
        "fullName", "John Doe",
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
        .statusCode(409)
        .body("message", equalTo("CPF already exists"));
  }

  /**
   * Cenário: criar um cliente com e-mail já existente.
   * Expectativa: retornar 409 e corpo de erro com mensagem "Email already exists".
   */
  @Test
  void create_whenEmailAlreadyExists_shouldReturn409() {
    QuarkusTransaction.requiringNew().run(() -> {
      final var existing = CustomerEntity.builder()
          .fullName("Existing")
          .motherName("Mother")
          .fullAddress("Address")
          .zipCode("12345678")
          .cpf("12345678901")
          .rg("RG")
          .birthDate(LocalDate.parse("1990-01-01"))
          .cellPhone("11999999999")
          .email("existing@acme.com")
          .build();
      customerRepositoryJpa.persist(existing);
      customerRepositoryJpa.getEntityManager().flush();
    });

    final var request = Map.of(
        "fullName", "John Doe",
        "motherName", "Jane Doe",
        "fullAddress", "Street 1",
        "zipCode", "12345-678",
        "cpf", "98765432100",
        "rg", "MG-123",
        "birthDate", LocalDate.parse("1990-01-01").toString(),
        "cellPhone", "31999999999",
        "email", "existing@acme.com"
    );

    final var response = given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/api/customers");

    response.then()
        .statusCode(409)
        .body("message", equalTo("Email already exists"));
  }

  private static Response postCreateCustomer(final Map<String, ?> request) {
    return given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/api/customers");
  }
}

