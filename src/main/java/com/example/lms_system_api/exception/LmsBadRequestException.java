package com.example.lms_system_api.exception;

public class LmsBadRequestException extends RuntimeException {
    public LmsBadRequestException(String message) {
        super(message);
    }
}
