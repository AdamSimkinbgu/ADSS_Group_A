package DomainLayer.EmployeeSubModule.Repository.interfaces;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Repository interface for authorization-related entities.
 * This interface defines methods for accessing and manipulating roles and permissions.
 */
public interface AuthorisationRepository {
    
    /**
     * Creates a new role.
     *
     * @param roleName The name of the role to create
     * @return true if the role was created successfully, false otherwise
     */
    boolean createRole(String roleName);
    
    /**
     * Deletes a role.
     *
     * @param roleName The name of the role to delete
     * @return true if the role was deleted successfully, false otherwise
     */
    boolean deleteRole(String roleName);
    
    /**
     * Creates a new permission.
     *
     * @param permissionName The name of the permission to create
     * @return true if the permission was created successfully, false otherwise
     */
    boolean createPermission(String permissionName);
    
    /**
     * Deletes a permission.
     *
     * @param permissionName The name of the permission to delete
     * @return true if the permission was deleted successfully, false otherwise
     */
    boolean deletePermission(String permissionName);
    
    /**
     * Adds a permission to a role.
     *
     * @param roleName The name of the role
     * @param permissionName The name of the permission to add
     * @return true if the permission was added successfully, false otherwise
     */
    boolean addPermissionToRole(String roleName, String permissionName);
    
    /**
     * Removes a permission from a role.
     *
     * @param roleName The name of the role
     * @param permissionName The name of the permission to remove
     * @return true if the permission was removed successfully, false otherwise
     */
    boolean removePermissionFromRole(String roleName, String permissionName);
    
    /**
     * Retrieves all roles.
     *
     * @return A set of all role names
     */
    Set<String> getAllRoles();
    
    /**
     * Retrieves all permissions.
     *
     * @return A set of all permission names
     */
    Set<String> getAllPermissions();
    
    /**
     * Retrieves all roles with their associated permissions.
     *
     * @return A map of role names to sets of permission names
     */
    Map<String, HashSet<String>> getAllRolesWithPermissions();
    
    /**
     * Retrieves all permissions for a specific role.
     *
     * @param roleName The name of the role
     * @return A set of permission names associated with the role
     */
    Set<String> getPermissionsForRole(String roleName);
    
    /**
     * Checks if a role exists.
     *
     * @param roleName The name of the role to check
     * @return true if the role exists, false otherwise
     */
    boolean roleExists(String roleName);
    
    /**
     * Checks if a permission exists.
     *
     * @param permissionName The name of the permission to check
     * @return true if the permission exists, false otherwise
     */
    boolean permissionExists(String permissionName);
}