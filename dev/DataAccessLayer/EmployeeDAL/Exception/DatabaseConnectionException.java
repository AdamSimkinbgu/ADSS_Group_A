package DataAccessLayer.EmployeeDAL.Exception;

import java.sql.SQLException;

/**
 * Represents an exception that is thrown when a database connection operation fails.
 * This class extends {@code SQLException}, providing a specialized exception
 * for handling errors related to establishing or maintaining database connections.
 */
public class DatabaseConnectionException extends SQLException {
    public DatabaseConnectionException(String message) {
        super(message);
    }
    public DatabaseConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
