package com.github.sidd6p.store.dtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RegisterProductRequest {
    private String name;
    private BigDecimal price;
    private Integer category_id;
}

