package DomainLayer.EmployeeSubModule;

import DomainLayer.EmployeeSubModule.Repository.AuthorisationRepositoryImpl;
import DomainLayer.EmployeeSubModule.Repository.BranchRepositoryImpl;
import DomainLayer.EmployeeSubModule.Repository.EmployeeRepositoryImpl;
import DomainLayer.EmployeeSubModule.Repository.interfaces.AuthorisationRepository;
import DomainLayer.EmployeeSubModule.Repository.interfaces.BranchRepository;
import DomainLayer.EmployeeSubModule.Repository.interfaces.EmployeeRepository;
import DomainLayer.exception.InvalidInputException;
import DomainLayer.exception.UnauthorizedPermissionException;
import DataAccessLayer.EmployeeDAL.AuthorisationDAO;
import DataAccessLayer.EmployeeDAL.BranchDAO;
import DataAccessLayer.EmployeeDAL.EmployeeDAO;
import DTOs.BranchDTO;
import DTOs.EmployeeDTO;
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
 * Test class for EmployeeController
 * Uses JUnit 5 and a real database for testing
 */
public class EmployeeControllerTest {

    private static Connection connection;
    private static EmployeeDAO employeeDAO;
    private static BranchDAO branchDAO;
    private static AuthorisationDAO authorisationDAO;
    private static EmployeeRepository employeeRepository;
    private static BranchRepository branchRepository;
    private static AuthorisationRepository authorisationRepository;
    private static AuthorisationController authorisationController;
    private static EmployeeController employeeController;
    private static Employee testEmployee;
    private static Branch testBranch;

    @BeforeAll
    public static void setUpClass() throws SQLException {
        // Set up database connection using the Database utility class
        Database.init(false); // Ensure the database is initialized

        connection = Database.getConnection();

        // Set autoCommit to false for tests
        connection.setAutoCommit(false);

        // Initialize DAOs
        employeeDAO = new EmployeeDAO(connection);
        branchDAO = new BranchDAO(connection);
        authorisationDAO = new AuthorisationDAO(connection);

        // Initialize repositories
        employeeRepository = new EmployeeRepositoryImpl(employeeDAO);
        branchRepository = new BranchRepositoryImpl(branchDAO);
        authorisationRepository = new AuthorisationRepositoryImpl(authorisationDAO);

        // Initialize controllers
        authorisationController = new AuthorisationController(authorisationRepository);
        employeeController = new EmployeeController(employeeRepository, branchRepository, authorisationRepository, authorisationController);
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

        // Create a test branch
        testBranch = new Branch(1L, "Test Branch", 123, "Test Address", null);
        BranchDTO branchDTO = new BranchDTO(
                testBranch.getBranchId(),
                testBranch.getBranchName(),
                testBranch.getAreaCode(),
                testBranch.getBranchAddress(),
                testBranch.getManagerID()
        );
        branchDAO.insert(branchDTO);

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
                testBranch.getBranchId() // branchId
        );

        // Insert test employee into database
        EmployeeDTO employeeDTO = new EmployeeDTO(
                testEmployee.getIsraeliId(),
                testEmployee.getFirstName(),
                testEmployee.getLastName(),
                testEmployee.getSalary(),
                testEmployee.getTermsOfEmployment(),
                testEmployee.getRoles(),
                testEmployee.getStartOfEmployment(),
                testEmployee.isActive(),
                testEmployee.getCreationDate(),
                testEmployee.getUpdateDate(),
                testEmployee.getBranchId()
        );
        employeeDAO.insert(employeeDTO);
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
            stmt.execute("DELETE FROM EmployeeTerms");
            stmt.execute("DELETE FROM EmployeeRoles");
            stmt.execute("DELETE FROM Employees");
            stmt.execute("DELETE FROM Branches");
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
            stmt.execute("DELETE FROM EmployeeTerms");
            stmt.execute("DELETE FROM EmployeeRoles");
            stmt.execute("DELETE FROM Employees");
            stmt.execute("DELETE FROM Branches");
            stmt.execute("DELETE FROM RolePermissions");
            stmt.execute("DELETE FROM Roles");
            stmt.execute("DELETE FROM Permissions");

