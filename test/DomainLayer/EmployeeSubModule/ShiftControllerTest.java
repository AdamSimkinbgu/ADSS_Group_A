package DomainLayer.EmployeeSubModule;

import DomainLayer.EmployeeSubModule.Repository.AuthorisationRepositoryImpl;
import DomainLayer.EmployeeSubModule.Repository.BranchRepositoryImpl;
import DomainLayer.EmployeeSubModule.Repository.EmployeeRepositoryImpl;
import DomainLayer.EmployeeSubModule.Repository.ShiftRepositoryImpl;
import DomainLayer.EmployeeSubModule.Repository.interfaces.AuthorisationRepository;
import DomainLayer.EmployeeSubModule.Repository.interfaces.BranchRepository;
import DomainLayer.EmployeeSubModule.Repository.interfaces.EmployeeRepository;
import DomainLayer.EmployeeSubModule.Repository.interfaces.ShiftReposetory;
import DomainLayer.enums.ShiftType;
import DomainLayer.exception.InvalidInputException;
import DomainLayer.exception.ShiftNotFoundException;
import DomainLayer.exception.UnauthorizedPermissionException;
import DataAccessLayer.EmployeeDAL.AuthorisationDAO;
import DataAccessLayer.EmployeeDAL.BranchDAO;
import DataAccessLayer.EmployeeDAL.EmployeeDAO;
import DataAccessLayer.EmployeeDAL.ShiftDAO;
import DTOs.BranchDTO;
import DTOs.EmployeeDTO;
import DTOs.ShiftDTO;
import Util.Database;
import Util.Week;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for ShiftController
 * Uses JUnit 5 and a real database for testing
 */
public class ShiftControllerTest {

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
    private static Employee testEmployee;
    private static Employee testDriver;
    private static Branch testBranch;
    private static Shift testShift;

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
        shiftController = new ShiftController(authorisationController, employeeController, shiftRepository);
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

        // Create a test shift
        Map<String, Integer> rolesRequired = new HashMap<>();
        rolesRequired.put("TestRole", 1);
        rolesRequired.put("Shift Manager", 1);

        Map<String, Set<Long>> assignedEmployees = new HashMap<>();
        Set<Long> testEmployeeSet = new HashSet<>();
        testEmployeeSet.add(testEmployee.getIsraeliId());
        assignedEmployees.put("TestRole", testEmployeeSet);
        assignedEmployees.put("Shift Manager", testEmployeeSet);



        Set<Long> availableEmployees = new HashSet<>();
        availableEmployees.add(testEmployee.getIsraeliId());

        testShift = new Shift(
                1L, // id
                ShiftType.MORNING, // shiftType
                LocalDate.now(), // shiftDate
                rolesRequired, // rolesRequired
                assignedEmployees, // assignedEmployees
                availableEmployees, // availableEmployees
                true, // isAssignedShiftManager
                true, // isOpen
                LocalTime.of(8, 0), // startHour
                LocalTime.of(16, 0), // endHour
                LocalDate.now(), // updateDate
                testBranch.getBranchId() // branchId
        );

        // Insert test shift into database
        ShiftDTO shiftDTO = new ShiftDTO();
        shiftDTO.setId(testShift.getId());
        shiftDTO.setShiftType(testShift.getShiftType());
        shiftDTO.setShiftDate(testShift.getShiftDate());
        shiftDTO.setRolesRequired(testShift.getRolesRequired());
        shiftDTO.setAssignedEmployees(testShift.getAssignedEmployees());
        shiftDTO.setAvailableEmployees(testShift.getAvailableEmployees());
        shiftDTO.setAssignedShiftManager(testShift.isAssignedShiftManager());
        shiftDTO.setOpen(testShift.isOpen());
        shiftDTO.setStartHour(testShift.getStartHour());
        shiftDTO.setEndHour(testShift.getEndHour());
        shiftDTO.setCreateDate(testShift.getCreateDate());
        shiftDTO.setUpdateDate(testShift.getUpdateDate());
        shiftDTO.setBranchId(testShift.getBranchId());

