package com.github.sidd6p.store.mappers;

import com.github.sidd6p.store.dtos.CartItemDto;
import com.github.sidd6p.store.entities.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface CartItemMapper {
    @Mapping(target = "cart", ignore = true) // Ignore cart to avoid circular reference
    CartItemDto toDto(CartItem cartItem);
}
