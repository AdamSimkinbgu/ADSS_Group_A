package DomainLayer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
class AuthorisationControllerTest {

    AuthorisationController authorisationController;

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
    }

    @Test
    void hasPermission() {
        assertTrue(authorisationController.HasPermission(shira, CREATE_EMPLOYEE)); // Shira has the ADMIN role which has the CREATE_EMPLOYEE permission
        assertFalse(authorisationController.HasPermission(shira, CASHIER)); // Shira does not have the CASHIER role
        assertFalse(authorisationController.HasPermission(cochava, CREATE_EMPLOYEE)); // Cochava has the CASHIER role which does not have the CREATE_EMPLOYEE permission

    }

    @Test
    void hasRole() {
        assertTrue(authorisationController.HasRole(shira, admin)); // Shira has the ADMIN role
        assertFalse(authorisationController.HasRole(cochava, admin)); // Cochava does not have the ADMIN role
        assertTrue(authorisationController.HasRole(cochava, cashier)); // Cochava has the CASHIER role
    }

    @Test
    void addRole() {
        assertTrue(authorisationController.AddRole(cochava, admin)); // Cochava does not have the ADMIN role
        assertFalse(authorisationController.AddRole(cochava, cashier)); // Cochava already has the CASHIER role
        assertTrue(authorisationController.AddRole(shira, cashier)); // Shira does not have the CASHIER role
    }

    @Test
    void removeRole() {
        assertTrue(authorisationController.removeRole(shira, admin)); // Shira has the ADMIN role
        assertFalse(authorisationController.removeRole(cochava, admin)); // Cochava does not have the ADMIN role
        assertTrue(authorisationController.removeRole(cochava, cashier)); // Cochava has the CASHIER role
    }

    @Test
    void addPermissionToRole() {
        assertTrue(authorisationController.addPermissionToRole(admin, CASHIER)); // ADMIN role does not have the CASHIER permission
        assertFalse(authorisationController.addPermissionToRole(admin, CREATE_EMPLOYEE)); // ADMIN role already has the CREATE_EMPLOYEE permission
    }

    @Test
    void addPermissionToRoleByName() {
        assertTrue(authorisationController.addPermissionToRoleByName("ADMIN", CASHIER)); // ADMIN role does not have the CASHIER permission
        assertFalse(authorisationController.addPermissionToRoleByName("ADMIN", CREATE_EMPLOYEE)); // ADMIN role already has the CREATE_EMPLOYEE permission
    }

    @Test
    void removePermissionFromRole() {
        assertFalse(authorisationController.removePermissionFromRole("ADMIN", CASHIER)); // ADMIN role doesn't have the CASHIER permission
        assertTrue(authorisationController.removePermissionFromRole("ADMIN", CREATE_EMPLOYEE)); // ADMIN role have the CREATE_EMPLOYEE permission
    }

    @Test
    void createRole() {
        assertTrue(authorisationController.createRole(999,"MANAGER", Set.of(CREATE_EMPLOYEE, ADD_EMPLOYEE))); // MANAGER role does not exist
        assertFalse(authorisationController.createRole(999,"ADMIN", Set.of(CREATE_EMPLOYEE, ADD_EMPLOYEE))); // ADMIN role already exists
    }

    @Test
    void deleteRole() {
        assertFalse(authorisationController.deleteRole("MANAGER")); // MANAGER role does not exist - cannot delete - False
        assertTrue(authorisationController.deleteRole("ADMIN")); // ADMIN role already exists
    }

    @Test
    void createPermission() {
        assertTrue(authorisationController.CreatePermission("MANAGER")); // MANAGER permission does not exist
        assertFalse(authorisationController.CreatePermission(CREATE_EMPLOYEE)); // CREATE_EMPLOYEE permission already exists
    }

    @Test
    void deletePermission() {
        assertFalse(authorisationController.DeletePermission("MANAGER")); // MANAGER permission does not exist - cannot delete - False
        assertTrue(authorisationController.DeletePermission(CREATE_EMPLOYEE)); // CREATE_EMPLOYEE permission already exists
    }

    @Test
    void getRoleByName() {
        assertEquals(admin, authorisationController.getRoleByName("ADMIN")); // ADMIN role exists
        assertNull(authorisationController.getRoleByName("MANAGER")); // MANAGER role does not exist
    }
}