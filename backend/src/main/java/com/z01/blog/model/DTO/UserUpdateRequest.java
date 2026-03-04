package com.z01.blog.model.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserUpdateRequest {
    @NotBlank(message = "Login is required")
    @Size(min = 3, max = 32, message = "Login must be between 3 and 32 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_.-]+$", message = "Login may only contain letters, numbers, underscores, hyphens, and dots")
    public String login;

    @NotBlank(message = "First name is required")
    @Size(min = 1, max = 64, message = "First name must be between 1 and 64 characters")
    public String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 1, max = 64, message = "Last name must be between 1 and 64 characters")
    public String lastName;
}