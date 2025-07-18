package com.github.sidd6p.store.dtos;

import lombok.Data;

// The @Data annotation generates getters, setters, toString, equals, and hashCode methods automatically.
@Data
public class RegisterUserRequest {
    private String user_name;
    private String password;
    private String email;
}
