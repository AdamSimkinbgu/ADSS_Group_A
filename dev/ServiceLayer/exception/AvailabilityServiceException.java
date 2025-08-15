package ServiceLayer.exception;

/**
 * Exception thrown when availability-related operations fail in the service layer.
 * This provides a specific exception type for employee availability errors.
 */
public class AvailabilityServiceException extends ServiceException {
    
    public AvailabilityServiceException(String message) {
        super(message);
    }
    
    public AvailabilityServiceException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Creates an AvailabilityServiceException for a specific employee and shift
     * 
     * @param employeeId The ID of the employee involved in the availability operation
     * @param shiftId The ID of the shift involved in the availability operation
     * @param reason The reason for the exception
     * @return A new AvailabilityServiceException with a formatted message
     */
    public static AvailabilityServiceException forAvailability(long employeeId, long shiftId, String reason) {
        return new AvailabilityServiceException(
            "Availability operation failed for employee ID " + employeeId + 
            " and shift ID " + shiftId + ": " + reason
        );
    }
    
    /**
     * Creates an AvailabilityServiceException for a specific employee and date
     * 
     * @param employeeId The ID of the employee involved in the availability operation
     * @param date The date involved in the availability operation
     * @param reason The reason for the exception
     * @return A new AvailabilityServiceException with a formatted message
     */
    public static AvailabilityServiceException forDate(long employeeId, Object date, String reason) {
        return new AvailabilityServiceException(
            "Availability operation failed for employee ID " + employeeId + 
            " on date " + date + ": " + reason
        );
    }
    
    /**
     * Creates an AvailabilityServiceException for weekend availability restrictions
     * 
     * @param employeeId The ID of the employee involved in the availability operation
     * @return A new AvailabilityServiceException with a formatted message about weekend restrictions
     */
    public static AvailabilityServiceException forWeekendRestriction(long employeeId) {
        return new AvailabilityServiceException(
            "Availability updates for employee ID " + employeeId + 
            " are blocked on weekends according to system policy"
        );
    }
}