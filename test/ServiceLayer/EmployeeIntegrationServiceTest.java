package ServiceLayer;

import DomainLayer.EmployeeSubModule.AuthorisationController;
import DomainLayer.EmployeeSubModule.Branch;
import DomainLayer.EmployeeSubModule.Employee;
import DomainLayer.EmployeeSubModule.Repository.AuthorisationRepositoryImpl;
import DomainLayer.EmployeeSubModule.Repository.BranchRepositoryImpl;
import DomainLayer.EmployeeSubModule.Repository.EmployeeRepositoryImpl;
import DomainLayer.EmployeeSubModule.Repository.ShiftRepositoryImpl;
import DomainLayer.EmployeeSubModule.Repository.interfaces.AuthorisationRepository;
import DomainLayer.EmployeeSubModule.Repository.interfaces.BranchRepository;
import DomainLayer.EmployeeSubModule.Repository.interfaces.EmployeeRepository;
import DomainLayer.EmployeeSubModule.Repository.interfaces.ShiftReposetory;
import DomainLayer.EmployeeSubModule.ShiftController;
import DomainLayer.EmployeeSubModule.EmployeeController;
import DomainLayer.EmployeeSubModule.AssignmentController;
import DomainLayer.EmployeeSubModule.AvailabilityController;
import DomainLayer.enums.ShiftType;
import DataAccessLayer.EmployeeDAL.AuthorisationDAO;
import DataAccessLayer.EmployeeDAL.BranchDAO;
import DataAccessLayer.EmployeeDAL.EmployeeDAO;
import DataAccessLayer.EmployeeDAL.ShiftDAO;
import DTOs.BranchDTO;
import DTOs.EmployeeDTO;
import DTOs.ShiftDTO;
import ServiceLayer.EmployeeSubModule.EmployeeService;
import ServiceLayer.EmployeeSubModule.ShiftService;
import ServiceLayer.exception.ServiceException;
import Util.Database;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for EmployeeIntegrationService
 * Uses JUnit 5 and a real database for testing
 */
public class EmployeeIntegrationServiceTest {

    private static Connection connection;
    private static EmployeeDAO employeeDAO;
    private static BranchDAO branchDAO;
    private static AuthorisationDAO authorisationDAO;
    private static ShiftDAO shiftDAO;
    private static EmployeeRepository employeeRepository;
    private static BranchRepository branchRepository;
    private static AuthorisationRepository authorisationRepository;
    private static ShiftReposetory shiftRepository;
    private static AuthorisationController authorisationController;
    private static EmployeeController employeeController;
    private static ShiftController shiftController;
    private static AssignmentController assignmentController;
    private static AvailabilityController availabilityController;
    private static EmployeeService employeeService;
    private static ShiftService shiftService;
    private static EmployeeIntegrationService employeeIntegrationService;
    private static Employee testEmployee;
    private static Employee driverEmployee;
    private static Branch testBranch;
    private static long shiftId;

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
        shiftDAO = new ShiftDAO(connection);

        // Initialize repositories
        employeeRepository = new EmployeeRepositoryImpl(employeeDAO);
        branchRepository = new BranchRepositoryImpl(branchDAO);
        authorisationRepository = new AuthorisationRepositoryImpl(authorisationDAO);
        shiftRepository = new ShiftRepositoryImpl();

        // Initialize controllers
        authorisationController = new AuthorisationController(authorisationRepository);
        employeeController = new EmployeeController(employeeRepository, branchRepository, authorisationRepository, authorisationController);
        shiftController = new ShiftController(employeeController, shiftRepository);
        assignmentController = new AssignmentController(employeeController, shiftController, shiftRepository);
        availabilityController = new AvailabilityController(employeeController, shiftController, shiftRepository);

        // Initialize services
        employeeService = new EmployeeService(employeeController, authorisationController);
        shiftService = new ShiftService(shiftController, assignmentController, availabilityController);

        // Initialize the service under test
        employeeIntegrationService = new EmployeeIntegrationService(employeeService, shiftService);
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

        // Create a driver employee
        Set<String> driverRoles = new HashSet<>();
        driverRoles.add("Driver");
        Map<String, Object> driverTerms = new HashMap<>();
        driverTerms.put("contractType", "FullTime");

        driverEmployee = new Employee(
                987654321, // israeliId
                "Driver", // firstName
                "Test", // lastName
                6000, // salary
                driverTerms, // termsOfEmployment
                driverRoles, // roles
                LocalDate.now(), // startOfEmployment
                true, // isActive
                LocalDate.now(), // creationDate
                LocalDate.now(), // updateDate
                testBranch.getBranchId() // branchId
        );

