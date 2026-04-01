package com.perinity.store.infrastructure.web.sale;

import com.perinity.store.domain.model.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleRequest {

  @NotNull
  private UUID customerCode;

  @NotNull
  private PaymentMethod paymentMethod;

  private BigDecimal cashPaidAmount;

  private String cardNumber;

  @NotNull
  private List<@Valid SaleItemRequest> items;

}
