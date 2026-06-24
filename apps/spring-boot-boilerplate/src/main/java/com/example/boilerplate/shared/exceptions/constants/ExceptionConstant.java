package com.example.boilerplate.shared.exceptions.constants;

public final class ExceptionConstant {

    public static final String ABOUT_BLANK = "about:blank";
    public static final String BAD_REQUEST_TITLE = "Bad Request";
    public static final String VALIDATION_FAILED = "Validation failed";
    public static final String DEFAULT_FIELD_ERROR_MESSAGE = "Invalid value";
    public static final String PROPERTY_ERRORS = "errors";
    public static final String PROPERTY_FIELD = "field";
    public static final String PROPERTY_MESSAGE = "message";
    public static final String PROPERTY_ERROR_CODE = "errorCode";
    public static final String PROPERTY_CAUSE_TYPE = "causeType";
    public static final String DOT = ".";

    private ExceptionConstant() {
        throw new AssertionError();
    }

}
