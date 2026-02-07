package com.example.lms_system_api.exception;

import com.example.lms_system_api.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(IllegalArgumentException ex) {
        log.error(ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.builder()
                        .status(404)
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler(LmsBadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(LmsBadRequestException ex) {
        log.error((ex.getMessage()));
        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.builder()
                        .status(400)
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        log.error(ex.getMessage());
        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.builder()
                        .status(400)
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        log.error(ex.getMessage());
        return ResponseEntity
                .internalServerError()
                .body(ErrorResponse.builder()
                        .status(500)
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler(LmsNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(LmsNotFoundException ex) {
        log.error(ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.builder()
                        .status(404)
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler(LmsAuthException.class)
    public ResponseEntity<ErrorResponse> handleAuthException(LmsAuthException ex) {
        log.error(ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.builder()
                        .message(ex.getMessage())
                        .status(401)
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler(LmsUsernameAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUsernameAlreadyExists(LmsUsernameAlreadyExistsException ex) {
        log.error(ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ErrorResponse.builder()
                        .message(ex.getMessage())
                        .status(409)
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler(LmsIncorrectPasswordException.class)
    public ResponseEntity<ErrorResponse> handleIncorrectPasswordException(LmsIncorrectPasswordException ex) {
        log.error(ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ErrorResponse.builder()
                        .status(422)
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler(LmsTokenExpiredException.class)
    public ResponseEntity<ErrorResponse> handleTokenExpiredException(LmsTokenExpiredException ex) {
        log.error(ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.builder()
                        .status(401)
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler(LmsInternalException.class)
    public ResponseEntity<ErrorResponse> handleInternalException(LmsInternalException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.builder()
                        .status(500)
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build());
    }
}
