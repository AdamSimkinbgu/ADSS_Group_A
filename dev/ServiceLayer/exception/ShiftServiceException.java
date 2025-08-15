package ServiceLayer.exception;

/**
 * Exception thrown when shift-related operations fail in the service layer.
 * This provides a specific exception type for shift-related errors.
 */
public class ShiftServiceException extends ServiceException {
    
    public ShiftServiceException(String message) {
        super(message);
    }
    
    public ShiftServiceException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Creates a ShiftServiceException for a specific shift ID
     * 
     * @param shiftId The ID of the shift that caused the exception
     * @param reason The reason for the exception
     * @return A new ShiftServiceException with a formatted message
     */
    public static ShiftServiceException forShift(long shiftId, String reason) {
        return new ShiftServiceException("Operation failed for shift ID " + shiftId + ": " + reason);
    }
    
    /**
     * Creates a ShiftServiceException for a specific date and shift type
     * 
     * @param date The date of the shift that caused the exception
     * @param shiftType The type of the shift that caused the exception
     * @param reason The reason for the exception
     * @return A new ShiftServiceException with a formatted message
     */
    public static ShiftServiceException forShift(Object date, Object shiftType, String reason) {
        return new ShiftServiceException("Operation failed for shift on " + date + " (" + shiftType + "): " + reason);
    }
}