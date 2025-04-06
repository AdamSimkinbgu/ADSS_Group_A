package DomainLayer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeControllerTest {

    private EmployeeController employeeController;
    private AuthorisationController authorisationController;
    private Set<Employee> employees;
    private Set<Role> roles;
    private Set<Permission> permissions;
    TermsOfEmployment terms = new TermsOfEmployment(TermsOfEmployment.ContractType.HOURLY, 34.50, 6.0, 8.33, true, 23.60, true, 1.25, 1.5, 42, 12, 18, 2.25);

    @BeforeEach
    void setUp() {
        // Initialize the sets
        employees = new HashSet<>();
        permissions = new HashSet<>();
        roles = new HashSet<>();
        // Create some roles and permissions for testing
        Permission CREATE_EMPLOYEE = new Permission(1, "CREATE_EMPLOYEE", "Permission to create employee");
        Permission UPDATE_EMPLOYEE = new Permission(2, "UPDATE_EMPLOYEE", "Permission to update employee");
        Permission ADD_ROLE_TO_EMPLOYEE = new Permission(3, "ADD_ROLE_TO_EMPLOYEE", "Permission to add role to employee");
        permissions.add(CREATE_EMPLOYEE);
        permissions.add(UPDATE_EMPLOYEE);
        permissions.add(ADD_ROLE_TO_EMPLOYEE);

        Role adminRole = new Role(1, "Admin", new HashSet<>(Set.of(CREATE_EMPLOYEE, UPDATE_EMPLOYEE, ADD_ROLE_TO_EMPLOYEE)));
        roles.add(adminRole);

        // Create Admin employee
        Employee admin = new Employee(1, 123456789, "Admin", "User", 100000, terms, roles, LocalDate.now(), true, LocalDate.now(), LocalDate.now());
        employees.add(admin);

        authorisationController = new AuthorisationController(roles, permissions);
        employeeController = new EmployeeController(employees, authorisationController);
    }

    @Test
    void testCreateEmployee() {
        boolean result = employeeController.createEmployee(123456789, 11111111, "John", "Doe", 50000, terms, new HashSet<Role>(), LocalDate.now());
        assertTrue(result); // Employee created successfully
        assertEquals(2, employees.size()); // 1 admin + 1 new employee
    }

    @Test
    void testCreateEmployeeAlreadyExists() {
        employeeController.createEmployee(123456789, 11111111, "John", "Doe", 50000, terms, new HashSet<>(), LocalDate.now());
        Exception exception = assertThrows(RuntimeException.class, () -> {
            employeeController.createEmployee(123456789, 11111111, "John", "Doe", 50000, terms, new HashSet<>(), LocalDate.now());
        });
        assertEquals("Employee already exists", exception.getMessage());
    }

    @Test
    void testUpdateEmployee() {
        employeeController.createEmployee(123456789, 11111111, "John", "Doe", 50000, terms, new HashSet<>(), LocalDate.now());
        boolean result = employeeController.updateEmployee(123456789, 11111111, "Jane", "Doe", 60000, terms, true);
        assertTrue(result);
        Employee employee = employeeController.getEmployeeByIsraeliId(11111111);
        assertEquals("Jane", employee.getFirstName());
        assertEquals(60000, employee.getSalary());
    }

    @Test
    void testDeleteEmployee() {
        employeeController.createEmployee(1, 123456789, "John", "Doe", 50000, terms, new HashSet<>(), LocalDate.now());
        boolean result = employeeController.deleteEmployee(1, 123456789);
        assertTrue(result);
        assertNull(employeeController.getEmployeeByIsraeliId(123456789));
    }

    @Test
    void testAddRoleToEmployee() {
        employeeController.createEmployee(123456789, 11111111, "Ramzi", "Abed-Ramzi", 50000, terms, new HashSet<>(), LocalDate.now());
        Permission ADD_ROLE_TO_EMPLOYEE = new Permission(4, "ADD_ROLE_TO_EMPLOYEE", "Permission to add role to employee");
        permissions.add(ADD_ROLE_TO_EMPLOYEE);
        Role RoleEditor = new Role(2, "Editor", new HashSet<>(Set.of(ADD_ROLE_TO_EMPLOYEE)));
        roles.add(RoleEditor);
        boolean result = employeeController.addRoleToEmployee(123456789, 123456789, "Editor");
        assertTrue(result);
        Role role = new Role(1, "Sub-Manager", new HashSet<>(Set.of(ADD_ROLE_TO_EMPLOYEE)));
        roles.add(role);
        result = employeeController.addRoleToEmployee(123456789, 11111111, "Sub-Manager");
        assertTrue(result);
        Employee employee = employeeController.getEmployeeByIsraeliId(11111111);
        assertTrue(employee.getRoles().contains(role));
    }

    @Test
    void testCreateEmployeeWithInvalidData() {
        assertThrows(RuntimeException.class, () -> {
            employeeController.createEmployee(1, 123456789, "", "Doe", 50000, terms, new HashSet<>(), LocalDate.now());
        });
        assertThrows(RuntimeException.class, () -> {
            employeeController.createEmployee(1, 123456789, "John", "", 50000, terms, new HashSet<>(), LocalDate.now());
        });
        assertThrows(RuntimeException.class, () -> {
            employeeController.createEmployee(1, 123456789, "John", "Doe", 0, terms, new HashSet<>(), LocalDate.now());
        });
        assertThrows(RuntimeException.class, () -> {
            employeeController.createEmployee(1, 123456789, "John", "Doe", 50000, null, new HashSet<>(), LocalDate.now());
        });
        assertThrows(RuntimeException.class, () -> {
            employeeController.createEmployee(1, 123456789, "John", "Doe", 50000, terms, new HashSet<>(), null);
        });
    }

    @Test
    void testUpdateEmployeeWithInvalidData() {
        assertThrows(RuntimeException.class, () -> {
            employeeController.updateEmployee(123456789, 123456789, "", "Doe", 60000, terms, true);
        });
        assertThrows(RuntimeException.class, () -> {
            employeeController.updateEmployee(123456789, 123456789, "Jane", "", 60000, terms, true);
        });
        assertThrows(RuntimeException.class, () -> {
            employeeController.updateEmployee(123456789, 123456789, "Jane", "Doe", 0, terms, true);
        });
        assertThrows(RuntimeException.class, () -> {
            employeeController.updateEmployee(123456789, 123456789, "Jane", "Doe", 60000, null, true);
        });
    }

    @Test
    void testAddRoleToNonExistentEmployee() {
        Permission permission = new Permission(1, "ADD_ROLE_TO_EMPLOYEE", "Permission to add role to employee");
        permissions.add(permission);
        Role role = new Role(1, "Manager", new HashSet<>(Set.of(permission)));
        roles.add(role);
        Exception exception = assertThrows(RuntimeException.class, () -> {
            employeeController.addRoleToEmployee(1, 123456789, "Manager");
        });
        assertEquals("Employee not found", exception.getMessage());
    }
}