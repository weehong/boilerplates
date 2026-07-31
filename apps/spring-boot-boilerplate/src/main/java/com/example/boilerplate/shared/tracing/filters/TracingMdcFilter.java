package com.example.boilerplate.shared.tracing.filters;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import org.springframework.beans.factory.ObjectProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class TracingMdcFilter extends OncePerRequestFilter {

    public static final String MDC_TRACE_ID = "traceId";
    public static final String MDC_SPAN_ID = "spanId";
    public static final String TRACE_ID_HEADER = "X-Trace-Id";

    private final ObjectProvider<Tracer> tracerProvider;

    public TracingMdcFilter(ObjectProvider<Tracer> tracerProvider) {
        this.tracerProvider = tracerProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String previousTraceId = MDC.get(MDC_TRACE_ID);
        String previousSpanId = MDC.get(MDC_SPAN_ID);

        try {
            Tracer tracer = tracerProvider.getIfAvailable();
            Span currentSpan = (tracer == null) ? null : tracer.currentSpan();

            if (currentSpan != null) {
                String traceId = currentSpan.context().traceId();
                MDC.put(MDC_TRACE_ID, traceId);
                MDC.put(MDC_SPAN_ID, currentSpan.context().spanId());
                response.setHeader(TRACE_ID_HEADER, traceId);
            }

            filterChain.doFilter(request, response);
        } finally {
            restoreMdcValue(MDC_TRACE_ID, previousTraceId);
            restoreMdcValue(MDC_SPAN_ID, previousSpanId);
        }
    }

    private void restoreMdcValue(String key, String previousValue) {
        if (previousValue == null) {
            MDC.remove(key);
        } else {
            MDC.put(key, previousValue);
        }
    }

}
