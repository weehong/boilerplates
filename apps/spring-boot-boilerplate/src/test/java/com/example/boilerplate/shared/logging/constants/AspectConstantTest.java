package com.example.boilerplate.shared.logging.constants;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AspectConstantTest {

    // ===== constructor =====

    @Test
    void given_privateConstructor_when_invoked_then_throwsAssertionError()
        throws NoSuchMethodException {
        Constructor<AspectConstant> constructor =
            AspectConstant.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertThatThrownBy(constructor::newInstance)
            .isInstanceOf(InvocationTargetException.class)
            .cause()
            .isInstanceOf(AssertionError.class);
    }

    @Test
    void given_class_when_checkingInstantiation_then_constructorIsPrivate()
        throws NoSuchMethodException {
        Constructor<AspectConstant> constructor =
            AspectConstant.class.getDeclaredConstructor();

        assertThat(constructor.canAccess(null)).isFalse();
    }

}
