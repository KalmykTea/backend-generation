package com.example.generation.framework.exceptions;

public class DailyLimitReachedException extends RuntimeException {
    public DailyLimitReachedException() {
        super("Daily limit reached or transfer amount exceeds the daily limit.");
    }
}
