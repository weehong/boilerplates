package com.example.boilerplate.shared.constants;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ServiceOperationExecutorConstantTest {

    private static final int EXPECTED_MAX_RESPONSE_BODY_SIZE = 10_000;

    // ===== Constants =====

    @Test
    void given_maxResponseBodySize_when_accessed_then_returnsExpectedValue() {
        assertThat(ServiceOperationExecutorConstant.MAX_RESPONSE_BODY_SIZE)
            .isEqualTo(EXPECTED_MAX_RESPONSE_BODY_SIZE);
    }

    @Test
    void given_mdcRequestIdKey_when_accessed_then_returnsRequestId() {
        assertThat(ServiceOperationExecutorConstant.MDC_REQUEST_ID_KEY)
            .isEqualTo("requestId");
    }

    @Test
    void given_truncationIndicator_when_accessed_then_isNotBlank() {
        assertThat(ServiceOperationExecutorConstant.TRUNCATION_INDICATOR)
            .isNotBlank();
    }

    @Test
    void given_logConstants_when_accessed_then_containPlaceholders() {
        assertThat(ServiceOperationExecutorConstant.LOG_EXECUTING_OPERATION)
            .contains("{}");
        assertThat(ServiceOperationExecutorConstant.LOG_OPERATION_COMPLETED)
            .contains("{}");
        assertThat(ServiceOperationExecutorConstant.LOG_ILLEGAL_ARGUMENT)
            .contains("{}");
        assertThat(ServiceOperationExecutorConstant.LOG_DATABASE_EXCEPTION)
            .contains("{}");
        assertThat(ServiceOperationExecutorConstant.LOG_REST_CLIENT_EXCEPTION)
            .contains("{}");
        assertThat(ServiceOperationExecutorConstant.LOG_INVALID_FILE)
            .contains("{}");
        assertThat(ServiceOperationExecutorConstant.LOG_RESOURCE_NOT_FOUND)
            .contains("{}");
        assertThat(ServiceOperationExecutorConstant.LOG_PROGRAMMER_ERROR)
            .contains("{}");
        assertThat(ServiceOperationExecutorConstant.LOG_UNEXPECTED_EXCEPTION)
            .contains("{}");
    }

    @Test
    void given_errorMessageConstants_when_accessed_then_areNotBlank() {
        assertThat(ServiceOperationExecutorConstant.UNEXPECTED_ERROR_DURING)
            .isNotBlank();
        assertThat(ServiceOperationExecutorConstant.CONCURRENT_MODIFICATION_DETECTED)
            .isNotBlank();
        assertThat(ServiceOperationExecutorConstant.DATABASE_DEADLOCK_DETECTED)
            .isNotBlank();
        assertThat(ServiceOperationExecutorConstant.CANNOT_ACQUIRE_DATABASE_LOCK)
            .isNotBlank();
        assertThat(ServiceOperationExecutorConstant.ENTITY_ALREADY_EXISTS)
            .isNotBlank();
        assertThat(ServiceOperationExecutorConstant.ENTITY_NOT_FOUND_MESSAGE)
            .isNotBlank();
    }

    @Test
    void given_formatConstants_when_accessed_then_containFormatSpecifiers() {
        assertThat(ServiceOperationExecutorConstant.EXTERNAL_SERVICE_ERROR_FORMAT)
            .contains("%s");
        assertThat(ServiceOperationExecutorConstant.EXTERNAL_SERVICE_UNAVAILABLE_FORMAT)
            .contains("%s");
    }

}
