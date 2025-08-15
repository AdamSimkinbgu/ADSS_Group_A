package DomainLayer.EmployeeSubModule.Repository.interfaces;

import DTOs.EmployeeDTO;

import java.util.List;

/**
 * Repository interface for Employee entities.
 * This interface defines methods for accessing and manipulating Employee data.
 * Uses DTOs for data transfer between layers.
 */
public interface EmployeeRepository {

    /**
     * Retrieves an employee by their Israeli ID.
     *
     * @param israeliId The Israeli ID of the employee to retrieve
     * @return The employee DTO with the given Israeli ID, or null if not found
     */
    EmployeeDTO getById(long israeliId);

    /**
     * Retrieves all employees.
     *
     * @return A list of all employee DTOs
     */
    List<EmployeeDTO> getAll();

    /**
     * Retrieves all employees assigned to a specific branch.
     *
     * @param branchId The ID of the branch
     * @return A list of employee DTOs assigned to the branch
     */
    List<EmployeeDTO> getByBranch(long branchId);

    /**
     * Creates a new employee.
     *
     * @param employeeDTO The employee DTO to create
     * @return true if the employee was created successfully, false otherwise
     */
    boolean create(EmployeeDTO employeeDTO);

    /**
     * Updates an existing employee.
     *
     * @param employeeDTO The employee DTO to update
     * @return true if the employee was updated successfully, false otherwise
     */
    boolean update(EmployeeDTO employeeDTO);

    /**
     * Deletes an employee.
     *
     * @param israeliId The Israeli ID of the employee to delete
     * @return true if the employee was deleted successfully, false otherwise
     */
    boolean delete(long israeliId);

    /**
     * Checks if an employee exists.
     *
     * @param israeliId The Israeli ID of the employee to check
     * @return true if the employee exists, false otherwise
     */
    boolean exists(long israeliId);

    /**
     * Checks if an employee is active.
     *
     * @param israeliId The Israeli ID of the employee to check
     * @return true if the employee is active, false otherwise
     */
    boolean isActive(long israeliId);

    /**
     * Checks if an employee has a specific role.
     *
     * @param israeliId The Israeli ID of the employee to check
     * @param role The role to check for
     * @return true if the employee has the role, false otherwise
     */
    boolean hasRole(long israeliId, String role);

    /**
     * Gets all employees with a specific role.
     *
     * @param role The role to filter by
     * @return A list of employee DTOs with the specified role
     */
    List<EmployeeDTO> getByRole(String role);

    List<EmployeeDTO> getDrivers();
}
