package com.example.boilerplate.shared.constants;

public final class ServiceOperationExecutorConstant {

    public static final String TRUNCATION_INDICATOR = "\n\n--- RESPONSE TRUNCATED (exceeded 10000 byte limit) ---";
    public static final String MDC_REQUEST_ID_KEY = "requestId";
    public static final int MAX_RESPONSE_BODY_SIZE = 10_000;
    public static final String OPERATION_SUPPLIER_NOT_NULL = "Operation supplier must not be null";
    public static final String OPERATION_TYPE_NOT_NULL = "Operation type must not be null";
    public static final String EXCEPTION_FACTORY_NOT_NULL = "ExceptionFactory must not be null";
    public static final String LOG_EXECUTING_OPERATION = "[{}] Executing {} operation";
    public static final String LOG_OPERATION_COMPLETED = "[{}] {} operation completed successfully";
    public static final String LOG_ILLEGAL_ARGUMENT = "[{}] {} operation failed with illegal argument: {}";
    public static final String LOG_DATABASE_EXCEPTION = "[{}] {} operation failed with database exception: {}";
    public static final String LOG_REST_CLIENT_EXCEPTION = "[{}] {} operation failed with RestClient exception: {}";
    public static final String LOG_INVALID_FILE = "[{}] {} operation failed with invalid file: {}";
    public static final String LOG_RESOURCE_NOT_FOUND = "[{}] {} operation failed - resource not found: {}";
    public static final String LOG_PROGRAMMER_ERROR = "[{}] {} operation failed with programmer error - bubbling up: "
                                                      + "{}";
    public static final String LOG_UNEXPECTED_EXCEPTION = "[{}] {} operation failed with unexpected exception: {}";
    public static final String UNEXPECTED_ERROR_DURING = "Unexpected error occurred during ";
    public static final String LOG_HANDLING_DATABASE_EXCEPTION = "[{}] Handling database exception: {}";
    public static final String LOG_UNHANDLED_DATABASE_EXCEPTION = "[{}] Unhandled database exception type: {}";
    public static final String UNEXPECTED_DATABASE_ERROR_DURING = "Unexpected database error during ";
    public static final String CONCURRENT_MODIFICATION_DETECTED = "Concurrent modification detected - please retry "
                                                                  + "the operation";
    public static final String DATABASE_DEADLOCK_DETECTED = "Database deadlock detected - please retry the operation";
    public static final String CANNOT_ACQUIRE_DATABASE_LOCK = "Cannot acquire database lock - resource may be in use";
    public static final String RESOURCE_LOCKED_BY_ANOTHER_TRANSACTION = "Resource is locked by another transaction";
    public static final String ENTITY_ALREADY_EXISTS = "Entity already exists";
    public static final String JPA_SYSTEM_ERROR = "JPA system error occurred";
    public static final String INVALID_DATA_ACCESS_API_USAGE = "Invalid usage of the Data Access API";
    public static final String PERSISTENCE_ERROR = "Persistence error occurred";
    public static final String TABLE_KEYWORD = "Table";
    public static final String TABLE_DOESNT_EXIST_KEYWORD = "doesn't exist";
    public static final String TABLE_DOES_NOT_EXIST_MESSAGE =
        "Database table does not exist. "
        + "Please ensure the database schema is up to date";
    public static final String COLUMN_KEYWORD = "column";
    public static final String COLUMN_DOES_NOT_EXIST_KEYWORD = "does not exist";
    public static final String COLUMN_DOES_NOT_EXIST_MESSAGE =
        "Database column does not exist. "
        + "Please verify entity mappings match the database schema";
    public static final String UNKNOWN_COLUMN_KEYWORD = "Unknown column";
    public static final String COLUMN_MISMATCH_MESSAGE =
        "Database column mismatch detected. "
        + "Please verify entity mappings match the database schema";
    public static final String SYNTAX_ERROR_KEYWORD = "Syntax error";
    public static final String INVALID_SQL_SYNTAX_MESSAGE = "Invalid SQL query syntax detected";
    public static final String UNABLE_TO_ACCESS_DATABASE_RESOURCE =
        "Unable to access database resource. "
        + "Please verify the database configuration and schema";
    public static final String ENTITY_NOT_FOUND_MESSAGE = "Entity not found";
    public static final String ENTITY_CANNOT_UPDATE = "Entity does not exist or was deleted - cannot update";
    public static final String ENTITY_ALREADY_DELETED = "Entity does not exist or was already deleted";
    public static final String INTEGRITY_DUPLICATE_ENTRY = "duplicate entry or constraint failure";
    public static final String INTEGRITY_UPDATE_VIOLATES = "update violates constraints";
    public static final String INTEGRITY_FOREIGN_KEY = "cannot delete due to foreign key constraint";
    public static final String INTEGRITY_DEFAULT = "constraint violation";
    public static final String DATA_INTEGRITY_VIOLATION_PREFIX = "Data integrity violation - ";
    public static final String CONSTRAINT_ENTITY_FAILS = "entity fails validation rules";
    public static final String CONSTRAINT_UPDATE_FAILS = "update fails validation rules";
    public static final String CONSTRAINT_DELETE_PREVENTED = "delete operation prevented";
    public static final String CONSTRAINT_DEFAULT = "validation rules violated";
    public static final String CONSTRAINT_VIOLATION_PREFIX = "Constraint violation - ";
    public static final String EXTERNAL_SERVICE_ERROR_FORMAT = "External service error during %s: %s";
    public static final String EXTERNAL_SERVICE_UNAVAILABLE_FORMAT = "External service unavailable during %s: %s";
    public static final String LOG_RESPONSE_BODY_TRUNCATED = "[{}] RestClient response body truncated from {} to {} "
                                                             + "bytes";
    public static final String HTTP_PREFIX = "HTTP ";

    private ServiceOperationExecutorConstant() {
    }

}
