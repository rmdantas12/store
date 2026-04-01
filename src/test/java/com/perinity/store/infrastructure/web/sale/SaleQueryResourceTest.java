package com.perinity.store.infrastructure.web.sale;

import com.perinity.store.infrastructure.persistence.customer.CustomerEntity;
import com.perinity.store.infrastructure.persistence.customer.CustomerRepositoryJpa;
import com.perinity.store.infrastructure.persistence.product.ProductEntity;
import com.perinity.store.infrastructure.persistence.product.ProductRepositoryJpa;
import com.perinity.store.infrastructure.persistence.sale.SaleRepositoryJpa;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
class SaleQueryResourceTest {

  @Inject
  SaleRepositoryJpa saleRepositoryJpa;

  @Inject
  CustomerRepositoryJpa customerRepositoryJpa;

  @Inject
  ProductRepositoryJpa productRepositoryJpa;

  @BeforeEach
  void setUp() {
    QuarkusTransaction.requiringNew().run(() -> {
      saleRepositoryJpa.getEntityManager().createQuery("delete from SaleItemEntity").executeUpdate();
      saleRepositoryJpa.deleteAll();
      productRepositoryJpa.deleteAll();
      customerRepositoryJpa.deleteAll();
      saleRepositoryJpa.getEntityManager().flush();
    });
  }

  /**
   * Cenário: listar vendas quando não há registros.
   * Expectativa: retornar 200 com lista vazia.
   */
  @Test
  void findAll_whenNoSales_shouldReturn200WithEmptyList() {
    final var response = given()
        .contentType(ContentType.JSON)
        .when()
        .get("/api/sales");

    response.then()
        .statusCode(200)
        .body("", hasSize(0));
  }

  /**
   * Cenário: buscar venda criada via API por código.
   * Expectativa: retornar 200 com totais e itens.
   */
  @Test
  void findByCode_whenSaleExists_shouldReturn200WithBody() {
    final var customerCode = seedCustomer("John Doe");
    final var productCode = seedProduct("Amortecedor", new BigDecimal("100.00"));

    final var createRequest = Map.of(
        "customerCode", customerCode.toString(),
        "paymentMethod", "CASH",
        "cashPaidAmount", new BigDecimal("1000.00"),
        "items", List.of(
            Map.of("productCode", productCode.toString(), "quantity", 2)
        )
    );

    final var createResponse = given()
        .contentType(ContentType.JSON)
        .body(createRequest)
        .when()
        .post("/api/sales");

    createResponse.then()
        .statusCode(201)
        .body("code", notNullValue());

    final var saleCode = createResponse.jsonPath().getString("code");

    final var getResponse = given()
        .contentType(ContentType.JSON)
        .when()
        .get("/api/sales/" + saleCode);

    getResponse.then()
        .statusCode(200)
        .body("code", equalTo(saleCode))
        .body("customerName", equalTo("John Doe"))
        .body("items", hasSize(1))
        .body("items[0].productName", equalTo("Amortecedor"))
        .body("items[0].quantity", equalTo(2))
        .body("items[0].unitPrice", equalTo(100.00F))
        .body("items[0].total", equalTo(200.00F))
        .body("productsTotal", equalTo(200.00F))
        .body("taxAmount", equalTo(18.00F))
        .body("saleTotal", equalTo(218.00F));
  }

  /**
   * Cenário: buscar venda inexistente.
   * Expectativa: retornar 404.
   */
  @Test
  void findByCode_whenSaleDoesNotExist_shouldReturn404() {
    final var response = given()
        .contentType(ContentType.JSON)
        .when()
        .get("/api/sales/" + UUID.randomUUID());

    response.then()
        .statusCode(404)
        .body("message", equalTo("Sale not found"));
  }

  private UUID seedCustomer(final String fullName) {
    final var code = UUID.randomUUID();

    QuarkusTransaction.requiringNew().run(() -> {
      final var entity = CustomerEntity.builder()
          .code(code)
          .fullName(fullName)
          .motherName("Mother")
          .fullAddress("Street 1")
          .zipCode("12345678")
          .cpf(String.format("%011d", Math.floorMod(System.nanoTime(), 1_000_000_00000L)))
          .rg("RG")
          .birthDate(LocalDate.parse("1990-01-01"))
          .cellPhone("11999999999")
          .email("john.doe+" + code + "@acme.com")
          .build();

      customerRepositoryJpa.persist(entity);
      customerRepositoryJpa.getEntityManager().flush();
    });

    return code;
  }

  private UUID seedProduct(final String name, final BigDecimal salePrice) {
    final var code = UUID.randomUUID();

    QuarkusTransaction.requiringNew().run(() -> {
      final var entity = ProductEntity.builder()
          .code(code)
          .name(name)
          .type("ELETRICO")
          .details("Carro A")
          .heightCm(new BigDecimal("10.00"))
          .widthCm(new BigDecimal("20.00"))
          .depthCm(new BigDecimal("30.00"))
          .weightKg(new BigDecimal("2.500"))
          .purchasePrice(new BigDecimal("80.00"))
          .salePrice(salePrice)
          .build();

      productRepositoryJpa.persist(entity);
      productRepositoryJpa.getEntityManager().flush();
    });

    return code;
  }

}

