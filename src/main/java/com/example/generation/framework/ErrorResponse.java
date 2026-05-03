package com.example.generation.framework;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
public class ErrorResponse {
    private int status;
    private String message;
    private LocalDateTime timestamp;
    private List<Map<String, String>> errors;

    public ErrorResponse(int status, String message, List<Map<String, String>> errors) {
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.errors = errors;
    }
}