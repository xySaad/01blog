package com.z01.blog.model.DTO;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.z01.blog.model.User.UserEntity;

public record MeResponse(@JsonUnwrapped UserEntity user, List<String> permissions) {
}
