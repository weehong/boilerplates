package com.example.boilerplate.shared.logging.aspects;

import com.example.boilerplate.shared.logging.exceptions.MethodExecutionException;
import com.example.boilerplate.shared.logging.properties.LogAspectProperties;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.concurrent.TimeUnit;

import static com.example.boilerplate.shared.logging.constants.AspectConstant.COMPONENT_CONTROLLER;
import static com.example.boilerplate.shared.logging.constants.AspectConstant.COMPONENT_SERVICE;
import static com.example.boilerplate.shared.logging.constants.AspectConstant.LOG_METHOD_FAILURE;
import static com.example.boilerplate.shared.logging.constants.AspectConstant.LOG_METHOD_SLOW;
import static com.example.boilerplate.shared.logging.constants.AspectConstant.LOG_METHOD_SUCCESS;
import static com.example.boilerplate.shared.logging.constants.AspectConstant.MDC_ENDPOINT;
import static com.example.boilerplate.shared.logging.constants.AspectConstant.MDC_HTTP_METHOD;
import static com.example.boilerplate.shared.logging.constants.AspectConstant.MDC_METHOD;
import static com.example.boilerplate.shared.logging.constants.AspectConstant.MDC_REQUEST_ID;
import static com.example.boilerplate.shared.logging.constants.AspectConstant.METRIC_NAME;
import static com.example.boilerplate.shared.logging.constants.AspectConstant.METRIC_TAG_COMPONENT;
import static com.example.boilerplate.shared.logging.constants.AspectConstant.REQUEST_ID_SEPARATOR;
import static com.example.boilerplate.shared.logging.constants.AspectConstant.STATUS_FAILURE;
import static com.example.boilerplate.shared.logging.constants.AspectConstant.STATUS_SUCCESS;
import static com.example.boilerplate.shared.logging.constants.AspectConstant.STATUS_TAG;
import static java.util.UUID.randomUUID;

@Aspect
@Component
@EnableConfigurationProperties(LogAspectProperties.class)
@ConditionalOnProperty(prefix = "app.logging.aspect", name = "enabled", havingValue = "true", matchIfMissing = true)
public class LogAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogAspect.class);
    private static final int REQUEST_ID_LENGTH = 8;

    private final MeterRegistry meterRegistry;
    private final LogAspectProperties logAspectProperties;

    public LogAspect(MeterRegistry meterRegistry, LogAspectProperties logAspectProperties) {
        this.meterRegistry = meterRegistry;
        this.logAspectProperties = logAspectProperties;
    }

    @Pointcut("execution(public * com.example.boilerplate..services..*(..))")
    public void serviceMethods() {
    }

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *) || "
              + "within(@org.springframework.stereotype.Controller *)")
    public void controllerMethods() {
    }

    @Around("serviceMethods() || controllerMethods()")
    public Object logExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.nanoTime();
        String method = joinPoint.getSignature().toShortString();
        String component = resolveComponent(joinPoint);
        boolean isNewRequestId = MDC.get(MDC_REQUEST_ID) == null;

        if (isNewRequestId) {
            MDC.put(MDC_REQUEST_ID, initRequestId());
        }

        MDC.put(MDC_METHOD, method);

        HttpServletRequest request = currentHttpRequest();
        boolean hasHttpContext = false;

        if (request != null) {
            MDC.put(MDC_HTTP_METHOD, request.getMethod());
            MDC.put(MDC_ENDPOINT, request.getRequestURI());
            hasHttpContext = true;
        }

        try {
            Object result = joinPoint.proceed();
            Long durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
            recordMetrics(component, durationMs, false);
            logSuccess(method, durationMs);

            return result;
        } catch (Throwable t) {
            Long durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
            recordMetrics(component, durationMs, true);
            logFailure(method, durationMs, t);

            if (t instanceof Error) {
                throw t;
            }

            if (t instanceof RuntimeException rtEx) {
                throw rtEx;
            }

            throw new MethodExecutionException(method, durationMs, t);
        } finally {
            if (isNewRequestId) {
                MDC.remove(MDC_REQUEST_ID);
            }

            MDC.remove(MDC_METHOD);

            if (hasHttpContext) {
                MDC.remove(MDC_HTTP_METHOD);
                MDC.remove(MDC_ENDPOINT);
            }
        }
    }

    private String initRequestId() {
        return randomUUID().toString().replace(REQUEST_ID_SEPARATOR, "").substring(0, REQUEST_ID_LENGTH).toUpperCase();
    }

    private HttpServletRequest currentHttpRequest() {
        try {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

            if (requestAttributes instanceof ServletRequestAttributes servletRequestAttributes) {
                return servletRequestAttributes.getRequest();
            }

            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private String resolveComponent(ProceedingJoinPoint joinPoint) {
        Class<?> declaringType = joinPoint.getSignature().getDeclaringType();

        if (declaringType.isAnnotationPresent(RestController.class)
            || declaringType.isAnnotationPresent(Controller.class)) {
            return COMPONENT_CONTROLLER;
        }

        return COMPONENT_SERVICE;
    }

    private void recordMetrics(String component, Long durationMs, boolean failed) {
        String status = failed
                        ? STATUS_FAILURE
                        : STATUS_SUCCESS;
        meterRegistry.timer(
                         METRIC_NAME,
                         METRIC_TAG_COMPONENT,
                         component,
                         STATUS_TAG,
                         status)
                     .record(durationMs, TimeUnit.MILLISECONDS);
    }

    private void logSuccess(String method, Long durationMs) {
        if (durationMs >= logAspectProperties.slowExecutionThresholdMs()) {
            LOGGER.warn(LOG_METHOD_SLOW, method, durationMs);
        } else {
            LOGGER.info(LOG_METHOD_SUCCESS, method, durationMs);
        }
    }

    private void logFailure(String method, Long durationMs, Throwable t) {
        LOGGER.error(
            LOG_METHOD_FAILURE,
            method,
            durationMs,
            t.getClass().getSimpleName(),
            t.getMessage());
    }

}
