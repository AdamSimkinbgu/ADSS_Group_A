package DomainLayer.EmployeeSubModule;

import DTOs.BranchDTO;
import DTOs.EmployeeDTO;
import DomainLayer.EmployeeSubModule.Repository.interfaces.AuthorisationRepository;
import DomainLayer.EmployeeSubModule.Repository.interfaces.BranchRepository;
import DomainLayer.EmployeeSubModule.Repository.interfaces.EmployeeRepository;
import DomainLayer.exception.InvalidInputException;
import DomainLayer.exception.UnauthorizedPermissionException;
import Util.config;

import java.time.LocalDate;
import java.util.*;

public class EmployeeController {
    private final EmployeeRepository employeeRepository;
    private final BranchRepository branchRepository;
    private final AuthorisationRepository authorisationRepository;
    private final AuthorisationController authorisationController;

    ///   Naim: I think you need the Transport Facade connected here to update the   <<-----------------   NOTE
    ///         driverIdToInTransportID hashmap when adding/removing a Driver   <<------------   NOTE

    public EmployeeController(EmployeeRepository employeeRepository, BranchRepository branchRepository,
            AuthorisationRepository authorisationRepository, AuthorisationController authorisationController) {
        this.employeeRepository = employeeRepository;
        this.branchRepository = branchRepository;
        this.authorisationRepository = authorisationRepository;
        this.authorisationController = authorisationController;
    }

    /**
     * Constructor that accepts in-memory collections for backward compatibility.
     * This constructor is deprecated and will be removed in a future version.
     *
     * @param employees               The set of employees
     * @param authorisationController The authorisation controller
     * @param branches                The set of branches
     */
    @Deprecated
    public EmployeeController(Set<Employee> employees, AuthorisationController authorisationController,
            Set<Branch> branches) {
        // This constructor is kept for backward compatibility
        // It should not be used in new code
        throw new UnsupportedOperationException(
                "This constructor is deprecated. Use the repository-based constructor instead.");
    }

    /**
     * Retrieves an employee by their Israeli ID.
     * 
     * @param israeliId - The Israeli ID of the employee to retrieve
     * @return The employee with the given Israeli ID, or null if not found
     */
    public Employee getEmployeeByIsraeliId(long israeliId) {
        EmployeeDTO employeeDTO = employeeRepository.getById(israeliId);
        if (employeeDTO == null) {
            return null;
        }
        return convertToEntity(employeeDTO);
    }

    /**
     * Converts an EmployeeDTO to an Employee domain object.
     *
     * @param dto The EmployeeDTO to convert
     * @return The corresponding Employee domain object
     */
    private Employee convertToEntity(EmployeeDTO dto) {
        if (dto == null) {
            return null;
        }

        // Convert BankAccountDTO to BankAccount if it exists
        BankAccount bankAccount = null;
        if (dto.getBankAccount() != null) {
            DTOs.BankAccountDTO bankAccountDTO = dto.getBankAccount();
            bankAccount = new BankAccount(
                    dto.getIsraeliId(),
                    bankAccountDTO.getBankNumber(),
                    bankAccountDTO.getBankBranchNumber(),
                    bankAccountDTO.getBankAccountNumber());
        }

        return new Employee(
                dto.getIsraeliId(),
                dto.getFirstName(),
                dto.getLastName(),
                dto.getSalary(),
                dto.getTermsOfEmployment(),
                dto.getRoles(),
                dto.getStartOfEmployment(),
                dto.isActive(),
                dto.getCreationDate(),
                dto.getUpdateDate(),
                dto.getBranchId(),
                bankAccount);
    }

    /**
     * Converts an Employee domain object to an EmployeeDTO.
     *
     * @param employee The Employee domain object to convert
     * @return The corresponding EmployeeDTO
     */
    private EmployeeDTO convertToDTO(Employee employee) {
        if (employee == null) {
            return null;
        }

        // Convert BankAccount to BankAccountDTO if it exists
        DTOs.BankAccountDTO bankAccountDTO = null;
        if (employee.getBankAccount() != null) {
            BankAccount bankAccount = employee.getBankAccount();
            bankAccountDTO = new DTOs.BankAccountDTO(
                    employee.getIsraeliId(),
                    bankAccount.getBankNumber(),
                    bankAccount.getBankBranchNumber(),
                    bankAccount.getBankAccountNumber());
        }

        return new EmployeeDTO(
                employee.getIsraeliId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getSalary(),
                employee.getTermsOfEmployment(),
                employee.getRoles(),
                employee.getStartOfEmployment(),
                employee.isActive(),
                employee.getCreationDate(),
                employee.getUpdateDate(),
                employee.getBranchId(),
                bankAccountDTO);
    }

