package com.perinity.store.infrastructure.web.sale;

import com.perinity.store.infrastructure.persistence.customer.CustomerEntity;
import com.perinity.store.infrastructure.persistence.customer.CustomerRepositoryJpa;
import com.perinity.store.infrastructure.persistence.product.ProductEntity;
import com.perinity.store.infrastructure.persistence.product.ProductRepositoryJpa;
import com.perinity.store.infrastructure.persistence.sale.SaleRepositoryJpa;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
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
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;

@QuarkusTest
class SaleCreateResourceTest {

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
   * Cenário: criar uma venda válida (dinheiro).
   * Expectativa: retornar 201, com `Location` preenchido e totais calculados.
   */
  @Test
  void create_whenValidCashSale_shouldReturn201WithTotals() {
    final var customerCode = seedCustomer("John Doe");
    final var productCode = seedProduct("Amortecedor", new BigDecimal("100.00"));

    final var request = Map.of(
        "customerCode", customerCode.toString(),
        "sellerCode", "S-001",
        "paymentMethod", "CASH",
        "cashPaidAmount", new BigDecimal("1000.00"),
        "items", List.of(
            Map.of("productCode", productCode.toString(), "quantity", 2)
        )
    );

    final Response response = given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/api/sales");

    response.then()
        .statusCode(201)
        .header("Location", notNullValue())
        .body("code", notNullValue())
        .body("customerName", equalTo("John Doe"))
        .body("productsTotal", equalTo(200.00F))
        .body("taxAmount", equalTo(18.00F))
        .body("saleTotal", equalTo(218.00F))
        .body("paymentMethod", equalTo("CASH"));
  }

  /**
   * Cenário: criar venda com `items` inválido (quantity <= 0).
   * Expectativa: retornar 400 com validação apontando `quantity`.
   */
  @Test
  void create_whenItemQuantityIsInvalid_shouldReturn400WithFieldNameInErrorBody() {
    final var customerCode = seedCustomer("John Doe");
    final var productCode = seedProduct("Amortecedor", new BigDecimal("100.00"));

    final var request = Map.of(
        "customerCode", customerCode.toString(),
        "paymentMethod", "CASH",
        "cashPaidAmount", new BigDecimal("1000.00"),
        "items", List.of(
            Map.of("productCode", productCode.toString(), "quantity", 0)
        )
    );

    final Response response = given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/api/sales");

    response.then()
        .statusCode(400)
        .body("message", equalTo("Validation failed"))
        .body("details", hasItem(startsWith("quantity:")));
  }

  /**
   * Cenário: criar venda em dinheiro sem `cashPaidAmount`.
   * Expectativa: retornar 400.
   */
  @Test
  void create_whenCashWithoutPaidAmount_shouldReturn400() {
    final var customerCode = seedCustomer("John Doe");
    final var productCode = seedProduct("Amortecedor", new BigDecimal("100.00"));

    final var request = Map.of(
        "customerCode", customerCode.toString(),
        "paymentMethod", "CASH",
        "items", List.of(
            Map.of("productCode", productCode.toString(), "quantity", 1)
        )
    );

    final Response response = given()
        .contentType(ContentType.JSON)
        .body(request)
        .when()
        .post("/api/sales");

    response.then()
        .statusCode(400)
        .body("message", equalTo("cashPaidAmount is required for CASH payments"));
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

