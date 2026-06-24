package com.example.boilerplate.shared.exceptions.handlers;

import com.example.boilerplate.shared.exceptions.types.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private static final int EXPECTED_ERROR_COUNT = 2;

    private final GlobalExceptionHandler handler =
        new GlobalExceptionHandler();

    @Test
    void given_baseException_when_handled_then_returnCorrectResponse() {
        ResourceNotFoundException ex =
            new ResourceNotFoundException("User not found");
        HttpServletRequest request =
            mock(HttpServletRequest.class);
        when(request.getRequestURI())
            .thenReturn("/api/users/1");

        ResponseEntity<ProblemDetail> response =
            handler.handleBaseException(ex, request);

        assertThat(response.getStatusCode())
            .isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getDetail())
            .isEqualTo("User not found");
        assertThat(response.getBody().getInstance().toString())
            .isEqualTo("/api/users/1");
    }

    @Test
    void given_validationFailure_when_handled_then_returnBadRequest() {
        MethodArgumentNotValidException ex =
            mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult =
            mock(BindingResult.class);
        FieldError fieldError = new FieldError(
            "user", "name", "must not be blank");
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(ex.getMessage())
            .thenReturn("Validation failed");
        when(bindingResult.getFieldErrors())
            .thenReturn(List.of(fieldError));
        ServletWebRequest webRequest =
            mock(ServletWebRequest.class);
        HttpServletRequest servletRequest =
            mock(HttpServletRequest.class);
        when(webRequest.getRequest())
            .thenReturn(servletRequest);
        when(servletRequest.getRequestURI())
            .thenReturn("/api/users");

        ResponseEntity<Object> response =
            handler.handleMethodArgumentNotValid(
                ex,
                HttpHeaders.EMPTY,
                HttpStatus.BAD_REQUEST,
                webRequest);

        assertThat(response.getStatusCode())
            .isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody())
            .isInstanceOfSatisfying(
                ProblemDetail.class, body -> {
                    assertThat(body.getTitle())
                        .isEqualTo("Bad Request");
                    assertThat(body.getDetail())
                        .isEqualTo(
                            "Validation failed");
                    assertThat(body.getInstance()
                                   .toString())
                        .isEqualTo("/api/users");
                    assertThat(body.getProperties())
                        .containsKey("errors");
                });
    }

    @Test
    void given_nonServletRequest_when_validationFails_then_noInstance() {
        MethodArgumentNotValidException ex =
            mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult =
            mock(BindingResult.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(ex.getMessage())
            .thenReturn("Validation failed");
        when(bindingResult.getFieldErrors())
            .thenReturn(List.of());
        WebRequest webRequest = mock(WebRequest.class);

        ResponseEntity<Object> response =
            handler.handleMethodArgumentNotValid(
                ex,
                HttpHeaders.EMPTY,
                HttpStatus.BAD_REQUEST,
                webRequest);

        assertThat(response.getBody())
            .isInstanceOfSatisfying(
                ProblemDetail.class, body ->
                    assertThat(body.getInstance())
                        .isNull());
    }

    @Test
    void given_malformedServletUri_when_validationFails_then_noInstance() {
        MethodArgumentNotValidException ex =
            mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult =
            mock(BindingResult.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(ex.getMessage())
            .thenReturn("Validation failed");
        when(bindingResult.getFieldErrors())
            .thenReturn(List.of());

        ServletWebRequest webRequest =
            mock(ServletWebRequest.class);
        HttpServletRequest servletRequest =
            mock(HttpServletRequest.class);
        when(webRequest.getRequest())
            .thenReturn(servletRequest);
        when(servletRequest.getRequestURI())
            .thenReturn("bad uri with spaces");

        ResponseEntity<Object> response =
            handler.handleMethodArgumentNotValid(
                ex,
                HttpHeaders.EMPTY,
                HttpStatus.BAD_REQUEST,
                webRequest);

        assertThat(response.getBody())
            .isInstanceOfSatisfying(
                ProblemDetail.class, body ->
                    assertThat(body.getInstance())
                        .isNull());
    }

    @Test
    void given_constraintViolation_when_handled_then_returnBadRequest() {
        ConstraintViolation<?> violation =
            mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("user.email");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage())
            .thenReturn("must be a valid email");
        ConstraintViolationException ex =
            new ConstraintViolationException(
                Set.of(violation));
        HttpServletRequest request =
            mock(HttpServletRequest.class);
        when(request.getRequestURI())
            .thenReturn("/api/users");

        ResponseEntity<ProblemDetail> response =
            handler.handleConstraintViolationException(
                ex, request);

        assertThat(response.getStatusCode())
            .isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle())
            .isEqualTo("Bad Request");
        assertThat(response.getBody().getProperties())
            .containsKey("errors");
    }

    @Test
    void given_simplePropertyPath_when_constraintViolation_then_fieldExtracted() {
        ConstraintViolation<?> violation =
            mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("name");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage())
            .thenReturn("must not be blank");
        ConstraintViolationException ex =
            new ConstraintViolationException(
                Set.of(violation));
        HttpServletRequest request =
            mock(HttpServletRequest.class);
        when(request.getRequestURI())
            .thenReturn("/api/test");

        ResponseEntity<ProblemDetail> response =
            handler.handleConstraintViolationException(
                ex, request);

        assertThat(response.getStatusCode())
            .isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getProperties())
            .extractingByKey("errors")
            .asString()
            .contains("name")
            .contains("must not be blank");
    }

    @Test
    void given_malformedUri_when_constraintViolation_then_noInstance() {
        ConstraintViolation<?> violation =
            mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("name");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage())
            .thenReturn("must not be blank");
        ConstraintViolationException ex =
            new ConstraintViolationException(
                Set.of(violation));
        HttpServletRequest request =
            mock(HttpServletRequest.class);
        when(request.getRequestURI())
            .thenReturn("bad uri with spaces");

        ResponseEntity<ProblemDetail> response =
            handler.handleConstraintViolationException(
                ex, request);

        assertThat(response.getStatusCode())
            .isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getInstance())
            .isNull();
    }

    // ===== Multiple errors =====

    @Test
    void given_multipleFieldErrors_when_validationFails_then_allErrorsPresent() {
        MethodArgumentNotValidException ex =
            mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult =
            mock(BindingResult.class);
        FieldError fieldError1 = new FieldError(
            "user", "name", "must not be blank");
        FieldError fieldError2 = new FieldError(
            "user", "email", "must be a valid email");
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(ex.getMessage())
            .thenReturn("Validation failed");
        when(bindingResult.getFieldErrors())
            .thenReturn(List.of(fieldError1, fieldError2));
        ServletWebRequest webRequest =
            mock(ServletWebRequest.class);
        HttpServletRequest servletRequest =
            mock(HttpServletRequest.class);
        when(webRequest.getRequest())
            .thenReturn(servletRequest);
        when(servletRequest.getRequestURI())
            .thenReturn("/api/users");

        ResponseEntity<Object> response =
            handler.handleMethodArgumentNotValid(
                ex,
                HttpHeaders.EMPTY,
                HttpStatus.BAD_REQUEST,
                webRequest);

        assertThat(response.getBody())
            .isInstanceOfSatisfying(
                ProblemDetail.class, body -> {
                    assertThat(body.getProperties()
                                   .get("errors")).asList()
                                                  .hasSize(EXPECTED_ERROR_COUNT)
                                                  .anySatisfy(item ->
                                                                  assertThat(item)
                                                                      .isInstanceOfSatisfying(
                                                                          Map.class, error -> {
                                                                              assertThat(
                                                                                  error.get(
                                                                                      "field"))
                                                                                  .isEqualTo(
                                                                                      "name");
                                                                              assertThat(
                                                                                  error.get(
                                                                                      "message"))
                                                                                  .isEqualTo(
                                                                                      "must not"
                                                                                      + " be"
                                                                                      + " blank");
                                                                          }))
                                                  .anySatisfy(item ->
                                                                  assertThat(item)
                                                                      .isInstanceOfSatisfying(
                                                                          Map.class, error -> {
                                                                              assertThat(
                                                                                  error.get(
                                                                                      "field"))
                                                                                  .isEqualTo(
                                                                                      "email");
                                                                              assertThat(
                                                                                  error.get(
                                                                                      "message"))
                                                                                  .isEqualTo(
                                                                                      "must be"
                                                                                      + " a valid"
                                                                                      + " email");
                                                                          }));
                });
    }

    @Test
    void given_multipleConstraintViolations_when_handled_then_allViolationsPresent() {
        ConstraintViolation<?> violation1 =
            mock(ConstraintViolation.class);
        Path path1 = mock(Path.class);
        when(path1.toString()).thenReturn("user.name");
        when(violation1.getPropertyPath()).thenReturn(path1);
        when(violation1.getMessage())
            .thenReturn("must not be blank");

        ConstraintViolation<?> violation2 =
            mock(ConstraintViolation.class);
        Path path2 = mock(Path.class);
        when(path2.toString()).thenReturn("user.email");
        when(violation2.getPropertyPath()).thenReturn(path2);
        when(violation2.getMessage())
            .thenReturn("must be a valid email");

        ConstraintViolationException ex =
            new ConstraintViolationException(
                Set.of(violation1, violation2));
        HttpServletRequest request =
            mock(HttpServletRequest.class);
        when(request.getRequestURI())
            .thenReturn("/api/users");

        ResponseEntity<ProblemDetail> response =
            handler.handleConstraintViolationException(
                ex, request);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getProperties()
                           .get("errors")).asList()
                                          .hasSize(EXPECTED_ERROR_COUNT)
                                          .anySatisfy(item ->
                                                          assertThat(item)
                                                              .isInstanceOfSatisfying(
                                                                  Map.class, error -> {
                                                                      assertThat(error.get("field"))
                                                                          .isEqualTo("name");
                                                                      assertThat(
                                                                          error.get("message"))
                                                                          .isEqualTo(
                                                                              "must not be blank");
                                                                  }))
                                          .anySatisfy(item ->
                                                          assertThat(item)
                                                              .isInstanceOfSatisfying(
                                                                  Map.class, error -> {
                                                                      assertThat(error.get("field"))
                                                                          .isEqualTo("email");
                                                                      assertThat(
                                                                          error.get("message"))
                                                                          .isEqualTo(
                                                                              "must be a valid"
                                                                              + " email");
                                                                  }));
    }

    // ===== BaseException property propagation =====

    @Test
    void given_baseExceptionWithCustomProperties_when_handled_then_propertiesFlowThrough() {
        ResourceNotFoundException ex =
            new ResourceNotFoundException("User not found");
        ex.addProperty("entityId", "42")
          .addProperty("entityType", "User");
        HttpServletRequest request =
            mock(HttpServletRequest.class);
        when(request.getRequestURI())
            .thenReturn("/api/users/42");

        ResponseEntity<ProblemDetail> response =
            handler.handleBaseException(ex, request);

        assertThat(response.getStatusCode())
            .isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getProperties())
            .containsEntry("entityId", "42")
            .containsEntry("entityType", "User")
            .containsEntry(
                "errorCode", "RESOURCE_NOT_FOUND");
    }

    @Test
    void given_baseExceptionWithCause_when_handled_then_causeTypeFlowsThrough() {
        ResourceNotFoundException ex =
            new ResourceNotFoundException("User not found");
        ex.initCause(
            new IllegalStateException("db error"));
        HttpServletRequest request =
            mock(HttpServletRequest.class);
        when(request.getRequestURI())
            .thenReturn("/api/users/1");

        ResponseEntity<ProblemDetail> response =
            handler.handleBaseException(ex, request);

        assertThat(response.getStatusCode())
            .isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getProperties())
            .containsEntry(
                "causeType", "IllegalStateException")
            .containsEntry(
                "errorCode", "RESOURCE_NOT_FOUND");
    }

}
