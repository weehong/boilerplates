package com.example.boilerplate.shared.helpers;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ValidationHelperTest {

    private static final int VALID_SIZE = 10;
    private static final int MAX_SIZE = 1000;
    private static final int OVER_MAX_SIZE = 1001;

    // ===== validatePaginationParameters =====

    @Test
    void given_validParams_when_validatePagination_then_noException() {
        ValidationHelper.validatePaginationParameters(
            0, VALID_SIZE);
    }

    @Test
    void given_maxSize_when_validatePagination_then_noException() {
        ValidationHelper.validatePaginationParameters(
            0, MAX_SIZE);
    }

    @Test
    void given_negativePage_when_validatePagination_then_throwIllegalArgument() {
        assertThatThrownBy(() ->
                               ValidationHelper.validatePaginationParameters(
                                   -1, VALID_SIZE))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Page must be >= 0");
    }

    @Test
    void given_zeroSize_when_validatePagination_then_throwIllegalArgument() {
        assertThatThrownBy(() ->
                               ValidationHelper.validatePaginationParameters(
                                   0, 0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Size must be > 0");
    }

    @Test
    void given_negativeSize_when_validatePagination_then_throwIllegalArgument() {
        assertThatThrownBy(() ->
                               ValidationHelper.validatePaginationParameters(
                                   0, -1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Size must be > 0");
    }

    @Test
    void given_sizeExceedsMax_when_validatePagination_then_throwIllegalArgument() {
        assertThatThrownBy(() ->
                               ValidationHelper.validatePaginationParameters(
                                   0, OVER_MAX_SIZE))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("<= " + MAX_SIZE);
    }

    // ===== validateSortBy =====

    @Test
    void given_validField_when_validateSortBy_then_noException() {
        ValidationHelper.validateSortBy(
            "name", "name", "createdAt");
    }

    @Test
    void given_fieldWithWhitespace_when_validateSortBy_then_noException() {
        ValidationHelper.validateSortBy(
            "  name  ", "name", "createdAt");
    }

    @Test
    void given_nullSortBy_when_validateSortBy_then_throwIllegalArgument() {
        assertThatThrownBy(() ->
                               ValidationHelper.validateSortBy(
                                   null, "name"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining(
                "sortBy cannot be null or empty");
    }

    @Test
    void given_emptySortBy_when_validateSortBy_then_throwIllegalArgument() {
        assertThatThrownBy(() ->
                               ValidationHelper.validateSortBy(
                                   "", "name"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining(
                "sortBy cannot be null or empty");
    }

    @Test
    void given_blankSortBy_when_validateSortBy_then_throwIllegalArgument() {
        assertThatThrownBy(() ->
                               ValidationHelper.validateSortBy(
                                   "   ", "name"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining(
                "sortBy cannot be null or empty");
    }

    @Test
    void given_invalidField_when_validateSortBy_then_throwIllegalArgument() {
        assertThatThrownBy(() ->
                               ValidationHelper.validateSortBy(
                                   "invalid", "name", "createdAt"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid sortBy field")
            .hasMessageContaining("invalid")
            .hasMessageContaining("name")
            .hasMessageContaining("createdAt");
    }

}
