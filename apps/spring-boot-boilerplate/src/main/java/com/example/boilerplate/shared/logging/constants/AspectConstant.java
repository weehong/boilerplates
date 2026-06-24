package com.example.boilerplate.shared.logging.constants;

public final class AspectConstant {

    public static final boolean DEFAULT_ENABLED = true;
    public static final long SLOW_EXECUTION_THRESHOLD_MS = 1000;
    public static final String MDC_REQUEST_ID = "requestId";
    public static final String MDC_METHOD = "method";
    public static final String MDC_HTTP_METHOD = "httpMethod";
    public static final String MDC_ENDPOINT = "endpoint";
    public static final String STATUS_TAG = "status";
    public static final String COMPONENT_CONTROLLER = "controller";
    public static final String COMPONENT_SERVICE = "service";
    public static final String METRIC_NAME = "app.method.execution";
    public static final String METRIC_TAG_COMPONENT = "component";
    public static final String STATUS_FAILURE = "failure";
    public static final String STATUS_SUCCESS = "success";
    public static final String REQUEST_ID_SEPARATOR = "-";
    public static final String LOG_METHOD_SLOW = "Method {} executed slowly in {} ms";
    public static final String LOG_METHOD_SUCCESS = "Method {} executed successfully in {} ms";
    public static final String LOG_METHOD_FAILURE = "Method {} failed in {} ms with {}: {}";

    private AspectConstant() {
        throw new AssertionError();
    }

}
