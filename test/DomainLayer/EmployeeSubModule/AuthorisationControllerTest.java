package DomainLayer.EmployeeSubModule;

import DomainLayer.EmployeeSubModule.Repository.AuthorisationRepositoryImpl;
import DomainLayer.EmployeeSubModule.Repository.interfaces.AuthorisationRepository;
import DomainLayer.exception.InvalidInputException;
import DomainLayer.exception.UnauthorizedPermissionException;
import DataAccessLayer.EmployeeDAL.AuthorisationDAO;
import DTOs.RoleDTO;
import Util.Database;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for AuthorisationController
 * Uses JUnit 5 and a real database for testing
 */
public class AuthorisationControllerTest {

    private static Connection connection;
    private static AuthorisationDAO authorisationDAO;
    private static AuthorisationRepository authorisationRepository;
    private static AuthorisationController authorisationController;
    private static Employee testEmployee;

    @BeforeAll
    public static void setUpClass() throws SQLException {
        // Set up database connection using the Database utility class

        Database.init(true);
        connection = Database.getConnection();

        // Set autoCommit to false for tests
        connection.setAutoCommit(false);

        // Initialize DAO and repository
        authorisationDAO = new AuthorisationDAO(connection);
        authorisationRepository = new AuthorisationRepositoryImpl(authorisationDAO);
        authorisationController = new AuthorisationController(authorisationRepository);
    }

    @AfterAll
    public static void tearDownClass() throws SQLException {
        // Clean up database
        cleanupDatabase();

        // Close connection
        if (connection != null) {
            connection.close();
        }
    }

    @BeforeEach
    public void setUp() throws SQLException {
        // Clear tables and insert test data
        clearTables();
        insertTestData();

        // Create a test employee
        Set<String> roles = new HashSet<>();
        roles.add("TestRole");
        Map<String, Object> termsOfEmployment = new HashMap<>();
        termsOfEmployment.put("contractType", "FullTime");

        testEmployee = new Employee(
                123456789, // israeliId
                "Test", // firstName
                "Employee", // lastName
                5000, // salary
                termsOfEmployment, // termsOfEmployment
                roles, // roles
                LocalDate.now(), // startOfEmployment
                true, // isActive
                LocalDate.now(), // creationDate
                LocalDate.now(), // updateDate
                1L // branchId
        );
    }

    @AfterEach
    public void tearDown() throws SQLException {
        // Rollback transaction to clean up after each test
        connection.rollback();
    }

    /**
     * Cleans up all tables after all tests are done.
     * This ensures the database is left in a clean state.
     */
    public static void cleanupDatabase() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Disable foreign key constraints temporarily
            stmt.execute("PRAGMA foreign_keys = OFF");

            // Delete all data from tables
            stmt.execute("DELETE FROM RolePermissions");
            stmt.execute("DELETE FROM Roles");
            stmt.execute("DELETE FROM Permissions");

