package DataAccessLayer.EmployeeDAL.Exception;

/**
 * Represents a custom runtime exception for the data access layer.
 * This exception is typically used to indicate errors or issues
 * when interacting with the data layer of an application.
 * It serves as a general-purpose exception for data layer-related operations
 * that do not require more specific exceptions.
 */
public class DataLayerException extends RuntimeException {
    public DataLayerException(String message) {
        super(message);
    }
    public DataLayerException(String message, Throwable cause) {
        super(message, cause);
    }
}
