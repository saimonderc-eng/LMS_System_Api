package com.example.lms_system_api.exception;

public class LmsIncorrectPasswordException extends RuntimeException {
    public LmsIncorrectPasswordException(String message) {
        super(message);
    }
}
