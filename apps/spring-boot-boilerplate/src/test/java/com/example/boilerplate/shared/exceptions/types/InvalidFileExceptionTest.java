package com.example.boilerplate.shared.exceptions.types;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class InvalidFileExceptionTest {

    private static final String TEST_MESSAGE = "Failed to parse venues JSON file.";

    // ===== Construction =====

    @Test
    void given_messageAndCause_when_constructed_then_messageIsSet() {
        InvalidFileException exception =
            new InvalidFileException(
                TEST_MESSAGE, new IOException("disk full"));

        assertThat(exception.getMessage())
            .isEqualTo(TEST_MESSAGE);
    }

    @Test
    void given_messageAndCause_when_constructed_then_causeIsSet() {
        IOException cause = new IOException("disk full");

        InvalidFileException exception =
            new InvalidFileException(TEST_MESSAGE, cause);

        assertThat(exception.getCause()).isSameAs(cause);
    }

    @Test
    void given_nullCause_when_constructed_then_causeIsNull() {
        InvalidFileException exception =
            new InvalidFileException(TEST_MESSAGE, null);

        assertThat(exception.getCause()).isNull();
    }

    // ===== Inheritance =====

    @Test
    void given_exception_when_isRuntimeException_then_true() {
        InvalidFileException exception =
            new InvalidFileException(TEST_MESSAGE, null);

        assertThat(exception)
            .isInstanceOf(RuntimeException.class);
    }

}
