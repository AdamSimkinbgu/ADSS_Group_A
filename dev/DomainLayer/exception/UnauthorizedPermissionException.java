package DomainLayer.exception;

public class UnauthorizedPermissionException extends RuntimeException {
    public UnauthorizedPermissionException(String message) {
        super(message);
    }
    public UnauthorizedPermissionException(String message, Throwable cause) {
        super(message, cause);
    }
}
