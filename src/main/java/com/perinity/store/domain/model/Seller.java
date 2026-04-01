package com.perinity.store.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Vendedor (identificado pelo subject/username do token).
 */
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Seller {

  /**
   * Código único do vendedor (proveniente do token).
   */
  @EqualsAndHashCode.Include
  private String code;

  /**
   * Nome do vendedor (proveniente do token).
   */
  private String name;
}

