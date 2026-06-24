package com.example.boilerplate.shared.exceptions.types;

import com.example.boilerplate.shared.exceptions.constants.ExceptionConstant;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
public abstract class BaseException extends RuntimeException {

    private static final Set<String> RESERVED_PROPERTY_KEYS =
        Set.of(ExceptionConstant.PROPERTY_ERROR_CODE, ExceptionConstant.PROPERTY_CAUSE_TYPE);
    private static final String RESERVED_KEY_ERROR = "Reserved property key is not allowed: ";

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final transient Map<String, Object> customProperties;

    protected BaseException(String message, HttpStatus httpStatus, String errorCode) {
        super(message);

        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.customProperties = new HashMap<>();
    }

    public final BaseException addProperty(String key, Object value) {
        if (RESERVED_PROPERTY_KEYS.contains(key)) {
            throw new IllegalArgumentException(RESERVED_KEY_ERROR + key);
        }

        this.customProperties.put(key, value);

        return this;
    }

    public final ProblemDetail toProblemDetail(String instance) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(httpStatus, getMessage());
        problemDetail.setType(URI.create(ExceptionConstant.ABOUT_BLANK));
        problemDetail.setTitle(httpStatus.getReasonPhrase());

        if (instance != null) {
            try {
                problemDetail.setInstance(URI.create(instance));
            } catch (IllegalArgumentException ignored) {
                // Keep instance unset when the provided value is malformed.
            }
        }

        if (errorCode != null) {
            problemDetail.setProperty(ExceptionConstant.PROPERTY_ERROR_CODE, errorCode);
        }

        if (getCause() != null) {
            problemDetail.setProperty(
                ExceptionConstant.PROPERTY_CAUSE_TYPE, getCause().getClass().getSimpleName());
        }

        customProperties.forEach(problemDetail::setProperty);

        return problemDetail;
    }

}
