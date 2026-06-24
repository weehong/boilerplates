package com.example.boilerplate.shared.database.entities;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BaseEntityTest {

    private static final String UUID_FIELD = "uuid";

    private static class TestEntity extends BaseEntity {

    }

    // ===== onCreate =====

    @Test
    void given_newEntity_when_onCreate_then_uuidGenerated() {
        TestEntity entity = new TestEntity();
        assertThat(entity.getUuid()).isNull();

        entity.onCreate();

        assertThat(entity.getUuid()).isNotNull();
    }

    @Test
    void given_existingUuid_when_onCreate_then_uuidUnchanged() {
        TestEntity entity = new TestEntity();
        UUID existingUuid = UUID.randomUUID();
        ReflectionTestUtils.setField(entity, UUID_FIELD, existingUuid);

        entity.onCreate();

        assertThat(entity.getUuid()).isEqualTo(existingUuid);
    }

    // ===== isDeleted =====

    @Test
    void given_deletedAtNull_when_isDeleted_then_returnsFalse() {
        TestEntity entity = new TestEntity();

        assertThat(entity.isDeleted()).isFalse();
    }

    @Test
    void given_deletedAtSet_when_isDeleted_then_returnsTrue() {
        TestEntity entity = new TestEntity();
        entity.markDeleted();

        assertThat(entity.isDeleted()).isTrue();
    }

    // ===== markDeleted =====

    @Test
    void given_entity_when_markDeleted_then_deletedAtSet() {
        TestEntity entity = new TestEntity();
        assertThat(entity.getDeletedAt()).isNull();

        entity.markDeleted();

        assertThat(entity.getDeletedAt()).isNotNull();
    }

}
