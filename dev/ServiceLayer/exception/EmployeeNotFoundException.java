package ServiceLayer.exception;

/**
 * Exception thrown when an employee cannot be found.
 * This is typically thrown when trying to retrieve, update, or delete an employee that doesn't exist.
 */
public class EmployeeNotFoundException extends ServiceException {
    
    public EmployeeNotFoundException(String message) {
        super(message);
    }
    
    public EmployeeNotFoundException(long employeeId) {
        super("Employee with ID " + employeeId + " not found");
    }
    
    public EmployeeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}