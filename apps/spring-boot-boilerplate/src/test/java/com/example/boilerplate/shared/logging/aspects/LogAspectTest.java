package com.example.boilerplate.shared.logging.aspects;

import com.example.boilerplate.shared.logging.exceptions.MethodExecutionException;
import com.example.boilerplate.shared.logging.properties.LogAspectProperties;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.RestController;

import static com.example.boilerplate.shared.logging.constants.AspectConstant.MDC_METHOD;
import static com.example.boilerplate.shared.logging.constants.AspectConstant.MDC_REQUEST_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LogAspectTest {

    private static final String TEST_METHOD_SIGNATURE = "TestService.doWork()";
    private static final String TEST_RESULT = "result";
    private static final Long SLOW_THRESHOLD_MS = 1000L;

    @Mock
    private MeterRegistry meterRegistry;
    @Mock
    private LogAspectProperties logAspectProperties;
    @Mock
    private ProceedingJoinPoint joinPoint;
    @Mock
    private Signature signature;
    @Mock
    private Timer timer;

    private LogAspect logAspect;

    @BeforeEach
    void setUp() {
        logAspect = new LogAspect(meterRegistry, logAspectProperties);
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    // ===== logExecution =====

    @Test
    void given_successfulExecution_when_logExecution_then_mdcIsClearedAfter()
        throws Throwable {
        configureJoinPoint(ServiceClass.class);
        when(joinPoint.proceed()).thenReturn(TEST_RESULT);
        when(logAspectProperties.slowExecutionThresholdMs()).thenReturn(SLOW_THRESHOLD_MS);
        configureMeterRegistry();

        logAspect.logExecution(joinPoint);

        assertThat(MDC.get(MDC_REQUEST_ID)).isNull();
        assertThat(MDC.get(MDC_METHOD)).isNull();
    }

    @Test
    void given_successfulExecution_when_logExecution_then_resultIsReturned()
        throws Throwable {
        configureJoinPoint(ServiceClass.class);
        when(joinPoint.proceed()).thenReturn(TEST_RESULT);
        when(logAspectProperties.slowExecutionThresholdMs()).thenReturn(SLOW_THRESHOLD_MS);
        configureMeterRegistry();

        Object result = logAspect.logExecution(joinPoint);

        assertThat(result).isEqualTo(TEST_RESULT);
    }

    @Test
    void given_successfulExecution_when_logExecution_then_metricsAreRecorded()
        throws Throwable {
        configureJoinPoint(ServiceClass.class);
        when(joinPoint.proceed()).thenReturn(TEST_RESULT);
        when(logAspectProperties.slowExecutionThresholdMs()).thenReturn(SLOW_THRESHOLD_MS);
        configureMeterRegistry();

        logAspect.logExecution(joinPoint);

        verify(meterRegistry).timer(
            anyString(),
            anyString(),
            anyString(),
            anyString(),
            anyString());
        verify(timer).record(anyLong(), any());
    }

    @Test
    void given_runtimeException_when_logExecution_then_exceptionIsRethrown()
        throws Throwable {
        configureJoinPoint(ServiceClass.class);
        RuntimeException exception = new RuntimeException("test error");
        when(joinPoint.proceed()).thenThrow(exception);
        configureMeterRegistry();

        assertThatThrownBy(() -> logAspect.logExecution(joinPoint))
            .isSameAs(exception);
    }

    @Test
    void given_runtimeException_when_logExecution_then_mdcIsClearedAfter()
        throws Throwable {
        configureJoinPoint(ServiceClass.class);
        when(joinPoint.proceed()).thenThrow(new RuntimeException("test error"));
        configureMeterRegistry();

        try {
            logAspect.logExecution(joinPoint);
        } catch (RuntimeException ignored) {
        }

        assertThat(MDC.get(MDC_REQUEST_ID)).isNull();
        assertThat(MDC.get(MDC_METHOD)).isNull();
    }

    @Test
    void given_error_when_logExecution_then_errorIsRethrown() throws Throwable {
        configureJoinPoint(ServiceClass.class);
        Error error = new StackOverflowError("test error");
        when(joinPoint.proceed()).thenThrow(error);
        configureMeterRegistry();

        assertThatThrownBy(() -> logAspect.logExecution(joinPoint))
            .isSameAs(error);
    }

    @Test
    void given_checkedException_when_logExecution_then_wrappedInMethodExecutionException()
        throws Throwable {
        configureJoinPoint(ServiceClass.class);
        Exception checkedException = new Exception("checked");
        when(joinPoint.proceed()).thenThrow(checkedException);
        configureMeterRegistry();

        assertThatThrownBy(() -> logAspect.logExecution(joinPoint))
            .isInstanceOf(MethodExecutionException.class)
            .hasCause(checkedException);
    }

    @Test
    void given_existingRequestId_when_logExecution_then_requestIdIsPreserved()
        throws Throwable {
        String existingRequestId = "EXISTING1";
        MDC.put(MDC_REQUEST_ID, existingRequestId);
        configureJoinPoint(ServiceClass.class);
        when(joinPoint.proceed()).thenReturn(TEST_RESULT);
        when(logAspectProperties.slowExecutionThresholdMs()).thenReturn(SLOW_THRESHOLD_MS);
        configureMeterRegistry();

        logAspect.logExecution(joinPoint);

        assertThat(MDC.get(MDC_REQUEST_ID)).isEqualTo(existingRequestId);
    }

    @Test
    void given_controllerClass_when_logExecution_then_metricsTaggedAsController()
        throws Throwable {
        configureJoinPoint(ControllerClass.class);
        when(joinPoint.proceed()).thenReturn(TEST_RESULT);
        when(logAspectProperties.slowExecutionThresholdMs()).thenReturn(SLOW_THRESHOLD_MS);
        configureMeterRegistry();

        logAspect.logExecution(joinPoint);

        verify(meterRegistry).timer(
            anyString(),
            anyString(),
            anyString(),
            anyString(),
            anyString());
    }

    private void configureJoinPoint(Class<?> declaringType) {
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.toShortString()).thenReturn(TEST_METHOD_SIGNATURE);
        when(signature.getDeclaringType()).thenReturn(declaringType);
    }

    private void configureMeterRegistry() {
        when(meterRegistry.timer(
            anyString(),
            anyString(),
            anyString(),
            anyString(),
            anyString()))
            .thenReturn(timer);
    }

    private static class ServiceClass {
    }

    @RestController
    private static class ControllerClass {
    }

}