            // Re-enable foreign key constraints
            stmt.execute("PRAGMA foreign_keys = ON");
        }
    }

    private void insertTestData() throws SQLException {
        // Insert test roles and permissions
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO Roles (roleName) VALUES ('TestRole')");
            stmt.execute("INSERT INTO Roles (roleName) VALUES ('AdminRole')");
            stmt.execute("INSERT INTO Roles (roleName) VALUES ('DriverRole')");

            stmt.execute("INSERT INTO Permissions (permissionName) VALUES ('TestPermission')");
            stmt.execute("INSERT INTO Permissions (permissionName) VALUES ('AdminPermission')");
            stmt.execute("INSERT INTO Permissions (permissionName) VALUES ('EmployeeManagement')");
            stmt.execute("INSERT INTO Permissions (permissionName) VALUES ('TransportManagement')");
            stmt.execute("INSERT INTO Permissions (permissionName) VALUES ('CREATE_EMPLOYEE')");
            stmt.execute("INSERT INTO Permissions (permissionName) VALUES ('UPDATE_EMPLOYEE')");
            stmt.execute("INSERT INTO Permissions (permissionName) VALUES ('DELETE_EMPLOYEE')");
            stmt.execute("INSERT INTO Permissions (permissionName) VALUES ('ROLE_PERMISSION')");
            stmt.execute("INSERT INTO Permissions (permissionName) VALUES ('DEACTIVATE_EMPLOYEE')");
            stmt.execute("INSERT INTO Permissions (permissionName) VALUES ('EDIT_EMPLOYEE')");

            // Assign all permissions to TestRole so our test employee can perform all operations
            stmt.execute("INSERT INTO RolePermissions (roleName, permissionName) VALUES ('TestRole', 'TestPermission')");
            stmt.execute("INSERT INTO RolePermissions (roleName, permissionName) VALUES ('TestRole', 'CREATE_EMPLOYEE')");
            stmt.execute("INSERT INTO RolePermissions (roleName, permissionName) VALUES ('TestRole', 'UPDATE_EMPLOYEE')");
            stmt.execute("INSERT INTO RolePermissions (roleName, permissionName) VALUES ('TestRole', 'DELETE_EMPLOYEE')");
            stmt.execute("INSERT INTO RolePermissions (roleName, permissionName) VALUES ('TestRole', 'ROLE_PERMISSION')");
            stmt.execute("INSERT INTO RolePermissions (roleName, permissionName) VALUES ('TestRole', 'DEACTIVATE_EMPLOYEE')");
            stmt.execute("INSERT INTO RolePermissions (roleName, permissionName) VALUES ('TestRole', 'EmployeeManagement')");
            stmt.execute("INSERT INTO RolePermissions (roleName, permissionName) VALUES ('TestRole', 'EDIT_EMPLOYEE')");
            stmt.execute("INSERT INTO RolePermissions (roleName, permissionName) VALUES ('TestRole', 'TransportManagement')");

            stmt.execute("INSERT INTO RolePermissions (roleName, permissionName) VALUES ('AdminRole', 'AdminPermission')");
            stmt.execute("INSERT INTO RolePermissions (roleName, permissionName) VALUES ('AdminRole', 'EmployeeManagement')");
            stmt.execute("INSERT INTO RolePermissions (roleName, permissionName) VALUES ('DriverRole', 'TransportManagement')");
        }
    }

    // Test methods for Employee CRUD operations

    @Test
    @DisplayName("Test getEmployeeByIsraeliId with existing employee")
    public void testGetEmployeeByIsraeliIdWithExistingEmployee() {
        // Act
        Employee employee = employeeController.getEmployeeByIsraeliId(testEmployee.getIsraeliId());

        // Assert
        assertNotNull(employee, "Should return an employee");
        assertEquals(testEmployee.getIsraeliId(), employee.getIsraeliId(), "Should return the correct employee");
        assertEquals(testEmployee.getFirstName(), employee.getFirstName(), "First name should match");
        assertEquals(testEmployee.getLastName(), employee.getLastName(), "Last name should match");
    }

    @Test
    @DisplayName("Test getEmployeeByIsraeliId with non-existing employee")
    public void testGetEmployeeByIsraeliIdWithNonExistingEmployee() {
        // Act
        Employee employee = employeeController.getEmployeeByIsraeliId(999999999);

        // Assert
        assertNull(employee, "Should return null for non-existing employee");
    }

    @Test
    @DisplayName("Test createEmployee with valid data")
    public void testCreateEmployeeWithValidData() {
        // Arrange
        long israeliId = 987654321;
        String firstName = "New";
        String lastName = "Employee";
        long salary = 6000;
        Map<String, Object> termsOfEmployment = new HashMap<>();
        termsOfEmployment.put("contractType", "PartTime");
        Set<String> roles = new HashSet<>();
        roles.add("TestRole");
        LocalDate startOfEmployment = LocalDate.now();
        long branchId = testBranch.getBranchId();

        // Act
        boolean result = employeeController.createEmployee(
                testEmployee.getIsraeliId(), // doneBy
                israeliId,
                firstName,
                lastName,
                salary,
                termsOfEmployment,
                roles,
                startOfEmployment,
                branchId
        );

        // Assert
        assertTrue(result, "Should return true when employee is created successfully");

        // Verify that the employee was created
        Employee createdEmployee = employeeController.getEmployeeByIsraeliId(israeliId);
        assertNotNull(createdEmployee, "Created employee should exist");
        assertEquals(firstName, createdEmployee.getFirstName(), "First name should match");
        assertEquals(lastName, createdEmployee.getLastName(), "Last name should match");
        assertEquals(salary, createdEmployee.getSalary(), "Salary should match");
        assertEquals(branchId, createdEmployee.getBranchId(), "Branch ID should match");
        assertTrue(createdEmployee.getRoles().contains("TestRole"), "Employee should have the TestRole");
    }

    @Test
    @DisplayName("Test createEmployee with existing employee ID")
    public void testCreateEmployeeWithExistingEmployeeId() {
        // Arrange
        long israeliId = testEmployee.getIsraeliId(); // Use existing ID
        String firstName = "New";
        String lastName = "Employee";
        long salary = 6000;
        Map<String, Object> termsOfEmployment = new HashMap<>();
        termsOfEmployment.put("contractType", "PartTime");
        Set<String> roles = new HashSet<>();
        roles.add("TestRole");
        LocalDate startOfEmployment = LocalDate.now();
        long branchId = testBranch.getBranchId();

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            employeeController.createEmployee(
                    testEmployee.getIsraeliId(), // doneBy
                    israeliId,
                    firstName,
                    lastName,
                    salary,
                    termsOfEmployment,
                    roles,
                    startOfEmployment,
                    branchId
            );
        }, "Should throw InvalidInputException when employee ID already exists");
    }

    @Test
    @DisplayName("Test createEmployee with invalid branch ID")
    public void testCreateEmployeeWithInvalidBranchId() {
        // Arrange
        long israeliId = 987654321;
        String firstName = "New";
        String lastName = "Employee";
        long salary = 6000;
        Map<String, Object> termsOfEmployment = new HashMap<>();
        termsOfEmployment.put("contractType", "PartTime");
        Set<String> roles = new HashSet<>();
        roles.add("TestRole");
        LocalDate startOfEmployment = LocalDate.now();
        long branchId = 999; // Non-existing branch ID

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            employeeController.createEmployee(
                    testEmployee.getIsraeliId(), // doneBy
                    israeliId,
                    firstName,
                    lastName,
                    salary,
                    termsOfEmployment,
                    roles,
                    startOfEmployment,
                    branchId
            );
        }, "Should throw InvalidInputException when branch ID is invalid");
    }

    @Test
    @DisplayName("Test updateEmployee with valid data")
    public void testUpdateEmployeeWithValidData() {
        // Arrange
        String newFirstName = "Updated";
        String newLastName = "Name";
        long newSalary = 7000;
        Map<String, Object> newTermsOfEmployment = new HashMap<>();
        newTermsOfEmployment.put("contractType", "PartTime");
        newTermsOfEmployment.put("vacationDays", 20);

        // Act
        boolean result = employeeController.updateEmployee(
                testEmployee.getIsraeliId(), // doneBy
                testEmployee.getIsraeliId(), // israeliId to update
                newFirstName,
                newLastName,
                newSalary,
                newTermsOfEmployment,
                true // active
        );

        // Assert
        assertTrue(result, "Should return true when employee is updated successfully");

        // Verify that the employee was updated
        Employee updatedEmployee = employeeController.getEmployeeByIsraeliId(testEmployee.getIsraeliId());
        assertNotNull(updatedEmployee, "Updated employee should exist");
        assertEquals(newFirstName, updatedEmployee.getFirstName(), "First name should be updated");
        assertEquals(newLastName, updatedEmployee.getLastName(), "Last name should be updated");
        assertEquals(newSalary, updatedEmployee.getSalary(), "Salary should be updated");
        assertEquals("PartTime", updatedEmployee.getTermsOfEmployment().get("contractType"), "Terms of employment should be updated");
        assertEquals("20", updatedEmployee.getTermsOfEmployment().get("vacationDays"), "Terms of employment should be updated");
    }

    @Test
    @DisplayName("Test updateEmployee with non-existing employee ID")
    public void testUpdateEmployeeWithNonExistingEmployeeId() {
        // Arrange
        long nonExistingId = 999999999;
        String newFirstName = "Updated";
        String newLastName = "Name";
        long newSalary = 7000;
        Map<String, Object> newTermsOfEmployment = new HashMap<>();
        newTermsOfEmployment.put("contractType", "PartTime");

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            employeeController.updateEmployee(
                    testEmployee.getIsraeliId(), // doneBy
                    nonExistingId, // israeliId to update
                    newFirstName,
                    newLastName,
                    newSalary,
                    newTermsOfEmployment,
                    true // active
            );
        }, "Should throw InvalidInputException when employee ID doesn't exist");
    }

    @Test
    @DisplayName("Test deleteEmployee with existing employee")
    public void testDeleteEmployeeWithExistingEmployee() {
        // Act
        boolean result = employeeController.deleteEmployee(
                testEmployee.getIsraeliId(), // doneBy
                testEmployee.getIsraeliId() // israeliId to delete
        );

        // Assert
        assertTrue(result, "Should return true when employee is deleted successfully");

        // Verify that the employee was deleted
        Employee deletedEmployee = employeeController.getEmployeeByIsraeliId(testEmployee.getIsraeliId());
        assertNull(deletedEmployee, "Deleted employee should not exist");
    }

    @Test
    @DisplayName("Test deleteEmployee with non-existing employee ID")
    public void testDeleteEmployeeWithNonExistingEmployeeId() {
        // Arrange
        long nonExistingId = 999999999;

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            employeeController.deleteEmployee(
                    testEmployee.getIsraeliId(), // doneBy
                    nonExistingId // israeliId to delete
            );
        }, "Should throw InvalidInputException when employee ID doesn't exist");
    }

    // Test methods for Role management

    @Test
    @DisplayName("Test addRoleToEmployee with valid role")
    public void testAddRoleToEmployeeWithValidRole() {
        // Act
        boolean result = employeeController.addRoleToEmployee(
                testEmployee.getIsraeliId(), // doneBy
                testEmployee.getIsraeliId(), // israeliId
                "AdminRole" // roleName
        );

        // Assert
        assertTrue(result, "Should return true when role is added successfully");

        // Verify that the role was added
        Employee updatedEmployee = employeeController.getEmployeeByIsraeliId(testEmployee.getIsraeliId());
        assertTrue(updatedEmployee.getRoles().contains("AdminRole"), "Employee should have the AdminRole");
    }

    @Test
    @DisplayName("Test addRoleToEmployee with role that employee already has")
    public void testAddRoleToEmployeeWithExistingRole() {
        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            employeeController.addRoleToEmployee(
                    testEmployee.getIsraeliId(), // doneBy
                    testEmployee.getIsraeliId(), // israeliId
                    "TestRole" // roleName (already has this role)
            );
        }, "Should throw InvalidInputException when employee already has the role");
    }

    @Test
    @DisplayName("Test addRoleToEmployee with non-existing role")
    public void testAddRoleToEmployeeWithNonExistingRole() {
        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            employeeController.addRoleToEmployee(
                    testEmployee.getIsraeliId(), // doneBy
                    testEmployee.getIsraeliId(), // israeliId
                    "NonExistingRole" // roleName
            );
        }, "Should throw InvalidInputException when role doesn't exist");
    }

    @Test
    @DisplayName("Test removeRoleFromEmployee with valid role")
    public void testRemoveRoleFromEmployeeWithValidRole() {
        // Act
        boolean result = employeeController.removeRoleFromEmployee(
                testEmployee.getIsraeliId(), // doneBy
                testEmployee.getIsraeliId(), // israeliId
                "TestRole" // roleName
        );

        // Assert
        assertTrue(result, "Should return true when role is removed successfully");

        // Verify that the role was removed
        Employee updatedEmployee = employeeController.getEmployeeByIsraeliId(testEmployee.getIsraeliId());
        assertFalse(updatedEmployee.getRoles().contains("TestRole"), "Employee should not have the TestRole");
    }

    @Test
    @DisplayName("Test removeRoleFromEmployee with role that employee doesn't have")
    public void testRemoveRoleFromEmployeeWithNonExistingRole() {
        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            employeeController.removeRoleFromEmployee(
                    testEmployee.getIsraeliId(), // doneBy
                    testEmployee.getIsraeliId(), // israeliId
                    "AdminRole" // roleName (doesn't have this role)
            );
        }, "Should throw InvalidInputException when employee doesn't have the role");
    }

    // Test methods for Permission checking

    @Test
    @DisplayName("Test isEmployeeAuthorised with employee having the permission")
    public void testIsEmployeeAuthorisedWithEmployeeHavingPermission() {
        // Act
        boolean result = employeeController.isEmployeeAuthorised(
                testEmployee.getIsraeliId(), // israeliId
                "TestPermission" // permission
        );

        // Assert
        assertTrue(result, "Should return true when employee has the permission");
    }

    @Test
    @DisplayName("Test isEmployeeAuthorised with employee not having the permission")
    public void testIsEmployeeAuthorisedWithEmployeeNotHavingPermission() {
        // Act & Assert
        assertThrows(UnauthorizedPermissionException.class, () -> {
            employeeController.isEmployeeAuthorised(
                    testEmployee.getIsraeliId(), // israeliId
                    "AdminPermission" // permission
            );
        }, "Should throw UnauthorizedPermissionException when employee doesn't have the permission");
    }

    @Test
    @DisplayName("Test hasPermission with employee having the permission")
    public void testHasPermissionWithEmployeeHavingPermission() {
        // Act
        boolean result = employeeController.hasPermission(
                testEmployee.getIsraeliId(), // israeliId
                "TestPermission" // permission
        );

        // Assert
        assertTrue(result, "Should return true when employee has the permission");
    }

    @Test
    @DisplayName("Test hasPermission with employee not having the permission")
    public void testHasPermissionWithEmployeeNotHavingPermission() {
        // Act & Assert
        assertThrows(UnauthorizedPermissionException.class, () -> {
            employeeController.hasPermission(
                    testEmployee.getIsraeliId(), // israeliId
                    "AdminPermission" // permission
            );
        }, "Should throw UnauthorizedPermissionException when employee doesn't have the permission");
    }

    // Test methods for Branch operations

    @Test
    @DisplayName("Test updateEmployeeBranch with valid branch ID")
    public void testUpdateEmployeeBranchWithValidBranchId() throws SQLException {
        // Arrange
        // Create a new branch
        long newBranchId = 2L;
        BranchDTO newBranchDTO = new BranchDTO(
                newBranchId,
                "New Branch",
                456,
                "New Address",
                null
        );
        branchDAO.insert(newBranchDTO);

        // Act
        boolean result = employeeController.updateEmployeeBranch(
                testEmployee.getIsraeliId(), // israeliId
                newBranchId // branchId
        );

        // Assert
        assertTrue(result, "Should return true when branch is updated successfully");

        // Verify that the branch was updated
        Employee updatedEmployee = employeeController.getEmployeeByIsraeliId(testEmployee.getIsraeliId());
        assertEquals(newBranchId, updatedEmployee.getBranchId(), "Branch ID should be updated");
    }

    @Test
    @DisplayName("Test updateEmployeeBranch with invalid branch ID")
    public void testUpdateEmployeeBranchWithInvalidBranchId() {
        // Arrange
        long invalidBranchId = 999; // Non-existing branch ID

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            employeeController.updateEmployeeBranch(
                    testEmployee.getIsraeliId(), // israeliId
                    invalidBranchId // branchId
            );
        }, "Should throw InvalidInputException when branch ID is invalid");
    }

    @Test
    @DisplayName("Test getEmployeeBranch with existing employee")
    public void testGetEmployeeBranchWithExistingEmployee() {
        // Act
        long branchId = employeeController.getEmployeeBranch(testEmployee.getIsraeliId());

        // Assert
        assertEquals(testBranch.getBranchId(), branchId, "Should return the correct branch ID");
    }

    @Test
    @DisplayName("Test getEmployeeBranchName with existing employee")
    public void testGetEmployeeBranchNameWithExistingEmployee() {
        // Act
        String branchName = employeeController.getEmployeeBranchName(testEmployee.getIsraeliId());

        // Assert
        assertEquals(testBranch.getBranchName(), branchName, "Should return the correct branch name");
    }

    @Test
    @DisplayName("Test isBranchExists with existing branch")
    public void testIsBranchExistsWithExistingBranch() {
        // Act
        boolean result = employeeController.isBranchExists(testBranch.getBranchId());

        // Assert
        assertTrue(result, "Should return true for existing branch");
    }

    @Test
    @DisplayName("Test isBranchExists with non-existing branch")
    public void testIsBranchExistsWithNonExistingBranch() {
        // Act
        boolean result = employeeController.isBranchExists(999);

        // Assert
        assertFalse(result, "Should return false for non-existing branch");
    }

    // Test methods for Employee retrieval

    @Test
    @DisplayName("Test getAllEmployees")
    public void testGetAllEmployees() {
        // Act
        Map<Long, Employee> employees = employeeController.getAllEmployees();

        // Assert
        assertNotNull(employees, "Should return a map of employees");
        assertEquals(1, employees.size(), "Should return 1 employee");
        assertTrue(employees.containsKey(testEmployee.getIsraeliId()), "Should contain the test employee");
    }

    @Test
    @DisplayName("Test isEmployeeActive with active employee")
    public void testIsEmployeeActiveWithActiveEmployee() {
        // Act
        boolean result = employeeController.isEmployeeActive(testEmployee.getIsraeliId());

        // Assert
        assertTrue(result, "Should return true for active employee");
    }

    @Test
    @DisplayName("Test isEmployeeActive with inactive employee")
    public void testIsEmployeeActiveWithInactiveEmployee() throws SQLException {
        // Arrange
        // Update employee to be inactive
        EmployeeDTO employeeDTO = employeeDAO.getById(testEmployee.getIsraeliId());
        employeeDTO.setActive(false);
        employeeDAO.update(employeeDTO);

        // Act
        boolean result = employeeController.isEmployeeActive(testEmployee.getIsraeliId());

        // Assert
        assertFalse(result, "Should return false for inactive employee");
    }

    @Test
    @DisplayName("Test isEmployeeHaveRole with employee having the role")
    public void testIsEmployeeHaveRoleWithEmployeeHavingRole() {
        // Act
        boolean result = employeeController.isEmployeeHaveRole(
                testEmployee.getIsraeliId(), // employeeId
                "TestRole" // role
        );

        // Assert
        assertTrue(result, "Should return true when employee has the role");
    }

    @Test
    @DisplayName("Test isEmployeeHaveRole with employee not having the role")
    public void testIsEmployeeHaveRoleWithEmployeeNotHavingRole() {
        // Act
        boolean result = employeeController.isEmployeeHaveRole(
                testEmployee.getIsraeliId(), // employeeId
                "AdminRole" // role
        );

        // Assert
        assertFalse(result, "Should return false when employee doesn't have the role");
    }

    // Test methods for Driver-specific functionality

    @Test
    @DisplayName("Test getAllDrivers with no drivers")
    public void testGetAllDriversWithNoDrivers() {
        // Act
        String[] drivers = employeeController.getAllDrivers();

        // Assert
        assertNotNull(drivers, "Should return an array of drivers");
        assertEquals(0, drivers.length, "Should return 0 drivers");
    }


    // Test methods for Transport module access

    @Test
    @DisplayName("Test canAccessTransportModule with employee not having the permission")
    public void testCanAccessTransportModuleWithEmployeeNotHavingPermission() {
        // Act
        boolean result = employeeController.canAccessTransportModule(testEmployee.getIsraeliId());

        // Assert
        assertFalse(result, "Should return false when employee doesn't have the TransportManagement permission");
    }

    // Test methods for deactivating employees

    @Test
    @DisplayName("Test deactivateEmployee with existing employee")
    public void testDeactivateEmployeeWithExistingEmployee() {
        // Act
        boolean result = employeeController.deactivateEmployee(
                testEmployee.getIsraeliId(), // doneBy
                testEmployee.getIsraeliId() // israeliId
        );

        // Assert
        assertTrue(result, "Should return true when employee is deactivated successfully");

        // Verify that the employee was deactivated
        Employee deactivatedEmployee = employeeController.getEmployeeByIsraeliId(testEmployee.getIsraeliId());
        assertFalse(deactivatedEmployee.isActive(), "Employee should be inactive");
    }

    @Test
    @DisplayName("Test deactivateEmployee with non-existing employee ID")
    public void testDeactivateEmployeeWithNonExistingEmployeeId() {
        // Arrange
        long nonExistingId = 999999999;

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> {
            employeeController.deactivateEmployee(
                    testEmployee.getIsraeliId(), // doneBy
                    nonExistingId // israeliId
            );
        }, "Should throw InvalidInputException when employee ID doesn't exist");
    }
}