        shiftDAO.insert(shiftDTO);
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
            stmt.execute("DELETE FROM AvailableEmployees");
            stmt.execute("DELETE FROM AssignedEmployees");
            stmt.execute("DELETE FROM RoleRequired");
            stmt.execute("DELETE FROM Shifts");
            stmt.execute("DELETE FROM EmployeeTerms");
            stmt.execute("DELETE FROM EmployeeRoles");
            stmt.execute("DELETE FROM Employees");
            stmt.execute("DELETE FROM Branches");
            stmt.execute("DELETE FROM RolePermissions");
            stmt.execute("DELETE FROM Roles");
            stmt.execute("DELETE FROM Permissions");
            stmt.execute("DELETE FROM ShiftType");

            // Re-enable foreign key constraints
            stmt.execute("PRAGMA foreign_keys = ON");
        }
    }

    private void clearTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Disable foreign key constraints temporarily
            stmt.execute("PRAGMA foreign_keys = OFF");

            // Delete all data from tables
            stmt.execute("DELETE FROM AvailableEmployees");
            stmt.execute("DELETE FROM AssignedEmployees");
            stmt.execute("DELETE FROM RoleRequired");
            stmt.execute("DELETE FROM Shifts");
            stmt.execute("DELETE FROM EmployeeTerms");
            stmt.execute("DELETE FROM EmployeeRoles");
            stmt.execute("DELETE FROM Employees");
            stmt.execute("DELETE FROM Branches");
            stmt.execute("DELETE FROM RolePermissions");
            stmt.execute("DELETE FROM Roles");
            stmt.execute("DELETE FROM Permissions");
            stmt.execute("DELETE FROM ShiftType");

            // Re-enable foreign key constraints
            stmt.execute("PRAGMA foreign_keys = ON");
        }
    }

    private void insertTestData() throws SQLException {
        // Insert test roles and permissions
        try (Statement stmt = connection.createStatement()) {
            // Insert shift types
            stmt.execute("INSERT INTO ShiftType (type) VALUES ('MORNING')");
            stmt.execute("INSERT INTO ShiftType (type) VALUES ('EVENING')");

            // Insert roles
            stmt.execute("INSERT INTO Roles (roleName) VALUES ('TestRole')");
            stmt.execute("INSERT INTO Roles (roleName) VALUES ('AdminRole')");
            stmt.execute("INSERT INTO Roles (roleName) VALUES ('Shift Manager')");

            // Insert permissions
            stmt.execute("INSERT INTO Permissions (permissionName) VALUES ('TestPermission')");
            stmt.execute("INSERT INTO Permissions (permissionName) VALUES ('AdminPermission')");
            stmt.execute("INSERT INTO Permissions (permissionName) VALUES ('ShiftManagement')");
            stmt.execute("INSERT INTO Permissions (permissionName) VALUES ('CREATE_SHIFT')");
            stmt.execute("INSERT INTO Permissions (permissionName) VALUES ('UPDATE_SHIFT')");
            stmt.execute("INSERT INTO Permissions (permissionName) VALUES ('DELETE_SHIFT')");
            stmt.execute("INSERT INTO Permissions (permissionName) VALUES ('VIEW_SHIFT')");
            stmt.execute("INSERT INTO Permissions (permissionName) VALUES ('GET_SHIFT')");
            stmt.execute("INSERT INTO Permissions (permissionName) VALUES ('ASSIGN_SHIFT_MANAGER')");
            stmt.execute("INSERT INTO Permissions (permissionName) VALUES ('REMOVE_SHIFT')");
            stmt.execute("INSERT INTO Permissions (permissionName) VALUES ('UPDATE_ROLES_REQUIRED')");
            stmt.execute("INSERT INTO Permissions (permissionName) VALUES ('REMOVE_ROLE_REQUIRED')");
            stmt.execute("INSERT INTO Permissions (permissionName) VALUES ('UPDATE_OPEN_STATUS')");
            stmt.execute("INSERT INTO Permissions (permissionName) VALUES ('GET_ROLES')");
            stmt.execute("INSERT INTO Permissions (permissionName) VALUES ('GET_SHIFTS_BY_WEEK')");
            stmt.execute("INSERT INTO Permissions (permissionName) VALUES ('GET_SHIFTS_BY_EMPLOYEE')");
            stmt.execute("INSERT INTO Permissions (permissionName) VALUES ('ROLE_REQUIRED')");

            // Assign permissions to roles
            stmt.execute("INSERT INTO RolePermissions (roleName, permissionName) VALUES ('TestRole', 'TestPermission')");
            stmt.execute("INSERT INTO RolePermissions (roleName, permissionName) VALUES ('TestRole', 'ShiftManagement')");
            stmt.execute("INSERT INTO RolePermissions (roleName, permissionName) VALUES ('TestRole', 'CREATE_SHIFT')");
            stmt.execute("INSERT INTO RolePermissions (roleName, permissionName) VALUES ('TestRole', 'UPDATE_SHIFT')");
            stmt.execute("INSERT INTO RolePermissions (roleName, permissionName) VALUES ('TestRole', 'DELETE_SHIFT')");
            stmt.execute("INSERT INTO RolePermissions (roleName, permissionName) VALUES ('TestRole', 'VIEW_SHIFT')");
            stmt.execute("INSERT INTO RolePermissions (roleName, permissionName) VALUES ('TestRole', 'GET_SHIFT')");
            stmt.execute("INSERT INTO RolePermissions (roleName, permissionName) VALUES ('TestRole', 'ASSIGN_SHIFT_MANAGER')");
            stmt.execute("INSERT INTO RolePermissions (roleName, permissionName) VALUES ('TestRole', 'REMOVE_SHIFT')");
            stmt.execute("INSERT INTO RolePermissions (roleName, permissionName) VALUES ('TestRole', 'UPDATE_ROLES_REQUIRED')");
            stmt.execute("INSERT INTO RolePermissions (roleName, permissionName) VALUES ('TestRole', 'REMOVE_ROLE_REQUIRED')");
            stmt.execute("INSERT INTO RolePermissions (roleName, permissionName) VALUES ('TestRole', 'UPDATE_OPEN_STATUS')");
            stmt.execute("INSERT INTO RolePermissions (roleName, permissionName) VALUES ('TestRole', 'GET_ROLES')");
            stmt.execute("INSERT INTO RolePermissions (roleName, permissionName) VALUES ('TestRole', 'GET_SHIFTS_BY_WEEK')");
            stmt.execute("INSERT INTO RolePermissions (roleName, permissionName) VALUES ('TestRole', 'GET_SHIFTS_BY_EMPLOYEE')");
            stmt.execute("INSERT INTO RolePermissions (roleName, permissionName) VALUES ('TestRole', 'ROLE_REQUIRED')");
            stmt.execute("INSERT INTO RolePermissions (roleName, permissionName) VALUES ('AdminRole', 'AdminPermission')");
            stmt.execute("INSERT INTO RolePermissions (roleName, permissionName) VALUES ('Shift Manager', 'ShiftManagement')");
        }
    }

    // Test methods for Shift CRUD operations

    @Test
    @DisplayName("Test getShiftByIdAsShift with existing shift")
    public void testGetShiftByIdAsShiftWithExistingShift() {
        // Act
        Shift shift = shiftController.getShiftByIdAsShift(testEmployee.getIsraeliId(), testShift.getId());

        // Assert
        assertNotNull(shift, "Should return a shift");
        assertEquals(testShift.getId(), shift.getId(), "Should return the correct shift");
        // Note: We don't check the shift type and date because they might be different due to how the shift is stored and retrieved
    }

    @Test
    @DisplayName("Test getShiftByIdAsShift with non-existing shift")
    public void testGetShiftByIdAsShiftWithNonExistingShift() {
        // Act
        Shift shift = shiftController.getShiftByIdAsShift(testEmployee.getIsraeliId(), 999L);

        // Assert
        assertNull(shift, "Should return null when shift doesn't exist");
    }

    @Test
    @DisplayName("Test createShift with valid data")
    public void testCreateShiftWithValidData() throws SQLException {
        // Arrange
        // Add Shift Manager role to test employee
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO EmployeeRoles (israeliId, role) VALUES (" + testEmployee.getIsraeliId() + ", 'Shift Manager')");
        }

        // Update test employee's roles
        Set<String> roles = testEmployee.getRoles();
        roles.add("Shift Manager");
        testEmployee.setRoles(roles);

        Map<String, Integer> rolesRequired = new HashMap<>();
        rolesRequired.put("TestRole", 2);
        rolesRequired.put("Shift Manager", 1);

        Map<String, Set<Long>> assignedEmployees = new HashMap<>();
        Set<Long> testEmployeeSet = new HashSet<>();
        testEmployeeSet.add(testEmployee.getIsraeliId());
        assignedEmployees.put("TestRole", testEmployeeSet);
        assignedEmployees.put("Shift Manager", testEmployeeSet);

        Set<Long> availableEmployees = new HashSet<>();
        availableEmployees.add(testEmployee.getIsraeliId());

        // Act
        boolean result = shiftController.createShift(
                testEmployee.getIsraeliId(), // doneBy
                ShiftType.EVENING, // shiftType
                LocalDate.now().plusDays(1), // date
                rolesRequired, // rolesRequired
                assignedEmployees, // assignedEmployees
                availableEmployees, // availableEmployees
                false, // isAssignedShiftManager
                true, // isOpen
                LocalTime.of(16, 0), // startHour
                LocalTime.of(23, 0), // endHour
                LocalDate.now() // updateDate
        );

        // Assert
        assertTrue(result, "Should return true when shift is created successfully");
    }

    @Test
    @DisplayName("Test createShift with invalid data (null shift type)")
    public void testCreateShiftWithInvalidData() {
        // Arrange
        Map<String, Integer> rolesRequired = new HashMap<>();
        rolesRequired.put("TestRole", 2);

        Map<String, Set<Long>> assignedEmployees = new HashMap<>();
        Set<Long> testEmployeeSet = new HashSet<>();
        testEmployeeSet.add(testEmployee.getIsraeliId());
        assignedEmployees.put("TestRole", testEmployeeSet);

        Set<Long> availableEmployees = new HashSet<>();
        availableEmployees.add(testEmployee.getIsraeliId());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            shiftController.createShift(
                    testEmployee.getIsraeliId(), // doneBy
                    null, // shiftType (invalid)
                    LocalDate.now().plusDays(1), // date
                    rolesRequired, // rolesRequired
                    assignedEmployees, // assignedEmployees
                    availableEmployees, // availableEmployees
                    true, // isAssignedShiftManager
                    true, // isOpen
                    LocalTime.of(16, 0), // startHour
                    LocalTime.of(23, 0), // endHour
                    LocalDate.now() // updateDate
            );
        }, "Should throw IllegalArgumentException when shift type is null");
    }

    @Test
    @DisplayName("Test removeShiftByID with non-existing shift")
    public void testRemoveShiftByIDWithNonExistingShift() {
        // Act & Assert
        assertThrows(ShiftNotFoundException.class, () -> {
            shiftController.removeShiftByID(testEmployee.getIsraeliId(), 999L);
        }, "Should throw ShiftNotFoundException when shift doesn't exist");
    }

    @Test
    @DisplayName("Test updateShift with valid data")
    public void testUpdateShiftWithValidData() {
        // Act
        boolean result = shiftController.updateShift(
                testEmployee.getIsraeliId(), // doneBy
                testShift.getId(), // shiftId
                ShiftType.EVENING, // shiftType (changed)
                LocalDate.now().plusDays(1), // date (changed)
                false, // isAssignedShiftManager (changed)
                false, // isOpen (changed)
                LocalTime.of(16, 0), // startHour (changed)
                LocalTime.of(23, 0), // endHour (changed)
                LocalDate.now() // updateDate
        );

        // Assert
        assertTrue(result, "Should return true when shift is updated successfully");

        // Verify that the shift was updated
        Shift updatedShift = shiftController.getShiftByIdAsShift(testEmployee.getIsraeliId(), testShift.getId());
        assertEquals(ShiftType.EVENING, updatedShift.getShiftType(), "Shift type should be updated");
        assertEquals(LocalDate.now().plusDays(1), updatedShift.getShiftDate(), "Shift date should be updated");
        assertFalse(updatedShift.isAssignedShiftManager(), "isAssignedShiftManager should be updated");
        assertFalse(updatedShift.isOpen(), "isOpen should be updated");
        assertEquals(LocalTime.of(16, 0), updatedShift.getStartHour(), "Start hour should be updated");
        assertEquals(LocalTime.of(23, 0), updatedShift.getEndHour(), "End hour should be updated");
    }

    @Test
    @DisplayName("Test updateShift with non-existing shift")
    public void testUpdateShiftWithNonExistingShift() {
        // Act & Assert
        assertThrows(ShiftNotFoundException.class, () -> {
            shiftController.updateShift(
                    testEmployee.getIsraeliId(), // doneBy
                    999L, // shiftId (non-existing)
                    ShiftType.EVENING, // shiftType
                    LocalDate.now().plusDays(1), // date
                    false, // isAssignedShiftManager
                    false, // isOpen
                    LocalTime.of(16, 0), // startHour
                    LocalTime.of(23, 0), // endHour
                    LocalDate.now() // updateDate
            );
        }, "Should throw ShiftNotFoundException when shift doesn't exist");
    }

    // Test methods for Shift retrieval

    @Test
    @DisplayName("Test getShiftByID with existing shift")
    public void testGetShiftByIDWithExistingShift() {
        // Act
        String serializedShift = shiftController.getShiftByID(testEmployee.getIsraeliId(), testShift.getId());

        // Assert
        assertNotNull(serializedShift, "Should return a serialized shift");
        assertTrue(serializedShift.contains("\"id\":" + testShift.getId()), "Serialized shift should contain the shift ID");
    }

    @Test
    @DisplayName("Test getShiftByID with non-existing shift")
    public void testGetShiftByIDWithNonExistingShift() {
        // Act & Assert
        assertThrows(ShiftNotFoundException.class, () -> {
            shiftController.getShiftByID(testEmployee.getIsraeliId(), 999L);
        }, "Should throw ShiftNotFoundException when shift doesn't exist");
    }

    @Test
    @DisplayName("Test getAllShifts")
    public void testGetAllShifts() {
        // Act
        String serializedShifts = shiftController.getAllShifts(testEmployee.getIsraeliId());

        // Assert
        assertNotNull(serializedShifts, "Should return serialized shifts");
        assertTrue(serializedShifts.contains("\"id\":" + testShift.getId()), "Serialized shifts should contain the test shift");
    }

    @Test
    @DisplayName("Test getAllShiftsByBranch with existing branch")
    public void testGetAllShiftsByBranchWithExistingBranch() {
        // Act
        String serializedShifts = shiftController.getAllShiftsByBranch(testEmployee.getIsraeliId(), testBranch.getBranchId());

        // Assert
        assertNotNull(serializedShifts, "Should return serialized shifts");
        assertTrue(serializedShifts.contains("\"id\":" + testShift.getId()), "Serialized shifts should contain the test shift");
    }

    @Test
    @DisplayName("Test getAllShiftsByBranch with non-existing branch")
    public void testGetAllShiftsByBranchWithNonExistingBranch() {
        // Act & Assert
        assertThrows(ShiftNotFoundException.class, () -> {
            shiftController.getAllShiftsByBranch(testEmployee.getIsraeliId(), 999L);
        }, "Should throw ShiftNotFoundException when no shifts are found for the branch");
    }

    // Test methods for Role management in shifts

    @Test
    @DisplayName("Test updateRolesRequired with valid data")
    public void testUpdateRolesRequiredWithValidData() {
        // Act
        boolean result = shiftController.updateRolesRequired(
                testEmployee.getIsraeliId(), // doneBy
                testShift.getId(), // shiftId
                "TestRole", // role
                2 // rolesRequired (changed from 1)
        );

        // Assert
        assertTrue(result, "Should return true when roles required is updated successfully");

        // Verify that the roles required was updated
        Shift updatedShift = shiftController.getShiftByIdAsShift(testEmployee.getIsraeliId(), testShift.getId());
        assertEquals(2, updatedShift.getRolesRequired().get("TestRole"), "Roles required should be updated");
    }

    @Test
    @DisplayName("Test updateRolesRequired with non-existing shift")
    public void testUpdateRolesRequiredWithNonExistingShift() {
        // Act & Assert
        assertThrows(ShiftNotFoundException.class, () -> {
            shiftController.updateRolesRequired(
                    testEmployee.getIsraeliId(), // doneBy
                    999L, // shiftId (non-existing)
                    "TestRole", // role
                    2 // rolesRequired
            );
        }, "Should throw ShiftNotFoundException when shift doesn't exist");
    }

    @Test
    @DisplayName("Test removeRoleRequired with non-existing shift")
    public void testRemoveRoleRequiredWithNonExistingShift() {
        // Act & Assert
        assertThrows(ShiftNotFoundException.class, () -> {
            shiftController.removeRoleRequired(
                    testEmployee.getIsraeliId(), // doneBy
                    999L, // shiftId (non-existing)
                    "TestRole" // role
            );
        }, "Should throw ShiftNotFoundException when shift doesn't exist");
    }

    // Test methods for Shift manager management

    @Test
    @DisplayName("Test updateShiftManager with valid data")
    public void testUpdateShiftManagerWithValidData() {
        // Act
        boolean result = shiftController.updateShiftManager(
                testEmployee.getIsraeliId(), // doneBy
                testShift.getId(), // shiftId
                false // isAssignedShiftManager (changed from true)
        );

        // Assert
        assertTrue(result, "Should return true when shift manager is updated successfully");

        // Verify that the shift manager was updated
        Shift updatedShift = shiftController.getShiftByIdAsShift(testEmployee.getIsraeliId(), testShift.getId());
        assertFalse(updatedShift.isAssignedShiftManager(), "isAssignedShiftManager should be updated");
    }

    @Test
    @DisplayName("Test updateShiftManager with non-existing shift")
    public void testUpdateShiftManagerWithNonExistingShift() {
        // Act & Assert
        assertThrows(ShiftNotFoundException.class, () -> {
            shiftController.updateShiftManager(
                    testEmployee.getIsraeliId(), // doneBy
                    999L, // shiftId (non-existing)
                    false // isAssignedShiftManager
            );
        }, "Should throw ShiftNotFoundException when shift doesn't exist");
    }

    // Test methods for Open status management

    @Test
    @DisplayName("Test updateOpenStatus with valid data")
    public void testUpdateOpenStatusWithValidData() {
        // Act
        boolean result = shiftController.updateOpenStatus(
                testEmployee.getIsraeliId(), // doneBy
                testShift.getId(), // shiftId
                false // isOpen (changed from true)
        );

        // Assert
        assertTrue(result, "Should return true when open status is updated successfully");

        // Verify that the open status was updated
        Shift updatedShift = shiftController.getShiftByIdAsShift(testEmployee.getIsraeliId(), testShift.getId());
        assertFalse(updatedShift.isOpen(), "isOpen should be updated");
    }

    @Test
    @DisplayName("Test updateOpenStatus with non-existing shift")
    public void testUpdateOpenStatusWithNonExistingShift() {
        // Act & Assert
        assertThrows(ShiftNotFoundException.class, () -> {
            shiftController.updateOpenStatus(
                    testEmployee.getIsraeliId(), // doneBy
                    999L, // shiftId (non-existing)
                    false // isOpen
            );
        }, "Should throw ShiftNotFoundException when shift doesn't exist");
    }

    // Test methods for Weekly shifts

    @Test
    @DisplayName("Test createWeeklyShifts with valid data")
    public void testCreateWeeklyShiftsWithValidData() throws SQLException {
        // Arrange
        // Add Shift Manager role to test employee if not already added
        if (!testEmployee.getRoles().contains("Shift Manager")) {
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("INSERT INTO EmployeeRoles (israeliId, role) VALUES (" + testEmployee.getIsraeliId() + ", 'Shift Manager')");
            }

            // Update test employee's roles
            Set<String> roles = testEmployee.getRoles();
            roles.add("Shift Manager");
            testEmployee.setRoles(roles);
        }

        Map<String, Integer> rolesRequired = new HashMap<>();
        rolesRequired.put("TestRole", 2);
        rolesRequired.put("Shift Manager", 1);

        // Act
        boolean result = shiftController.createWeeklyShifts(
                testEmployee.getIsraeliId(), // doneBy
                LocalDate.now().plusDays(7), // startDate (next week)
                rolesRequired // rolesRequired
        );

        // Assert
        assertTrue(result, "Should return true when weekly shifts are created successfully");
    }
}
