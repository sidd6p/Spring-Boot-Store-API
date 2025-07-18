package com.github.sidd6p.store.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangePasswordRequest {

    @NotNull(message = "Old password is required")
    @NotBlank(message = "Old password cannot be blank")
    @JsonProperty("old_password")
    private String oldPassword;

    @NotNull(message = "New password is required")
    @NotBlank(message = "New password cannot be blank")
    @JsonProperty("new_password")
    private String newPassword;
}
