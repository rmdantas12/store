package com.perinity.store.infrastructure.web.customer;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class CustomerResponse {

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
