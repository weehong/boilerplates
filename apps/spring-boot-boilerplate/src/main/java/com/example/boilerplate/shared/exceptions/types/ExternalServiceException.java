package com.example.boilerplate.shared.exceptions.types;

import org.springframework.http.HttpStatus;

public class ExternalServiceException extends BaseException {

    private static final String ERROR_CODE = "EXTERNAL_SERVICE_ERROR";
    private static final String PROPERTY_HTTP_STATUS_CODE = "httpStatusCode";
    private static final String PROPERTY_RESPONSE_BODY = "responseBody";
    private static final int MAX_RESPONSE_BODY_LENGTH = 10_000;
    private static final String TRUNCATION_SUFFIX = "\n\n--- RESPONSE TRUNCATED (exceeded 10KB limit) ---";

    public ExternalServiceException(
        String message,
        Throwable cause,
        Integer statusCode,
        String responseBody) {
        super(message, mapStatusCode(statusCode), ERROR_CODE);

        if (cause != null) {
            initCause(cause);
        }

        if (statusCode != null) {
            addProperty(PROPERTY_HTTP_STATUS_CODE, statusCode);
        }

        if (responseBody != null && !responseBody.isEmpty()) {
            addProperty(PROPERTY_RESPONSE_BODY, truncateResponseBody(responseBody));
        }
    }

    private static String truncateResponseBody(String responseBody) {
        if (responseBody.length() > MAX_RESPONSE_BODY_LENGTH) {
            return responseBody.substring(0, MAX_RESPONSE_BODY_LENGTH) + TRUNCATION_SUFFIX;
        }

        return responseBody;
    }

    private static HttpStatus mapStatusCode(Integer statusCode) {
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }

        HttpStatus resolved = HttpStatus.resolve(statusCode);

        return resolved != null
               ? resolved
               : HttpStatus.INTERNAL_SERVER_ERROR;
    }

}
