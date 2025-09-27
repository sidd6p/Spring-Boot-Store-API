package com.github.sidd6p.store.dtos;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class CartReferenceDto {
    private UUID id;
    private LocalDate dateCreated;
}
