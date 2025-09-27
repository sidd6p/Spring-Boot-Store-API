package com.github.sidd6p.store.mappers;

import com.github.sidd6p.store.dtos.CartItemDto;
import com.github.sidd6p.store.entities.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ProductMapper.class, CartReferenceMapper.class})
public interface CartItemMapper {
    CartItemDto toDto(CartItem cartItem);
}
