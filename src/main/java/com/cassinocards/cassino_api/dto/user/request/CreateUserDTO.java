package com.cassinocards.cassino_api.dto.user.request;

import jakarta.validation.constraints.*;

public record CreateUserDTO(
        @NotBlank(message = "Username required")
        @Size(min = 3, max = 20, message = "Username must be 2 - 20 characters")
        @Pattern(
                regexp = "^[a-z0-9_]+$",
                message = "Username can only contain lowercase letters, numbers, and underscores"
        )
        String username,
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Size(max = 100, message = "Email must not exceed 100 characters")
        String email,
        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password
) {
}
