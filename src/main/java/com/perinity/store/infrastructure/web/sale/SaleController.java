package com.perinity.store.infrastructure.web.sale;

import com.perinity.store.domain.model.Sale;
import com.perinity.store.domain.model.SaleItem;
import com.perinity.store.domain.ports.incoming.SaleUseCase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.net.URI;
import java.util.UUID;

@RequiredArgsConstructor
@ApplicationScoped
public class SaleController implements SaleAPI {

  private final SaleUseCase saleUseCase;
  private final SaleWebMapper saleWebMapper;

  @Context
  private UriInfo uriInfo;

  @Override
  public Response create(final SaleRequest request) {
    final var sale = saleWebMapper.toDomain(request);
    final var created = saleUseCase.create(sale);

    final URI location = uriInfo.getAbsolutePathBuilder()
        .path(created.getCode().toString())
        .build();

    return Response.created(location)
        .entity(toResponse(created))
        .build();
  }

  @Override
  public Response update(final UUID code, final SaleUpdateRequest request) {
    final var updated = saleUseCase.update(code, saleWebMapper.toDomain(request));
    final var body = toResponse(updated);
    return Response.ok(body).build();
  }

  @Override
  public Response delete(final UUID code) {
    saleUseCase.delete(code);
    return Response.noContent().build();
  }

  @Override
  public Response findByCode(final UUID code) {
    final var sale = saleUseCase.findByCode(code);
    final var body = toResponse(sale);
    return Response.ok(body).build();
  }

  @Override
  public Response findAll() {
    final var body = saleUseCase.findAll().stream()
        .map(this::toResponse)
        .toList();

    return Response.ok(body).build();
  }

  private SaleResponse toResponse(final Sale sale) {
    final var items = sale.getItems()
        .stream()
        .map(this::toItemResponse)
        .toList();

    return SaleResponse.builder()
        .code(sale.getCode())
        .customerCode(sale.getCustomerCode())
        .customerName(sale.getCustomerName())
        .sellerCode(sale.getSellerCode())
        .createdAt(sale.getCreatedAt())
        .items(items)
        .productsTotal(sale.getProductsTotal())
        .taxAmount(sale.getTaxAmount())
        .saleTotal(sale.getSaleTotal())
        .paymentMethod(sale.getPaymentMethod())
        .cashPaidAmount(sale.getCashPaidAmount())
        .cardNumber(sale.getCardNumber())
        .updatedAt(sale.getUpdatedAt())
        .build();
  }

  private SaleItemResponse toItemResponse(final SaleItem item) {
    return SaleItemResponse.builder()
        .productCode(item.getProductCode())
        .productName(item.getProductName())
        .quantity(item.getQuantity())
        .unitPrice(item.getUnitPrice())
        .total(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
        .build();
  }

}

