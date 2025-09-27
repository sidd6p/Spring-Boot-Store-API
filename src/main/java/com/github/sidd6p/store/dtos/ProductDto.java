package com.github.sidd6p.store.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Data
public class ProductDto {
    private Integer id;
    private String name;

    @JsonProperty("category_name")
    private String categoryName;
    private BigDecimal price;
}
