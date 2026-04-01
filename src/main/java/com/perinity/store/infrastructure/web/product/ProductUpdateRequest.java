package com.perinity.store.infrastructure.web.product;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateRequest {

  private String name;

  private String type;

  private String details;

  @Positive
  private BigDecimal heightCm;

  @Positive
  private BigDecimal widthCm;

  @Positive
  private BigDecimal depthCm;

  @Positive
  private BigDecimal weightKg;

  @Positive
  private BigDecimal purchasePrice;

  @Positive
  private BigDecimal salePrice;
}

