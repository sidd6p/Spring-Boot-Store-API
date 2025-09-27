package com.github.sidd6p.store.mappers;

import com.github.sidd6p.store.dtos.CartItemDto;
import com.github.sidd6p.store.entities.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface CartItemMapper {
    @Mapping(source = "cart.id", target = "cartId")
    CartItemDto toDto(CartItem cartItem);
}
