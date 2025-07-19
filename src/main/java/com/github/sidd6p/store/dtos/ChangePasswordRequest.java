package com.github.sidd6p.store.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {

    @NotNull(message = "Old password cannot be null")
    @NotBlank(message = "Old password cannot be blank")
    @JsonProperty("old_password")
    private String oldPassword;

    @NotNull(message = "New password cannot be null")
    @NotBlank(message = "New password cannot be blank")
    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
    @JsonProperty("new_password")
    private String newPassword;
}
