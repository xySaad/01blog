package com.z01.blog.model.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CommentRequest {
        @NotBlank(message = "Content is required")
        @Size(min = 1, max = 2000, message = "Comment must be between 1 and 2000 characters")
        public String content;
}
