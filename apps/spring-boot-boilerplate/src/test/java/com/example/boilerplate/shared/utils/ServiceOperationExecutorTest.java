package com.example.boilerplate.shared.utils;

import com.example.boilerplate.shared.enums.OperationStatus;
import com.example.boilerplate.shared.exceptions.types.ExceptionFactory;
import com.example.boilerplate.shared.exceptions.types.ExternalServiceException;
import com.example.boilerplate.shared.exceptions.types.InvalidFileException;
import com.example.boilerplate.shared.exceptions.types.ResourceNotFoundException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.DeadlockLoserDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import java.io.IOException;

import static com.example.boilerplate.shared.constants.ServiceOperationExecutorConstant.MDC_REQUEST_ID_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ServiceOperationExecutorTest {

    private static final ExceptionFactory FACTORY =
        RuntimeException::new;

    // ===== Happy path =====

    @Test
    void given_successfulOperation_when_execute_then_returnResult() {
        String result = ServiceOperationExecutor.execute(
            () -> "success",
            OperationStatus.RETRIEVE,
            FACTORY);

        assertThat(result).isEqualTo("success");
    }

    @Test
    void given_successfulRunnable_when_executeVoid_then_complete() {
        boolean[] executed = { false };

        ServiceOperationExecutor.executeVoid(
            () -> executed[0] = true,
            OperationStatus.SAVE,
            FACTORY);

        assertThat(executed[0]).isTrue();
    }

    // ===== Null parameter validation =====

    @Test
    void given_nullOperation_when_execute_then_throwNullPointer() {
        assertThatThrownBy(() ->
                               ServiceOperationExecutor.execute(
                                   null,
                                   OperationStatus.RETRIEVE,
                                   FACTORY))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("Operation supplier");
    }

    @Test
    void given_nullOperationType_when_execute_then_throwNullPointer() {
        assertThatThrownBy(() ->
                               ServiceOperationExecutor.execute(
                                   () -> "x", null, FACTORY))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("Operation type");
    }

    @Test
    void given_nullFactory_when_execute_then_throwNullPointer() {
        assertThatThrownBy(() ->
                               ServiceOperationExecutor.execute(
                                   () -> "x",
                                   OperationStatus.RETRIEVE,
                                   null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("ExceptionFactory");
    }

    // ===== Direct rethrow exceptions =====

    @Test
    void given_illegalArgument_when_execute_then_rethrowSame() {
        IllegalArgumentException original =
            new IllegalArgumentException("bad arg");

        assertThatThrownBy(() ->
                               ServiceOperationExecutor.execute(
                                   () -> {
                                       throw original;
                                   },
                                   OperationStatus.SAVE,
                                   FACTORY))
            .isSameAs(original);
    }

    @Test
    void given_resourceNotFound_when_execute_then_rethrowSame() {
        ResourceNotFoundException original =
            new ResourceNotFoundException("not found");

        assertThatThrownBy(() ->
                               ServiceOperationExecutor.execute(
                                   () -> {
                                       throw original;
                                   },
                                   OperationStatus.RETRIEVE,
                                   FACTORY))
            .isSameAs(original);
    }

    @Test
    void given_nullPointerException_when_execute_then_rethrowAsProgrammerError() {
        NullPointerException original =
            new NullPointerException("npe");

        assertThatThrownBy(() ->
                               ServiceOperationExecutor.execute(
                                   () -> {
                                       throw original;
                                   },
                                   OperationStatus.RETRIEVE,
                                   FACTORY))
            .isSameAs(original);
    }

    @Test
    void given_illegalState_when_execute_then_rethrowAsProgrammerError() {
        IllegalStateException original =
            new IllegalStateException("bad state");

        assertThatThrownBy(() ->
                               ServiceOperationExecutor.execute(
                                   () -> {
                                       throw original;
                                   },
                                   OperationStatus.RETRIEVE,
                                   FACTORY))
            .isSameAs(original);
    }

    @Test
    void given_classCast_when_execute_then_rethrowAsProgrammerError() {
        ClassCastException original =
            new ClassCastException("bad cast");

        assertThatThrownBy(() ->
                               ServiceOperationExecutor.execute(
                                   () -> {
                                       throw original;
                                   },
                                   OperationStatus.RETRIEVE,
                                   FACTORY))
            .isSameAs(original);
    }

    // ===== Wrapped exceptions =====

    @Test
    void given_invalidFile_when_execute_then_wrapWithFactory() {
        InvalidFileException original =
            new InvalidFileException("bad file", null);

        assertThatThrownBy(() ->
                               ServiceOperationExecutor.execute(
                                   () -> {
                                       throw original;
                                   },
                                   OperationStatus.SAVE,
                                   FACTORY))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("bad file")
            .hasCause(original);
    }

    @Test
    void given_unexpectedRuntime_when_execute_then_wrapWithFactory() {
        RuntimeException original =
            new RuntimeException("unexpected");

        assertThatThrownBy(() ->
                               ServiceOperationExecutor.execute(
                                   () -> {
                                       throw original;
                                   },
                                   OperationStatus.SAVE,
                                   FACTORY))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining(
                "Unexpected error occurred")
            .hasCause(original);
    }

    // ===== Database - Locking exceptions =====

    @Test
    void given_optimisticLocking_when_execute_then_concurrentModificationMsg() {
        assertThatThrownBy(() ->
                               ServiceOperationExecutor.execute(
                                   () -> {
                                       throw new OptimisticLockingFailureException(
                                           "lock");
                                   },
                                   OperationStatus.UPDATE,
                                   FACTORY))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining(
                "Concurrent modification");
    }

    @Test
    void given_deadlock_when_execute_then_deadlockDetectedMsg() {
        assertThatThrownBy(() ->
                               ServiceOperationExecutor.execute(
                                   () -> {
                                       throw new DeadlockLoserDataAccessException(
                                           "deadlock", null);
                                   },
                                   OperationStatus.UPDATE,
                                   FACTORY))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("deadlock");
    }

    @Test
    void given_cannotAcquireLock_when_execute_then_lockUnavailableMsg() {
        assertThatThrownBy(() ->
                               ServiceOperationExecutor.execute(
                                   () -> {
                                       throw new CannotAcquireLockException(
                                           "lock", null);
                                   },
                                   OperationStatus.UPDATE,
                                   FACTORY))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining(
                "Cannot acquire database lock");
    }

    @Test
    void given_pessimisticLocking_when_execute_then_resourceLockedMsg() {
        assertThatThrownBy(() ->
                               ServiceOperationExecutor.execute(
                                   () -> {
                                       throw new PessimisticLockingFailureException(
                                           "lock");
                                   },
                                   OperationStatus.UPDATE,
                                   FACTORY))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining(
                "locked by another transaction");
    }

    // ===== Database - Entity exceptions =====

    @Test
    void given_entityNotFound_retrieve_when_execute_then_notFoundMsg() {
        assertThatThrownBy(() ->
                               ServiceOperationExecutor.execute(
                                   () -> {
                                       throw new EntityNotFoundException(
                                           "missing");
                                   },
                                   OperationStatus.RETRIEVE,
                                   FACTORY))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Entity not found");
    }

    @Test
    void given_entityNotFound_update_when_execute_then_cannotUpdateMsg() {
        assertThatThrownBy(() ->
                               ServiceOperationExecutor.execute(
                                   () -> {
                                       throw new EntityNotFoundException(
                                           "missing");
                                   },
                                   OperationStatus.UPDATE,
                                   FACTORY))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("cannot update");
    }

    @Test
    void given_entityNotFound_delete_when_execute_then_alreadyDeletedMsg() {
        assertThatThrownBy(() ->
                               ServiceOperationExecutor.execute(
                                   () -> {
                                       throw new EntityNotFoundException(
                                           "missing");
                                   },
                                   OperationStatus.DELETE,
                                   FACTORY))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("already deleted");
    }

    @Test
    void given_entityNotFound_otherOp_when_execute_then_genericNotFoundMsg() {
        assertThatThrownBy(() ->
                               ServiceOperationExecutor.execute(
                                   () -> {
                                       throw new EntityNotFoundException(
                                           "missing");
                                   },
                                   OperationStatus.OTHER,
                                   FACTORY))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Entity not found");
    }

    @Test
    void given_entityExists_when_execute_then_alreadyExistsMsg() {
        assertThatThrownBy(() ->
                               ServiceOperationExecutor.execute(
                                   () -> {
                                       throw new EntityExistsException(
                                           "exists");
                                   },
                                   OperationStatus.SAVE,
                                   FACTORY))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("already exists");
    }

    // ===== Database - Data integrity violations =====

    @Test
    void given_dataIntegrity_save_when_execute_then_duplicateMsg() {
        assertThatThrownBy(() ->
                               ServiceOperationExecutor.execute(
                                   () -> {
                                       throw new DataIntegrityViolationException(
                                           "dup");
                                   },
                                   OperationStatus.SAVE,
                                   FACTORY))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("duplicate entry");
    }

    @Test
    void given_dataIntegrity_update_when_execute_then_constraintsMsg() {
        assertThatThrownBy(() ->
                               ServiceOperationExecutor.execute(
                                   () -> {
                                       throw new DataIntegrityViolationException(
                                           "bad");
                                   },
                                   OperationStatus.UPDATE,
                                   FACTORY))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining(
                "update violates constraints");
    }

    @Test
    void given_dataIntegrity_delete_when_execute_then_foreignKeyMsg() {
        assertThatThrownBy(() ->
                               ServiceOperationExecutor.execute(
                                   () -> {
                                       throw new DataIntegrityViolationException(
                                           "fk");
                                   },
                                   OperationStatus.DELETE,
                                   FACTORY))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining(
                "foreign key constraint");
    }

    @Test
    void given_dataIntegrity_other_when_execute_then_constraintViolationMsg() {
        assertThatThrownBy(() ->
                               ServiceOperationExecutor.execute(
                                   () -> {
                                       throw new DataIntegrityViolationException(
                                           "bad");
                                   },
                                   OperationStatus.OTHER,
                                   FACTORY))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("constraint violation");
    }

    // ===== Database - Data access exceptions =====

    @Test
    void given_missingTable_when_execute_then_tableDoesNotExistMsg() {
        Exception rootCause = new RuntimeException(
            "Table 'users' doesn't exist");

        assertThatThrownBy(() ->
                               ServiceOperationExecutor.execute(
                                   () -> {
                                       throw new InvalidDataAccessResourceUsageException(
                                           "sql", rootCause);
                                   },
                                   OperationStatus.RETRIEVE,
                                   FACTORY))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("table does not exist");
    }

    @Test
    void given_missingColumn_when_execute_then_columnDoesNotExistMsg() {
        Exception rootCause = new RuntimeException(
            "column 'name' does not exist");

        assertThatThrownBy(() ->
                               ServiceOperationExecutor.execute(
                                   () -> {
                                       throw new InvalidDataAccessResourceUsageException(
                                           "sql", rootCause);
                                   },
                                   OperationStatus.RETRIEVE,
                                   FACTORY))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining(
                "column does not exist");
    }

    @Test
    void given_unknownColumn_when_execute_then_columnMismatchMsg() {
        Exception rootCause = new RuntimeException(
            "Unknown column 'foo'");

        assertThatThrownBy(() ->
                               ServiceOperationExecutor.execute(
                                   () -> {
                                       throw new InvalidDataAccessResourceUsageException(
                                           "sql", rootCause);
                                   },
                                   OperationStatus.RETRIEVE,
                                   FACTORY))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("column mismatch");
    }

    @Test
    void given_syntaxError_when_execute_then_syntaxMsg() {
        Exception rootCause = new RuntimeException(
            "Syntax error in SQL");

        assertThatThrownBy(() ->
                               ServiceOperationExecutor.execute(
                                   () -> {
                                       throw new InvalidDataAccessResourceUsageException(
                                           "sql", rootCause);
                                   },
                                   OperationStatus.RETRIEVE,
                                   FACTORY))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("syntax");
    }

    @Test
    void given_otherResourceUsageCause_when_execute_then_genericResourceMsg() {
        Exception rootCause = new RuntimeException(
            "unknown error");

        assertThatThrownBy(() ->
                               ServiceOperationExecutor.execute(
                                   () -> {
                                       throw new InvalidDataAccessResourceUsageException(
                                           "sql", rootCause);
                                   },
                                   OperationStatus.RETRIEVE,
                                   FACTORY))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining(
                "Unable to access database resource");
    }

    @Test
    void given_nullRootCauseMessage_when_execute_then_genericResourceMsg() {
        String nullMessage = null;
        Exception rootCause = new RuntimeException(nullMessage);

        assertThatThrownBy(() ->
                               ServiceOperationExecutor.execute(
                                   () -> {
                                       throw new InvalidDataAccessResourceUsageException(
                                           "sql", rootCause);
                                   },
                                   OperationStatus.RETRIEVE,
                                   FACTORY))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining(
                "Unable to access database resource");
    }

    @Test
    void given_jpaSystemException_when_execute_then_jpaErrorMsg() {
        assertThatThrownBy(() ->
                               ServiceOperationExecutor.execute(
                                   () -> {
                                       throw new JpaSystemException(
                                           new RuntimeException("jpa"));
                                   },
                                   OperationStatus.RETRIEVE,
                                   FACTORY))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("JPA system error");
    }

    @Test
    void given_invalidApiUsage_when_execute_then_apiUsageMsg() {
        assertThatThrownBy(() ->
                               ServiceOperationExecutor.execute(
                                   () -> {
                                       throw new InvalidDataAccessApiUsageException(
                                           "bad api");
                                   },
                                   OperationStatus.RETRIEVE,
                                   FACTORY))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Invalid usage");
    }

    @Test
    void given_persistenceException_when_execute_then_persistenceErrorMsg() {
        assertThatThrownBy(() ->
                               ServiceOperationExecutor.execute(
                                   () -> {
                                       throw new PersistenceException("generic");
                                   },
                                   OperationStatus.RETRIEVE,
                                   FACTORY))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining(
                "Persistence error occurred");
    }

    @Test
    void given_unmatchedDataAccess_when_execute_then_unexpectedDatabaseMsg() {
        assertThatThrownBy(() ->
                               ServiceOperationExecutor.execute(
                                   () -> {
                                       throw new DataRetrievalFailureException(
                                           "fail");
                                   },
                                   OperationStatus.RETRIEVE,
                                   FACTORY))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining(
                "Unexpected database error");
    }

    // ===== REST client exceptions =====

    @Test
    void given_httpStatusCode_when_execute_then_externalServiceError() {
        assertThatThrownBy(() ->
                               ServiceOperationExecutor.execute(
                                   () -> {
                                       throw new HttpClientErrorException(
                                           HttpStatus.NOT_FOUND);
                                   },
                                   OperationStatus.RETRIEVE,
                                   FACTORY))
            .isInstanceOf(ExternalServiceException.class)
            .hasMessageContaining(
                "External service error");
    }

    @Test
    void given_resourceAccess_when_execute_then_serviceUnavailableMsg() {
        assertThatThrownBy(() ->
                               ServiceOperationExecutor.execute(
                                   () -> {
                                       throw new ResourceAccessException(
                                           "timeout",
                                           new IOException("connect"));
                                   },
                                   OperationStatus.RETRIEVE,
                                   FACTORY))
            .isInstanceOf(ExternalServiceException.class)
            .hasMessageContaining("unavailable");
    }

    @Test
    void given_genericRestClient_when_execute_then_externalServiceError() {
        assertThatThrownBy(() ->
                               ServiceOperationExecutor.execute(
                                   () -> {
                                       throw new RestClientException(
                                           "rest error");
                                   },
                                   OperationStatus.RETRIEVE,
                                   FACTORY))
            .isInstanceOf(ExternalServiceException.class)
            .hasMessageContaining(
                "External service error");
    }

    // ===== executeVoid propagation =====

    @Test
    void given_failingRunnable_when_executeVoid_then_propagateException() {
        assertThatThrownBy(() ->
                               ServiceOperationExecutor.executeVoid(
                                   () -> {
                                       throw new IllegalArgumentException(
                                           "fail");
                                   },
                                   OperationStatus.SAVE,
                                   FACTORY))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void given_noExistingTraceId_when_execute_then_cleanupCreatedMdcValue() {
        MDC.remove(MDC_REQUEST_ID_KEY);

        ServiceOperationExecutor.execute(
            () -> "ok",
            OperationStatus.RETRIEVE,
            FACTORY);

        assertThat(MDC.get(MDC_REQUEST_ID_KEY)).isNull();
    }

    @Test
    void given_existingTraceId_when_execute_then_preserveOriginalMdcValue() {
        String existingTraceId = "existing-trace-id";
        MDC.put(MDC_REQUEST_ID_KEY, existingTraceId);

        try {
            ServiceOperationExecutor.execute(
                () -> "ok",
                OperationStatus.RETRIEVE,
                FACTORY);

            assertThat(MDC.get(MDC_REQUEST_ID_KEY))
                .isEqualTo(existingTraceId);
        } finally {
            MDC.remove(MDC_REQUEST_ID_KEY);
        }
    }

}
