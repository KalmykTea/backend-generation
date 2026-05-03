package com.example.generation.framework.exceptions;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException() {
        super("Balance cannot go below absolute limit");
    }
}
