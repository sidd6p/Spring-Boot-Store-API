package com.github.sidd6p.store.mappers;

import com.github.sidd6p.store.dtos.CartDto;
import com.github.sidd6p.store.entities.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CartItemMapper.class})
public interface CartMapper {
    @Mapping(target = "price", expression = "java(cart.getCartItems().stream().map(item -> item.getProduct().getPrice().multiply(java.math.BigDecimal.valueOf(item.getQuantity()))).reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add))")
    CartDto toDto(Cart cart);
}
