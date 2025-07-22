package com.github.sidd6p.store.dtos;

import com.github.sidd6p.store.entities.CartItem;
import lombok.Data;

import java.math.BigDecimal;
import java.util.*;

@Data
public class CartDto {
    private UUID id;
    private Set<CartItem> cartItems = new HashSet<>();
    private BigDecimal price = BigDecimal.ZERO;
}

