package com.github.sidd6p.store.dtos;

import com.github.sidd6p.store.validations.LowerCase;
import jakarta.validation.constraints.Size;
import lombok.Data;

// The @Data annotation generates getters, setters, toString, equals, and hashCode methods automatically.
// This create ToString method that includes all fields, which is useful for logging and debugging. And sometimes it can cause issue with LAZY loaded fields.
@Data
public class RegisterUserRequest {
    private String user_name;

    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
    private String password;

    @LowerCase
    private String email;
}
