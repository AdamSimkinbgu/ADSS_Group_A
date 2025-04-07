package DomainLayer;

import java.util.HashSet;
import java.util.Set;

public class AuthorisationController {

    private final Set<Role> roles; // Set of all roles
    private final Set<String> permissions; // Set of all permissions

    public AuthorisationController(Set<Role> roles, Set<String> permissions) {
        this.roles = new HashSet<>(roles);
        this.permissions = new HashSet<>(permissions);
    }

    /**
     * Checks if the employee has the required permission.
     *
     * @param employee           - The employee to check permissions for
     * @param permissionRequired - The permission to check for
     * @return true if the employee has the permission, false otherwise
     */
    public boolean HasPermission(Employee employee, String permissionRequired) {
        boolean has = employee.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .anyMatch(permission -> permission.equals(permissionRequired));
        if (!has) {
            return false; // TODO: throw new UnauthorizedPermission("Employee does not have permission: " + permissionRequired);
        }
        return has;
    }

    /**
     * Checks if the employee has the required role.
     *
     * @param employee     - The employee to check role for
     * @param roleRequired - The role to check for
     * @return true if the employee has the role, false otherwise
     */
    boolean HasRole(Employee employee, Role roleRequired) {
        return employee.getRoles().stream().anyMatch(role -> role.getName().equals(roleRequired.getName()));
    }

    /**
     * Adds a role to the employee's roles.
     *
     * @param employee - The employee to add the role to
     * @param role     - The role to add
     * @return true if the role was added successfully, false if the role already exists
     */
    boolean AddRole(Employee employee, Role role) {
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
    public boolean removeRole(Employee employee, Role roleRequired) {
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
     */
    public boolean addPermissionToRole(Role role, String permission) {
        if (role.getPermissions().contains(permission)) {
            return false; // Permission already exists
        }
        role.getPermissions().add(permission);
        return true;
    }

    public boolean addPermissionToRoleByName(String roleName, String permissionName) {
        Role role = getRoleByName(roleName);

        if (role == null || permissionName == null) {
            return false; // Role does not exist
        }
        return addPermissionToRole(role, permissionName);
    }

    /**
     * Removes a permission from the role's permissions.
     *
     * @param role       - The role to remove the permission from
     * @param permission - The permission to remove
     * @return true if the permission was removed successfully, false if the permission does not exist
     */
    public boolean removePermissionFromRole(String roleName, String permissionName) {
        Role role = getRoleByName(roleName);
        if (!role.getPermissions().contains(permissionName)) {
            return false; // Permission does not exist
        }
        role.getPermissions().remove(permissionName);
        return true;
    }

    /**
     * Creates a new role with the specified name and permissions.
     *
     * @param roleName    - The name of the new role
     * @param permissions - The permissions to assign to the new role
     * @return true if the role was created successfully, false if the role already exists
     */
    public boolean createRole(long doneBy, String roleName, Set<String> permissions) {
        String PERMISSION_REQUIRED = "CREATE_ROLE"; // Permission required to create a role

        if (roles.stream().anyMatch(role -> role.getName().equals(roleName))) {
            return false; // Role already exists
        }
        Role newRole = new Role(roles.size() + 1, roleName, permissions);
        roles.add(newRole);
        return true;
    }

    /**
     * Deletes a role from the system.
     *
     * @param role - The role to delete
     * @return true if the role was deleted successfully, false if the role does not exist
     */
    boolean deleteRole(String role) {
        Role roleToDelete = getRoleByName(role);

        if (!roles.contains(roleToDelete)) {
            return false; // Role does not exist
        }
        roles.remove(roleToDelete);
        return true;
    }

    /**
     * Creates a new permission with the specified name and description.
     *
     * @param permissionName - The name of the new permission
     * @return true if the permission was created successfully, false if the permission already exists
     */
    boolean CreatePermission(String permissionName) {
        // Validate input
        if (permissions.stream().anyMatch(permission -> permission.equals(permissionName))) {
            return false; // Permission already exists
        }
        permissions.add(permissionName);
        return true;
    }

    /**
     * Deletes a permission from the system.
     *
     * @param permission - The permission to delete
     * @return true if the permission was deleted successfully, false if the permission does not exist
     */
    boolean DeletePermission(String permission) {
        if (!permissions.contains(permission)) {
            return false; // Permission does not exist
        }
        permissions.remove(permission);
        return true;
    }

    /**
     * Retrieves a role by its name.
     *
     * @param roleName - The name of the role to search for
     * @return The role if found, null otherwise
     */
    public Role getRoleByName(String roleName) {
        return roles.stream()
                .filter(role -> role.getName().equals(roleName))
                .findFirst()
                .orElse(null); // Return null if role not found
    }
}
