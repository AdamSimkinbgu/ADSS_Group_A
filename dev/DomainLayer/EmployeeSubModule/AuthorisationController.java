package DomainLayer.EmployeeSubModule;

import DTOs.RoleDTO;
import DomainLayer.EmployeeSubModule.Repository.interfaces.AuthorisationRepository;
import DomainLayer.exception.InvalidInputException;
import DomainLayer.exception.UnauthorizedPermissionException;

import java.util.*;

public class AuthorisationController {

    private final AuthorisationRepository authorisationRepository;

    public AuthorisationController(AuthorisationRepository authorisationRepository) {
        this.authorisationRepository = authorisationRepository;
    }

    public boolean isRoleExists(String role) {
        return authorisationRepository.roleExists(role);
    }

    /**
     * Checks if the employee has the required permission.
     *
     * @param employee           - The employee to check permissions for
     * @param permissionRequired - The permission to check for
     * @return true if the employee has the permission
     * @throws UnauthorizedPermissionException if the employee does not have the permission
     */
    public boolean hasPermission(Employee employee, String permissionRequired) {
        if (employee == null) {
            throw new InvalidInputException("Employee cannot be null");
        }
        if (permissionRequired == null || permissionRequired.isEmpty()) {
            throw new InvalidInputException("Permission required cannot be null or empty");
        }

        Map<String, HashSet<String>> rolesWithPermissions = authorisationRepository.getAllRolesWithPermissions();
        boolean hasPermission = employee.getRoles()
                .stream()
                .anyMatch(role -> rolesWithPermissions.containsKey(role) && rolesWithPermissions.get(role).contains(permissionRequired));

        if (!hasPermission) {
            throw new UnauthorizedPermissionException("Employee does not have the required permission: " + permissionRequired);
        }

        return true;
    }

    /**
     * Checks if the employee has the required role.
     *
     * @param employee     - The employee to check role for
     * @param roleRequired - The role to check for
     * @return true if the employee has the role, false otherwise
     * @throws InvalidInputException if employee or roleRequired is null
     */
    public boolean hasRole(Employee employee, String roleRequired) {
        if (employee == null) {
            throw new InvalidInputException("Employee cannot be null");
        }
        if (roleRequired == null || roleRequired.isEmpty()) {
            throw new InvalidInputException("Role required cannot be null or empty");
        }
        return employee.getRoles().stream().anyMatch(role -> role.equals(roleRequired));
    }

    /**
     * Adds a role to the employee's roles.
     *
     * @param employee - The employee to add the role to
     * @param role     - The role to add
     * @return true if the role was added successfully, false if the role already exists
     * @throws InvalidInputException if employee or role is null or if role doesn't exist
     */
    public boolean addRole(Employee employee, String role) {
        if (employee == null) {
            throw new InvalidInputException("Employee cannot be null");
        }
        if (role == null || role.isEmpty()) {
            throw new InvalidInputException("Role cannot be null or empty");
        }
        if (!authorisationRepository.roleExists(role)) {
            throw new InvalidInputException("Role does not exist: " + role);
        }
        if (employee.getRoles().contains(role)) {
            return false; // Role already exists
        }
        employee.getRoles().add(role);
        return true;
    }

    /**
     * Removes a role from the employee's roles.
     *
     * @param employee     - The employee to remove the role from
     * @param roleRequired - The role to remove
     * @return true if the role was removed successfully, false if the role does not exist
     */
    public boolean removeRole(Employee employee, String roleRequired) {
        if (!employee.getRoles().contains(roleRequired)) {
            return false; // Role does not exist
        }
        employee.getRoles().remove(roleRequired);
        return true;
    }

    /**
     * Adds a permission to the role's permissions.
     *
     * @param role       - The role to add the permission to
     * @param permission - The permission to add
     * @return true if the permission was added successfully, false if the permission already exists
     * @throws InvalidInputException if role or permission is null or empty, or if role or permission doesn't exist
     */
    public boolean addPermissionToRole(String role, String permission) {
        // Validate input
        if (role == null || role.trim().isEmpty()) {
            throw new InvalidInputException("Role name cannot be null or empty");
        }
        if (permission == null || permission.trim().isEmpty()) {
            throw new InvalidInputException("Permission name cannot be null or empty");
        }

        // Check if role exists
        if (!authorisationRepository.roleExists(role)) {
            throw new InvalidInputException("Role does not exist: " + role);
        }

        // Check if permission exists
        if (!authorisationRepository.permissionExists(permission)) {
            throw new InvalidInputException("Permission does not exist: " + permission);
        }

        // Check if permission already exists in role
        Set<String> rolePermissions = authorisationRepository.getPermissionsForRole(role);
        if (rolePermissions.contains(permission)) {
            return false; // Permission already exists
        }

        // Add permission to role
        return authorisationRepository.addPermissionToRole(role, permission);
    }

    /**
     * Removes a permission from the role's permissions.
     *
     * @param roleName       - The role to remove the permission from
     * @param permissionName - The permission to remove
     * @return true if the permission was removed successfully, false if the permission does not exist
     * @throws InvalidInputException if roleName or permissionName is null or empty, or if role doesn't exist
     */
    public boolean removePermissionFromRole(String roleName, String permissionName) {
        // Validate input
        if (roleName == null || roleName.trim().isEmpty()) {
            throw new InvalidInputException("Role name cannot be null or empty");
        }
        if (permissionName == null || permissionName.trim().isEmpty()) {
            throw new InvalidInputException("Permission name cannot be null or empty");
        }

        // Check if role exists
        if (!authorisationRepository.roleExists(roleName)) {
            throw new InvalidInputException("Role does not exist: " + roleName);
        }

        // Check if permission exists in role
        Set<String> rolePermissions = authorisationRepository.getPermissionsForRole(roleName);
        if (!rolePermissions.contains(permissionName)) {
            return false; // Permission does not exist in this role
        }

        // Remove permission from role
        return authorisationRepository.removePermissionFromRole(roleName, permissionName);
    }

