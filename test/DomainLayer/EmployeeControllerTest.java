package DomainLayer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
class EmployeeControllerTest {

    AuthorisationController authorisationController;
    EmployeeController employeeController;

    final String CREATE_EMPLOYEE = "CREATE_EMPLOYEE";
    final String ADD_EMPLOYEE = "ADD_EMPLOYEE";
    final String UPDATE_EMPLOYEE = "UPDATE_EMPLOYEE";
    final String CASHIER = "CASHIER";
    Role admin;
    Role cashier;
    Set<String> permissions;
    Set<Role> roles;
    Employee shira;
    Employee cochava;

    @BeforeEach
    void setUp() {
        admin = new Role(0,"ADMIN", Set.of(CREATE_EMPLOYEE, ADD_EMPLOYEE, UPDATE_EMPLOYEE));
        cashier = new Role(1,"CASHIER", Set.of(CASHIER));

        permissions = Set.of(CREATE_EMPLOYEE, ADD_EMPLOYEE, UPDATE_EMPLOYEE);
        roles = Set.of(admin, cashier);

        shira = new Employee(0,0,"Shira", "Shtinboch", 10000, null, Set.of(admin), LocalDate.now().minusYears(5),true, LocalDate.now().minusYears(5), LocalDate.now());
        cochava = new Employee(1,1,"Cochava", "Shavit", 10000, null, Set.of(cashier), LocalDate.now().minusYears(5),true, LocalDate.now().minusYears(5), LocalDate.now());

        authorisationController = new AuthorisationController(roles, permissions);
        Set<Employee> employees = new HashSet<>();
        employees.add(shira);
        employees.add(cochava);
        employeeController = new EmployeeController(employees, authorisationController);
    }

    @Test
    void getEmployeeByIsraeliId() {
        // Test for existing employee
        Employee employee = employeeController.getEmployeeByIsraeliId(shira.getIsraeliId());
        assertNotNull(employee);
        assertEquals(shira, employee);

        // Test for non-existing employee
        employee = employeeController.getEmployeeByIsraeliId(99999);
        assertNull(employee);
    }

    @Test
    void createEmployee() {
        // Test for creating a new employee
        Set<Role> roles = Set.of(cashier);
        LocalDate startOfEmployment = LocalDate.now();
        Map<String, Object> terms = Map.of("Days Off", 5000, "Contract Type", "Full Time");

        boolean result = employeeController.createEmployee(shira.getIsraeliId(), 12345, "Ramzi", "Abed-Ramzi", 5000, terms, roles, startOfEmployment);
        assertTrue(result);

        // Test for creating an existing employee
        assertThrows(RuntimeException.class, () -> {
            employeeController.createEmployee(shira.getIsraeliId(), shira.getIsraeliId(), "Shira", "Shtinboch", 10000, terms, roles, startOfEmployment);
        });

        // Test for invalid input
        assertThrows(RuntimeException.class, () -> {
            employeeController.createEmployee(shira.getIsraeliId(), 12345, "", "Employee", 5000, terms, roles, startOfEmployment);
        });
    }

    @Test
    void updateEmployee() {
        // Test for updating an existing employee
        Set<Role> roles = Set.of(cashier);
        Map<String, Object> terms = Map.of("Days Off", 5000, "Contract Type", "Full Time");
        boolean result = employeeController.updateEmployee(shira.getIsraeliId(), shira.getIsraeliId(), "Shira", "Shtinboch", 12000, terms, true);
        assertTrue(result);

        // Test for updating a non-existing employee
        assertThrows(RuntimeException.class, () -> {
            employeeController.updateEmployee(shira.getIsraeliId(), 99999, "Non-Existing", "Employee", 5000, terms, true);
        });

        // Test for invalid input
        assertThrows(RuntimeException.class, () -> {
            employeeController.updateEmployee(shira.getIsraeliId(), shira.getIsraeliId(), "", "Employee", 5000, terms, true);
        });
    }

    @Test
    void deleteEmployee() {
        // Test for deleting an existing employee
        boolean result = employeeController.deleteEmployee(shira.getIsraeliId(), shira.getIsraeliId());
        assertTrue(result);

        // Test for deleting a non-existing employee
        assertThrows(RuntimeException.class, () -> {
            employeeController.deleteEmployee(shira.getIsraeliId(), 99999);
        });

        // Test for invalid input
        assertThrows(RuntimeException.class, () -> {
            employeeController.deleteEmployee(shira.getIsraeliId(), shira.getIsraeliId());
        });
    }

    @Test
    void isEmployeeAuthorised() {
        // Test for authorisation
        boolean result = employeeController.isEmployeeAuthorised(shira.getIsraeliId(), CREATE_EMPLOYEE);
        assertTrue(result);

        // Test for non-authorisation
        result = employeeController.isEmployeeAuthorised(cochava.getIsraeliId(), CREATE_EMPLOYEE);
        assertFalse(result);

        // Test for invalid input
        assertThrows(RuntimeException.class, () -> {
            employeeController.isEmployeeAuthorised(99999, CREATE_EMPLOYEE);
        });
    }

    @Test
    void addRoleToEmployee() {
        // Test for adding a new role
        assertThrows(RuntimeException.class, () -> {
            employeeController.addRoleToEmployee(shira.getIsraeliId(), cochava.getIsraeliId(), "CASHIER");
        });

        // Test for adding an existing role
        assertThrows(RuntimeException.class, () -> {
            employeeController.addRoleToEmployee(shira.getIsraeliId(),cochava.getIsraeliId() ,"CASHIER");
        });

        // Test for invalid input
        assertThrows(RuntimeException.class, () -> {
            employeeController.addRoleToEmployee(shira.getEmployeeId(),cochava.getIsraeliId(), "cashier");
        });
    }

    @Test
    void removeRoleFromEmployee() {
        // Test for removing an existing role
        assertThrows(RuntimeException.class, () -> {
            employeeController.removeRoleFromEmployee(shira.getIsraeliId(), cochava.getIsraeliId(), "CASHIER");
        });

        // Test for removing a non-existing role
        assertThrows(RuntimeException.class, () -> {
            employeeController.removeRoleFromEmployee(shira.getIsraeliId(), cochava.getIsraeliId(), "NON_EXISTING_ROLE");
        });

        // Test for invalid input
        assertThrows(RuntimeException.class, () -> {
            employeeController.removeRoleFromEmployee(shira.getIsraeliId(), cochava.getIsraeliId(), "cashier");
        });
    }
}