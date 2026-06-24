package com.example.boilerplate.shared.logging.exceptions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MethodExecutionExceptionTest {

    private static final String TEST_METHOD_NAME = "processPayment";
    private static final long TEST_EXECUTION_TIME_MS = 1500L;
    private static final long ZERO_EXECUTION_TIME_MS = 0L;
    private static final String EXPECTED_MESSAGE = "Execution failed for method processPayment"
                                                   + " after 1500 ms";

    // ===== constructor =====

    @Test
    void given_methodNameAndTime_when_constructed_then_messageIsFormatted() {
        MethodExecutionException exception =
            new MethodExecutionException(
                TEST_METHOD_NAME,
                TEST_EXECUTION_TIME_MS,
                new RuntimeException("timeout"));

        assertThat(exception.getMessage())
            .isEqualTo(EXPECTED_MESSAGE);
    }

    @Test
    void given_cause_when_constructed_then_causeIsSet() {
        RuntimeException cause =
            new RuntimeException("timeout");

        MethodExecutionException exception =
            new MethodExecutionException(
                TEST_METHOD_NAME,
                TEST_EXECUTION_TIME_MS,
                cause);

        assertThat(exception.getCause()).isSameAs(cause);
    }

    @Test
    void given_nullCause_when_constructed_then_causeIsNull() {
        MethodExecutionException exception =
            new MethodExecutionException(
                TEST_METHOD_NAME,
                TEST_EXECUTION_TIME_MS,
                null);

        assertThat(exception.getCause()).isNull();
    }

    @Test
    void given_zeroExecutionTime_when_constructed_then_messageContainsZero() {
        MethodExecutionException exception =
            new MethodExecutionException(
                TEST_METHOD_NAME,
                ZERO_EXECUTION_TIME_MS,
                null);

        assertThat(exception.getMessage())
            .contains("after 0 ms");
    }

    @Test
    void given_exception_when_isRuntimeException_then_true() {
        MethodExecutionException exception =
            new MethodExecutionException(
                TEST_METHOD_NAME,
                TEST_EXECUTION_TIME_MS,
                null);

        assertThat(exception)
            .isInstanceOf(RuntimeException.class);
    }

}
