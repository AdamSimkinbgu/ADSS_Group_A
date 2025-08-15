package ServiceLayer.exception;

/**
 * Exception thrown when input validation fails in the service layer.
 * This is typically thrown when input parameters don't meet the required criteria.
 */
public class ValidationException extends ServiceException {
    
    public ValidationException(String message) {
        super(message);
    }
    
    public ValidationException(String fieldName, String reason) {
        super("Validation failed for field '" + fieldName + "': " + reason);
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}