package com.z01.blog.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthRequest {
    @Email
    @NotBlank
    public String email;

    @Size(min = 8, max = 32)
    @NotBlank
    public String password;
}
