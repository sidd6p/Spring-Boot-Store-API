package com.github.sidd6p.store.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Data
public class CartDto {
    private UUID id;
    private LocalDate dateCreated;
    private Set<CartItemDto> cartItems = new HashSet<>();
    private BigDecimal price = BigDecimal.ZERO;
}
