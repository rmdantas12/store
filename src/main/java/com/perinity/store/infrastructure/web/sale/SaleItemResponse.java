package com.perinity.store.infrastructure.web.sale;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
public class SaleItemResponse {

  private UUID productCode;

  private String productName;

  private Integer quantity;

  private BigDecimal unitPrice;

  private BigDecimal total;
}

