package com.example.generation.framework;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ErrorResponse {
    private int status;
    private String message;
    private LocalDateTime timestamp;
    private List<Map<String, String>> errors;

    // Constructor
    public ErrorResponse(int status, String message, List<Map<String, String>> errors) {
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.errors = errors;
    }
}