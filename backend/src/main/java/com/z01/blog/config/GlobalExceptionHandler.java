package com.z01.blog.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.z01.blog.exception.AppError;
import com.z01.blog.exception.AppException;

//TODO: create a log system to for extra error information other than name and status
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<String> handleAppException(AppException ex) {
        AppError error = ex.getError();
        return ResponseEntity.status(error.status()).body(error.name());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationErrors(MethodArgumentNotValidException ex) {
        var firstError = ex.getBindingResult().getFieldErrors().get(0);
        return ResponseEntity.badRequest().body(firstError.getDefaultMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        System.err.println("Unhandled exception: " + ex);
        ex.printStackTrace(); // prints full stack trace
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(AppError.INTERNAL_ERROR.name());
    }
}