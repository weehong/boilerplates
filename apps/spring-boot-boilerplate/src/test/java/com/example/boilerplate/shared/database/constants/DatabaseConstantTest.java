package com.example.boilerplate.shared.database.constants;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DatabaseConstantTest {

    // ===== constructor =====

    @Test
    void given_privateConstructor_when_invoked_then_throwsAssertionError()
        throws NoSuchMethodException {
        Constructor<DatabaseConstant> constructor =
            DatabaseConstant.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertThatThrownBy(constructor::newInstance)
            .isInstanceOf(InvocationTargetException.class)
            .cause()
            .isInstanceOf(AssertionError.class);
    }

    @Test
    void given_class_when_checkingInstantiation_then_constructorIsPrivate()
        throws NoSuchMethodException {
        Constructor<DatabaseConstant> constructor =
            DatabaseConstant.class.getDeclaredConstructor();

        assertThat(constructor.canAccess(null)).isFalse();
    }

}
