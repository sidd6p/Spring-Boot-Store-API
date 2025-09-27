package com.github.sidd6p.store.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class AddItemToCartResponse {
    private ProductDto product;
    private int quantity;
    private BigDecimal totalPrice;
}
