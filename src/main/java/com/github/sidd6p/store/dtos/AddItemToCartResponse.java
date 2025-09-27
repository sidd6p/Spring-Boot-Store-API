package com.github.sidd6p.store.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class AddItemToCartResponse {

    @Data
    @AllArgsConstructor
    public static class ProductInfo {
        private Integer id;
        private String name;
        private BigDecimal price;
    }

    private ProductInfo product;
    private int quantity;
    private BigDecimal totalPrice;
}
