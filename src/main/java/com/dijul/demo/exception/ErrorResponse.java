package com.dijul.demo.exception;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class ErrorResponse {
    private LocalDateTime timestamp;
    private String error;
    private String message;
    private int status;
    private Map<String, String> validationErrors;
}