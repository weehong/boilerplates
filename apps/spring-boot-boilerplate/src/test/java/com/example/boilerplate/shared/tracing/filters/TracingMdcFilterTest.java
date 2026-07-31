package com.example.boilerplate.shared.tracing.filters;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.micrometer.tracing.test.autoconfigure.AutoConfigureTracing;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static com.example.boilerplate.shared.tracing.filters.TracingMdcFilter.TRACE_ID_HEADER;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTracing(export = false)
class TracingMdcFilterTest {

    private static final String PUBLIC_HEALTH_ENDPOINT = "/api/v1/public/health-checks";
    private static final String PROTECTED_ENDPOINT = "/api/v1/private/missing";
    private static final String TRACEPARENT_HEADER = "traceparent";
    private static final String TRACE_ID_PATTERN = "[0-9a-f]{32}";
    private static final String INBOUND_TRACE_ID = "0af7651916cd43dd8448eb211c80319c";
    private static final String INBOUND_TRACEPARENT =
        "00-" + INBOUND_TRACE_ID + "-b7ad6b7169203331-01";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void given_requestWithoutTraceContext_when_requested_then_w3cTraceIdIsReturned() throws Exception {
        mockMvc.perform(get(PUBLIC_HEALTH_ENDPOINT))
            .andExpect(status().isOk())
            .andExpect(header().string(TRACE_ID_HEADER, matchesPattern(TRACE_ID_PATTERN)));
    }

    @Test
    void given_inboundW3cTraceContext_when_requested_then_traceIdIsContinued() throws Exception {
        mockMvc.perform(get(PUBLIC_HEALTH_ENDPOINT)
                            .header(TRACEPARENT_HEADER, INBOUND_TRACEPARENT))
            .andExpect(status().isOk())
            .andExpect(header().string(TRACE_ID_HEADER, INBOUND_TRACE_ID));
    }

    @Test
    void given_frameworkLevelAuthenticationFailure_when_requested_then_traceIdIsReturned() throws Exception {
        mockMvc.perform(get(PROTECTED_ENDPOINT))
            .andExpect(status().isForbidden())
            .andExpect(header().string(TRACE_ID_HEADER, matchesPattern(TRACE_ID_PATTERN)));
    }

}
