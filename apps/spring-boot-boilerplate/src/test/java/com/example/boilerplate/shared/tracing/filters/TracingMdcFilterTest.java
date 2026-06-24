package com.example.boilerplate.shared.tracing.filters;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TracingMdcFilterTest {

    private static final String TEST_TRACE_ID = "abc123def456";
    private static final String TEST_SPAN_ID = "span789";

    @Mock
    private Tracer tracer;
    @Mock
    private Span span;
    @Mock
    private TraceContext traceContext;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

    private TracingMdcFilter tracingMdcFilter;

    @BeforeEach
    void setUp() {
        tracingMdcFilter = new TracingMdcFilter(tracer);
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    // ===== doFilterInternal =====

    @Test
    void given_currentSpanExists_when_doFilterInternal_then_mdcIsPopulated()
        throws ServletException, IOException {
        when(tracer.currentSpan()).thenReturn(span);
        when(span.context()).thenReturn(traceContext);
        when(traceContext.traceId()).thenReturn(TEST_TRACE_ID);
        when(traceContext.spanId()).thenReturn(TEST_SPAN_ID);

        AtomicReference<String> traceIdDuringChain = new AtomicReference<>();
        AtomicReference<String> spanIdDuringChain = new AtomicReference<>();
        doAnswer(invocation -> {
            traceIdDuringChain.set(MDC.get(TracingMdcFilter.MDC_TRACE_ID));
            spanIdDuringChain.set(MDC.get(TracingMdcFilter.MDC_SPAN_ID));

            return null;
        }).when(filterChain).doFilter(request, response);

        tracingMdcFilter.doFilterInternal(request, response, filterChain);

        assertThat(traceIdDuringChain.get()).isEqualTo(TEST_TRACE_ID);
        assertThat(spanIdDuringChain.get()).isEqualTo(TEST_SPAN_ID);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void given_currentSpanExists_when_doFilterInternal_then_mdcIsClearedAfterChain()
        throws ServletException, IOException {
        when(tracer.currentSpan()).thenReturn(span);
        when(span.context()).thenReturn(traceContext);
        when(traceContext.traceId()).thenReturn(TEST_TRACE_ID);
        when(traceContext.spanId()).thenReturn(TEST_SPAN_ID);

        tracingMdcFilter.doFilterInternal(request, response, filterChain);

        assertThat(MDC.get(TracingMdcFilter.MDC_TRACE_ID)).isNull();
        assertThat(MDC.get(TracingMdcFilter.MDC_SPAN_ID)).isNull();
    }

    @Test
    void given_noCurrentSpan_when_doFilterInternal_then_mdcIsNotPopulated()
        throws ServletException, IOException {
        when(tracer.currentSpan()).thenReturn(null);

        tracingMdcFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(MDC.get(TracingMdcFilter.MDC_TRACE_ID)).isNull();
        assertThat(MDC.get(TracingMdcFilter.MDC_SPAN_ID)).isNull();
    }

}
