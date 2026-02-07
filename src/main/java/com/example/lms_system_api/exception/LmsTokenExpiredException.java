package com.example.lms_system_api.exception;

public class LmsTokenExpiredException extends RuntimeException {
    public LmsTokenExpiredException(String message) {
        super(message);
    }
}
