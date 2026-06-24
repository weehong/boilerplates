package com.example.boilerplate.shared.exceptions.handlers;

import com.example.boilerplate.shared.exceptions.constants.ExceptionConstant;
import com.example.boilerplate.shared.exceptions.types.BaseException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String LOG_BASE_EXCEPTION = "BaseException: {}";
    private static final String LOG_VALIDATION_FAILED = "Validation failed: {}";
    private static final String LOG_CONSTRAINT_VIOLATION = "Constraint violation: {}";

    @ExceptionHandler(BaseException.class)
    public final ResponseEntity<ProblemDetail> handleBaseException(
        BaseException ex,
        HttpServletRequest httpServletRequest) {
        LOGGER.error(LOG_BASE_EXCEPTION, ex.getMessage(), ex);

        ProblemDetail problemDetail = ex.toProblemDetail(httpServletRequest.getRequestURI());

        return ResponseEntity.status(ex.getHttpStatus()).body(problemDetail);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex,
        HttpHeaders headers,
        HttpStatusCode status,
        WebRequest request) {
        LOGGER.warn(LOG_VALIDATION_FAILED, ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST, ExceptionConstant.VALIDATION_FAILED);
        problemDetail.setType(URI.create(ExceptionConstant.ABOUT_BLANK));
        problemDetail.setTitle(ExceptionConstant.BAD_REQUEST_TITLE);

        if (request instanceof ServletWebRequest servletWebRequest) {
            try {
                problemDetail.setInstance(
                    URI.create(servletWebRequest.getRequest().getRequestURI()));
            } catch (IllegalArgumentException ignored) {
                // Keep instance unset when the request URI is malformed.
            }
        }

        List<Map<String, String>> errors = ex.getBindingResult().getFieldErrors().stream().map(fieldError -> {
            Map<String, String> error = new HashMap<>();
            error.put(ExceptionConstant.PROPERTY_FIELD, fieldError.getField());
            error.put(
                ExceptionConstant.PROPERTY_MESSAGE,
                fieldError.getDefaultMessage() != null
                    ? fieldError.getDefaultMessage()
                    : ExceptionConstant.DEFAULT_FIELD_ERROR_MESSAGE);

            return error;
        }).toList();

        problemDetail.setProperty(ExceptionConstant.PROPERTY_ERRORS, errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolationException(
        ConstraintViolationException ex,
        HttpServletRequest httpServletRequest) {
        LOGGER.warn(LOG_CONSTRAINT_VIOLATION, ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST, ExceptionConstant.VALIDATION_FAILED);
        problemDetail.setType(URI.create(ExceptionConstant.ABOUT_BLANK));
        problemDetail.setTitle(ExceptionConstant.BAD_REQUEST_TITLE);

        try {
            problemDetail.setInstance(URI.create(httpServletRequest.getRequestURI()));
        } catch (IllegalArgumentException ignored) {
            // Keep instance unset when the request URI is malformed.
        }

        List<Map<String, String>> errors = ex.getConstraintViolations().stream().map(violation -> {
            Map<String, String> error = new HashMap<>();
            String propertyPath = violation.getPropertyPath().toString();
            String field = propertyPath.contains(ExceptionConstant.DOT)
                           ? propertyPath.substring(propertyPath.lastIndexOf('.') + 1)
                           : propertyPath;
            error.put(ExceptionConstant.PROPERTY_FIELD, field);
            error.put(ExceptionConstant.PROPERTY_MESSAGE, violation.getMessage());

            return error;
        }).toList();

        problemDetail.setProperty(ExceptionConstant.PROPERTY_ERRORS, errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

}
