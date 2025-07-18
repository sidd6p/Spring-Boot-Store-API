package com.github.sidd6p.store.dtos;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String user_name;
    private String email;
}
