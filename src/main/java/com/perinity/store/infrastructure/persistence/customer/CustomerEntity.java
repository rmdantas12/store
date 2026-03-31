package com.perinity.store.infrastructure.persistence.customer;

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

import java.time.LocalDate;
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
@Table(name = "customer")
public class CustomerEntity {

  @Id
  @EqualsAndHashCode.Include
  @Column(columnDefinition = "UUID")
  private UUID code;

  private String fullName;

  private String motherName;

  private String fullAddress;

  private String zipCode;

  @Column(unique = true, updatable = false, nullable = false)
  private String cpf;

  private String rg;

  private LocalDate birthDate;

  private String cellPhone;

  @Column(unique = true, nullable = false)
  private String email;

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
