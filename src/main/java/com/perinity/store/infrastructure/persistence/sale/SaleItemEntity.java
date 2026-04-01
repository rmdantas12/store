package com.perinity.store.infrastructure.persistence.sale;

import com.perinity.store.infrastructure.persistence.product.ProductEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Entity
@Table(name = "sale_item")
public class SaleItemEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "sale_code", nullable = false)
  private SaleEntity sale;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "product_code", nullable = false)
  private ProductEntity product;

  @Column(nullable = false)
  private Integer quantity;

  @Column(nullable = false, precision = 15, scale = 2)
  private BigDecimal unitPrice;

}

