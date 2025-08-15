package DataAccessLayer.EmployeeDAL;

import DTOs.RoleDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Data Access Object for authorization-related entities.
 * This class provides methods to interact with the Roles and Permissions tables in the database.
 * Uses RoleDTO for data transfer between layers where appropriate.
 */
public class AuthorisationDAO {
    private Connection connection;

    public AuthorisationDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Creates a new role in the database.
     *
     * @param roleName The name of the role to create
     * @return true if the role was created successfully, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public boolean createRole(String roleName) throws SQLException {
        String sql = "INSERT INTO Roles (roleName) VALUES (?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, roleName);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Deletes a role from the database.
     *
     * @param roleName The name of the role to delete
     * @return true if the role was deleted successfully, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public boolean deleteRole(String roleName) throws SQLException {
        // First delete role-permission associations
        deleteRolePermissions(roleName);

        // Then delete the role
        String sql = "DELETE FROM Roles WHERE roleName = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, roleName);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Creates a new permission in the database.
     *
     * @param permissionName The name of the permission to create
     * @return true if the permission was created successfully, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public boolean createPermission(String permissionName) throws SQLException {
        String sql = "INSERT INTO Permissions (permissionName) VALUES (?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, permissionName);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Deletes a permission from the database.
     *
     * @param permissionName The name of the permission to delete
     * @return true if the permission was deleted successfully, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public boolean deletePermission(String permissionName) throws SQLException {
        // First check if the permission is used by any role
        if (isPermissionInUse(permissionName)) {
            return false;
        }

        // Then delete the permission
        String sql = "DELETE FROM Permissions WHERE permissionName = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, permissionName);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Adds a permission to a role.
     *
     * @param roleName The name of the role
     * @param permissionName The name of the permission to add
     * @return true if the permission was added successfully, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public boolean addPermissionToRole(String roleName, String permissionName) throws SQLException {
        String sql = "INSERT INTO RolePermissions (roleName, permissionName) VALUES (?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, roleName);
            pstmt.setString(2, permissionName);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Removes a permission from a role.
     *
     * @param roleName The name of the role
     * @param permissionName The name of the permission to remove
     * @return true if the permission was removed successfully, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public boolean removePermissionFromRole(String roleName, String permissionName) throws SQLException {
        String sql = "DELETE FROM RolePermissions WHERE roleName = ? AND permissionName = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, roleName);
            pstmt.setString(2, permissionName);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Deletes all permissions associated with a role.
     *
     * @param roleName The name of the role
     * @throws SQLException if a database access error occurs
     */
    private void deleteRolePermissions(String roleName) throws SQLException {
        String sql = "DELETE FROM RolePermissions WHERE roleName = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, roleName);
            pstmt.executeUpdate();
        }
    }

    /**
     * Checks if a permission is used by any role.
     *
     * @param permissionName The name of the permission to check
     * @return true if the permission is used by at least one role, false otherwise
     * @throws SQLException if a database access error occurs
     */
    private boolean isPermissionInUse(String permissionName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM RolePermissions WHERE permissionName = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, permissionName);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }
        }
    }

    /**
     * Retrieves all roles from the database.
     *
     * @return A set of all role names
     * @throws SQLException if a database access error occurs
     */
    public Set<String> getAllRoles() throws SQLException {
        String sql = "SELECT roleName FROM Roles";
        Set<String> roles = new HashSet<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                roles.add(rs.getString("roleName"));
            }

            return roles;
        }
    }

    /**
     * Retrieves all permissions from the database.
     *
     * @return A set of all permission names
     * @throws SQLException if a database access error occurs
     */
    public Set<String> getAllPermissions() throws SQLException {
        String sql = "SELECT permissionName FROM Permissions";
        Set<String> permissions = new HashSet<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                permissions.add(rs.getString("permissionName"));
            }

            return permissions;
        }
    }

    /**
     * Retrieves all roles with their associated permissions.
     *
     * @return A list of RoleDTO objects
     * @throws SQLException if a database access error occurs
     */
    public List<RoleDTO> getAllRolesWithPermissions() throws SQLException {
        List<RoleDTO> roleDTOs = new ArrayList<>();

        // First get all roles
        Set<String> roles = getAllRoles();

        // For each role, get its permissions and create a RoleDTO
        for (String roleName : roles) {
            Set<String> permissions = getPermissionsForRole(roleName);
            RoleDTO roleDTO = new RoleDTO(roleName, permissions);
            roleDTOs.add(roleDTO);
        }

        return roleDTOs;
    }

    /**
     * Retrieves a specific role with its associated permissions.
     *
     * @param roleName The name of the role to retrieve
     * @return The RoleDTO if found, null otherwise
     * @throws SQLException if a database access error occurs
     */
    public RoleDTO getRole(String roleName) throws SQLException {
        if (!roleExists(roleName)) {
            return null;
        }

        Set<String> permissions = getPermissionsForRole(roleName);
        return new RoleDTO(roleName, permissions);
    }

    /**
     * Retrieves all permissions for a specific role.
     *
     * @param roleName The name of the role
     * @return A set of permission names associated with the role
     * @throws SQLException if a database access error occurs
     */
    public Set<String> getPermissionsForRole(String roleName) throws SQLException {
        String sql = "SELECT permissionName FROM RolePermissions WHERE roleName = ?";
        Set<String> permissions = new HashSet<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, roleName);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    permissions.add(rs.getString("permissionName"));
                }

                return permissions;
            }
        }
    }

    /**
     * Checks if a role exists in the database.
     *
     * @param roleName The name of the role to check
     * @return true if the role exists, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public boolean roleExists(String roleName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Roles WHERE roleName = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, roleName);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }
        }
    }

    /**
     * Checks if a permission exists in the database.
     *
     * @param permissionName The name of the permission to check
     * @return true if the permission exists, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public boolean permissionExists(String permissionName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Permissions WHERE permissionName = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, permissionName);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }
        }
    }
}
