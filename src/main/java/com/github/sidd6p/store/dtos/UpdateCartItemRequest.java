package com.github.sidd6p.store.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateCartItemRequest {
    @NotNull(message = "quantity must be at least 1")
    @Min(value = 1, message = "quantity must be at least 1")
    private Integer quantity;
}
