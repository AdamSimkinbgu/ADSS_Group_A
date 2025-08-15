package ServiceLayer.exception;

/**
 * Exception thrown when a user doesn't have the required permissions to perform an action.
 * This is typically thrown during authorization checks before performing sensitive operations.
 */
public class AuthorizationException extends ServiceException {
    
    public AuthorizationException(String message) {
        super(message);
    }
    
    public AuthorizationException(long userId, String requiredPermission) {
        super("User " + userId + " does not have the required permission: " + requiredPermission);
    }
    
    public AuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }
}