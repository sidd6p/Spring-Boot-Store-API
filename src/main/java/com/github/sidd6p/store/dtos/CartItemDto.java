package com.github.sidd6p.store.dtos;

import lombok.Data;

@Data
public class CartItemDto {
    private Integer id;
    private CartDto cart;
    private ProductDto product;
    private int quantity;
}
