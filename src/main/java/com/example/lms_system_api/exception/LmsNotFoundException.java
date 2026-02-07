package com.example.lms_system_api.exception;

public class LmsNotFoundException extends RuntimeException {
    public LmsNotFoundException(String message) {
        super(message);
    }
}
