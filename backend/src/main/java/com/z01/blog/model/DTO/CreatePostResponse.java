package com.z01.blog.model.DTO;

import java.time.LocalDateTime;

public record CreatePostResponse(String id, boolean isPublic, LocalDateTime updatedAt) {
};