            // Re-enable foreign key constraints
            stmt.execute("PRAGMA foreign_keys = ON");
        }
    }

    private void clearTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Disable foreign key constraints temporarily
            stmt.execute("PRAGMA foreign_keys = OFF");

            // Delete all data from tables
            stmt.execute("DELETE FROM RolePermissions");
            stmt.execute("DELETE FROM Roles");
            stmt.execute("DELETE FROM Permissions");

            // Re-enable foreign key constraints
            stmt.execute("PRAGMA foreign_keys = ON");
        }
    }

    private void insertTestData() throws SQLException {
        // Insert test roles
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO Roles (roleName) VALUES ('TestRole')");
            stmt.execute("INSERT INTO Roles (roleName) VALUES ('AdminRole')");

            // Insert test permissions
            stmt.execute("INSERT INTO Permissions (permissionName) VALUES ('TestPermission')");
            stmt.execute("INSERT INTO Permissions (permissionName) VALUES ('AdminPermission')");

            // Assign permissions to roles
            stmt.execute("INSERT INTO RolePermissions (roleName, permissionName) VALUES ('TestRole', 'TestPermission')");
            stmt.execute("INSERT INTO RolePermissions (roleName, permissionName) VALUES ('AdminRole', 'AdminPermission')");
        }
    }

    // Test methods

    @Test
    @DisplayName("Test isRoleExists with existing role")
    public void testIsRoleExistsWithExistingRole() {
        // Act
        boolean result = authorisationController.isRoleExists("TestRole");

        // Assert
        assertTrue(result, "Should return true for existing role");
    }

    @Test
    @DisplayName("Test isRoleExists with non-existing role")
    public void testIsRoleExistsWithNonExistingRole() {
        // Act
        boolean result = authorisationController.isRoleExists("NonExistingRole");

        // Assert
        assertFalse(result, "Should return false for non-existing role");
    }

    @Test
    @DisplayName("Test hasPermission with employee having the permission")
    public void testHasPermissionWithEmployeeHavingPermission() {
        // Act & Assert
        assertTrue(authorisationController.hasPermission(testEmployee, "TestPermission"), 
                "Should return true when employee has the permission");
    }

    @Test
    @DisplayName("Test hasPermission with employee not having the permission")
    public void testHasPermissionWithEmployeeNotHavingPermission() {
        // Act & Assert
        assertThrows(UnauthorizedPermissionException.class, () -> {
            authorisationController.hasPermission(testEmployee, "AdminPermission");
        }, "Should throw UnauthorizedPermissionException when employee doesn't have the permission");
    }

    @Test
    @DisplayName("Test hasPermission with null employee")
    public void testHasPermissionWithNullEmployee() {
        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            authorisationController.hasPermission(null, "TestPermission");
        }, "Should throw InvalidInputException when employee is null");
    }

    @Test
    @DisplayName("Test hasPermission with null permission")
    public void testHasPermissionWithNullPermission() {
        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            authorisationController.hasPermission(testEmployee, null);
        }, "Should throw InvalidInputException when permission is null");
    }

    @Test
    @DisplayName("Test hasPermission with empty permission")
    public void testHasPermissionWithEmptyPermission() {
        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            authorisationController.hasPermission(testEmployee, "");
        }, "Should throw InvalidInputException when permission is empty");
    }

    @Test
    @DisplayName("Test hasRole with employee having the role")
    public void testHasRoleWithEmployeeHavingRole() {
        // Act
        boolean result = authorisationController.hasRole(testEmployee, "TestRole");

        // Assert
        assertTrue(result, "Should return true when employee has the role");
    }

    @Test
    @DisplayName("Test hasRole with employee not having the role")
    public void testHasRoleWithEmployeeNotHavingRole() {
        // Act
        boolean result = authorisationController.hasRole(testEmployee, "AdminRole");

        // Assert
        assertFalse(result, "Should return false when employee doesn't have the role");
    }

    @Test
    @DisplayName("Test hasRole with null employee")
    public void testHasRoleWithNullEmployee() {
        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            authorisationController.hasRole(null, "TestRole");
        }, "Should throw InvalidInputException when employee is null");
    }

    @Test
    @DisplayName("Test hasRole with null role")
    public void testHasRoleWithNullRole() {
        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            authorisationController.hasRole(testEmployee, null);
        }, "Should throw InvalidInputException when role is null");
    }

    @Test
    @DisplayName("Test hasRole with empty role")
    public void testHasRoleWithEmptyRole() {
        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            authorisationController.hasRole(testEmployee, "");
        }, "Should throw InvalidInputException when role is empty");
    }

    @Test
    @DisplayName("Test addRole with valid role")
    public void testAddRoleWithValidRole() {
        // Act
        boolean result = authorisationController.addRole(testEmployee, "AdminRole");

        // Assert
        assertTrue(result, "Should return true when role is added successfully");
        assertTrue(testEmployee.getRoles().contains("AdminRole"), "Employee should have the new role");
    }

    @Test
    @DisplayName("Test addRole with role that employee already has")
    public void testAddRoleWithExistingRole() {
        // Act
        boolean result = authorisationController.addRole(testEmployee, "TestRole");

        // Assert
        assertFalse(result, "Should return false when employee already has the role");
    }

    @Test
    @DisplayName("Test addRole with non-existing role")
    public void testAddRoleWithNonExistingRole() {
        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            authorisationController.addRole(testEmployee, "NonExistingRole");
        }, "Should throw InvalidInputException when role doesn't exist");
    }

    @Test
    @DisplayName("Test addRole with null employee")
    public void testAddRoleWithNullEmployee() {
        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            authorisationController.addRole(null, "TestRole");
        }, "Should throw InvalidInputException when employee is null");
    }

    @Test
    @DisplayName("Test addRole with null role")
    public void testAddRoleWithNullRole() {
        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            authorisationController.addRole(testEmployee, null);
        }, "Should throw InvalidInputException when role is null");
    }

    @Test
    @DisplayName("Test addRole with empty role")
    public void testAddRoleWithEmptyRole() {
        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            authorisationController.addRole(testEmployee, "");
        }, "Should throw InvalidInputException when role is empty");
    }

    @Test
    @DisplayName("Test removeRole with valid role")
    public void testRemoveRoleWithValidRole() {
        // Act
        boolean result = authorisationController.removeRole(testEmployee, "TestRole");

        // Assert
        assertTrue(result, "Should return true when role is removed successfully");
        assertFalse(testEmployee.getRoles().contains("TestRole"), "Employee should not have the removed role");
    }

    @Test
    @DisplayName("Test removeRole with role that employee doesn't have")
    public void testRemoveRoleWithNonExistingRole() {
        // Act
        boolean result = authorisationController.removeRole(testEmployee, "AdminRole");

        // Assert
        assertFalse(result, "Should return false when employee doesn't have the role");
    }

    @Test
    @DisplayName("Test addPermissionToRole with valid permission and role")
    public void testAddPermissionToRoleWithValidPermissionAndRole() {
        // Act
        boolean result = authorisationController.addPermissionToRole("TestRole", "AdminPermission");

        // Assert
        assertTrue(result, "Should return true when permission is added to role successfully");

        // Verify that the permission was added
        Set<String> permissions = authorisationRepository.getPermissionsForRole("TestRole");
        assertTrue(permissions.contains("AdminPermission"), "Role should have the new permission");
    }

    @Test
    @DisplayName("Test addPermissionToRole with permission that role already has")
    public void testAddPermissionToRoleWithExistingPermission() {
        // Act
        boolean result = authorisationController.addPermissionToRole("TestRole", "TestPermission");

        // Assert
        assertFalse(result, "Should return false when role already has the permission");
    }

    @Test
    @DisplayName("Test addPermissionToRole with non-existing role")
    public void testAddPermissionToRoleWithNonExistingRole() {
        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            authorisationController.addPermissionToRole("NonExistingRole", "TestPermission");
        }, "Should throw InvalidInputException when role doesn't exist");
    }

    @Test
    @DisplayName("Test addPermissionToRole with non-existing permission")
    public void testAddPermissionToRoleWithNonExistingPermission() {
        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            authorisationController.addPermissionToRole("TestRole", "NonExistingPermission");
        }, "Should throw InvalidInputException when permission doesn't exist");
    }

    @Test
    @DisplayName("Test addPermissionToRole with null role")
    public void testAddPermissionToRoleWithNullRole() {
        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            authorisationController.addPermissionToRole(null, "TestPermission");
        }, "Should throw InvalidInputException when role is null");
    }

    @Test
    @DisplayName("Test addPermissionToRole with empty role")
    public void testAddPermissionToRoleWithEmptyRole() {
        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            authorisationController.addPermissionToRole("", "TestPermission");
        }, "Should throw InvalidInputException when role is empty");
    }

    @Test
    @DisplayName("Test addPermissionToRole with null permission")
    public void testAddPermissionToRoleWithNullPermission() {
        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            authorisationController.addPermissionToRole("TestRole", null);
        }, "Should throw InvalidInputException when permission is null");
    }

    @Test
    @DisplayName("Test addPermissionToRole with empty permission")
    public void testAddPermissionToRoleWithEmptyPermission() {
        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            authorisationController.addPermissionToRole("TestRole", "");
        }, "Should throw InvalidInputException when permission is empty");
    }

    @Test
    @DisplayName("Test removePermissionFromRole with valid permission and role")
    public void testRemovePermissionFromRoleWithValidPermissionAndRole() {
        // Act
        boolean result = authorisationController.removePermissionFromRole("TestRole", "TestPermission");

        // Assert
        assertTrue(result, "Should return true when permission is removed from role successfully");

        // Verify that the permission was removed
        Set<String> permissions = authorisationRepository.getPermissionsForRole("TestRole");
        assertFalse(permissions.contains("TestPermission"), "Role should not have the removed permission");
    }

    @Test
    @DisplayName("Test removePermissionFromRole with permission that role doesn't have")
    public void testRemovePermissionFromRoleWithNonExistingPermission() {
        // Act
        boolean result = authorisationController.removePermissionFromRole("TestRole", "AdminPermission");

        // Assert
        assertFalse(result, "Should return false when role doesn't have the permission");
    }

    @Test
    @DisplayName("Test removePermissionFromRole with non-existing role")
    public void testRemovePermissionFromRoleWithNonExistingRole() {
        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            authorisationController.removePermissionFromRole("NonExistingRole", "TestPermission");
        }, "Should throw InvalidInputException when role doesn't exist");
    }

    @Test
    @DisplayName("Test removePermissionFromRole with null role")
    public void testRemovePermissionFromRoleWithNullRole() {
        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            authorisationController.removePermissionFromRole(null, "TestPermission");
        }, "Should throw InvalidInputException when role is null");
    }

    @Test
    @DisplayName("Test removePermissionFromRole with empty role")
    public void testRemovePermissionFromRoleWithEmptyRole() {
        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            authorisationController.removePermissionFromRole("", "TestPermission");
        }, "Should throw InvalidInputException when role is empty");
    }

    @Test
    @DisplayName("Test removePermissionFromRole with null permission")
    public void testRemovePermissionFromRoleWithNullPermission() {
        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            authorisationController.removePermissionFromRole("TestRole", null);
        }, "Should throw InvalidInputException when permission is null");
    }

    @Test
    @DisplayName("Test removePermissionFromRole with empty permission")
    public void testRemovePermissionFromRoleWithEmptyPermission() {
        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            authorisationController.removePermissionFromRole("TestRole", "");
        }, "Should throw InvalidInputException when permission is empty");
    }

    @Test
    @DisplayName("Test createRole with valid role name and permissions")
    public void testCreateRoleWithValidRoleNameAndPermissions() {
        // Arrange
        Set<String> permissions = new HashSet<>();
        permissions.add("TestPermission");

        // Act
        boolean result = authorisationController.createRole(123456789, "NewRole", permissions);

        // Assert
        assertTrue(result, "Should return true when role is created successfully");
        assertTrue(authorisationController.isRoleExists("NewRole"), "New role should exist");

        // Verify that the permissions were added to the role
        Set<String> rolePermissions = authorisationRepository.getPermissionsForRole("NewRole");
        assertTrue(rolePermissions.contains("TestPermission"), "New role should have the specified permission");
    }

    @Test
    @DisplayName("Test createRole with existing role name")
    public void testCreateRoleWithExistingRoleName() {
        // Arrange
        Set<String> permissions = new HashSet<>();
        permissions.add("TestPermission");

        // Act
        boolean result = authorisationController.createRole(123456789, "TestRole", permissions);

        // Assert
        assertFalse(result, "Should return false when role already exists");
    }

    @Test
    @DisplayName("Test createRole with non-existing permission")
    public void testCreateRoleWithNonExistingPermission() {
        // Arrange
        Set<String> permissions = new HashSet<>();
        permissions.add("NonExistingPermission");

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            authorisationController.createRole(123456789, "NewRole", permissions);
        }, "Should throw InvalidInputException when permission doesn't exist");
    }

    @Test
    @DisplayName("Test createRole with null role name")
    public void testCreateRoleWithNullRoleName() {
        // Arrange
        Set<String> permissions = new HashSet<>();
        permissions.add("TestPermission");

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            authorisationController.createRole(123456789, null, permissions);
        }, "Should throw InvalidInputException when role name is null");
    }

    @Test
    @DisplayName("Test createRole with empty role name")
    public void testCreateRoleWithEmptyRoleName() {
        // Arrange
        Set<String> permissions = new HashSet<>();
        permissions.add("TestPermission");

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            authorisationController.createRole(123456789, "", permissions);
        }, "Should throw InvalidInputException when role name is empty");
    }

    @Test
    @DisplayName("Test createRole with null permissions")
    public void testCreateRoleWithNullPermissions() {
        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            authorisationController.createRole(123456789, "NewRole", null);
        }, "Should throw InvalidInputException when permissions is null");
    }

    @Test
    @DisplayName("Test deleteRole with existing role")
    public void testDeleteRoleWithExistingRole() {
        // Act
        boolean result = authorisationController.deleteRole("TestRole");

        // Assert
        assertTrue(result, "Should return true when role is deleted successfully");
        assertFalse(authorisationController.isRoleExists("TestRole"), "Role should no longer exist");
    }

    @Test
    @DisplayName("Test deleteRole with non-existing role")
    public void testDeleteRoleWithNonExistingRole() {
        // Act
        boolean result = authorisationController.deleteRole("NonExistingRole");

        // Assert
        assertFalse(result, "Should return false when role doesn't exist");
    }

    @Test
    @DisplayName("Test deleteRole with null role name")
    public void testDeleteRoleWithNullRoleName() {
        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            authorisationController.deleteRole(null);
        }, "Should throw InvalidInputException when role name is null");
    }

    @Test
    @DisplayName("Test deleteRole with empty role name")
    public void testDeleteRoleWithEmptyRoleName() {
        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            authorisationController.deleteRole("");
        }, "Should throw InvalidInputException when role name is empty");
    }

    @Test
    @DisplayName("Test createPermission with valid permission name")
    public void testCreatePermissionWithValidPermissionName() {
        // Act
        boolean result = authorisationController.createPermission("NewPermission");

        // Assert
        assertTrue(result, "Should return true when permission is created successfully");
        assertTrue(authorisationRepository.permissionExists("NewPermission"), "New permission should exist");
    }

    @Test
    @DisplayName("Test createPermission with existing permission name")
    public void testCreatePermissionWithExistingPermissionName() {
        // Act
        boolean result = authorisationController.createPermission("TestPermission");

        // Assert
        assertFalse(result, "Should return false when permission already exists");
    }

    @Test
    @DisplayName("Test createPermission with null permission name")
    public void testCreatePermissionWithNullPermissionName() {
        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            authorisationController.createPermission(null);
        }, "Should throw InvalidInputException when permission name is null");
    }

    @Test
    @DisplayName("Test createPermission with empty permission name")
    public void testCreatePermissionWithEmptyPermissionName() {
        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            authorisationController.createPermission("");
        }, "Should throw InvalidInputException when permission name is empty");
    }

    @Test
    @DisplayName("Test deletePermission with existing permission")
    public void testDeletePermissionWithExistingPermission() throws SQLException {
        // Create a new permission that is not in use by any role
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO Permissions (permissionName) VALUES ('UnusedPermission')");
        }

        // Act
        boolean result = authorisationController.deletePermission("UnusedPermission");

        // Assert
        assertTrue(result, "Should return true when permission is deleted successfully");
        assertFalse(authorisationRepository.permissionExists("UnusedPermission"), "Permission should no longer exist");
    }

    @Test
    @DisplayName("Test deletePermission with non-existing permission")
    public void testDeletePermissionWithNonExistingPermission() {
        // Act
        boolean result = authorisationController.deletePermission("NonExistingPermission");

        // Assert
        assertFalse(result, "Should return false when permission doesn't exist");
    }

    @Test
    @DisplayName("Test deletePermission with permission in use by a role")
    public void testDeletePermissionWithPermissionInUse() {
        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            authorisationController.deletePermission("TestPermission");
        }, "Should throw InvalidInputException when permission is in use by a role");
    }

    @Test
    @DisplayName("Test deletePermission with null permission name")
    public void testDeletePermissionWithNullPermissionName() {
        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            authorisationController.deletePermission(null);
        }, "Should throw InvalidInputException when permission name is null");
    }

    @Test
    @DisplayName("Test deletePermission with empty permission name")
    public void testDeletePermissionWithEmptyPermissionName() {
        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            authorisationController.deletePermission("");
        }, "Should throw InvalidInputException when permission name is empty");
    }

    @Test
    @DisplayName("Test getAllRoles")
    public void testGetAllRoles() {
        // Act
        Set<String> roles = authorisationController.getAllRoles();

        // Assert
        assertNotNull(roles, "Roles should not be null");
        assertEquals(2, roles.size(), "Should return 2 roles");
        assertTrue(roles.contains("TestRole"), "Should contain TestRole");
        assertTrue(roles.contains("AdminRole"), "Should contain AdminRole");
    }

    @Test
    @DisplayName("Test getAllPermissions")
    public void testGetAllPermissions() {
        // Act
        Set<String> permissions = authorisationController.getAllPermissions();

        // Assert
        assertNotNull(permissions, "Permissions should not be null");
        assertEquals(2, permissions.size(), "Should return 2 permissions");
        assertTrue(permissions.contains("TestPermission"), "Should contain TestPermission");
        assertTrue(permissions.contains("AdminPermission"), "Should contain AdminPermission");
    }

    @Test
    @DisplayName("Test getAllRolesWithPermissions")
    public void testGetAllRolesWithPermissions() {
        // Act
        Map<String, String[]> rolesWithPermissions = authorisationController.getAllRolesWithPermissions();

        // Assert
        assertNotNull(rolesWithPermissions, "Roles with permissions should not be null");
        assertEquals(2, rolesWithPermissions.size(), "Should return 2 roles");

        // Check TestRole permissions
        String[] testRolePermissions = rolesWithPermissions.get("TestRole");
        assertNotNull(testRolePermissions, "TestRole permissions should not be null");
        assertEquals(1, testRolePermissions.length, "TestRole should have 1 permission");
        assertEquals("TestPermission", testRolePermissions[0], "TestRole should have TestPermission");

        // Check AdminRole permissions
        String[] adminRolePermissions = rolesWithPermissions.get("AdminRole");
        assertNotNull(adminRolePermissions, "AdminRole permissions should not be null");
        assertEquals(1, adminRolePermissions.length, "AdminRole should have 1 permission");
        assertEquals("AdminPermission", adminRolePermissions[0], "AdminRole should have AdminPermission");
    }

    @Test
    @DisplayName("Test getRoleDetails with existing role")
    public void testGetRoleDetailsWithExistingRole() {
        // Act
        Map<String, HashSet<String>> roleDetails = authorisationController.getRoleDetails("TestRole");

        // Assert
        assertNotNull(roleDetails, "Role details should not be null");
        assertEquals(1, roleDetails.size(), "Should return 1 role");

        // Check TestRole permissions
        HashSet<String> testRolePermissions = roleDetails.get("TestRole");
        assertNotNull(testRolePermissions, "TestRole permissions should not be null");
        assertEquals(1, testRolePermissions.size(), "TestRole should have 1 permission");
        assertTrue(testRolePermissions.contains("TestPermission"), "TestRole should have TestPermission");
    }

    @Test
    @DisplayName("Test getRoleDetails with non-existing role")
    public void testGetRoleDetailsWithNonExistingRole() {
        // Act
        Map<String, HashSet<String>> roleDetails = authorisationController.getRoleDetails("NonExistingRole");

        // Assert
        assertNotNull(roleDetails, "Role details should not be null");
        assertEquals(0, roleDetails.size(), "Should return 0 roles");
    }

    @Test
    @DisplayName("Test getRoleDTO with existing role")
    public void testGetRoleDTOWithExistingRole() {
        // Act
        RoleDTO roleDTO = authorisationController.getRoleDTO("TestRole");

        // Assert
        assertNotNull(roleDTO, "RoleDTO should not be null");
        assertEquals("TestRole", roleDTO.getName(), "RoleDTO name should be TestRole");
        assertEquals(1, roleDTO.getPermissions().size(), "RoleDTO should have 1 permission");
        assertTrue(roleDTO.getPermissions().contains("TestPermission"), "RoleDTO should have TestPermission");
    }

    @Test
    @DisplayName("Test getRoleDTO with non-existing role")
    public void testGetRoleDTOWithNonExistingRole() {
        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            authorisationController.getRoleDTO("NonExistingRole");
        }, "Should throw InvalidInputException when role doesn't exist");
    }

    @Test
    @DisplayName("Test getRoleDTO with null role name")
    public void testGetRoleDTOWithNullRoleName() {
        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            authorisationController.getRoleDTO(null);
        }, "Should throw InvalidInputException when role name is null");
    }

    @Test
    @DisplayName("Test getRoleDTO with empty role name")
    public void testGetRoleDTOWithEmptyRoleName() {
        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            authorisationController.getRoleDTO("");
        }, "Should throw InvalidInputException when role name is empty");
    }
}
