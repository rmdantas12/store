package com.perinity.store.infrastructure.web.sale;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleItemRequest {

  @NotNull
  private UUID productCode;

  @NotNull
  @Positive
  private Integer quantity;
}

