package com.perinity.store.infrastructure.web.product;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class ProductResponse {

  private UUID code;

  private String name;

  private String type;

  private String details;

  private BigDecimal heightCm;

  private BigDecimal widthCm;

  private BigDecimal depthCm;

  private BigDecimal weightKg;

  private BigDecimal purchasePrice;

  private BigDecimal salePrice;

  private LocalDateTime createdAt;
}

