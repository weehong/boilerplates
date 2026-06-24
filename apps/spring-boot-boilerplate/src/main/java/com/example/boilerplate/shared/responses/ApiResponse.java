package com.example.boilerplate.shared.responses;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
    Boolean success,
    T data,
    String message,
    Instant timestamp,
    String path) {

    public static <T> ApiResponse<T> success(final T data, final String message, final String path) {
        return new ApiResponse<>(
            true,
            data,
            message,
            Instant.now(),
            path);
    }

    public static <T> ApiResponse<T> error(final String message, final String path) {
        return new ApiResponse<>(
            false,
            null,
            message,
            Instant.now(),
            path);
    }

}