    /**
     * Creates a new role with the specified name and permissions.
     *
     * @param doneBy      - The ID of the employee creating the role
     * @param roleName    - The name of the new role
     * @param permissions - The permissions to assign to the new role
     * @return true if the role was created successfully, false if the role already exists
     * @throws InvalidInputException if roleName or permissions is null or if any permission doesn't exist
     */
    public boolean createRole(long doneBy, String roleName, Set<String> permissions) {
        // Permission check is in the Service layer

        // Validate input
        if (roleName == null || roleName.trim().isEmpty()) {
            throw new InvalidInputException("Role name cannot be null or empty");
        }
        if (permissions == null) {
            throw new InvalidInputException("Permissions cannot be null");
        }

        // Check if all permissions exist
        for (String permission : permissions) {
            if (!authorisationRepository.permissionExists(permission)) {
                throw new InvalidInputException("Permission does not exist: " + permission);
            }
        }

        // Check if role already exists
        if (authorisationRepository.roleExists(roleName)) {
            return false; // Role already exists
        }

        // Create role
        boolean created = authorisationRepository.createRole(roleName.trim());
        if (!created) {
            return false;
        }

        // Add permissions to role
        for (String permission : permissions) {
            authorisationRepository.addPermissionToRole(roleName.trim(), permission);
        }

        return true;
    }

    /**
     * Deletes a role from the system.
     *
     * @param roleToDelete - The role to delete
     * @return true if the role was deleted successfully, false if the role does not exist
     * @throws InvalidInputException if roleToDelete is null or empty
     */
    public boolean deleteRole(String roleToDelete) {
        if (roleToDelete == null || roleToDelete.trim().isEmpty()) {
            throw new InvalidInputException("Role name cannot be null or empty");
        }

        if (!authorisationRepository.roleExists(roleToDelete)) {
            return false; // Role does not exist
        }
        
        return authorisationRepository.deleteRole(roleToDelete);
    }

    /**
     * Creates a new permission with the specified name.
     *
     * @param permissionName - The name of the new permission
     * @return true if the permission was created successfully, false if the permission already exists
     * @throws InvalidInputException if permissionName is null or empty
     */
    public boolean createPermission(String permissionName) {
        // Validate input
        if (permissionName == null || permissionName.trim().isEmpty()) {
            throw new InvalidInputException("Permission name cannot be null or empty");
        }

        String trimmedPermissionName = permissionName.trim();
        if (authorisationRepository.permissionExists(trimmedPermissionName)) {
            return false; // Permission already exists
        }

        return authorisationRepository.createPermission(trimmedPermissionName);
    }

    /**
     * Deletes a permission from the system.
     *
     * @param permission - The permission to delete
     * @return true if the permission was deleted successfully, false if the permission does not exist
     * @throws InvalidInputException if permission is null or empty
     */
    public boolean deletePermission(String permission) {
        if (permission == null || permission.trim().isEmpty()) {
            throw new InvalidInputException("Permission name cannot be null or empty");
        }

        if (!authorisationRepository.permissionExists(permission)) {
            return false; // Permission does not exist
        }

        // Check if any role uses this permission
        Map<String, HashSet<String>> rolesWithPermissions = authorisationRepository.getAllRolesWithPermissions();
        for (HashSet<String> rolePermissions : rolesWithPermissions.values()) {
            if (rolePermissions.contains(permission)) {
                throw new InvalidInputException("Cannot delete permission that is in use by a role");
            }
        }

        return authorisationRepository.deletePermission(permission);
    }

    public Set<String> getAllRoles() {
        return authorisationRepository.getAllRoles();
    }

    public Map<String,String[]> getAllRolesWithPermissions() {
        Map<String,String[]> rolesMap = new HashMap<>();
        Map<String, HashSet<String>> rolesWithPermissions = authorisationRepository.getAllRolesWithPermissions();
        
        for (Map.Entry<String, HashSet<String>> entry : rolesWithPermissions.entrySet()) {
            String roleName = entry.getKey();
            String[] permissionsArray = entry.getValue().toArray(new String[0]);
            rolesMap.put(roleName, permissionsArray);
        }
        return rolesMap;
    }

    public Set<String> getAllPermissions() {
        return authorisationRepository.getAllPermissions();
    }

    public Map<String, HashSet<String>> getRoleDetails(String roleName) {
        Map<String, HashSet<String>> roleDetails = new HashMap<>();
        if (authorisationRepository.roleExists(roleName)) {
            Set<String> permissions = authorisationRepository.getPermissionsForRole(roleName);
            roleDetails.put(roleName, new HashSet<>(permissions));
        }
        return roleDetails;
    }
    public RoleDTO getRoleDTO(String roleName) {
        if (roleName == null || roleName.trim().isEmpty()) {
            throw new InvalidInputException("Role name cannot be null or empty");
        }
        if (!authorisationRepository.roleExists(roleName)) {
            throw new InvalidInputException("Role does not exist: " + roleName);
        }
        Set<String> permissions = authorisationRepository.getPermissionsForRole(roleName);
        return new RoleDTO(roleName, permissions);
    }
}