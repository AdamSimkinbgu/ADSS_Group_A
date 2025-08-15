package DomainLayer.exception;

public class ShiftManagerException extends RuntimeException {
    public ShiftManagerException(String message) {
        super(message);
    }
    public ShiftManagerException(String message, Throwable cause) {
        super(message, cause);
    }
    public ShiftManagerException(Throwable cause) {
        super(cause);
    }
    public ShiftManagerException() {
        super("Shift Manager is not assigned to this shift");
    }
}