        // Insert driver employee into database
        EmployeeDTO driverDTO = new EmployeeDTO(
                driverEmployee.getIsraeliId(),
                driverEmployee.getFirstName(),
                driverEmployee.getLastName(),
                driverEmployee.getSalary(),
                driverEmployee.getTermsOfEmployment(),
                driverEmployee.getRoles(),
                driverEmployee.getStartOfEmployment(),
                driverEmployee.isActive(),
                driverEmployee.getCreationDate(),
                driverEmployee.getUpdateDate(),
                driverEmployee.getBranchId()
        );
        employeeDAO.insert(driverDTO);

        // Create a test shift
        LocalDate shiftDate = LocalDate.now();
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(17, 0);

        // Create roles required for the shift
        Map<String, Integer> rolesRequired = new HashMap<>();
        rolesRequired.put("Driver", 1);
        rolesRequired.put("WarehouseMan", 1);

        // Create assigned employees for the shift
        Map<String, Set<Long>> assignedEmployees = new HashMap<>();
        Set<Long> driverSet = new HashSet<>();
        driverSet.add(driverEmployee.getIsraeliId());
        assignedEmployees.put("Driver", driverSet);

        Set<Long> WarehouseManSet = new HashSet<>();
        WarehouseManSet.add(testEmployee.getIsraeliId());
        assignedEmployees.put("WarehouseMan", WarehouseManSet);

        // Create available employees for the shift
        Set<Long> availableEmployees = new HashSet<>();
        availableEmployees.add(testEmployee.getIsraeliId());
        availableEmployees.add(driverEmployee.getIsraeliId());

        // Create the shift
        shiftId = 1L;
        ShiftDTO shiftDTO = new ShiftDTO(
                shiftId,
                ShiftType.MORNING,
                shiftDate,
                rolesRequired,
                assignedEmployees,
                availableEmployees,
                true, // isAssignedShiftManager
                true, // isOpen
                startTime,
                endTime,
                LocalDate.now(), // creationDate
                LocalDate.now(), // updateDate
                testBranch.getBranchId() // branchId
        );

        // Use the repository to create the shift instead of directly using the DAO
        // This ensures the shift is added to the in-memory collection
        shiftRepository.create(shiftDTO);
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
            stmt.execute("DELETE FROM AssignedEmployees");
            stmt.execute("DELETE FROM AvailableEmployees");
            stmt.execute("DELETE FROM RoleRequired");
            stmt.execute("DELETE FROM Shifts");

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
            stmt.execute("DELETE FROM AssignedEmployees");
            stmt.execute("DELETE FROM AvailableEmployees");
            stmt.execute("DELETE FROM RoleRequired");
            stmt.execute("DELETE FROM Shifts");

