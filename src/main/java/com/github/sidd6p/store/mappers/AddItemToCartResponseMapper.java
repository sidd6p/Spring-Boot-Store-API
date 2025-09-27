package com.github.sidd6p.store.mappers;

import com.github.sidd6p.store.dtos.AddItemToCartResponse;
import com.github.sidd6p.store.entities.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface AddItemToCartResponseMapper {

    @Mapping(target = "product.id", source = "product.id")
    @Mapping(target = "product.name", source = "product.name")
    @Mapping(target = "product.price", source = "product.price")
    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "totalPrice", source = ".", qualifiedByName = "calculateTotalPrice")
    AddItemToCartResponse toResponse(CartItem cartItem);

    @Named("calculateTotalPrice")
    default BigDecimal calculateTotalPrice(CartItem cartItem) {
        return cartItem.getProduct().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
    }
}
