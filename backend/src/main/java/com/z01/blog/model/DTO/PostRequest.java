package com.z01.blog.model.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PostRequest {
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    public String title;

    @NotBlank(message = "Content is required")
    @Size(min = 1, max = 100_000, message = "Content must not exceed 100,000 characters")
    public String content;

    public boolean isPublic;
}
