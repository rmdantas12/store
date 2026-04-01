package com.perinity.store.infrastructure.web.sale;

import com.perinity.store.domain.model.PaymentMethod;
import jakarta.validation.Valid;
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
public class SaleUpdateRequest {

  private UUID customerCode;

  private String sellerCode;

  private PaymentMethod paymentMethod;

  private BigDecimal cashPaidAmount;

  private String cardNumber;

  private List<@Valid SaleItemRequest> items;
}

