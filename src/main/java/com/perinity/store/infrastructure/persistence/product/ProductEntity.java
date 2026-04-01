package com.perinity.store.infrastructure.persistence.product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product")
public class ProductEntity {

  @Id
  @EqualsAndHashCode.Include
  @Column(columnDefinition = "UUID")
  private UUID code;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String type;

  @Column(nullable = false)
  private String details;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal heightCm;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal widthCm;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal depthCm;

  @Column(nullable = false, precision = 10, scale = 3)
  private BigDecimal weightKg;

  @Column(nullable = false, precision = 15, scale = 2)
  private BigDecimal purchasePrice;

  @Column(nullable = false, precision = 15, scale = 2)
  private BigDecimal salePrice;

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