            // Re-enable foreign key constraints
            stmt.execute("PRAGMA foreign_keys = ON");
        }
    }

    private void insertTestData() throws SQLException {
        // Insert test roles and permissions
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO Roles (roleName) VALUES ('TestRole')");
            stmt.execute("INSERT INTO Roles (roleName) VALUES ('AdminRole')");
            stmt.execute("INSERT INTO Roles (roleName) VALUES ('Driver')");
            stmt.execute("INSERT INTO Roles (roleName) VALUES ('WarehouseMan')");

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
            stmt.execute("INSERT INTO Permissions (permissionName) VALUES ('GET_ASSIGN')");
            stmt.execute("INSERT INTO Permissions (permissionName) VALUES ('UPDATE_AVAILABLE')");

            // Assign permissions to roles
            stmt.execute("INSERT INTO RolePermissions (roleName, permissionName) VALUES ('TestRole', 'TestPermission')");
            stmt.execute("INSERT INTO RolePermissions (roleName, permissionName) VALUES ('TestRole', 'CREATE_EMPLOYEE')");
            stmt.execute("INSERT INTO RolePermissions (roleName, permissionName) VALUES ('TestRole', 'UPDATE_EMPLOYEE')");
            stmt.execute("INSERT INTO RolePermissions (roleName, permissionName) VALUES ('TestRole', 'DELETE_EMPLOYEE')");
            stmt.execute("INSERT INTO RolePermissions (roleName, permissionName) VALUES ('TestRole', 'ROLE_PERMISSION')");
            stmt.execute("INSERT INTO RolePermissions (roleName, permissionName) VALUES ('TestRole', 'DEACTIVATE_EMPLOYEE')");
            stmt.execute("INSERT INTO RolePermissions (roleName, permissionName) VALUES ('TestRole', 'EmployeeManagement')");
            stmt.execute("INSERT INTO RolePermissions (roleName, permissionName) VALUES ('TestRole', 'EDIT_EMPLOYEE')");
            stmt.execute("INSERT INTO RolePermissions (roleName, permissionName) VALUES ('TestRole', 'TransportManagement')");
            stmt.execute("INSERT INTO RolePermissions (roleName, permissionName) VALUES ('TestRole', 'GET_ASSIGN')");
            stmt.execute("INSERT INTO RolePermissions (roleName, permissionName) VALUES ('TestRole', 'UPDATE_AVAILABLE')");

            stmt.execute("INSERT INTO RolePermissions (roleName, permissionName) VALUES ('AdminRole', 'AdminPermission')");
            stmt.execute("INSERT INTO RolePermissions (roleName, permissionName) VALUES ('AdminRole', 'EmployeeManagement')");
            stmt.execute("INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Driver', 'TransportManagement')");
            stmt.execute("INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Driver', 'GET_ASSIGN')");
            stmt.execute("INSERT INTO RolePermissions (roleName, permissionName) VALUES ('WarehouseMan', 'EmployeeManagement')");
            stmt.execute("INSERT INTO RolePermissions (roleName, permissionName) VALUES ('WarehouseMan', 'GET_ASSIGN')");
        }
    }

    // Test methods for EmployeeIntegrationService

    @Test
    @DisplayName("Test isActive with active employee")
    public void testIsActiveWithActiveEmployee() {
        // Act
        boolean result = employeeIntegrationService.isActive(testEmployee.getIsraeliId());

        // Assert
        assertTrue(result, "Should return true for active employee");
    }

    @Test
    @DisplayName("Test isActive with inactive employee")
    public void testIsActiveWithInactiveEmployee() throws SQLException {
        // Arrange
        // Update employee to be inactive
        EmployeeDTO employeeDTO = employeeDAO.getById(testEmployee.getIsraeliId());
        employeeDTO.setActive(false);
        employeeDAO.update(employeeDTO);

        // Act
        boolean result = employeeIntegrationService.isActive(testEmployee.getIsraeliId());

        // Assert
        assertFalse(result, "Should return false for inactive employee");
    }

    @Test
    @DisplayName("Test isActive with non-existent employee")
    public void testIsActiveWithNonExistentEmployee() {
        // Act
        boolean result = employeeIntegrationService.isActive(999999999);

        // Assert
        assertFalse(result, "Should return false for non-existent employee");
    }

    @Test
    @DisplayName("Test hasRole with employee having the role")
    public void testHasRoleWithEmployeeHavingRole() {
        // Act
        boolean result = employeeIntegrationService.hasRole(testEmployee.getIsraeliId(), "TestRole");

        // Assert
        assertTrue(result, "Should return true when employee has the role");
    }

    @Test
    @DisplayName("Test hasRole with employee not having the role")
    public void testHasRoleWithEmployeeNotHavingRole() {
        // Act
        boolean result = employeeIntegrationService.hasRole(testEmployee.getIsraeliId(), "AdminRole");

        // Assert
        assertFalse(result, "Should return false when employee doesn't have the role");
    }

    @Test
    @DisplayName("Test hasRole with non-existent employee")
    public void testHasRoleWithNonExistentEmployee() {
        // Act
        boolean result = employeeIntegrationService.hasRole(999999999, "TestRole");

        // Assert
        assertFalse(result, "Should return false for non-existent employee");
    }

    @Test
    @DisplayName("Test isDriverOnShiftAt with driver on shift")
    public void testIsDriverOnShiftAtWithDriverOnShift() {
        // Arrange
        LocalDateTime dateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 0));

        // Act
        boolean result = employeeIntegrationService.isDriverOnShiftAt(
                driverEmployee.getIsraeliId(),
                dateTime,
                testBranch.getBranchAddress(),
                testBranch.getAreaCode()
        );

        // Assert
        assertTrue(result, "Should return true when driver is on shift at the specified time");
    }

    @Test
    @DisplayName("Test isDriverOnShiftAt with non-driver employee")
    public void testIsDriverOnShiftAtWithNonDriverEmployee() {
        // Arrange
        LocalDateTime dateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 0));

        // Act
        boolean result = employeeIntegrationService.isDriverOnShiftAt(
                testEmployee.getIsraeliId(), // Not a driver
                dateTime,
                testBranch.getBranchAddress(),
                testBranch.getAreaCode()
        );

        // Assert
        assertFalse(result, "Should return false when employee is not a driver");
    }

    @Test
    @DisplayName("Test isWarehouseManOnShiftAt with WarehouseMan on shift")
    public void testIsWarehouseManOnShiftAtWithWarehouseManOnShift() {
        // Arrange
        LocalDateTime dateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 0));

        // Act
        boolean result = employeeIntegrationService.isWarehousemanOnShiftAt(
                dateTime,
                testBranch.getBranchAddress(),
                testBranch.getAreaCode()
        );

        // Assert
        assertTrue(result, "Should return true when WarehouseMan is on shift at the specified time");
    }


    @Test
    @DisplayName("Test isEmployeeAuthorised with employee having the permission")
    public void testIsEmployeeAuthorisedWithEmployeeHavingPermission() {
        // Act
        boolean result = employeeIntegrationService.isEmployeeAuthorised(
                testEmployee.getIsraeliId(),
                "TestPermission"
        );

        // Assert
        assertTrue(result, "Should return true when employee has the permission");
    }

    @Test
    @DisplayName("Test isEmployeeAuthorised with employee not having the permission")
    public void testIsEmployeeAuthorisedWithEmployeeNotHavingPermission() {
        // Act & Assert
        assertThrows(ServiceLayer.exception.AuthorizationException.class, () -> {
            employeeIntegrationService.isEmployeeAuthorised(
                    testEmployee.getIsraeliId(),
                    "AdminPermission"
            );
        }, "Should throw AuthorizationException when employee doesn't have the permission");
    }

    @Test
    @DisplayName("Test isBranch with valid branch")
    public void testIsBranchWithValidBranch() {
        // Act
        boolean result = employeeIntegrationService.isBranch(
                testBranch.getBranchAddress(),
                testBranch.getAreaCode()
        );

        // Assert
        assertTrue(result, "Should return true for valid branch");
    }

    @Test
    @DisplayName("Test isBranch with invalid branch")
    public void testIsBranchWithInvalidBranch() {
        // Act
        boolean result = employeeIntegrationService.isBranch(
                "Invalid Address",
                999
        );

        // Assert
        assertFalse(result, "Should return false for invalid branch");
    }

    // Edge cases and tests with multiple employees

    @Test
    @DisplayName("Test with multiple employees having the same role")
    public void testWithMultipleEmployeesHavingSameRole() throws SQLException {
        // Arrange
        // Create another employee with the same role
        long newEmployeeId = 555555555;
        Set<String> roles = new HashSet<>();
        roles.add("TestRole");
        Map<String, Object> termsOfEmployment = new HashMap<>();
        termsOfEmployment.put("contractType", "FullTime");

        EmployeeDTO newEmployeeDTO = new EmployeeDTO(
                newEmployeeId,
                "Another",
                "Employee",
                5500,
                termsOfEmployment,
                roles,
                LocalDate.now(),
                true,
                LocalDate.now(),
                LocalDate.now(),
                testBranch.getBranchId()
        );
        employeeDAO.insert(newEmployeeDTO);

        // Act
        boolean result1 = employeeIntegrationService.hasRole(testEmployee.getIsraeliId(), "TestRole");
        boolean result2 = employeeIntegrationService.hasRole(newEmployeeId, "TestRole");

        // Assert
        assertTrue(result1, "First employee should have the role");
        assertTrue(result2, "Second employee should have the role");
    }

    @Test
    @DisplayName("Test with employee having multiple roles")
    public void testWithEmployeeHavingMultipleRoles() throws SQLException {
        // Arrange
        // Add another role to the test employee
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO EmployeeRoles (israeliId, role) VALUES (" + 
                    testEmployee.getIsraeliId() + ", 'AdminRole')");
        }

        // Act
        boolean result1 = employeeIntegrationService.hasRole(testEmployee.getIsraeliId(), "TestRole");
        boolean result2 = employeeIntegrationService.hasRole(testEmployee.getIsraeliId(), "AdminRole");

        // Assert
        assertTrue(result1, "Employee should have the TestRole");
        assertTrue(result2, "Employee should have the AdminRole");
    }

    @Test
    @DisplayName("Test with inactive driver on shift")
    public void testWithInactiveDriverOnShift() throws SQLException {
        // Arrange
        // Update driver to be inactive
        EmployeeDTO driverDTO = employeeDAO.getById(driverEmployee.getIsraeliId());
        driverDTO.setActive(false);
        employeeDAO.update(driverDTO);

        LocalDateTime dateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 0));

        // Act
        boolean isActive = employeeIntegrationService.isActive(driverEmployee.getIsraeliId());
        boolean isOnShift = employeeIntegrationService.isDriverOnShiftAt(
                driverEmployee.getIsraeliId(),
                dateTime,
                testBranch.getBranchAddress(),
                testBranch.getAreaCode()
        );

        // Assert
        assertFalse(isActive, "Driver should be inactive");
        assertTrue(isOnShift, "Driver should still be on shift even if inactive");
    }

}
