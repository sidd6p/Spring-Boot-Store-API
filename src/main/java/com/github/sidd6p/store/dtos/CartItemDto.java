package com.github.sidd6p.store.dtos;

import lombok.Data;
import java.util.UUID;

@Data
public class CartItemDto {
    private Integer id;
    private UUID cartId;
    private ProductDto product;
    private int quantity;
}
