package com.perinity.store.infrastructure.persistence.sale;

import com.perinity.store.domain.model.PaymentMethod;
import com.perinity.store.infrastructure.persistence.customer.CustomerEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sale")
public class SaleEntity {

  @Id
  @EqualsAndHashCode.Include
  @Column(columnDefinition = "UUID")
  private UUID code;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "customer_code", nullable = false)
  private CustomerEntity customer;

  private String sellerCode;

  @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<SaleItemEntity> items = new ArrayList<>();

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PaymentMethod paymentMethod;

  @Column(precision = 15, scale = 2)
  private BigDecimal cashPaidAmount;

  private String cardNumber;

  @Column(nullable = false, precision = 15, scale = 2)
  private BigDecimal productsTotal;

  @Column(nullable = false, precision = 15, scale = 2)
  private BigDecimal taxAmount;

  @Column(nullable = false, precision = 15, scale = 2)
  private BigDecimal saleTotal;

  @Column(updatable = false, nullable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  private LocalDateTime updatedAt;

  @PrePersist
  void prePersist() {
    if (Objects.isNull(code)) {
      code = UUID.randomUUID();
    }

    final var now = LocalDateTime.now();
    createdAt = now;
    updatedAt = now;
  }

  @PreUpdate
  void preUpdate() {
    updatedAt = LocalDateTime.now();
  }

}

