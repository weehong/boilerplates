package com.example.boilerplate.shared.exceptions.types;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceNotFoundExceptionTest {

    private static final String ERROR_CODE = "RESOURCE_NOT_FOUND";
    private static final String TEST_MESSAGE = "User not found";
    private static final String TEST_INSTANCE = "/api/users/1";

    // ===== Construction =====

    @Test
    void given_message_when_constructed_then_fieldsAreCorrect() {
        ResourceNotFoundException exception =
            new ResourceNotFoundException(TEST_MESSAGE);

        assertThat(exception.getMessage())
            .isEqualTo(TEST_MESSAGE);
        assertThat(exception.getHttpStatus())
            .isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exception.getErrorCode())
            .isEqualTo(ERROR_CODE);
        assertThat(exception.getCause()).isNull();
    }

    // ===== ProblemDetail =====

    @Test
    void given_message_when_toProblemDetail_then_returns404WithErrorCode() {
        ResourceNotFoundException exception =
            new ResourceNotFoundException(TEST_MESSAGE);

        ProblemDetail problemDetail =
            exception.toProblemDetail(TEST_INSTANCE);

        assertThat(problemDetail.getStatus())
            .isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(problemDetail.getDetail())
            .isEqualTo(TEST_MESSAGE);
        assertThat(problemDetail.getTitle())
            .isEqualTo(
                HttpStatus.NOT_FOUND.getReasonPhrase());
        assertThat(problemDetail.getProperties())
            .containsEntry("errorCode", ERROR_CODE);
        assertThat(problemDetail.getInstance().toString())
            .isEqualTo(TEST_INSTANCE);
    }

    @Test
    void given_noCause_when_toProblemDetail_then_causeTypeAbsent() {
        ResourceNotFoundException exception =
            new ResourceNotFoundException(TEST_MESSAGE);

        ProblemDetail problemDetail =
            exception.toProblemDetail(TEST_INSTANCE);

        assertThat(problemDetail.getProperties())
            .doesNotContainKey("causeType");
    }

    @Test
    void given_customProperties_when_toProblemDetail_then_propertiesIncluded() {
        ResourceNotFoundException exception =
            new ResourceNotFoundException(TEST_MESSAGE);
        exception.addProperty("entityId", "123")
                 .addProperty("entityType", "User");

        ProblemDetail problemDetail =
            exception.toProblemDetail(TEST_INSTANCE);

        assertThat(problemDetail.getProperties())
            .containsEntry("entityId", "123")
            .containsEntry("entityType", "User")
            .containsEntry("errorCode", ERROR_CODE);
    }

    // ===== Inheritance =====

    @Test
    void given_exception_when_isRuntimeException_then_true() {
        ResourceNotFoundException exception =
            new ResourceNotFoundException(TEST_MESSAGE);

        assertThat(exception)
            .isInstanceOf(RuntimeException.class);
    }

}
