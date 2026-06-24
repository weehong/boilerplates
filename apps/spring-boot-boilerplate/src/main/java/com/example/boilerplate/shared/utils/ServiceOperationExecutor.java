package com.example.boilerplate.shared.utils;

import com.example.boilerplate.shared.enums.OperationStatus;
import com.example.boilerplate.shared.exceptions.types.ExceptionFactory;
import com.example.boilerplate.shared.exceptions.types.ExternalServiceException;
import com.example.boilerplate.shared.exceptions.types.InvalidFileException;
import com.example.boilerplate.shared.exceptions.types.ResourceNotFoundException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DeadlockLoserDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import java.util.Objects;
import java.util.function.Supplier;

import static com.example.boilerplate.shared.constants.ServiceOperationExecutorConstant.CANNOT_ACQUIRE_DATABASE_LOCK;
import static com.example.boilerplate.shared.constants.ServiceOperationExecutorConstant.COLUMN_DOES_NOT_EXIST_KEYWORD;
import static com.example.boilerplate.shared.constants.ServiceOperationExecutorConstant.COLUMN_DOES_NOT_EXIST_MESSAGE;
import static com.example.boilerplate.shared.constants.ServiceOperationExecutorConstant.COLUMN_KEYWORD;
import static com.example.boilerplate.shared.constants.ServiceOperationExecutorConstant.COLUMN_MISMATCH_MESSAGE;
import static com.example.boilerplate.shared.constants.ServiceOperationExecutorConstant.CONCURRENT_MODIFICATION_DETECTED;
import static com.example.boilerplate.shared.constants.ServiceOperationExecutorConstant.CONSTRAINT_DEFAULT;
import static com.example.boilerplate.shared.constants.ServiceOperationExecutorConstant.CONSTRAINT_DELETE_PREVENTED;
import static com.example.boilerplate.shared.constants.ServiceOperationExecutorConstant.CONSTRAINT_ENTITY_FAILS;
import static com.example.boilerplate.shared.constants.ServiceOperationExecutorConstant.CONSTRAINT_UPDATE_FAILS;
import static com.example.boilerplate.shared.constants.ServiceOperationExecutorConstant.CONSTRAINT_VIOLATION_PREFIX;
import static com.example.boilerplate.shared.constants.ServiceOperationExecutorConstant.DATABASE_DEADLOCK_DETECTED;
import static com.example.boilerplate.shared.constants.ServiceOperationExecutorConstant.DATA_INTEGRITY_VIOLATION_PREFIX;
import static com.example.boilerplate.shared.constants.ServiceOperationExecutorConstant.ENTITY_ALREADY_DELETED;
import static com.example.boilerplate.shared.constants.ServiceOperationExecutorConstant.ENTITY_ALREADY_EXISTS;
import static com.example.boilerplate.shared.constants.ServiceOperationExecutorConstant.ENTITY_CANNOT_UPDATE;
import static com.example.boilerplate.shared.constants.ServiceOperationExecutorConstant.ENTITY_NOT_FOUND_MESSAGE;
import static com.example.boilerplate.shared.constants.ServiceOperationExecutorConstant.EXCEPTION_FACTORY_NOT_NULL;
import static com.example.boilerplate.shared.constants.ServiceOperationExecutorConstant.EXTERNAL_SERVICE_ERROR_FORMAT;
import static com.example.boilerplate.shared.constants.ServiceOperationExecutorConstant.EXTERNAL_SERVICE_UNAVAILABLE_FORMAT;
import static com.example.boilerplate.shared.constants.ServiceOperationExecutorConstant.HTTP_PREFIX;
import static com.example.boilerplate.shared.constants.ServiceOperationExecutorConstant.INTEGRITY_DEFAULT;
import static com.example.boilerplate.shared.constants.ServiceOperationExecutorConstant.INTEGRITY_DUPLICATE_ENTRY;
import static com.example.boilerplate.shared.constants.ServiceOperationExecutorConstant.INTEGRITY_FOREIGN_KEY;
import static com.example.boilerplate.shared.constants.ServiceOperationExecutorConstant.INTEGRITY_UPDATE_VIOLATES;
import static com.example.boilerplate.shared.constants.ServiceOperationExecutorConstant.INVALID_DATA_ACCESS_API_USAGE;
import static com.example.boilerplate.shared.constants.ServiceOperationExecutorConstant.INVALID_SQL_SYNTAX_MESSAGE;
import static com.example.boilerplate.shared.constants.ServiceOperationExecutorConstant.JPA_SYSTEM_ERROR;
import static com.example.boilerplate.shared.constants.ServiceOperationExecutorConstant.LOG_DATABASE_EXCEPTION;
import static com.example.boilerplate.shared.constants.ServiceOperationExecutorConstant.LOG_EXECUTING_OPERATION;
import static com.example.boilerplate.shared.constants.ServiceOperationExecutorConstant.LOG_HANDLING_DATABASE_EXCEPTION;
import static com.example.boilerplate.shared.constants.ServiceOperationExecutorConstant.LOG_ILLEGAL_ARGUMENT;
import static com.example.boilerplate.shared.constants.ServiceOperationExecutorConstant.LOG_INVALID_FILE;
import static com.example.boilerplate.shared.constants.ServiceOperationExecutorConstant.LOG_OPERATION_COMPLETED;
import static com.example.boilerplate.shared.constants.ServiceOperationExecutorConstant.LOG_PROGRAMMER_ERROR;
import static com.example.boilerplate.shared.constants.ServiceOperationExecutorConstant.LOG_RESOURCE_NOT_FOUND;
import static com.example.boilerplate.shared.constants.ServiceOperationExecutorConstant.LOG_RESPONSE_BODY_TRUNCATED;
import static com.example.boilerplate.shared.constants.ServiceOperationExecutorConstant.LOG_REST_CLIENT_EXCEPTION;
import static com.example.boilerplate.shared.constants.ServiceOperationExecutorConstant.LOG_UNEXPECTED_EXCEPTION;
import static com.example.boilerplate.shared.constants.ServiceOperationExecutorConstant.LOG_UNHANDLED_DATABASE_EXCEPTION;
import static com.example.boilerplate.shared.constants.ServiceOperationExecutorConstant.MAX_RESPONSE_BODY_SIZE;
import static com.example.boilerplate.shared.constants.ServiceOperationExecutorConstant.MDC_REQUEST_ID_KEY;
import static com.example.boilerplate.shared.constants.ServiceOperationExecutorConstant.OPERATION_SUPPLIER_NOT_NULL;
import static com.example.boilerplate.shared.constants.ServiceOperationExecutorConstant.OPERATION_TYPE_NOT_NULL;
import static com.example.boilerplate.shared.constants.ServiceOperationExecutorConstant.PERSISTENCE_ERROR;
import static com.example.boilerplate.shared.constants.ServiceOperationExecutorConstant.RESOURCE_LOCKED_BY_ANOTHER_TRANSACTION;
import static com.example.boilerplate.shared.constants.ServiceOperationExecutorConstant.SYNTAX_ERROR_KEYWORD;
import static com.example.boilerplate.shared.constants.ServiceOperationExecutorConstant.TABLE_DOESNT_EXIST_KEYWORD;
import static com.example.boilerplate.shared.constants.ServiceOperationExecutorConstant.TABLE_DOES_NOT_EXIST_MESSAGE;
import static com.example.boilerplate.shared.constants.ServiceOperationExecutorConstant.TABLE_KEYWORD;
import static com.example.boilerplate.shared.constants.ServiceOperationExecutorConstant.TRUNCATION_INDICATOR;
import static com.example.boilerplate.shared.constants.ServiceOperationExecutorConstant.UNABLE_TO_ACCESS_DATABASE_RESOURCE;
import static com.example.boilerplate.shared.constants.ServiceOperationExecutorConstant.UNEXPECTED_DATABASE_ERROR_DURING;
import static com.example.boilerplate.shared.constants.ServiceOperationExecutorConstant.UNEXPECTED_ERROR_DURING;
import static com.example.boilerplate.shared.constants.ServiceOperationExecutorConstant.UNKNOWN_COLUMN_KEYWORD;
import static java.util.UUID.randomUUID;

