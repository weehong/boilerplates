package com.example.boilerplate.shared.exceptions.types;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class ExternalServiceExceptionTest {

    private static final int INVALID_HTTP_STATUS = 999;
    private static final int OVERSIZED_BODY_LENGTH = 15_000;
    private static final int MAX_RESPONSE_BODY_LENGTH = 10_000;

    // ===== mapStatusCode =====

    @Test
    void given_nullStatusCode_when_created_then_statusIs500() {
        ExternalServiceException ex =
            new ExternalServiceException(
                "error",
                null,
                null,
                null);

        assertThat(ex.getHttpStatus())
            .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void given_validStatusCode_when_created_then_statusMapped() {
        ExternalServiceException ex =
            new ExternalServiceException(
                "not found",
                null,
                HttpStatus.NOT_FOUND.value(),
                null);

        assertThat(ex.getHttpStatus())
            .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void given_invalidStatusCode_when_created_then_fallsBackTo500() {
        ExternalServiceException ex =
            new ExternalServiceException(
                "error",
                null,
                INVALID_HTTP_STATUS,
                null);

        assertThat(ex.getHttpStatus())
            .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // ===== Cause =====

    @Test
    void given_cause_when_created_then_causeSetAndPropertyAdded() {
        RuntimeException cause = new RuntimeException("root");
        ExternalServiceException ex =
            new ExternalServiceException(
                "error",
                cause,
                HttpStatus.BAD_GATEWAY.value(),
                null);

        assertThat(ex.getCause()).isSameAs(cause);
        assertThat(ex.toProblemDetail("/test").getProperties())
            .containsEntry(
                "causeType", "RuntimeException");
    }

    @Test
    void given_nullCause_when_created_then_noCauseProperty() {
        ExternalServiceException ex =
            new ExternalServiceException(
                "error",
                null,
                HttpStatus.BAD_GATEWAY.value(),
                null);

        assertThat(ex.getCause()).isNull();
        assertThat(ex.getCustomProperties())
            .doesNotContainKey("causeType");
    }

    // ===== Status code property =====

    @Test
    void given_statusCode_when_created_then_statusCodePropertyAdded() {
        ExternalServiceException ex =
            new ExternalServiceException(
                "error",
                null,
                HttpStatus.NOT_FOUND.value(),
                null);

        assertThat(ex.getCustomProperties())
            .containsEntry(
                "httpStatusCode",
                HttpStatus.NOT_FOUND.value());
    }

    @Test
    void given_nullStatusCode_when_created_then_noStatusCodeProperty() {
        ExternalServiceException ex =
            new ExternalServiceException(
                "error",
                null,
                null,
                null);

        assertThat(ex.getCustomProperties())
            .doesNotContainKey("httpStatusCode");
    }

    // ===== Response body =====

    @Test
    void given_responseBody_when_created_then_bodyPropertyAdded() {
        String body = "{\"error\": \"bad input\"}";
        ExternalServiceException ex =
            new ExternalServiceException(
                "error",
                null,
                HttpStatus.BAD_REQUEST.value(),
                body);

        assertThat(ex.getCustomProperties())
            .containsEntry("responseBody", body);
    }

    @Test
    void given_emptyResponseBody_when_created_then_noBodyProperty() {
        ExternalServiceException ex =
            new ExternalServiceException(
                "error",
                null,
                HttpStatus.BAD_REQUEST.value(),
                "");

        assertThat(ex.getCustomProperties())
            .doesNotContainKey("responseBody");
    }

    @Test
    void given_nullResponseBody_when_created_then_noBodyProperty() {
        ExternalServiceException ex =
            new ExternalServiceException(
                "error",
                null,
                HttpStatus.BAD_REQUEST.value(),
                null);

        assertThat(ex.getCustomProperties())
            .doesNotContainKey("responseBody");
    }

    @Test
    void given_oversizedResponseBody_when_created_then_bodyTruncated() {
        String oversizedBody = "x".repeat(OVERSIZED_BODY_LENGTH);
        ExternalServiceException ex =
            new ExternalServiceException(
                "error",
                null,
                HttpStatus.BAD_REQUEST.value(),
                oversizedBody);

        assertThat(ex.getCustomProperties().get("responseBody"))
            .asString()
            .hasSizeLessThan(oversizedBody.length())
            .endsWith(
                "--- RESPONSE TRUNCATED (exceeded 10KB limit) ---");
    }

    @Test
    void given_exactlyMaxResponseBody_when_created_then_bodyNotTruncated() {
        String exactBody = "x".repeat(MAX_RESPONSE_BODY_LENGTH);
        ExternalServiceException ex =
            new ExternalServiceException(
                "error",
                null,
                HttpStatus.BAD_REQUEST.value(),
                exactBody);

        assertThat(ex.getCustomProperties())
            .containsEntry("responseBody", exactBody);
    }

    // ===== Error code =====

    @Test
    void given_anyParams_when_created_then_errorCodeIsExternalService() {
        ExternalServiceException ex =
            new ExternalServiceException(
                "error",
                null,
                null,
                null);

        assertThat(ex.getErrorCode())
            .isEqualTo("EXTERNAL_SERVICE_ERROR");
    }

}
