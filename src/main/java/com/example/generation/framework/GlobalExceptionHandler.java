package com.example.generation.framework;

import com.example.generation.framework.exceptions.AccountAlreadyClosedException;
import com.example.generation.framework.exceptions.AccountBalanceNotEmptyException;
import com.example.generation.framework.exceptions.DailyLimitReachedException;
import com.example.generation.framework.exceptions.InsufficientBalanceException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationError(MethodArgumentNotValidException ex) {
        List<Map<String, String>> fieldErrors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(err -> Map.of("field", err.getField(), "message", err.getDefaultMessage()))
                .toList();

        return new ResponseEntity<>(new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(), "Validation Failed", fieldErrors
        ),  HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        List<Map<String, String>> fieldErrors = ex.getConstraintViolations()
                .stream()
                .map(v -> Map.of("field", v.getPropertyPath().toString(), "message", v.getMessage()))
                .toList();

        return new ResponseEntity<>(new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(), ex.getMessage(), fieldErrors
        ),  HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientBalance(InsufficientBalanceException ex) {
        return new ResponseEntity<>(new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(), ex.getMessage(), List.of()
        ),  HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DailyLimitReachedException.class)
    public ResponseEntity<ErrorResponse> handleDailyLimitReachedException(DailyLimitReachedException ex) {
        return new ResponseEntity<>(new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(), ex.getMessage(), List.of()
        ),  HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(EntityNotFoundException ex) {
        return new ResponseEntity<>(new ErrorResponse(
                HttpStatus.NOT_FOUND.value(), ex.getMessage(), List.of()
        ),  HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationDenied(AuthorizationDeniedException ex) {
        return new ResponseEntity<>(new ErrorResponse(
                HttpStatus.FORBIDDEN.value(), ex.getMessage(), List.of()
        ),   HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFound(UsernameNotFoundException ex) {
        return new ResponseEntity<>(new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(), ex.getMessage(), List.of()
        ),   HttpStatus.UNAUTHORIZED);
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

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(), ex.getMessage(), List.of()
        ),  HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ex.printStackTrace();
        return new ResponseEntity<>(new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected Server Error", List.of()
        ),  HttpStatus.INTERNAL_SERVER_ERROR);
    }
}