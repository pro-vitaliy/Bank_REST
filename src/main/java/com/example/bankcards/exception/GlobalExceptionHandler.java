package com.example.bankcards.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex) {
        HttpStatus s = ex.getErrorCode().getHttpStatus();
        return ResponseEntity.status(s).body(ex.getMessage());
    }

    @ExceptionHandler(UserOperationException.class)
    public ResponseEntity<String> handleUserOperationException(UserOperationException ex) {
        HttpStatus s = ex.getErrorCode().getHttpStatus();
        return ResponseEntity.status(s).body(ex.getMessage());
    }

    @ExceptionHandler(CardOperationException.class)
    public ResponseEntity<String> handleCardOperationException(CardOperationException ex) {
        HttpStatus s = ex.getErrorCode().getHttpStatus();
        return ResponseEntity.status(s).body(ex.getMessage());
    }
}
