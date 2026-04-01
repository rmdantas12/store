package com.perinity.store.infrastructure.web.sale;

import com.perinity.store.domain.model.PaymentMethod;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class SaleResponse {

  private UUID code;

  private UUID customerCode;

  private String customerName;

  private String sellerCode;

  private String sellerName;

  private LocalDateTime createdAt;

  private List<SaleItemResponse> items;

  private BigDecimal productsTotal;

  private BigDecimal taxAmount;

  private BigDecimal saleTotal;

  private PaymentMethod paymentMethod;

  private BigDecimal cashPaidAmount;

  private String cardNumber;

  private LocalDateTime updatedAt;

}
