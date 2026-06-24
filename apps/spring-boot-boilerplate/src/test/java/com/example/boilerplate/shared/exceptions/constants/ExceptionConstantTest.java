package com.example.boilerplate.shared.exceptions.constants;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExceptionConstantTest {

    // ===== Constants =====

    @Test
    void given_aboutBlank_when_accessed_then_returnsExpectedValue() {
        assertThat(ExceptionConstant.ABOUT_BLANK)
            .isEqualTo("about:blank");
    }

    @Test
    void given_badRequestTitle_when_accessed_then_returnsExpectedValue() {
        assertThat(ExceptionConstant.BAD_REQUEST_TITLE)
            .isEqualTo("Bad Request");
    }

    @Test
    void given_validationFailed_when_accessed_then_returnsExpectedValue() {
        assertThat(ExceptionConstant.VALIDATION_FAILED)
            .isEqualTo("Validation failed");
    }

    @Test
    void given_propertyKeys_when_accessed_then_areNotBlank() {
        assertThat(ExceptionConstant.PROPERTY_ERRORS).isNotBlank();
        assertThat(ExceptionConstant.PROPERTY_FIELD).isNotBlank();
        assertThat(ExceptionConstant.PROPERTY_MESSAGE).isNotBlank();
        assertThat(ExceptionConstant.PROPERTY_ERROR_CODE).isNotBlank();
        assertThat(ExceptionConstant.PROPERTY_CAUSE_TYPE).isNotBlank();
        assertThat(ExceptionConstant.DOT).isNotBlank();
    }

    // ===== Construction =====

    @Test
    void given_privateConstructor_when_invoked_then_throwsAssertionError()
        throws NoSuchMethodException {
        Constructor<ExceptionConstant> constructor =
            ExceptionConstant.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertThatThrownBy(constructor::newInstance)
            .isInstanceOf(InvocationTargetException.class)
            .hasCauseInstanceOf(AssertionError.class);
    }

}