    /**
     * Creates a new employee.
     *
     * @param doneBy            - The user who created the employee - for auditing
     *                          purposes and permissions
     * @param israeliId         - The Israeli ID of the new employee
     * @param firstName         - The first name of the new employee
     * @param lastName          - The last name of the new employee
     * @param salary            - The salary of the new employee
     * @param termsOfEmployment - The terms of employment of the new employee
     * @param roles             - The roles of the new employee
     * @param startOfEmployment - The start of employment date of the new employee
     * @param branchId          - The branch id that the employee is assigned to
     * @return True if the employee was created successfully
     * @throws UnauthorizedPermissionException if the user does not have permission
     * @throws InvalidInputException           if any input is invalid
     */
    public boolean createEmployee(long doneBy, long israeliId, String firstName, String lastName, long salary,
            Map<String, Object> termsOfEmployment, Set<String> roles, LocalDate startOfEmployment, long branchId) {
        return createEmployee(doneBy, israeliId, firstName, lastName, salary, termsOfEmployment, roles,
                startOfEmployment, branchId, null);
    }

    /**
     * Creates a new employee with bank account information.
     *
     * @param doneBy            - The user who created the employee - for auditing
     *                          purposes and permissions
     * @param israeliId         - The Israeli ID of the new employee
     * @param firstName         - The first name of the new employee
     * @param lastName          - The last name of the new employee
     * @param salary            - The salary of the new employee
     * @param termsOfEmployment - The terms of employment of the new employee
     * @param roles             - The roles of the new employee
     * @param startOfEmployment - The start of employment date of the new employee
     * @param branchId          - The branch id that the employee is assigned to
     * @param bankAccount       - The bank account information of the new employee
     * @return True if the employee was created successfully
     * @throws UnauthorizedPermissionException if the user does not have permission
     * @throws InvalidInputException           if any input is invalid
     */
    public boolean createEmployee(long doneBy, long israeliId, String firstName, String lastName, long salary,
            Map<String, Object> termsOfEmployment, Set<String> roles, LocalDate startOfEmployment, long branchId,
            BankAccount bankAccount) {
        // Permission handling
        String PERMISSION_REQUIRED = "CREATE_EMPLOYEE";
        if (!isEmployeeAuthorised(doneBy, PERMISSION_REQUIRED)) {
            throw new UnauthorizedPermissionException("User does not have permission to create employee");
        }

        Employee doneByEmployee = getEmployeeByIsraeliId(doneBy);
        if (doneByEmployee == null) {
            throw new InvalidInputException("Employee with ID " + doneBy + " not found");
        }

        // Check if employee already exists
        if (employeeRepository.exists(israeliId)) {
            throw new InvalidInputException("Employee with ID " + israeliId + " already exists");
        }

        // Validate input
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new InvalidInputException("First name cannot be null or empty");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new InvalidInputException("Last name cannot be null or empty");
        }
        if (salary <= 0) {
            throw new InvalidInputException("Salary must be greater than zero");
        }
        if (termsOfEmployment == null) {
            throw new InvalidInputException("Terms of employment cannot be null");
        }
        if (startOfEmployment == null) {
            throw new InvalidInputException("Start of employment date cannot be null");
        }
        if (String.valueOf(israeliId).length() != 9) {
            throw new InvalidInputException("Israeli ID must be 9 digits");
        }
        if (roles == null) {
            roles = new HashSet<String>();
        }
        if (!isBranchIdValid(branchId)) {
            throw new InvalidInputException("Branch info is invalid");
        }

        // Convert BankAccount to BankAccountDTO if it exists
        DTOs.BankAccountDTO bankAccountDTO = null;
        if (bankAccount != null) {
            bankAccountDTO = new DTOs.BankAccountDTO(
                    israeliId,
                    bankAccount.getBankNumber(),
                    bankAccount.getBankBranchNumber(),
                    bankAccount.getBankAccountNumber());
        }

