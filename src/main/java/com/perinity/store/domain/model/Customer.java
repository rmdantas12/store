package com.perinity.store.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

  @EqualsAndHashCode.Include
  private UUID code;

  private String fullName;

  private String motherName;

  private String fullAddress;

  private String zipCode;

  private String cpf;

  private String rg;

  private LocalDate birthDate;

  private String cellPhone;

  private String email;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

}
