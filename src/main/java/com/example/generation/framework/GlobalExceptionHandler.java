package com.example.generation.framework;

import com.example.generation.framework.exceptions.AccountAlreadyClosedException;
import com.example.generation.framework.exceptions.AccountBalanceNotEmptyException;
import com.example.generation.framework.exceptions.DailyLimitReachedException;
import com.example.generation.framework.exceptions.InsufficientBalanceException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String name = ex.getName();
        String type = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";
        String message = name + " should be of type " + type;
        if (ex.getRequiredType() != null && java.time.LocalDate.class.isAssignableFrom(ex.getRequiredType())) {
            message = "Invalid date format for " + name + ". Expected format is YYYY-MM-DD.";
        }
        return new ResponseEntity<>(new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(), message, List.of()
        ),  HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationError(MethodArgumentNotValidException ex) {
        List<Map<String, String>> fieldErrors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(err -> Map.of("field", err.getField(), "message", err.getDefaultMessage()))
                .toList();

        return new ResponseEntity<>(new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), "Validation Failed", fieldErrors
        ),  HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        List<Map<String, String>> fieldErrors = ex.getConstraintViolations()
                .stream()
                .map(v -> Map.of("field", v.getPropertyPath().toString(), "message", v.getMessage()))
                .toList();

        return new ResponseEntity<>(new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), fieldErrors
        ),  HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientBalance(InsufficientBalanceException ex) {
        return new ResponseEntity<>(new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(), ex.getMessage(), List.of()
        ),  HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DailyLimitReachedException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientBalance(DailyLimitReachedException ex) {
        return new ResponseEntity<>(new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(), ex.getMessage(), List.of()
        ),  HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccountAlreadyClosedException.class)
    public ResponseEntity<ErrorResponse> handleAccountAlreadyClosed(AccountAlreadyClosedException ex) {
        return new ResponseEntity<>(new ErrorResponse(
                HttpStatus.CONFLICT.value(), ex.getMessage(), List.of()
        ), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AccountBalanceNotEmptyException.class)
    public ResponseEntity<ErrorResponse> handleAccountBalanceNotEmpty(AccountBalanceNotEmptyException ex) {
        return new ResponseEntity<>(new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(), ex.getMessage(), List.of()
        ), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(EntityNotFoundException ex) {
        return new ResponseEntity<>(new ErrorResponse(
                HttpStatus.NOT_FOUND.value(), ex.getMessage(), List.of()
        ),  HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ex.printStackTrace();
        return new ResponseEntity<>(new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(), "Unexpected Server Error", List.of()
        ),  HttpStatus.BAD_REQUEST);
    }
}