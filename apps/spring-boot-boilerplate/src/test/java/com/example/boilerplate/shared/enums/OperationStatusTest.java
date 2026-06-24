package com.example.boilerplate.shared.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OperationStatusTest {

    private static final int EXPECTED_VALUE_COUNT = 5;

    // ===== Enum values =====

    @Test
    void given_enumValues_when_counted_then_returnsExpectedCount() {
        assertThat(OperationStatus.values())
            .hasSize(EXPECTED_VALUE_COUNT);
    }

    @Test
    void given_retrieve_when_valueOf_then_returnsRetrieve() {
        assertThat(OperationStatus.valueOf("RETRIEVE"))
            .isEqualTo(OperationStatus.RETRIEVE);
    }

    @Test
    void given_save_when_valueOf_then_returnsSave() {
        assertThat(OperationStatus.valueOf("SAVE"))
            .isEqualTo(OperationStatus.SAVE);
    }

    @Test
    void given_update_when_valueOf_then_returnsUpdate() {
        assertThat(OperationStatus.valueOf("UPDATE"))
            .isEqualTo(OperationStatus.UPDATE);
    }

    @Test
    void given_delete_when_valueOf_then_returnsDelete() {
        assertThat(OperationStatus.valueOf("DELETE"))
            .isEqualTo(OperationStatus.DELETE);
    }

    @Test
    void given_other_when_valueOf_then_returnsOther() {
        assertThat(OperationStatus.valueOf("OTHER"))
            .isEqualTo(OperationStatus.OTHER);
    }

}
