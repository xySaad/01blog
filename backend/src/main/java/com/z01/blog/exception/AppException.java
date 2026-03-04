package com.z01.blog.exception;

public class AppException extends RuntimeException {
    private final AppError error;

    public AppException(AppError error) {
        super(error.name());
        this.error = error;
    }

    public AppError getError() {
        return error;
    }
}