public final class ServiceOperationExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceOperationExecutor.class);

    private ServiceOperationExecutor() {
        throw new UnsupportedOperationException("Cannot instantiate utility class");
    }

    public static <T> T execute(
        Supplier<T> operation,
        OperationStatus operationType,
        ExceptionFactory exceptionFactory) {
        validateExecuteArgs(operation, operationType, exceptionFactory);
        TraceContext traceContext = getOrCreateTraceId();
        String traceId = traceContext.traceId();
        LOGGER.debug(LOG_EXECUTING_OPERATION, traceId, operationType.name().toLowerCase());

        try {
            T result = operation.get();
            LOGGER.debug(LOG_OPERATION_COMPLETED, traceId, operationType.name().toLowerCase());

            return result;
        } catch (IllegalArgumentException ex) {
            LOGGER.warn(
                LOG_ILLEGAL_ARGUMENT,
                traceId,
                operationType,
                ex.getMessage());

            throw ex;
        } catch (DataAccessException | PersistenceException ex) {
            LOGGER.error(
                LOG_DATABASE_EXCEPTION,
                traceId,
                operationType,
                ex.getMessage(),
                ex);

            throw handleDatabaseException(
                ex,
                operationType,
                exceptionFactory,
                traceId);
        } catch (RestClientException ex) {
            LOGGER.error(
                LOG_REST_CLIENT_EXCEPTION,
                traceId,
                operationType,
                ex.getMessage());

            throw handleRestClientException(ex, operationType, traceId);
        } catch (InvalidFileException ex) {
            LOGGER.error(
                LOG_INVALID_FILE,
                traceId,
                operationType,
                ex.getMessage());

            throw exceptionFactory.create(ex.getMessage(), ex);
        } catch (ResourceNotFoundException ex) {
            LOGGER.warn(
                LOG_RESOURCE_NOT_FOUND,
                traceId,
                operationType,
                ex.getMessage());

            throw ex;
        } catch (RuntimeException ex) {
            if (isProgrammerError(ex)) {
                LOGGER.error(
                    LOG_PROGRAMMER_ERROR,
                    traceId,
                    operationType,
                    ex.getClass().getSimpleName(),
                    ex);

                throw ex;
            }

            LOGGER.error(
                LOG_UNEXPECTED_EXCEPTION,
                traceId,
                operationType,
                ex.getMessage(),
                ex);

            throw exceptionFactory.create(UNEXPECTED_ERROR_DURING + operationType.name().toLowerCase(), ex);
        } finally {
            if (traceContext.created()) {
                MDC.remove(MDC_REQUEST_ID_KEY);
            }
        }
    }

    public static void executeVoid(
        Runnable operation,
        OperationStatus operationType,
        ExceptionFactory exceptionFactory) {
        execute(() -> {
            operation.run();

            return null;
        }, operationType, exceptionFactory);
    }

    private static <T> void validateExecuteArgs(
        Supplier<T> operation,
        OperationStatus operationType,
        ExceptionFactory exceptionFactory) {
        Objects.requireNonNull(operation, OPERATION_SUPPLIER_NOT_NULL);
        Objects.requireNonNull(operationType, OPERATION_TYPE_NOT_NULL);
        Objects.requireNonNull(exceptionFactory, EXCEPTION_FACTORY_NOT_NULL);
    }

    private static TraceContext getOrCreateTraceId() {
        String existingTraceId = MDC.get(MDC_REQUEST_ID_KEY);

        if (existingTraceId != null && !existingTraceId.isBlank()) {

            return new TraceContext(existingTraceId, false);
        }

        String newTraceId = randomUUID().toString();
        MDC.put(MDC_REQUEST_ID_KEY, newTraceId);

        return new TraceContext(newTraceId, true);
    }

    private static boolean isProgrammerError(RuntimeException ex) {

        return ex instanceof NullPointerException
               || ex instanceof IllegalStateException
               || ex instanceof UnsupportedOperationException
               || ex instanceof ClassCastException
               || ex instanceof IndexOutOfBoundsException
               || ex instanceof ArithmeticException;
    }

    private static RuntimeException handleDatabaseException(
        Exception ex,
        OperationStatus operationType,
        ExceptionFactory exceptionFactory,
        String traceId) {
        LOGGER.debug(
            LOG_HANDLING_DATABASE_EXCEPTION,
            traceId,
            ex.getClass().getSimpleName());

        RuntimeException lockingException =
            handleLockingExceptions(ex, exceptionFactory);

        if (lockingException != null) {

            return lockingException;
        }

        RuntimeException entityException =
            handleEntityExceptions(ex, operationType, exceptionFactory);

        if (entityException != null) {

            return entityException;
        }

        RuntimeException dataAccessException =
            handleDataAccessExceptions(ex, exceptionFactory);

        if (dataAccessException != null) {

            return dataAccessException;
        }

        LOGGER.warn(
            LOG_UNHANDLED_DATABASE_EXCEPTION,
            traceId,
            ex.getClass().getName());

        return exceptionFactory.create(UNEXPECTED_DATABASE_ERROR_DURING + operationType.name().toLowerCase(), ex);
    }

    private static RuntimeException handleLockingExceptions(
        Exception ex, ExceptionFactory exceptionFactory) {
        if (ex instanceof OptimisticLockingFailureException) {

            return exceptionFactory.create(
                CONCURRENT_MODIFICATION_DETECTED, ex);
        }

        if (ex instanceof DeadlockLoserDataAccessException) {

            return exceptionFactory.create(
                DATABASE_DEADLOCK_DETECTED, ex);
        }

        if (ex instanceof CannotAcquireLockException) {

            return exceptionFactory.create(
                CANNOT_ACQUIRE_DATABASE_LOCK, ex);
        }

        if (ex instanceof PessimisticLockingFailureException) {

            return exceptionFactory.create(
                RESOURCE_LOCKED_BY_ANOTHER_TRANSACTION, ex);
        }

        return null;
    }

    private static RuntimeException handleEntityExceptions(
        Exception ex,
        OperationStatus operationType,
        ExceptionFactory exceptionFactory) {
        if (ex instanceof EntityNotFoundException) {

            return handleEntityNotFound(
                operationType, ex, exceptionFactory);
        }

        if (ex instanceof EntityExistsException) {

            return exceptionFactory.create(ENTITY_ALREADY_EXISTS, ex);
        }

        if (ex instanceof DataIntegrityViolationException) {

            return handleDataIntegrityViolation(
                operationType, ex, exceptionFactory);
        }

        if (ex instanceof ConstraintViolationException) {

            return handleConstraintViolation(
                operationType, ex, exceptionFactory);
        }

        return null;
    }

    private static RuntimeException handleDataAccessExceptions(
        Exception ex, ExceptionFactory exceptionFactory) {
        if (ex instanceof InvalidDataAccessResourceUsageException idaEx) {

            return handleInvalidDataAccessResourceUsage(
                idaEx, exceptionFactory);
        }

        if (ex instanceof JpaSystemException) {

            return exceptionFactory.create(JPA_SYSTEM_ERROR, ex);
        }

        if (ex instanceof InvalidDataAccessApiUsageException) {

            return exceptionFactory.create(
                INVALID_DATA_ACCESS_API_USAGE, ex);
        }

        if (ex instanceof PersistenceException) {

            return exceptionFactory.create(PERSISTENCE_ERROR, ex);
        }

        return null;
    }

    private static RuntimeException handleInvalidDataAccessResourceUsage(
        InvalidDataAccessResourceUsageException ex,
        ExceptionFactory exceptionFactory) {
        String rootCauseMessage = ex.getMostSpecificCause().getMessage();
        String detailMessage;

        if (rootCauseMessage != null) {

            if (rootCauseMessage.contains(TABLE_KEYWORD)
                && rootCauseMessage.contains(TABLE_DOESNT_EXIST_KEYWORD)) {
                detailMessage = TABLE_DOES_NOT_EXIST_MESSAGE;
            } else if (rootCauseMessage.contains(COLUMN_KEYWORD)
                       && rootCauseMessage.contains(COLUMN_DOES_NOT_EXIST_KEYWORD)) {
                detailMessage = COLUMN_DOES_NOT_EXIST_MESSAGE;
            } else if (rootCauseMessage.contains(UNKNOWN_COLUMN_KEYWORD)) {
                detailMessage = COLUMN_MISMATCH_MESSAGE;
            } else if (rootCauseMessage.contains(SYNTAX_ERROR_KEYWORD)) {
                detailMessage = INVALID_SQL_SYNTAX_MESSAGE;
            } else {
                detailMessage = UNABLE_TO_ACCESS_DATABASE_RESOURCE;
            }

        } else {
            detailMessage = UNABLE_TO_ACCESS_DATABASE_RESOURCE;
        }

        return exceptionFactory.create(detailMessage, ex);
    }

    private static RuntimeException handleEntityNotFound(
        OperationStatus type,
        Exception ex,
        ExceptionFactory factory) {

        return switch (type) {
            case RETRIEVE -> factory.create(ENTITY_NOT_FOUND_MESSAGE, ex);
            case UPDATE -> factory.create(ENTITY_CANNOT_UPDATE, ex);
            case DELETE -> factory.create(ENTITY_ALREADY_DELETED, ex);
            default -> factory.create(ENTITY_NOT_FOUND_MESSAGE, ex);
        };
    }

    private static RuntimeException handleDataIntegrityViolation(
        OperationStatus type,
        Exception ex,
        ExceptionFactory factory) {
        String baseMessage = switch (type) {
            case SAVE -> INTEGRITY_DUPLICATE_ENTRY;
            case UPDATE -> INTEGRITY_UPDATE_VIOLATES;
            case DELETE -> INTEGRITY_FOREIGN_KEY;
            default -> INTEGRITY_DEFAULT;
        };

        return factory.create(
            DATA_INTEGRITY_VIOLATION_PREFIX + baseMessage, ex);
    }

    private static RuntimeException handleConstraintViolation(
        OperationStatus type,
        Exception ex,
        ExceptionFactory factory) {
        String baseMessage = switch (type) {
            case SAVE -> CONSTRAINT_ENTITY_FAILS;
            case UPDATE -> CONSTRAINT_UPDATE_FAILS;
            case DELETE -> CONSTRAINT_DELETE_PREVENTED;
            default -> CONSTRAINT_DEFAULT;
        };

        return factory.create(
            CONSTRAINT_VIOLATION_PREFIX + baseMessage, ex);
    }

    private static RuntimeException handleRestClientException(
        RestClientException ex,
        OperationStatus type,
        String traceId) {
        if (ex instanceof HttpStatusCodeException statusCodeEx) {
            int status = statusCodeEx.getStatusCode().value();
            String response = extractRestClientResponseBody(statusCodeEx, traceId);
            String message = String.format(
                EXTERNAL_SERVICE_ERROR_FORMAT,
                type.name().toLowerCase(),
                resolveHttpMessage(status));

            return new ExternalServiceException(
                message,
                ex,
                status,
                response);
        }

        if (ex instanceof ResourceAccessException) {
            String message = String.format(
                EXTERNAL_SERVICE_UNAVAILABLE_FORMAT,
                type.name().toLowerCase(), ex.getMessage());

            return new ExternalServiceException(
                message,
                ex,
                null,
                null);
        }

        String message = String.format(
            EXTERNAL_SERVICE_ERROR_FORMAT,
            type.name().toLowerCase(), ex.getMessage());

        return new ExternalServiceException(
            message,
            ex,
            null,
            null);
    }

    private static String extractRestClientResponseBody(
        HttpStatusCodeException ex, String traceId) {
        String responseBody = ex.getResponseBodyAsString();

        if (responseBody == null || responseBody.isEmpty()) {

            return "";
        }

        return truncateResponseBody(responseBody, traceId);
    }

    private static String truncateResponseBody(
        String responseBody, String traceId) {
        if (responseBody.length() > MAX_RESPONSE_BODY_SIZE) {
            String truncated = responseBody.substring(0, MAX_RESPONSE_BODY_SIZE) + TRUNCATION_INDICATOR;
            LOGGER.debug(
                LOG_RESPONSE_BODY_TRUNCATED,
                traceId,
                responseBody.length(),
                MAX_RESPONSE_BODY_SIZE);

            return truncated;
        }

        return responseBody;
    }

    private static String resolveHttpMessage(int status) {
        HttpStatus httpStatus = HttpStatus.resolve(status);

        return httpStatus != null
               ? httpStatus.getReasonPhrase()
               : HTTP_PREFIX + status;
    }

    private record TraceContext(String traceId, Boolean created) {

    }

}
