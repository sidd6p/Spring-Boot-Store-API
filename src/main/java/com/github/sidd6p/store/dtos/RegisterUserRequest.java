package com.github.sidd6p.store.dtos;

import com.github.sidd6p.store.validations.LowerCase;
import jakarta.validation.constraints.Size;
import lombok.Data;

// The @Data annotation generates getters, setters, toString, equals, and hashCode methods automatically.
@Data
public class RegisterUserRequest {
    private String user_name;

    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
    private String password;

    @LowerCase
    private String email;
}
