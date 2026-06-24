package com.example.boilerplate.shared.helpers;

import com.example.boilerplate.shared.exceptions.types.ResourceNotFoundException;
import com.example.boilerplate.shared.repositories.UuidRepository;
import com.example.boilerplate.shared.responses.PagedResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public final class RepositoryHelper {

    private static final String NOT_FOUND_ID = " not found with ID: ";
    private static final String NOT_FOUND_UUID = " not found with UUID: ";

    private RepositoryHelper() {
        throw new IllegalStateException("Utility class");
    }

    public static <T, I> T findByIdOrThrow(
        final JpaRepository<T, I> repository,
        final I id,
        final Class<T> entityClass) {
        return repository.findById(id)
                         .orElseThrow(() ->
                                          new ResourceNotFoundException(
                                              entityClass.getSimpleName()
                                              + NOT_FOUND_ID + id));
    }

    public static <T> T findByUuidOrThrow(
        final UuidRepository<T> repository,
        final UUID uuid,
        final Class<T> entityClass) {
        return repository.findByUuid(uuid)
                         .orElseThrow(() ->
                                          new ResourceNotFoundException(
                                              entityClass.getSimpleName()
                                              + NOT_FOUND_UUID + uuid));
    }

    public static <T, R> PagedResponse<R> findAllPaged(
        final JpaRepository<T, ?> repository,
        final int page,
        final int size,
        final String sortBy,
        final String sortDirection,
        final Function<T, R> mapper) {
        ValidationHelper.validatePaginationParameters(page, size);
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortBy));

        return PagedResponse.of(repository.findAll(pageRequest), mapper);
    }

    public static <T, I> void deleteByIdOrThrow(
        final JpaRepository<T, I> repository,
        final I id,
        final Class<T> entityClass) {
        T entity = findByIdOrThrow(repository, id, entityClass);
        repository.delete(entity);
    }

    public static <T> void deleteByUuidOrThrow(
        final UuidRepository<T> repository,
        final UUID uuid,
        final Class<T> entityClass) {
        T entity = findByUuidOrThrow(repository, uuid, entityClass);
        repository.delete(entity);
    }

    public static <T> void softDeleteByIdOrThrow(
        final JpaRepository<T, Long> repository,
        final Long id,
        final Class<T> entityClass,
        final Consumer<T> markDeleted) {
        T entity = findByIdOrThrow(repository, id, entityClass);
        markDeleted.accept(entity);
        repository.save(entity);
    }

    public static <T> void softDeleteByUuidOrThrow(
        final UuidRepository<T> repository,
        final UUID uuid,
        final Class<T> entityClass,
        final Consumer<T> markDeleted) {
        T entity = findByUuidOrThrow(repository, uuid, entityClass);
        markDeleted.accept(entity);
        repository.save(entity);
    }

}
