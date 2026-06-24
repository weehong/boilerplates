package com.example.boilerplate.features.health.controllers;

import com.example.boilerplate.shared.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/public/health-checks")
public class HealthCheckController {

    private static final String STATUS_OK = "OK";
    private static final String MESSAGE = "Service is running";

    @GetMapping
    public ResponseEntity<ApiResponse<String>> healthCheck(HttpServletRequest request) {
        return ResponseEntity.ok(ApiResponse.success(STATUS_OK, MESSAGE, request.getRequestURI()));
    }

}
