package com.example.boilerplate.shared.exceptions.types;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BaseExceptionTest {

    // ===== toProblemDetail =====

    @Test
    void given_exception_when_toProblemDetail_then_allFieldsPopulated() {
        ResourceNotFoundException ex =
            new ResourceNotFoundException("User not found");

        ProblemDetail pd = ex.toProblemDetail("/api/users/1");

        assertThat(pd.getStatus())
            .isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(pd.getDetail()).isEqualTo("User not found");
        assertThat(pd.getType().toString())
            .isEqualTo("about:blank");
        assertThat(pd.getTitle())
            .isEqualTo(HttpStatus.NOT_FOUND.getReasonPhrase());
        assertThat(pd.getInstance().toString())
            .isEqualTo("/api/users/1");
        assertThat(pd.getProperties())
            .containsEntry("errorCode", "RESOURCE_NOT_FOUND");
    }

    @Test
    void given_nullInstance_when_toProblemDetail_then_instanceNotSet() {
        ResourceNotFoundException ex =
            new ResourceNotFoundException("not found");

        ProblemDetail pd = ex.toProblemDetail(null);

        assertThat(pd.getInstance()).isNull();
    }

    @Test
    void given_malformedInstance_when_toProblemDetail_then_instanceNotSet() {
        ResourceNotFoundException ex =
            new ResourceNotFoundException("not found");

        ProblemDetail pd = ex.toProblemDetail("bad uri with space");

        assertThat(pd.getInstance()).isNull();
    }

    @Test
    void given_exceptionWithCause_when_toProblemDetail_then_causeTypeIncluded() {
        ResourceNotFoundException ex =
            new ResourceNotFoundException("not found");
        ex.initCause(new IllegalStateException("bad state"));

        ProblemDetail pd = ex.toProblemDetail("/api/test");

        assertThat(pd.getProperties())
            .containsEntry(
                "causeType", "IllegalStateException");
    }

    @Test
    void given_noCause_when_toProblemDetail_then_causeTypeAbsent() {
        ResourceNotFoundException ex =
            new ResourceNotFoundException("not found");

        ProblemDetail pd = ex.toProblemDetail("/api/test");

        assertThat(pd.getProperties())
            .doesNotContainKey("causeType");
    }

    @Test
    void given_customProperties_when_toProblemDetail_then_allIncluded() {
        ResourceNotFoundException ex =
            new ResourceNotFoundException("not found");
        ex.addProperty("entityId", "123")
          .addProperty("entityType", "User");

        ProblemDetail pd = ex.toProblemDetail("/api/test");

        assertThat(pd.getProperties())
            .containsEntry("entityId", "123")
            .containsEntry("entityType", "User")
            .containsEntry("errorCode", "RESOURCE_NOT_FOUND");
    }

    // ===== addProperty =====

    @Test
    void given_addProperty_when_called_then_returnsSameInstance() {
        ResourceNotFoundException ex =
            new ResourceNotFoundException("not found");

        BaseException result = ex.addProperty("key", "value");

        assertThat(result).isSameAs(ex);
    }

    @Test
    void given_reservedPropertyKey_when_addProperty_then_throwIllegalArgument() {
        ResourceNotFoundException ex =
            new ResourceNotFoundException("not found");

        assertThatThrownBy(() -> ex.addProperty("errorCode", "custom"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Reserved property key");
    }

    // ===== Construction =====

    @Test
    void given_resourceNotFound_when_created_then_correctStatusAndCode() {
        ResourceNotFoundException ex =
            new ResourceNotFoundException("test message");

        assertThat(ex.getHttpStatus())
            .isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(ex.getErrorCode())
            .isEqualTo("RESOURCE_NOT_FOUND");
        assertThat(ex.getMessage()).isEqualTo("test message");
        assertThat(ex.getCustomProperties()).isEmpty();
    }

}
