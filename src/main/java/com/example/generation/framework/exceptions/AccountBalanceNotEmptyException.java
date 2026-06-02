package com.example.generation.framework.exceptions;

public class AccountBalanceNotEmptyException extends RuntimeException {
    public AccountBalanceNotEmptyException(String message) {
        super(message);
    }
}
