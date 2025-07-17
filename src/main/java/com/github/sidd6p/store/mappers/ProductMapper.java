package com.github.sidd6p.store.mappers;

import com.github.sidd6p.store.dtos.ProductDto;
import com.github.sidd6p.store.entities.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(source = "category.name", target = "categoryName")
    ProductDto toDto(Product product);
}
