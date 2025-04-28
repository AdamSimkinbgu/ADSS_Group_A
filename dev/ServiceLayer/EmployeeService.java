package ServiceLayer;

import DomainLayer.AuthorisationController;
import DomainLayer.Employee;
import DomainLayer.EmployeeController;
import DomainLayer.exception.InvalidInputException;
import DomainLayer.exception.UnauthorizedPermissionException;
import ServiceLayer.exception.AuthorizationException;
import ServiceLayer.exception.EmployeeNotFoundException;
import ServiceLayer.exception.ServiceException;
import ServiceLayer.exception.ValidationException;
import ServiceLayer.response.Response;
import ServiceLayer.util.JsonUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class EmployeeService {
    private final EmployeeController employeeController;
    private final AuthorisationController authorisationController;


    public EmployeeService(EmployeeController employeeController , AuthorisationController authorisationController) {
        this.employeeController = employeeController;
        this.authorisationController = authorisationController;
    }

    /**
     * Checks if an employee is authorized to perform an action.
     *
     * @param israeliId - The Israeli ID of the employee to check
     * @param permission - The permission to check for
     * @return JSON string containing a Response with the result of the authorization check
     */
    public String isEmployeeAuthorised(long israeliId, String permission) {
        try {
            boolean isAuthorized = employeeController.isEmployeeAuthorised(israeliId, permission);
            return JsonUtil.successResponse(isAuthorized);
        } catch (UnauthorizedPermissionException e) {
            return JsonUtil.errorResponse("Authorization error: " + e.getMessage());
        } catch (Exception e) {
            return JsonUtil.errorResponse("Error checking authorization: " + e.getMessage());
        }
    }

    // ========================
    // Get All methods for PL
    // ========================
    /**
     * Gets all employees in the system.
     *
     * @return JSON string containing a Response with all employees
     * @throws ServiceException if an error occurs while retrieving employees
     */
    public String getAllEmployees() {
        try {
            Map<Long, Employee> employeeMap = employeeController.getAllEmployees();
            List<Employee> employees = new ArrayList<>(employeeMap.values());
            return JsonUtil.successResponse(employees);
        } catch (Exception e) {
            return JsonUtil.errorResponse("Error retrieving all employees: " + e.getMessage());
        }
    }

    /**
     * Gets all roles in the system.
     *
     * @return JSON string containing a Response with all role names
     * @throws ServiceException if an error occurs while retrieving roles
     */
    public String getAllRoles() {
        try {
            Map<String, String[]> rolesMap = authorisationController.getAllRolesWithPermissions();
            List<String> roles = new ArrayList<>(rolesMap.keySet());
            return JsonUtil.successResponse(roles);
        } catch (Exception e) {
            return JsonUtil.errorResponse("Error retrieving all roles: " + e.getMessage());
        }
    }

    /**
     * Gets all permissions in the system.
     *
     * @return JSON string containing a Response with all permission names
     * @throws ServiceException if an error occurs while retrieving permissions
     */
    public String getAllPermissions() {
        try {
            Set<String> permissions = authorisationController.getAllPermissions();
            return JsonUtil.successResponse(new ArrayList<>(permissions));
        } catch (Exception e) {
            return JsonUtil.errorResponse("Error retrieving all permissions: " + e.getMessage());
        }
    }

    /**
     * Gets details of a specific role, including its permissions.
     *
     * @param roleName The name of the role to get details for
     * @return JSON string containing a Response with the role details
     * @throws ServiceException if an error occurs while retrieving role details
     */
    public String getRoleDetails(String roleName) {
        try {
            // Validate input
            if (roleName == null || roleName.trim().isEmpty()) {
                return JsonUtil.errorResponse("Role name cannot be null or empty");
            }

            Map<String, HashSet<String>> roleDetails = authorisationController.getRoleDetails(roleName);
            if (roleDetails != null && !roleDetails.isEmpty()) {
                return JsonUtil.successResponse(roleDetails);
            } else {
                return JsonUtil.errorResponse("Role not found: " + roleName);
            }
        } catch (InvalidInputException e) {
            return JsonUtil.errorResponse(e.getMessage());
        } catch (Exception e) {
            return JsonUtil.errorResponse("Error retrieving role details: " + e.getMessage());
        }
    }

    // ========================
    // Employee related methods
    // ========================

    /**
     * Creates a new employee.
     * NOTE: CreateEmployee with NO roles or permissions need to be added to the employee in another action!
     *
     * @param doneBy         The ID of the user who is creating the employee.
     * @param israeliId      The Israeli ID of the employee.
     * @param firstName      The first name of the employee.
     * @param lastName       The last name of the employee.
     * @param salary         The salary of the employee.
     * @param termsOfEmployment The terms of employment for the employee.
     * @param startOfEmployment The start date of employment for the employee.
     * @return JSON string containing a Response with a success or error message
     */
    public String createEmployee(long doneBy, long israeliId, String firstName, String lastName, long salary, Map<String, Object> termsOfEmployment, LocalDate startOfEmployment) {
        try {
            boolean result = employeeController.createEmployee(doneBy, israeliId, firstName, lastName, salary, termsOfEmployment, null, startOfEmployment);

            if (result) {
                return JsonUtil.successResponse("Employee created successfully");
            } else {
                return JsonUtil.errorResponse("Failed to create employee");
            }
        } catch (UnauthorizedPermissionException e) {
            return JsonUtil.errorResponse("Permission denied: " + e.getMessage());
        } catch (InvalidInputException e) {
            return JsonUtil.errorResponse("Invalid input: " + e.getMessage());
        } catch (Exception e) {
            return JsonUtil.errorResponse("Error creating employee: " + e.getMessage());
        }
    }
    /**
     * Updates an existing employee.
     *
     * @param doneBy         The ID of the user who is updating the employee.
     * @param israeliId      The Israeli ID of the employee.
     * @param firstName      The new first name of the employee.
     * @param lastName       The new last name of the employee.
     * @param salary         The new salary of the employee.
     * @param termsOfEmployment The new terms of employment for the employee.
     * @param active         Whether the employee is active or not.
     * @return JSON string containing a Response with a success or error message
     */
    public String updateEmployee(long doneBy, long israeliId, String firstName, String lastName, long salary, Map<String, Object> termsOfEmployment, boolean active) {
        try {
            // Check if employee exists
            Employee employee = employeeController.getEmployeeByIsraeliId(israeliId);
            if (employee == null) {
                return JsonUtil.errorResponse("Employee not found with ID: " + israeliId);
            }

            boolean result = employeeController.updateEmployee(doneBy, israeliId, firstName, lastName, salary, termsOfEmployment, active);
            if (result) {
                return JsonUtil.successResponse("Employee updated successfully");
            } else {
                return JsonUtil.errorResponse("Failed to update employee");
            }
        } catch (UnauthorizedPermissionException e) {
            return JsonUtil.errorResponse("Permission denied: " + e.getMessage());
        } catch (InvalidInputException e) {
            return JsonUtil.errorResponse("Invalid input: " + e.getMessage());
        } catch (Exception e) {
            return JsonUtil.errorResponse("Error updating employee: " + e.getMessage());
        }
    }

    /**
     * Deactivates an employee.
     *
     * @param doneBy    The ID of the user who is deactivating the employee.
     * @param israeliId The Israeli ID of the employee to deactivate.
     * @return JSON string containing a Response with a success or error message
     */
    public String deactivateEmployee(long doneBy, long israeliId) {
        try {
            // Validate input parameters
            if (String.valueOf(israeliId).length() != 9) {
                return JsonUtil.errorResponse("Israeli ID must be 9 digits");
            }

            // Check if employee exists
            Employee employee = employeeController.getEmployeeByIsraeliId(israeliId);
            if (employee == null) {
                return JsonUtil.errorResponse("Employee not found with ID: " + israeliId);
            }

            boolean result = employeeController.deactivateEmployee(doneBy, israeliId);
            if (result) {
                return JsonUtil.successResponse("Employee deactivated successfully");
            } else {
                return JsonUtil.errorResponse("Failed to deactivate employee");
            }
        } catch (UnauthorizedPermissionException e) {
            return JsonUtil.errorResponse("Permission denied: " + e.getMessage());
        } catch (InvalidInputException e) {
            return JsonUtil.errorResponse("Invalid input: " + e.getMessage());
        } catch (Exception e) {
            return JsonUtil.errorResponse("Error deactivating employee: " + e.getMessage());
        }
    }

    // ====================
    // Role related methods
    // ====================
    /**
     * Creates a new role.
     *
     * @param doneBy         The ID of the user who is creating the role.
     * @param roleName       The name of the role to be created.
     * @return JSON string containing a Response with a success or error message
     */
    public String createRole(long doneBy, String roleName) {
        try {
            // Check if user has permission to create roles
            String PERMISSION_REQUIRED = "CREATE_ROLE";
            boolean isAuth = employeeController.isEmployeeAuthorised(doneBy, PERMISSION_REQUIRED);
            if (!isAuth) {
                return JsonUtil.errorResponse("Permission denied: Cannot create roles");
            }

            // Create role with empty permissions set
            boolean success = authorisationController.createRole(doneBy, roleName, new HashSet<>());

            if (success) {
                return JsonUtil.successResponse("Role '" + roleName + "' created successfully");
            } else {
                return JsonUtil.errorResponse("Failed to create role: Role may already exist");
            }
        } catch (RuntimeException e) {
            return JsonUtil.errorResponse("Error creating role: " + e.getMessage());
        }
    }

    /**
     * Creates a new role with specified permissions.
     *
     * @param doneBy      The ID of the user who is creating the role
     * @param roleName    The name of the role to be created
     * @param permissions Set of permission names to assign to the role
     * @return JSON string containing a Response with a success or error message
     */
    public String createRoleWithPermissions(long doneBy, String roleName, Set<String> permissions) {
        try {
            // Check if user has permission to create roles
            String PERMISSION_REQUIRED = "CREATE_ROLE";
            boolean isAuth = employeeController.isEmployeeAuthorised(doneBy, PERMISSION_REQUIRED);
            if (!isAuth) {
                return JsonUtil.errorResponse("Permission denied: Cannot create roles");
            }

            // Validation is now handled in the domain layer
            boolean success = authorisationController.createRole(doneBy, roleName, permissions);

            if (success) {
                return JsonUtil.successResponse("Role '" + roleName + "' created successfully with " + permissions.size() + " permissions");
            } else {
                return JsonUtil.errorResponse("Failed to create role: Role may already exist");
            }
        } catch (UnauthorizedPermissionException e) {
            return JsonUtil.errorResponse("Permission denied: " + e.getMessage());
        } catch (InvalidInputException e) {
            return JsonUtil.errorResponse("Error creating role: " + e.getMessage());
        } catch (RuntimeException e) {
            return JsonUtil.errorResponse("Unexpected error: " + e.getMessage());
        }
    }
    /**
     * Adds a role to an employee.
     *
     * @param doneBy         The ID of the user who is adding the role.
     * @param israeliId      The Israeli ID of the employee.
     * @param roleName       The name of the role to be added.
     * @return JSON string containing a Response with a success or error message
     */
    public String addRoleToEmployee(long doneBy, long israeliId, String roleName) {
        try {
            // Validation is now handled in the domain layer
            boolean success = employeeController.addRoleToEmployee(doneBy, israeliId, roleName);
            if (success) {
                return JsonUtil.successResponse("Role '" + roleName + "' added successfully to employee with ID " + israeliId);
            } else {
                return JsonUtil.errorResponse("Failed to add role");
            }
        } catch (UnauthorizedPermissionException e) {
            return JsonUtil.errorResponse("Permission denied: " + e.getMessage());
        } catch (InvalidInputException e) {
            return JsonUtil.errorResponse("Error adding role: " + e.getMessage());
        } catch (RuntimeException e) {
            return JsonUtil.errorResponse("Unexpected error: " + e.getMessage());
        }
    }

    /**
     * Removes a role from an employee.
     *
     * @param doneBy         The ID of the user who is removing the role.
     * @param israeliId      The Israeli ID of the employee.
     * @param roleName       The name of the role to be removed.
     * @return JSON string containing a Response with a success or error message
     */
    public String removeRoleFromEmployee(long doneBy, long israeliId, String roleName) {
        try {
            // Validation is now handled in the domain layer
            boolean success = employeeController.removeRoleFromEmployee(doneBy, israeliId, roleName);
            if (success) {
                return JsonUtil.successResponse("Role '" + roleName + "' removed successfully from employee with ID " + israeliId);
            } else {
                return JsonUtil.errorResponse("Failed to remove role");
            }
        } catch (UnauthorizedPermissionException e) {
            return JsonUtil.errorResponse("Permission denied: " + e.getMessage());
        } catch (InvalidInputException e) {
            return JsonUtil.errorResponse("Error removing role: " + e.getMessage());
        } catch (RuntimeException e) {
            return JsonUtil.errorResponse("Unexpected error: " + e.getMessage());
        }
    }

    // ===========================
    // Permission related methods
    // ===========================

    /**
     * Adds a permission to a role.
     *
     * @param doneBy         The ID of the user who is adding the permission.
     * @param roleName       The name of the role to which the permission will be added.
     * @param permissionName  The name of the permission to be added.
     * @return JSON string containing a Response with a success or error message
     */
    public String addPermissionToRole(long doneBy, String roleName, String permissionName) {
        String PERMISSION_REQUIRED = "ADD_PERMISSION_TO_ROLE";
        try {
            // Check authorization
            boolean isAuth = employeeController.isEmployeeAuthorised(doneBy, PERMISSION_REQUIRED);
            if (!isAuth) {
                return JsonUtil.errorResponse("Permission denied: Cannot add permissions to roles");
            }

            // Validation is now handled in the domain layer
            boolean success = authorisationController.addPermissionToRole(roleName, permissionName);

            if (success) {
                return JsonUtil.successResponse("Permission '" + permissionName + "' added successfully to role '" + roleName + "'");
            } else {
                return JsonUtil.errorResponse("Failed to add permission: Permission is already assigned to this role");
            }
        } catch (InvalidInputException e) {
            return JsonUtil.errorResponse("Error adding permission: " + e.getMessage());
        } catch (RuntimeException e) {
            return JsonUtil.errorResponse("Unexpected error: " + e.getMessage());
        }
    }

    /**
     * Removes a permission from a role.
     *
     * @param doneBy         The ID of the user who is removing the permission.
     * @param roleName       The name of the role from which the permission will be removed.
     * @param permissionName  The name of the permission to be removed.
     * @return JSON string containing a Response with a success or error message
     */
    public String removePermissionFromRole(long doneBy, String roleName, String permissionName) {
        String PERMISSION_REQUIRED = "REMOVE_PERMISSION_FROM_ROLE";
        try {
            // Check authorization
            boolean isAuth = employeeController.isEmployeeAuthorised(doneBy, PERMISSION_REQUIRED);
            if (!isAuth) {
                return JsonUtil.errorResponse("Permission denied: Cannot remove permissions from roles");
            }

            // Validation is now handled in the domain layer
            boolean success = authorisationController.removePermissionFromRole(roleName, permissionName);

            if (success) {
                return JsonUtil.successResponse("Permission '" + permissionName + "' removed successfully from role '" + roleName + "'");
            } else {
                return JsonUtil.errorResponse("Failed to remove permission: Permission is not assigned to this role");
            }
        } catch (InvalidInputException e) {
            return JsonUtil.errorResponse("Error removing permission: " + e.getMessage());
        } catch (RuntimeException e) {
            return JsonUtil.errorResponse("Unexpected error: " + e.getMessage());
        }
    }

    /**
     * Creates a new permission.
     *
     * @param doneBy         The ID of the user who is creating the permission.
     * @param permissionName The name of the new permission.
     * @return JSON string containing a Response with a success or error message
     */
    public String createPermission(long doneBy, String permissionName) {
        try {
            // Check if user has permission to create permissions
            String PERMISSION_REQUIRED = "CREATE_PERMISSION";
            boolean isAuth = employeeController.isEmployeeAuthorised(doneBy, PERMISSION_REQUIRED);
            if (!isAuth) {
                return JsonUtil.errorResponse("Permission denied: Cannot create permissions");
            }

            // Validation is now handled in the domain layer
            boolean success = authorisationController.createPermission(permissionName);

            if (success) {
                return JsonUtil.successResponse("Permission '" + permissionName + "' created successfully");
            } else {
                return JsonUtil.errorResponse("Failed to create permission: Permission may already exist");
            }
        } catch (UnauthorizedPermissionException e) {
            return JsonUtil.errorResponse("Permission denied: " + e.getMessage());
        } catch (InvalidInputException e) {
            return JsonUtil.errorResponse("Error creating permission: " + e.getMessage());
        } catch (RuntimeException e) {
            return JsonUtil.errorResponse("Unexpected error: " + e.getMessage());
        }
    }

    /**
     * Clones an existing role to create a new role with the same permissions.
     *
     * @param doneBy          The ID of the user who is cloning the role
     * @param existingRoleName The name of the role to clone
     * @param newRoleName     The name of the new role to create
     * @return JSON string containing a Response with a success or error message
     */
    public String cloneRole(long doneBy, String existingRoleName, String newRoleName) {
        try {
            // Check if user has permission to create roles
            String PERMISSION_REQUIRED = "CREATE_ROLE";
            boolean isAuth = employeeController.isEmployeeAuthorised(doneBy, PERMISSION_REQUIRED);
            if (!isAuth) {
                return JsonUtil.errorResponse("Permission denied: Cannot create roles");
            }

            // Get existing role's permissions
            Map<String, HashSet<String>> roleDetails = authorisationController.getRoleDetails(existingRoleName);
            if (roleDetails.isEmpty()) {
                return JsonUtil.errorResponse("Source role not found: " + existingRoleName);
            }

            HashSet<String> permissions = roleDetails.get(existingRoleName);

            // Create new role with same permissions
            // Validation of the new role name and permissions is handled in the domain layer
            boolean success = authorisationController.createRole(doneBy, newRoleName, permissions);

            if (success) {
                return JsonUtil.successResponse("Role '" + newRoleName + "' cloned successfully from '" + existingRoleName + "'");
            } else {
                return JsonUtil.errorResponse("Failed to clone role: Target role may already exist");
            }
        } catch (UnauthorizedPermissionException e) {
            return JsonUtil.errorResponse("Permission denied: " + e.getMessage());
        } catch (InvalidInputException e) {
            return JsonUtil.errorResponse("Error cloning role: " + e.getMessage());
        } catch (RuntimeException e) {
            return JsonUtil.errorResponse("Unexpected error: " + e.getMessage());
        }
    }

    /**
     * Retrieves an employee by their Israeli ID.
     *
     * @param israeliId The Israeli ID of the employee to retrieve
     * @return JSON string containing a Response with the employee data
     */
    public String getEmployeeById(long israeliId) {
        try {
            // Validate input
            if (String.valueOf(israeliId).length() != 9) {
                return JsonUtil.errorResponse("Israeli ID must be 9 digits");
            }

            Employee employee = employeeController.getEmployeeByIsraeliId(israeliId);
            if (employee == null) {
                return JsonUtil.errorResponse("Employee not found with ID: " + israeliId);
            }
            return JsonUtil.successResponse(employee);
        } catch (InvalidInputException e) {
            return JsonUtil.errorResponse("Invalid input: " + e.getMessage());
        } catch (Exception e) {
            return JsonUtil.errorResponse("Error retrieving employee: " + e.getMessage());
        }
    }

    /**
     * Checks if an employee has a specific permission.
     *
     * @param israeliId The Israeli ID of the employee to check
     * @param permission The permission to check for
     * @return JSON string containing a Response with the result of the permission check
     */
    public String hasPermission(long israeliId, String permission) {
        try {
            boolean hasPermission = employeeController.hasPermission(israeliId, permission);
            return JsonUtil.successResponse(hasPermission);
        } catch (UnauthorizedPermissionException e) {
            return JsonUtil.errorResponse("Authorization error: " + e.getMessage());
        } catch (Exception e) {
            return JsonUtil.errorResponse("Error checking authorization: " + e.getMessage());
        }
    }
}
