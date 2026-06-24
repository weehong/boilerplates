package com.example.boilerplate.shared.helpers;

public final class ValidationHelper {

    private static final int DEFAULT_MAX_PAGE_SIZE = 1000;

    private ValidationHelper() {
        throw new IllegalStateException("Utility class");
    }

    public static void validatePaginationParameters(final int page, final int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Page must be >= 0");
        }

        if (size <= 0 || size > DEFAULT_MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("Size must be > 0 and <= " + DEFAULT_MAX_PAGE_SIZE);
        }
    }

    public static void validateSortBy(final String sortBy, final String... allowedFields) {
        if (sortBy == null || sortBy.trim().isEmpty()) {
            throw new IllegalArgumentException("sortBy cannot be null or empty");
        }

        String normalizedSortBy = sortBy.trim();

        for (String allowedField : allowedFields) {
            if (normalizedSortBy.equals(allowedField)) {
                return;
            }
        }

        throw new IllegalArgumentException(
            String.format("Invalid sortBy field: '%s'. Allowed fields: %s", sortBy, String.join(", ", allowedFields)));
    }

}
