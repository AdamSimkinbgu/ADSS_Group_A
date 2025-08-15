package DataAccessLayer.EmployeeDAL.Exception;

import java.sql.SQLException;

/**
 * Represents an exception that is thrown when a query execution fails.
 * This class extends {@code SQLException}, providing additional context
 * or customization for database query execution errors.
 */
public class QueryExecutionException extends SQLException {
    public QueryExecutionException(String message) {
        super(message);
    }
    public QueryExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
