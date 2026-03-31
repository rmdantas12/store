package com.perinity.store.infrastructure.persistence.customer;

import com.perinity.store.domain.model.Customer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface CustomerPersistenceMapper {

  CustomerEntity toEntity(Customer customer);

  Customer toDomain(CustomerEntity entity);

}
