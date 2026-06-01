package com.example.generation.framework.exceptions;

public class AccountAlreadyClosedException extends RuntimeException {
    public AccountAlreadyClosedException(String message) {
        super(message);
    }
}
