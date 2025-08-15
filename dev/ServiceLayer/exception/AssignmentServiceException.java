package ServiceLayer.exception;

/**
 * Exception thrown when assignment-related operations fail in the service layer.
 * This provides a specific exception type for employee assignment errors.
 */
public class AssignmentServiceException extends ServiceException {
    
    public AssignmentServiceException(String message) {
        super(message);
    }
    
    public AssignmentServiceException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Creates an AssignmentServiceException for a specific employee and shift
     * 
     * @param employeeId The ID of the employee involved in the assignment
     * @param shiftId The ID of the shift involved in the assignment
     * @param reason The reason for the exception
     * @return A new AssignmentServiceException with a formatted message
     */
    public static AssignmentServiceException forAssignment(long employeeId, long shiftId, String reason) {
        return new AssignmentServiceException(
            "Assignment operation failed for employee ID " + employeeId + 
            " and shift ID " + shiftId + ": " + reason
        );
    }
    
    /**
     * Creates an AssignmentServiceException for a specific employee, shift, and role
     * 
     * @param employeeId The ID of the employee involved in the assignment
     * @param shiftId The ID of the shift involved in the assignment
     * @param role The role involved in the assignment
     * @param reason The reason for the exception
     * @return A new AssignmentServiceException with a formatted message
     */
    public static AssignmentServiceException forAssignment(long employeeId, long shiftId, String role, String reason) {
        return new AssignmentServiceException(
            "Assignment operation failed for employee ID " + employeeId + 
            ", shift ID " + shiftId + ", and role '" + role + "': " + reason
        );
    }
}