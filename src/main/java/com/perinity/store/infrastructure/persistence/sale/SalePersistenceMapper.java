package com.perinity.store.infrastructure.persistence.sale;

import com.perinity.store.domain.model.Sale;
import com.perinity.store.domain.model.SaleItem;
import com.perinity.store.infrastructure.persistence.customer.CustomerRepositoryJpa;
import com.perinity.store.infrastructure.persistence.product.ProductRepositoryJpa;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SalePersistenceMapper {

  @Mapping(target = "customer", ignore = true)
  @Mapping(target = "items", ignore = true)
  SaleEntity toEntity(
      Sale sale,
      @Context CustomerRepositoryJpa customerRepositoryJpa,
      @Context ProductRepositoryJpa productRepositoryJpa
  );

  @Mapping(target = "sale", ignore = true)
  @Mapping(target = "product", ignore = true)
  @Mapping(target = "id", ignore = true)
  SaleItemEntity toEntity(final SaleItem item, @Context ProductRepositoryJpa productRepositoryJpa);

  @Mapping(target = "customerCode", source = "customer.code")
  @Mapping(target = "customerName", source = "customer.fullName")
  Sale toDomain(final SaleEntity entity);

  @Mapping(target = "productCode", source = "product.code")
  @Mapping(target = "productName", source = "product.name")
  SaleItem toDomain(final SaleItemEntity entity);

  @AfterMapping
  default void linkCustomerAndItems(
      final Sale sale,
      @MappingTarget final SaleEntity entity,
      @Context final CustomerRepositoryJpa customerRepositoryJpa,
      @Context final ProductRepositoryJpa productRepositoryJpa
  ) {
    final var customer = customerRepositoryJpa.findByCode(sale.getCustomerCode());

    customer.ifPresent(entity::setCustomer);

    final List<SaleItemEntity> items = Optional.ofNullable(sale.getItems())
        .orElse(List.of())
        .stream()
        .map(i -> toEntity(i, productRepositoryJpa))
        .toList();

    entity.getItems()
        .clear();

    for (final var item : items) {
      item.setSale(entity);
      entity.getItems().add(item);
    }
  }

  @AfterMapping
  default void linkProduct(
      final SaleItem item,
      @MappingTarget final SaleItemEntity entity,
      @Context final ProductRepositoryJpa productRepositoryJpa
  ) {
    final var product = productRepositoryJpa.findByCode(item.getProductCode());
    product.ifPresent(entity::setProduct);
  }

}

