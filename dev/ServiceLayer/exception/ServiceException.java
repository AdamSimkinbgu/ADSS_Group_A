package ServiceLayer.exception;

/**
 * Base exception class for all service layer exceptions.
 * This provides a common type for catching all service-related exceptions.
 */
public class ServiceException extends RuntimeException {
    
    public ServiceException(String message) {
        super(message);
    }
    
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}