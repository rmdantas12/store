package com.perinity.store.infrastructure.web.customer;

import com.perinity.store.domain.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public interface CustomerWebMapper {

  @Mapping(target = "code", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  Customer toDomain(final CustomerRequest request);

  @Mapping(target = "code", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  Customer toDomain(final CustomerUpdateRequest request);

  CustomerResponse toResponse(final Customer customer);

}
