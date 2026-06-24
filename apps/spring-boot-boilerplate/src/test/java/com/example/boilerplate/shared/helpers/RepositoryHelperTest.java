package com.example.boilerplate.shared.helpers;

import com.example.boilerplate.shared.exceptions.types
    .ResourceNotFoundException;
import com.example.boilerplate.shared.repositories
    .UuidRepository;
import com.example.boilerplate.shared.responses.PagedResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RepositoryHelperTest {

    private static final int PAGE_SIZE = 10;
    private static final int EXPECTED_TOTAL_ELEMENTS = 2;

    // ===== findByIdOrThrow =====

    @Test
    void given_existingId_when_findByIdOrThrow_then_returnEntity() {
        JpaRepository<String, Long> repo = mock(
            JpaRepository.class);
        when(repo.findById(1L))
            .thenReturn(Optional.of("found"));

        String result = RepositoryHelper.findByIdOrThrow(
            repo, 1L, String.class);

        assertThat(result).isEqualTo("found");
    }

    @Test
    void given_missingId_when_findByIdOrThrow_then_throwResourceNotFound() {
        JpaRepository<String, Long> repo = mock(
            JpaRepository.class);
        when(repo.findById(1L))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                               RepositoryHelper.findByIdOrThrow(
                                   repo, 1L, String.class))
            .isInstanceOf(
                ResourceNotFoundException.class)
            .hasMessageContaining("String")
            .hasMessageContaining("ID")
            .hasMessageContaining("1");
    }

    // ===== findByUuidOrThrow =====

    @Test
    void given_existingUuid_when_findByUuidOrThrow_then_returnEntity() {
        UuidRepository<String> repo = mock(
            UuidRepository.class);
        UUID uuid = UUID.randomUUID();
        when(repo.findByUuid(uuid))
            .thenReturn(Optional.of("found"));

        String result = RepositoryHelper.findByUuidOrThrow(
            repo, uuid, String.class);

        assertThat(result).isEqualTo("found");
    }

    @Test
    void given_missingUuid_when_findByUuidOrThrow_then_throwResourceNotFound() {
        UuidRepository<String> repo = mock(
            UuidRepository.class);
        UUID uuid = UUID.randomUUID();
        when(repo.findByUuid(uuid))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                               RepositoryHelper.findByUuidOrThrow(
                                   repo, uuid, String.class))
            .isInstanceOf(
                ResourceNotFoundException.class)
            .hasMessageContaining("String")
            .hasMessageContaining("UUID")
            .hasMessageContaining(uuid.toString());
    }

    // ===== findAllPaged =====

    @Test
    void given_validParams_when_findAllPaged_then_returnPagedResponse() {
        JpaRepository<String, Long> repo = mock(
            JpaRepository.class);
        Page<String> page = new PageImpl<>(
            List.of("a", "b"));
        when(repo.findAll(any(Pageable.class)))
            .thenReturn(page);

        PagedResponse<String> result =
            RepositoryHelper.findAllPaged(
                repo,
                0,
                PAGE_SIZE,
                "name",
                "ASC",
                String::toUpperCase);

        assertThat(result.content())
            .containsExactly("A", "B");
        assertThat(result.page().number()).isZero();
        assertThat(result.page().totalElements())
            .isEqualTo(EXPECTED_TOTAL_ELEMENTS);
    }

    @Test
    void given_invalidPage_when_findAllPaged_then_throwIllegalArgument() {
        JpaRepository<String, Long> repo = mock(
            JpaRepository.class);

        assertThatThrownBy(() ->
                               RepositoryHelper.findAllPaged(
                                   repo,
                                   -1,
                                   PAGE_SIZE,
                                   "name",
                                   "ASC",
                                   String::toUpperCase))
            .isInstanceOf(
                IllegalArgumentException.class);
    }

    @Test
    void given_invalidSortDirection_when_findAllPaged_then_throwIllegalArgument() {
        JpaRepository<String, Long> repo = mock(
            JpaRepository.class);

        assertThatThrownBy(() ->
                               RepositoryHelper.findAllPaged(
                                   repo,
                                   0,
                                   PAGE_SIZE,
                                   "name",
                                   "INVALID",
                                   String::toUpperCase))
            .isInstanceOf(
                IllegalArgumentException.class);
    }

    // ===== deleteByIdOrThrow =====

    @Test
    void given_existingId_when_deleteByIdOrThrow_then_delete() {
        JpaRepository<String, Long> repo = mock(
            JpaRepository.class);
        when(repo.findById(1L))
            .thenReturn(Optional.of("entity"));

        RepositoryHelper.deleteByIdOrThrow(
            repo, 1L, String.class);

        verify(repo).delete("entity");
    }

    @Test
    void given_missingId_when_deleteByIdOrThrow_then_throwResourceNotFound() {
        JpaRepository<String, Long> repo = mock(
            JpaRepository.class);
        when(repo.findById(1L))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                               RepositoryHelper.deleteByIdOrThrow(
                                   repo, 1L, String.class))
            .isInstanceOf(
                ResourceNotFoundException.class);

        verify(repo, never()).delete(any());
    }

    // ===== deleteByUuidOrThrow =====

    @Test
    void given_existingUuid_when_deleteByUuidOrThrow_then_delete() {
        UuidRepository<String> repo = mock(
            UuidRepository.class);
        UUID uuid = UUID.randomUUID();
        when(repo.findByUuid(uuid))
            .thenReturn(Optional.of("entity"));

        RepositoryHelper.deleteByUuidOrThrow(
            repo, uuid, String.class);

        verify(repo).delete("entity");
    }

    @Test
    void given_missingUuid_when_deleteByUuidOrThrow_then_throwResourceNotFound() {
        UuidRepository<String> repo = mock(
            UuidRepository.class);
        UUID uuid = UUID.randomUUID();
        when(repo.findByUuid(uuid))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                               RepositoryHelper.deleteByUuidOrThrow(
                                   repo, uuid, String.class))
            .isInstanceOf(
                ResourceNotFoundException.class);

        verify(repo, never()).delete(any());
    }

    // ===== softDeleteByIdOrThrow =====

    @Test
    void given_existingId_when_softDeleteById_then_markAndSave() {
        JpaRepository<StringBuilder, Long> repo = mock(
            JpaRepository.class);
        StringBuilder entity = new StringBuilder("active");
        when(repo.findById(1L))
            .thenReturn(Optional.of(entity));

        RepositoryHelper.softDeleteByIdOrThrow(
            repo, 1L, StringBuilder.class,
            sb -> sb.replace(
                0, sb.length(), "deleted"));

        assertThat(entity.toString()).isEqualTo("deleted");
        verify(repo).save(entity);
    }

    @Test
    void given_missingId_when_softDeleteById_then_throwResourceNotFound() {
        JpaRepository<String, Long> repo = mock(
            JpaRepository.class);
        when(repo.findById(1L))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                               RepositoryHelper.softDeleteByIdOrThrow(
                                   repo, 1L, String.class,
                                   s -> {
                                   }))
            .isInstanceOf(
                ResourceNotFoundException.class);

        verify(repo, never()).save(any());
    }

    // ===== softDeleteByUuidOrThrow =====

    @Test
    void given_existingUuid_when_softDeleteByUuid_then_markAndSave() {
        UuidRepository<StringBuilder> repo = mock(
            UuidRepository.class);
        UUID uuid = UUID.randomUUID();
        StringBuilder entity = new StringBuilder("active");
        when(repo.findByUuid(uuid))
            .thenReturn(Optional.of(entity));

        RepositoryHelper.softDeleteByUuidOrThrow(
            repo, uuid, StringBuilder.class,
            sb -> sb.replace(
                0, sb.length(), "deleted"));

        assertThat(entity.toString()).isEqualTo("deleted");
        verify(repo).save(entity);
    }

    @Test
    void given_missingUuid_when_softDeleteByUuid_then_throwResourceNotFound() {
        UuidRepository<String> repo = mock(
            UuidRepository.class);
        UUID uuid = UUID.randomUUID();
        when(repo.findByUuid(uuid))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                               RepositoryHelper.softDeleteByUuidOrThrow(
                                   repo, uuid, String.class,
                                   s -> {
                                   }))
            .isInstanceOf(
                ResourceNotFoundException.class);

        verify(repo, never()).save(any());
    }

}
