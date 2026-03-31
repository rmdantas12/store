package com.perinity.store.infrastructure.web.customer;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequest {

  @NotBlank
  private String fullName;

  private String motherName;

  @NotBlank
  private String fullAddress;

  @Pattern(regexp = "\\d{5}-?\\d{3}")
  private String zipCode;

  @NotBlank
  @Pattern(regexp = "\\d{11}")
  private String cpf;

  @NotBlank
  private String rg;

  @NotNull
  @Past
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate birthDate;

  @NotBlank
  @Pattern(regexp = "\\d{10,11}")
  private String cellPhone;

  @Email
  @NotBlank
  private String email;

}