        // Create new employee DTO
        EmployeeDTO employeeDTO = new EmployeeDTO(
                israeliId,
                firstName,
                lastName,
                salary,
                termsOfEmployment,
                roles,
                startOfEmployment,
                true,
                LocalDate.now(),
                LocalDate.now(),
                branchId,
                bankAccountDTO);

        // Create employee in repository
        return employeeRepository.create(employeeDTO);
    }

    private boolean isBranchIdValid(long branchId) {
        return branchRepository.exists(branchId);
    }

    /**
     * Updates an existing employee.
     *
     * @param doneBy            - The user who updated the employee - for auditing
     *                          purposes and permissions
     * @param israeliId         - The Israeli ID of the employee to update
     * @param firstName         - The new first name
     * @param lastName          - The new last name
     * @param salary            - The new salary
     * @param termsOfEmployment - The new terms of employment
     * @param active            - The new active status
     * @return True if the employee was updated successfully
     * @throws UnauthorizedPermissionException if the user does not have permission
     * @throws InvalidInputException           if any input is invalid or if the
     *                                         employee does not exist
     */
    public boolean updateEmployee(long doneBy, long israeliId, String firstName, String lastName, long salary,
            Map<String, Object> termsOfEmployment, boolean active) {
        return updateEmployee(doneBy, israeliId, firstName, lastName, salary, termsOfEmployment, active, null);
    }

    /**
     * Updates an existing employee with bank account information.
     *
     * @param doneBy            - The user who updated the employee - for auditing
     *                          purposes and permissions
     * @param israeliId         - The Israeli ID of the employee to update
     * @param firstName         - The new first name
     * @param lastName          - The new last name
     * @param salary            - The new salary
     * @param termsOfEmployment - The new terms of employment
     * @param active            - The new active status
     * @param bankAccount       - The new bank account information
     * @return True if the employee was updated successfully
     * @throws UnauthorizedPermissionException if the user does not have permission
     * @throws InvalidInputException           if any input is invalid or if the
     *                                         employee does not exist
     */
    public boolean updateEmployee(long doneBy, long israeliId, String firstName, String lastName, long salary,
            Map<String, Object> termsOfEmployment, boolean active, BankAccount bankAccount) {
        // Permission handling
        String PERMISSION_REQUIRED = "UPDATE_EMPLOYEE";
        if (!isEmployeeAuthorised(doneBy, PERMISSION_REQUIRED)) {
            throw new UnauthorizedPermissionException("User does not have permission to update employee");
        }

        Employee doneByEmployee = getEmployeeByIsraeliId(doneBy);
        if (doneByEmployee == null) {
            throw new InvalidInputException("Employee with ID " + doneBy + " not found");
        }

        // Check if employee exists
        EmployeeDTO employeeDTO = employeeRepository.getById(israeliId);
        if (employeeDTO == null) {
            throw new InvalidInputException("Employee with ID " + israeliId + " not found");
        }

        // Check if employee is active
        if (!employeeDTO.isActive()) {
            throw new InvalidInputException(
                    "Employee with ID " + israeliId + " is not active - cannot update information");
        }

        // Validate input
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new InvalidInputException("First name cannot be null or empty");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new InvalidInputException("Last name cannot be null or empty");
        }
        if (salary <= 0) {
            throw new InvalidInputException("Salary must be greater than zero");
        }
        if (termsOfEmployment == null) {
            throw new InvalidInputException("Terms of employment cannot be null");
        }
        if (String.valueOf(israeliId).length() != 9) {
            throw new InvalidInputException("Israeli ID must be 9 digits");
        }

        // Convert BankAccount to BankAccountDTO if it exists
        DTOs.BankAccountDTO bankAccountDTO = null;
        if (bankAccount != null) {
            bankAccountDTO = new DTOs.BankAccountDTO(
                    israeliId,
                    bankAccount.getBankNumber(),
                    bankAccount.getBankBranchNumber(),
                    bankAccount.getBankAccountNumber());
        }

        // Update employee details
        employeeDTO.setFirstName(firstName);
        employeeDTO.setLastName(lastName);
        employeeDTO.setSalary(salary);
        employeeDTO.setTermsOfEmployment(termsOfEmployment);
        employeeDTO.setActive(active);
        employeeDTO.setUpdateDate(LocalDate.now());
        employeeDTO.setBankAccount(bankAccountDTO);

        // Update employee in repository
        return employeeRepository.update(employeeDTO);
    }

    /**
     * Deletes an existing employee.
     *
     * @param doneBy    - The user who deleted the employee - for auditing purposes
     *                  and permissions
     * @param israeliId - The Israeli ID of the employee to delete
     * @return True if the employee was deleted successfully
     * @throws UnauthorizedPermissionException if the user does not have permission
     * @throws InvalidInputException           if the employee does not exist or is
     *                                         not active
     */
    public boolean deleteEmployee(long doneBy, long israeliId) {
        // Check if the user has permission to delete an employee
        String PERMISSION_REQUIRED = "DELETE_EMPLOYEE";
        if (!isEmployeeAuthorised(doneBy, PERMISSION_REQUIRED)) {
            throw new UnauthorizedPermissionException("User does not have permission to delete employee");
        }

        // Check if employee exists
        EmployeeDTO employeeDTO = employeeRepository.getById(israeliId);
        if (employeeDTO == null) {
            throw new InvalidInputException("Employee with ID " + israeliId + " not found");
        }

        // Check if employee is active
        if (!employeeDTO.isActive()) {
            throw new InvalidInputException("Employee with ID " + israeliId + " is not active - cannot delete");
        }

        // Delete employee
        return employeeRepository.delete(israeliId);
    }

    /**
     * Checks if an employee is authorized to perform an action.
     *
     * @param israeliId  - The Israeli ID of the employee to check
     * @param permission - The permission to check for
     * @return True if the employee is authorized, false otherwise
     * @throws InvalidInputException           if the employee does not exist
     * @throws UnauthorizedPermissionException if the employee does not have the
     *                                         permission
     */
    public boolean isEmployeeAuthorised(long israeliId, String permission) {
        // Check if employee exists
        EmployeeDTO employeeDTO = employeeRepository.getById(israeliId);
        if (employeeDTO == null) {
            throw new InvalidInputException("Employee with ID " + israeliId + " not found");
        }

        // Convert DTO to domain entity for authorization check
        Employee employee = convertToEntity(employeeDTO);

        boolean has = authorisationController.hasPermission(employee, permission);
        if (!has) {
            throw new UnauthorizedPermissionException("Employee does not have permission: " + permission);
        }

        // Check if employee has the required permission
        return true;
    }

    /**
     * Adds a role to an employee.
     *
     * @param doneBy    - The user who is adding the role - for auditing purposes
     *                  and permissions
     * @param israeliId - The Israeli ID of the employee to add the role to
     * @param roleName  - The name of the role to add
     * @return True if the role was added successfully
     * @throws UnauthorizedPermissionException if the user does not have permission
     * @throws InvalidInputException           if any input is invalid or if the
     *                                         employee already has the role
     */
    public boolean addRoleToEmployee(long doneBy, long israeliId, String roleName) {
        // Permission handling
        String PERMISSION_REQUIRED = "ROLE_PERMISSION";
        if (!isEmployeeAuthorised(doneBy, PERMISSION_REQUIRED)) {
            throw new UnauthorizedPermissionException("User does not have permission to add role to employee");
        }

        Employee doneByEmployee = getEmployeeByIsraeliId(doneBy);
        if (doneByEmployee == null) {
            throw new InvalidInputException("Employee with ID " + doneBy + " not found");
        }

        // Validate input
        if (roleName == null || roleName.trim().isEmpty()) {
            throw new InvalidInputException("Role name cannot be null or empty");
        }

        // Check if employee exists
        EmployeeDTO employeeDTO = employeeRepository.getById(israeliId);
        if (employeeDTO == null) {
            throw new InvalidInputException("Employee with ID " + israeliId + " not found");
        }

        // Check if role exists
        if (!authorisationRepository.roleExists(roleName)) {
            throw new InvalidInputException("Role '" + roleName + "' does not exist");
        }

        // Check if employee already has the role
        if (employeeDTO.getRoles().contains(roleName)) {
            throw new InvalidInputException("Employee with ID " + israeliId + " already has role '" + roleName + "'");
        }

        // Add role to employee
        Set<String> roles = new HashSet<>(employeeDTO.getRoles());
        roles.add(roleName);
        employeeDTO.setRoles(roles);
        employeeDTO.setUpdateDate(LocalDate.now());

        // Update employee in repository
        return employeeRepository.update(employeeDTO);
    }

    /**
     * Removes a role from an employee.
     *
     * @param doneBy    - The user who is removing the role - for auditing purposes
     *                  and permissions
     * @param israeliId - The Israeli ID of the employee to remove the role from
     * @param roleName  - The name of the role to remove
     * @return True if the role was removed successfully
     * @throws UnauthorizedPermissionException if the user does not have permission
     * @throws InvalidInputException           if any input is invalid
     */
    public boolean removeRoleFromEmployee(long doneBy, long israeliId, String roleName) {
        // Permission handling
        String PERMISSION_REQUIRED = "ROLE_PERMISSION";
        if (!isEmployeeAuthorised(doneBy, PERMISSION_REQUIRED)) {
            throw new UnauthorizedPermissionException("User does not have permission to remove role from employee");
        }

        Employee doneByEmployee = getEmployeeByIsraeliId(doneBy);
        if (doneByEmployee == null) {
            throw new InvalidInputException("Employee with ID " + doneBy + " not found");
        }

        // Validate input
        if (roleName == null || roleName.trim().isEmpty()) {
            throw new InvalidInputException("Role name cannot be null or empty");
        }

        // Check if employee exists
        EmployeeDTO employeeDTO = employeeRepository.getById(israeliId);
        if (employeeDTO == null) {
            throw new InvalidInputException("Employee with ID " + israeliId + " not found");
        }

        // Check if role exists
        if (!authorisationRepository.roleExists(roleName)) {
            throw new InvalidInputException("Role '" + roleName + "' does not exist");
        }

        // Check if employee has the role
        if (!employeeDTO.getRoles().contains(roleName)) {
            throw new InvalidInputException("Employee with ID " + israeliId + " does not have role '" + roleName + "'");
        }

        // Remove role from employee
        Set<String> roles = new HashSet<>(employeeDTO.getRoles());
        roles.remove(roleName);
        employeeDTO.setRoles(roles);
        employeeDTO.setUpdateDate(LocalDate.now());

        // Update employee in repository
        return employeeRepository.update(employeeDTO);
    }

    /**
     * Deactivates an employee.
     *
     * @param doneBy    - The user who is deactivating the employee - for auditing
     *                  purposes and permissions
     * @param israeliId - The Israeli ID of the employee to deactivate
     * @return True if the employee was deactivated successfully
     * @throws UnauthorizedPermissionException if the user does not have permission
     * @throws InvalidInputException           if the employee does not exist
     */
    public boolean deactivateEmployee(long doneBy, long israeliId) {
        // Permission handling
        String PERMISSION_REQUIRED = "EDIT_EMPLOYEE";
        if (!isEmployeeAuthorised(doneBy, PERMISSION_REQUIRED)) {
            throw new UnauthorizedPermissionException("User does not have permission to deactivate employee");
        }

        Employee doneByEmployee = getEmployeeByIsraeliId(doneBy);
        if (doneByEmployee == null) {
            throw new InvalidInputException("Employee with ID " + doneBy + " not found");
        }

        // Check if employee exists
        EmployeeDTO employeeDTO = employeeRepository.getById(israeliId);
        if (employeeDTO == null) {
            throw new InvalidInputException("Employee with ID " + israeliId + " not found");
        }

        // Check if employee is already inactive
        if (!employeeDTO.isActive()) {
            throw new InvalidInputException("Employee with ID " + israeliId + " is already inactive");
        }

        // Deactivate employee
        employeeDTO.setActive(false);
        employeeDTO.setUpdateDate(LocalDate.now());

        // Update employee in repository
        return employeeRepository.update(employeeDTO);
    }

    /**
     * Reactivates an employee by their Israeli ID if the user performing
     * the operation has the required permissions and the employee exists
     * and is currently inactive.
     *
     * @param doneBy    The ID of the user performing the reactivation.
     * @param israeliId The Israeli ID of the employee to be reactivated.
     * @return true if the employee was successfully reactivated, false otherwise.
     * @throws UnauthorizedPermissionException if the user does not have the
     *                                         required permission to reactivate an
     *                                         employee.
     * @throws InvalidInputException           if the user or employee IDs are
     *                                         invalid, or the employee is already
     *                                         active.
     */
    public boolean reactivateEmployee(long doneBy, long israeliId) {
        // Permission handling
        String PERMISSION_REQUIRED = "EDIT_EMPLOYEE";
        if (!isEmployeeAuthorised(doneBy, PERMISSION_REQUIRED)) {
            throw new UnauthorizedPermissionException("User does not have permission to deactivate employee");
        }

        Employee doneByEmployee = getEmployeeByIsraeliId(doneBy);
        if (doneByEmployee == null) {
            throw new InvalidInputException("Employee with ID " + doneBy + " not found");
        }

        // Check if employee exists
        EmployeeDTO employeeDTO = employeeRepository.getById(israeliId);
        if (employeeDTO == null) {
            throw new InvalidInputException("Employee with ID " + israeliId + " not found");
        }

        // Check if employee is already active
        if (employeeDTO.isActive()) {
            throw new InvalidInputException("Employee with ID " + israeliId + " is already inactive");
        }

        // Deactivate employee
        employeeDTO.setActive(true);
        employeeDTO.setUpdateDate(LocalDate.now());

        // Update employee in repository
        return employeeRepository.update(employeeDTO);
    }

    /**
     * Gets all employees in the system.
     *
     * @return A map of all employees, with Israeli ID as the key and Employee
     *         object as the value
     */
    public Map<Long, Employee> getAllEmployees() {
        Map<Long, Employee> employeesMap = new HashMap<>();
        List<EmployeeDTO> employeeDTOs = employeeRepository.getAll();
        for (EmployeeDTO employeeDTO : employeeDTOs) {
            Employee employee = convertToEntity(employeeDTO);
            employeesMap.put(employee.getIsraeliId(), employee);
        }
        return employeesMap;
    }

    /**
     * Gets the AuthorisationController used by this EmployeeController.
     * 
     * @return The AuthorisationController
     */
    public AuthorisationController getAuthorisationController() {
        return authorisationController;
    }

    // ===========================
    // Functions for integration with Transport module
    // ===========================
    public boolean hasPermission(long israeliId, String permission) {
        EmployeeDTO employeeDTO = employeeRepository.getById(israeliId);
        if (employeeDTO == null) {
            throw new InvalidInputException("Employee with ID " + israeliId + " not found");
        }

        // Convert DTO to domain entity for authorization check
        Employee employee = convertToEntity(employeeDTO);

        return authorisationController.hasPermission(employee, permission);
    }

    /**
     * Checks if an employee with the given Israeli ID is active.
     *
     * @param employeeId The Israeli ID of the employee to check.
     * @return True if the employee is active, false otherwise.
     */
    public boolean isEmployeeActive(long employeeId) {
        EmployeeDTO employeeDTO = employeeRepository.getById(employeeId);
        return employeeDTO != null && employeeDTO.isActive();
    }

    public boolean isActive(long israeliId) {
        return isEmployeeActive(israeliId);
    }

    /**
     * Checks if an employee with the given ID has a specific role.
     *
     * @param employeeId The Israeli ID of the employee to check.
     * @param role       The role to check for.
     * @return True if the employee has the specified role, false otherwise.
     */
    public boolean isEmployeeHaveRole(long employeeId, String role) {
        EmployeeDTO employeeDTO = employeeRepository.getById(employeeId);
        return employeeDTO != null && employeeDTO.getRoles().contains(role);
    }

    public String[] getAllDrivers() {
        // Get all employees with the "Driver" role
        // List<EmployeeDTO> driverDTOs = employeeRepository.getByRole("Driver"); //
        // didn't work
        List<EmployeeDTO> driverDTOs = employeeRepository.getDrivers();

        // Serialize the EmployeeDTO objects to strings
        String[] serializedDrivers = new String[driverDTOs.size()];

        // Serialize each EmployeeDTO object to a string
        for (int i = 0; i < driverDTOs.size(); i++) {
            serializedDrivers[i] = driverDTOs.get(i).serialize();
        }

        return serializedDrivers;
    }

    public boolean updateEmployeeBranch(long israeliId, long branchId) {
        // Check if the employee exists
        EmployeeDTO employeeDTO = employeeRepository.getById(israeliId);
        if (employeeDTO == null) {
            throw new InvalidInputException("Employee with ID " + israeliId + " not found");
        }

        // Check if the branch ID is valid
        if (!isBranchIdValid(branchId)) {
            throw new InvalidInputException("Invalid branch ID: " + branchId);
        }

        // Update the employee's branch
        employeeDTO.setBranchId(branchId);
        employeeDTO.setUpdateDate(LocalDate.now());

        // Update the employee in the repository
        return employeeRepository.update(employeeDTO);
    }

    public long getEmployeeBranch(long israeliId) {
        // Check if the employee exists
        EmployeeDTO employeeDTO = employeeRepository.getById(israeliId);
        if (employeeDTO == null) {
            throw new InvalidInputException("Employee with ID " + israeliId + " not found");
        }
        // Return the employee's branch ID
        return employeeDTO.getBranchId();
    }

    public long getBranchIdByAddress(String address, int areaCode) {
        // Check if the branch exists
        BranchDTO branchDTO = branchRepository.getByAddressAndAreaCode(address, areaCode);
        if (branchDTO == null) {
            throw new InvalidInputException(
                    "Branch with address " + address + " and area code " + areaCode + " not found");
        }
        // Return the branch ID
        return branchDTO.getBranchId();
    }

    public String getEmployeeBranchName(long israeliId) {
        // Check if the employee exists
        EmployeeDTO employeeDTO = employeeRepository.getById(israeliId);
        if (employeeDTO == null) {
            throw new InvalidInputException("Employee with ID " + israeliId + " not found");
        }

        // Get the employee's branch
        long branchId = employeeDTO.getBranchId();

        // Get the branch details and return the branch name
        BranchDTO branchDTO = branchRepository.getById(branchId);
        return branchDTO != null ? branchDTO.getBranchName() : null;
    }

    public boolean isBranchExists(long branchId) {
        // Check if the branch exists using the repository
        return branchRepository.exists(branchId);
    }

    public boolean isBranch(String address, int areaCode) {
        // Check if the branch exists using the repository
        BranchDTO branchDTO = branchRepository.getByAddressAndAreaCode(address, areaCode);
        return branchDTO != null;
    }

    public String getAllRolesAsString() {
        // Retrieve all roles from the authorisation repository
        Set<String> roles = authorisationRepository.getAllRoles();

        // Convert the set of roles to a single string, separated by commas
        return String.join(", ", roles);
    }

    /**
     * Gets all employees assigned to a specific branch.
     *
     * @param branchId The ID of the branch
     * @return A list of employee DTOs assigned to the branch
     */
    public List<EmployeeDTO> getEmployeesByBranch(long branchId) {
        // Check if the branch exists
        if (!isBranchExists(branchId)) {
            throw new InvalidInputException("Branch with ID " + branchId + " not found");
        }

        // Get all employees assigned to the branch
        return employeeRepository.getByBranch(branchId);
    }

    /**
     * Updates only the bank account information for an employee.
     *
     * @param doneBy      - The user who updated the employee - for auditing
     *                    purposes and permissions
     * @param israeliId   - The Israeli ID of the employee to update
     * @param bankAccount - The new bank account information
     * @return True if the bank account was updated successfully
     * @throws UnauthorizedPermissionException if the user does not have permission
     * @throws InvalidInputException           if any input is invalid or if the
     *                                         employee does not exist
     */
    public boolean updateEmployeeBankAccount(long doneBy, long israeliId, BankAccount bankAccount) {
        // Permission handling
        String PERMISSION_REQUIRED = "UPDATE_EMPLOYEE";
        if (!isEmployeeAuthorised(doneBy, PERMISSION_REQUIRED)) {
            throw new UnauthorizedPermissionException("User does not have permission to update employee bank account");
        }

        Employee doneByEmployee = getEmployeeByIsraeliId(doneBy);
        if (doneByEmployee == null) {
            throw new InvalidInputException("Employee with ID " + doneBy + " not found");
        }

        // Check if employee exists
        EmployeeDTO employeeDTO = employeeRepository.getById(israeliId);
        if (employeeDTO == null) {
            throw new InvalidInputException("Employee with ID " + israeliId + " not found");
        }

        // Check if employee is active
        if (!employeeDTO.isActive()) {
            throw new InvalidInputException(
                    "Employee with ID " + israeliId + " is not active - cannot update information");
        }

        // Validate bank account input
        if (bankAccount == null) {
            throw new InvalidInputException("Bank account information cannot be null");
        }

        // Convert BankAccount to BankAccountDTO
        DTOs.BankAccountDTO bankAccountDTO = new DTOs.BankAccountDTO(
                israeliId,
                bankAccount.getBankNumber(),
                bankAccount.getBankBranchNumber(),
                bankAccount.getBankAccountNumber());

        // Update only the bank account information
        employeeDTO.setBankAccount(bankAccountDTO);
        employeeDTO.setUpdateDate(LocalDate.now());

        // Update employee in repository
        return employeeRepository.update(employeeDTO);
    }

    /**
     * Removes the bank account information for an employee.
     *
     * @param doneBy    - The user who updated the employee - for auditing purposes
     *                  and permissions
     * @param israeliId - The Israeli ID of the employee to update
     * @return True if the bank account was removed successfully
     * @throws UnauthorizedPermissionException if the user does not have permission
     * @throws InvalidInputException           if any input is invalid or if the
     *                                         employee does not exist
     */
    public boolean removeEmployeeBankAccount(long doneBy, long israeliId) {
        // Permission handling
        String PERMISSION_REQUIRED = "UPDATE_EMPLOYEE";
        if (!isEmployeeAuthorised(doneBy, PERMISSION_REQUIRED)) {
            throw new UnauthorizedPermissionException("User does not have permission to update employee bank account");
        }

        Employee doneByEmployee = getEmployeeByIsraeliId(doneBy);
        if (doneByEmployee == null) {
            throw new InvalidInputException("Employee with ID " + doneBy + " not found");
        }

        // Check if employee exists
        EmployeeDTO employeeDTO = employeeRepository.getById(israeliId);
        if (employeeDTO == null) {
            throw new InvalidInputException("Employee with ID " + israeliId + " not found");
        }

        // Check if employee is active
        if (!employeeDTO.isActive()) {
            throw new InvalidInputException(
                    "Employee with ID " + israeliId + " is not active - cannot update information");
        }

        // Check if employee has bank account information
        if (employeeDTO.getBankAccount() == null) {
            throw new InvalidInputException(
                    "Employee with ID " + israeliId + " does not have bank account information");
        }

        // Remove bank account information
        employeeDTO.setBankAccount(null);
        employeeDTO.setUpdateDate(LocalDate.now());

        // Update employee in repository
        return employeeRepository.update(employeeDTO);
    }

    /**
     * Determines whether the user with the specified ID can access the Transport
     * module.
     *
     * This method checks if the user exists and has the necessary roles to access
     * the Transport module, such as Driver roles or the Transport Manager role.
     *
     * @param userId The ID of the user to check.
     * @return true if the user can access the Transport module; false otherwise.
     * @throws InvalidInputException if the user does not exist.
     */
    public boolean canAccessTransportModule(long userId) {
        // Check if the user has the "Transport" role
        EmployeeDTO employeeDTO = employeeRepository.getById(userId);
        if (employeeDTO == null) {
            throw new InvalidInputException("Employee with ID " + userId + " not found");
        }

        // Check if the employee has the one of the Driver roles or the Transport
        // Manager role
        Set<String> roles = employeeDTO.getRoles();
        return roles.stream().anyMatch(role -> role.startsWith("Driver") || role.equals(config.ROLE_TRANSPORT_MANAGER)
                || role.equals(config.ROLE_ADMIN));
    }

    public boolean canAccessSuppliersModule(long israeliId) {
        // Check if the employee exists
        EmployeeDTO employeeDTO = employeeRepository.getById(israeliId);
        if (employeeDTO == null) {
            throw new InvalidInputException("Employee with ID " + israeliId + " not found");
        }

        // Check if the employee has the required role for accessing the Suppliers
        // module
        Set<String> roles = employeeDTO.getRoles();
        return roles.contains(config.ROLE_SUPPLIERS_MANAGER) || roles.contains(config.ROLE_ADMIN);
    }

    public boolean canAccessInventoryModule(long israeliId) {
        // Check if the employee exists
        EmployeeDTO employeeDTO = employeeRepository.getById(israeliId);
        if (employeeDTO == null) {
            throw new InvalidInputException("Employee with ID " + israeliId + " not found");
        }

        // Check if the employee has the required role for accessing the Inventory
        // module
        Set<String> roles = employeeDTO.getRoles();
        return roles.contains(config.ROLE_INVENTORY_MANAGER) || roles.contains(config.ROLE_ADMIN);
    }

    public boolean canAccessHRModule(long israeliId) {
        // Check if the employee exists
        EmployeeDTO employeeDTO = employeeRepository.getById(israeliId);
        if (employeeDTO == null) {
            throw new InvalidInputException("Employee with ID " + israeliId + " not found");
        }

        // Check if the employee has the required role for accessing the HR module
        Set<String> roles = employeeDTO.getRoles();
        return roles.contains(config.ROLE_HR_MANAGER) || roles.contains(config.ROLE_ADMIN);
    }
}
