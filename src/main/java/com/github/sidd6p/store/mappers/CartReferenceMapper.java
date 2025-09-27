package com.github.sidd6p.store.mappers;

import com.github.sidd6p.store.dtos.CartReferenceDto;
import com.github.sidd6p.store.entities.Cart;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartReferenceMapper {
    CartReferenceDto toDto(Cart cart);
}
