package com.perinity.store.infrastructure.web.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class ProductRequest {

  @NotBlank
  private String name;

  @NotBlank
  private String type;

  @NotBlank
  private String details;

  @NotNull
  @Positive
  private BigDecimal heightCm;

  @NotNull
  @Positive
  private BigDecimal widthCm;

  @NotNull
  @Positive
  private BigDecimal depthCm;

  @NotNull
  @Positive
  private BigDecimal weightKg;

  @NotNull
  @Positive
  private BigDecimal purchasePrice;

  @NotNull
  @Positive
  private BigDecimal salePrice;
}

