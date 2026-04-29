package com.task.service.exception;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class ErrorResponse {
    private final String message;
    private final int status;
    private long timestamp = System.currentTimeMillis();

}