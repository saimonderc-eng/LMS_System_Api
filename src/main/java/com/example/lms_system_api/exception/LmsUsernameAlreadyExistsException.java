package com.example.lms_system_api.exception;

public class LmsUsernameAlreadyExistsException extends RuntimeException {
    public LmsUsernameAlreadyExistsException(String message) {
        super(message);
    }
}
