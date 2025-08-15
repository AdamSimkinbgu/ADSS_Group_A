package DomainLayer.EmployeeSubModule.Repository;

import DataAccessLayer.EmployeeDAL.AuthorisationDAO;
import DataAccessLayer.EmployeeDAL.EmployeeDALFactory;
import DTOs.RoleDTO;
import DomainLayer.EmployeeSubModule.Repository.interfaces.AuthorisationRepository;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of the AuthorisationRepository interface.
 * This class uses the AuthorisationDAO to interact with the database.
 */
public class AuthorisationRepositoryImpl implements AuthorisationRepository {

    private final AuthorisationDAO authorisationDAO;

    /**
     * Constructor that initializes the repository with the AuthorisationDAO.
     */
    public AuthorisationRepositoryImpl() {
        try {
            this.authorisationDAO = EmployeeDALFactory.getInstance().getAuthorisationDAO();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize AuthorisationRepository", e);
        }
    }

    /**
     * Constructor that accepts an AuthorisationDAO for testing purposes.
     *
     * @param authorisationDAO The AuthorisationDAO to use
     */
    public AuthorisationRepositoryImpl(AuthorisationDAO authorisationDAO) {
        this.authorisationDAO = authorisationDAO;
    }

    @Override
    public boolean createRole(String roleName) {
        try {
            return authorisationDAO.createRole(roleName);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create role: " + roleName, e);
        }
    }

    @Override
    public boolean deleteRole(String roleName) {
        try {
            return authorisationDAO.deleteRole(roleName);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete role: " + roleName, e);
        }
    }

    @Override
    public boolean createPermission(String permissionName) {
        try {
            return authorisationDAO.createPermission(permissionName);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create permission: " + permissionName, e);
        }
    }

    @Override
    public boolean deletePermission(String permissionName) {
        try {
            return authorisationDAO.deletePermission(permissionName);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete permission: " + permissionName, e);
        }
    }

    @Override
    public boolean addPermissionToRole(String roleName, String permissionName) {
        try {
            return authorisationDAO.addPermissionToRole(roleName, permissionName);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add permission to role: " + permissionName + " to " + roleName, e);
        }
    }

    @Override
    public boolean removePermissionFromRole(String roleName, String permissionName) {
        try {
            return authorisationDAO.removePermissionFromRole(roleName, permissionName);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to remove permission from role: " + permissionName + " from " + roleName, e);
        }
    }

    @Override
    public Set<String> getAllRoles() {
        try {
            return authorisationDAO.getAllRoles();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get all roles", e);
        }
    }

    @Override
    public Set<String> getAllPermissions() {
        try {
            return authorisationDAO.getAllPermissions();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get all permissions", e);
        }
    }

    @Override
    public Map<String, HashSet<String>> getAllRolesWithPermissions() {
        try {
            List<RoleDTO> roleDTOs = authorisationDAO.getAllRolesWithPermissions();
            Map<String, HashSet<String>> rolesWithPermissions = new HashMap<>();

            for (RoleDTO roleDTO : roleDTOs) {
                HashSet<String> permissions = new HashSet<>(roleDTO.getPermissions());
                rolesWithPermissions.put(roleDTO.getName(), permissions);
            }

            return rolesWithPermissions;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get all roles with permissions", e);
        }
    }

    @Override
    public Set<String> getPermissionsForRole(String roleName) {
        try {
            return authorisationDAO.getPermissionsForRole(roleName);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get permissions for role: " + roleName, e);
        }
    }

    @Override
    public boolean roleExists(String roleName) {
        try {
            return authorisationDAO.roleExists(roleName);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check if role exists: " + roleName, e);
        }
    }

    @Override
    public boolean permissionExists(String permissionName) {
        try {
            return authorisationDAO.permissionExists(permissionName);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check if permission exists: " + permissionName, e);
        }
    }
}
