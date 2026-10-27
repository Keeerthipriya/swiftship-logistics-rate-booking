package com.swiftship.logistics.api.dto;

import java.time.Instant;
import java.util.List;

/**
 * Standardized error payload for all REST API errors.
 */
public record ErrorResponseDto(
    String errorCode,
    String message,
    int status,
    Instant timestamp,
    List<String> details
) {
    public static ErrorResponseDto of(String errorCode, String message, int status) {
        return new ErrorResponseDto(errorCode, message, status, Instant.now(), List.of());
    }

    public static ErrorResponseDto of(String errorCode, String message, int status, List<String> details) {
        return new ErrorResponseDto(errorCode, message, status, Instant.now(), details);
    }
}
