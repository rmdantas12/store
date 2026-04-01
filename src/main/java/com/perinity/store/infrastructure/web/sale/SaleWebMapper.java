package com.perinity.store.infrastructure.web.sale;

import com.perinity.store.domain.model.Sale;
import com.perinity.store.domain.model.SaleItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public interface SaleWebMapper {

  @Mapping(target = "code", ignore = true)
  @Mapping(target = "customerName", ignore = true)
  @Mapping(target = "sellerCode", ignore = true)
  @Mapping(target = "sellerName", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "productsTotal", ignore = true)
  @Mapping(target = "taxAmount", ignore = true)
  @Mapping(target = "saleTotal", ignore = true)
  Sale toDomain(final SaleRequest request);

  @Mapping(target = "code", ignore = true)
  @Mapping(target = "customerName", ignore = true)
  @Mapping(target = "sellerCode", ignore = true)
  @Mapping(target = "sellerName", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "productsTotal", ignore = true)
  @Mapping(target = "taxAmount", ignore = true)
  @Mapping(target = "saleTotal", ignore = true)
  Sale toDomain(final SaleUpdateRequest request);

  @Mapping(target = "productName", ignore = true)
  @Mapping(target = "unitPrice", ignore = true)
  SaleItem toDomain(final SaleItemRequest request);

}

