package com.z01.blog.exception;

public class InternalException extends AppException {
    private final String details;

    public InternalException(String details) {
        super(AppError.INTERNAL_ERROR);
        this.details = details;
    }

    public void printDetails() {
        System.err.println(details);
    }
}
