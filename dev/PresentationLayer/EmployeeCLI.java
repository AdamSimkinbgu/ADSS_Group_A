package PresentationLayer;

import DomainLayer.Employee;
import ServiceLayer.EmployeeService;
import ServiceLayer.exception.AuthorizationException;
import ServiceLayer.response.Response;
import ServiceLayer.util.JsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.stream.Collectors;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public class EmployeeCLI {

    // Properties
    private final DateTimeFormatter dateFormatter = CliUtil.dateFormatter;
    private final EmployeeService employeeService;
    private final Scanner scanner;
    private final long doneBy;


    /**
     * Constructor for the Employee Command Line Interface
     *
     * @param employeeService The service layer for employee operations
     * @param doneBy The ID of the employee using the CLI
     */
    public EmployeeCLI(EmployeeService employeeService, long doneBy) {
        this.employeeService = employeeService;
        this.scanner = new Scanner(System.in);
        this.doneBy = doneBy;
    }

    /**
     * Start the CLI application
     */
    public void start() {
        printWelcomeBanner();
        boolean running = true;

        while (running) {
            int max = displayMenu();
            String choice = scanner.nextLine();
            running = processMenuChoice(choice, max);
        }
    }

    //==========================================================================================
    // MENU SYSTEM METHODS
    //==========================================================================================

    /**
     * Displays the main menu with options based on user permissions
     * @return The number of menu options
     */
    private int displayMenu() {
        CliUtil.printEmptyLine();
        printSectionHeader("Main Menu");

        // Display breadcrumb navigation
        CliUtil.printBreadcrumb("Employee Management");

        List<String> menuOptions = new ArrayList<>();
        int optionNumber = 1;

        menuOptions.add(CliUtil.YELLOW + optionNumber++ + CliUtil.RESET + ". View All Employees");
        menuOptions.add(CliUtil.YELLOW + optionNumber++ + CliUtil.RESET + ". View Employee Details");

        if (hasPermission("CREATE_EMPLOYEE")) {
            menuOptions.add(CliUtil.YELLOW + optionNumber++ + CliUtil.RESET + ". Create Employee");
        }

        if (hasPermission("UPDATE_EMPLOYEE")) {
            menuOptions.add(CliUtil.YELLOW + optionNumber++ + CliUtil.RESET + ". Update Employee");
        }

        if (hasPermission("DEACTIVATE_EMPLOYEE")) {
            menuOptions.add(CliUtil.YELLOW + optionNumber++ + CliUtil.RESET + ". Deactivate Employee");
        }

        menuOptions.add(CliUtil.YELLOW + optionNumber++ + CliUtil.RESET + ". View All Roles");
        menuOptions.add(CliUtil.YELLOW + optionNumber++ + CliUtil.RESET + ". View Role Details");

        if (hasPermission("CREATE_ROLE")) {
            menuOptions.add(CliUtil.YELLOW + optionNumber++ + CliUtil.RESET + ". Create Role");
        }

        if (hasPermission("CREATE_ROLE")) {
            menuOptions.add(CliUtil.YELLOW + optionNumber++ + CliUtil.RESET + ". Clone Role");
        }

        if (hasPermission("ROLE_PERMISSION")) {
            menuOptions.add(CliUtil.YELLOW + optionNumber++ + CliUtil.RESET + ". Add Role to Employee");
            menuOptions.add(CliUtil.YELLOW + optionNumber++ + CliUtil.RESET + ". Remove Role from Employee");
        }

        menuOptions.add(CliUtil.YELLOW + optionNumber++ + CliUtil.RESET + ". View All Permissions");

        if (hasPermission("CREATE_PERMISSION")) {
            menuOptions.add(CliUtil.YELLOW + optionNumber++ + CliUtil.RESET + ". Create Permission");
        }

        if (hasPermission("ADD_PERMISSION_TO_ROLE")) {
            menuOptions.add(CliUtil.YELLOW + optionNumber++ + CliUtil.RESET + ". Add Permission to Role");
        }

        if (hasPermission("REMOVE_PERMISSION_FROM_ROLE")) {
            menuOptions.add(CliUtil.YELLOW + optionNumber++ + CliUtil.RESET + ". Remove Permission from Role");
        }

        menuOptions.add(CliUtil.YELLOW + optionNumber + CliUtil.RESET + ". Back To Main Menu");

        // Print the menu options
        for (String option : menuOptions) {
            CliUtil.print("  " + option);
        }

        CliUtil.printEmptyLine();
        CliUtil.printPrompt("Enter your choice: ");
        return menuOptions.size();
    }

    /**
     * Processes the user's menu choice
     *
     * @param choice The user's input choice
     * @param max The maximum valid choice number
     * @return true to continue in the menu, false to return to main menu
     */
    private boolean processMenuChoice(String choice, int max) {
        CliUtil.printEmptyLine();

        try {
            int choiceNum = Integer.parseInt(choice);
            int currentOption = 1;

            if (choiceNum < 1 || choiceNum > max) {
                printError("Invalid choice. Please enter a number between 1 and " + max + ".");
                return true;
            }

            // Employee Management Section
            if (choiceNum == currentOption++) {
                printAllEmployees();
                return true;
            }

            if (choiceNum == currentOption++) {
                CliUtil.printBold("Enter Employee Israeli ID:");
                long israeliId = Long.parseLong(scanner.nextLine());
                printEmployeeDetails(israeliId);
                return true;
            }

            if (hasPermission("CREATE_EMPLOYEE")) {
                if (choiceNum == currentOption++) {
                    createEmployee();
                    return true;
                }
            }

            if (hasPermission("UPDATE_EMPLOYEE")) {
                if (choiceNum == currentOption++) {
                    updateEmployee();
                    return true;
                }
            }

            if (hasPermission("DEACTIVATE_EMPLOYEE")) {
                if (choiceNum == currentOption++) {
                    deactivateEmployee();
                    return true;
                }
            }

            // Role Management Section
            if (choiceNum == currentOption++) {
                printAllRoles();
                return true;
            }

            if (choiceNum == currentOption++) {
                CliUtil.printBold("Enter Role Name:");
                String roleName = scanner.nextLine();
                printRoleDetails(roleName);
                return true;
            }

            if (hasPermission("CREATE_ROLE")) {
                if (choiceNum == currentOption++) {
                    createRole();
                    return true;
                }
            }

            if (hasPermission("CREATE_ROLE")) {
                if (choiceNum == currentOption++) {
                    cloneRole();
                    return true;
                }
            }

            if (hasPermission("ROLE_PERMISSION")) {
                if (choiceNum == currentOption++) {
                    addRoleToEmployee();
                    return true;
                }

                if (choiceNum == currentOption++) {
                    removeRoleFromEmployee();
                    return true;
                }
            }

            if (choiceNum == currentOption++) {
                printAllPermissions();
                return true;
            }

            if (hasPermission("CREATE_PERMISSION")) {
                if (choiceNum == currentOption++) {
                    createPermission();
                    return true;
                }
            }

            if (hasPermission("ADD_PERMISSION_TO_ROLE")) {
                if (choiceNum == currentOption++) {
                    addPermissionToRole();
                    return true;
                }
            }

            if (hasPermission("REMOVE_PERMISSION_FROM_ROLE")) {
                if (choiceNum == currentOption++) {
                    removePermissionFromRole();
                    return true;
                }
            }

            if (choiceNum == currentOption++) {
                CliUtil.printReturnMessage("main menu");
                return false;
            }

            printError("Invalid choice. Please try again.");
            return true;
        } catch (NumberFormatException e) {
            printError("Please enter a valid input.");
            return true;
        }
    }

    //==========================================================================================
    // UI UTILITY METHODS
    //==========================================================================================

    /**
     * Prints an error message with formatting
     *
     * @param message The error message to display
     */
    private void printError(String message) {
        CliUtil.printError(message);
    }

    /**
     * Prints a success message with formatting
     *
     * @param message The success message to display
     */
    private void printSuccess(String message) {
        CliUtil.printSuccess(message);
    }

    /**
     * Waits for the user to press Enter to continue
     */
    private void waitForEnter() {
        CliUtil.waitForEnter(scanner);
    }

    /**
     * Gets user confirmation for an action
     *
     * @param message The confirmation message to display
     * @return true if confirmed, false otherwise
     */
    private boolean confirm(String message) {
        return CliUtil.confirm(message, scanner);
    }

    /**
     * Gets a long value from user input
     *
     * @param prompt The prompt to display
     * @return The long value entered by the user
     */
    private long getLongInput(String prompt) {
        return CliUtil.getLongInput(prompt, scanner);
    }

    /**
     * Checks if the current user has a specific permission
     *
     * @param permission The permission to check
     * @return true if the user has the permission, false otherwise
     */
    private boolean hasPermission(String permission) {
        try {
            String responseJson = employeeService.isEmployeeAuthorised(doneBy, permission);
            Response<Boolean> response = JsonUtil.fromJson(responseJson, new TypeReference<Response<Boolean>>() {});

            if (response.isError()) {
                //printError("Error checking permissions: " + response.getErrorMessage());
                return false;
            }

            return response.getValue();
        } catch (Exception e) {
            //printError("Error checking permissions: " + e.getMessage());
            return false;
        }
    }


    //==========================================================================================
    // EMPLOYEE MANAGEMENT METHODS
    //==========================================================================================

    /**
     * Deactivates an employee
     */
    private void deactivateEmployee() {
        printSectionHeader("Deactivate Employee");

        // Show all employees
        CliUtil.printInfo("Current employees:");
        try {
            // Get employees as JSON and parse
            String responseJson = employeeService.getAllEmployees();
            Response<List<Employee>> response = JsonUtil.fromJson(responseJson, 
                new TypeReference<Response<List<Employee>>>() {});

            if (response.isError()) {
                printError("Error retrieving employees: " + response.getErrorMessage());
                return;
            }

            List<Employee> employees = response.getValue();
            for (Employee employee : employees) {
                String status = employee.isActive() ? CliUtil.greenString("Active") : CliUtil.redString("Inactive");
                System.out.printf("  • ID: %-9s | Name: %-20s | Status: %s%n",
                    employee.getIsraeliId(),
                    employee.getFirstName() + " " + employee.getLastName(),
                    status);
            }
        } catch (Exception e) {
            printError("Error retrieving employees: " + e.getMessage());
            CliUtil.waitForEnter(scanner);
            return;
        }

        CliUtil.printEmptyLine();
        long israeliId = getLongInput("Employee Israeli ID: ");

        try {
            // Get employee by ID
            String employeeResponseJson = employeeService.getEmployeeById(israeliId);
            Response<Employee> employeeResponse = JsonUtil.fromJson(employeeResponseJson, 
                new TypeReference<Response<Employee>>() {});

            if (employeeResponse.isError()) {
                printError("Error retrieving employee: " + employeeResponse.getErrorMessage());
                CliUtil.waitForEnter(scanner);
                return;
            }

            Employee employee = employeeResponse.getValue();

            if (!employee.isActive()) {
                printError("Employee is already inactive.");
                CliUtil.waitForEnter(scanner);
                return;
            }

            // Confirm
            if (confirm("Confirm deactivating employee '" + employee.getFirstName() + " " + employee.getLastName() + "' (ID: " + israeliId + ")?")) {
                String resultJson = employeeService.deactivateEmployee(doneBy, israeliId);
                Response<String> resultResponse = JsonUtil.fromJson(resultJson, new TypeReference<Response<String>>() {});

                if (resultResponse.isError()) {
                    printError(resultResponse.getErrorMessage());
                } else {
                    printSuccess(resultResponse.getValue());
                }
            } else {
                CliUtil.printOperationCancelled();
            }
        } catch (Exception e) {
            printError("Error retrieving employee details: " + e.getMessage());
        }

        CliUtil.waitForEnter(scanner);
    }

    private void printWelcomeBanner() {
        CliUtil.printWelcomeBanner("EMPLOYEE MANAGEMENT SYSTEM", LocalDate.now().toString(), "Employee #" + doneBy);
    }

    /**
     * Prints a section header with a border.
     *
     * @param title - The title of the section to be printed.
     */
    private void  printSectionHeader(String title) {
        boolean isMainMenu = title.equalsIgnoreCase("Main Menu");
        CliUtil.printSectionHeader(title, isMainMenu, "SYSTEM");
    }

    private void createEmployee() {
        printSectionHeader("Create Employee");

        try {
            CliUtil.printBold("Israeli ID: ");
            long israeliId = Long.parseLong(scanner.nextLine());

            CliUtil.printBold("First Name: ");
            String firstName = scanner.nextLine();

            CliUtil.printBold("Last Name: ");
            String lastName = scanner.nextLine();

            CliUtil.printBold("Salary: ");
            long salary = Long.parseLong(scanner.nextLine());

            CliUtil.printBold("Start Date (dd-MM-yyyy): ");
            LocalDate startDate = LocalDate.parse(scanner.nextLine(), dateFormatter);

            Map<String, Object> termsOfEmployment = new HashMap<>();
            CliUtil.printEmptyLine();
            CliUtil.printSectionWithIcon("TERMS OF EMPLOYMENT", "📋");
            CliUtil.printInfo("Enter key-value pairs. Type 'done' when finished.");
            CliUtil.printEmptyLine();

            while (true) {
                CliUtil.printBold("Key (or 'done'): ");
                String key = scanner.nextLine();
                if (key.equalsIgnoreCase("done")) break;

                CliUtil.printBold("Value: ");
                String value = scanner.nextLine();
                termsOfEmployment.put(key, value);
                CliUtil.printSuccessWithCheckmark("Added: " + key + " = " + value);
            }
            String resultJson = employeeService.createEmployee(doneBy, israeliId, firstName, lastName, salary, termsOfEmployment, startDate);
            Response<String> resultResponse = JsonUtil.fromJson(resultJson, new TypeReference<Response<String>>() {});

            if (resultResponse.isError()) {
                printError(resultResponse.getErrorMessage());
            } else {
                printSuccess(resultResponse.getValue());
            }
        } catch (NumberFormatException e) {
            printError("Please enter valid input.");
        } catch (DateTimeParseException e) {
            printError("Please enter date in format dd-MM-yyyy.");
        } catch (Exception e) {
            printError("An error occurred: " + e.getMessage());
        }

        CliUtil.waitForEnter(scanner);
    }
    private void updateEmployee() {
        printSectionHeader("Update Employee");
        CliUtil.printBold("Israeli ID: ");
        long israeliId = Long.parseLong(scanner.nextLine());

        // Get employee by ID
        String employeeResponseJson = employeeService.getEmployeeById(israeliId);
        Response<Employee> employeeResponse = JsonUtil.fromJson(employeeResponseJson, 
            new TypeReference<Response<Employee>>() {});

        if (employeeResponse.isError()) {
            printError("Error retrieving employee: " + employeeResponse.getErrorMessage());
            return;
        }

        Employee existing = employeeResponse.getValue();

        printSectionHeader("Edit Employee: " + CliUtil.YELLOW + existing.getFirstName() + " " + existing.getLastName() + CliUtil.RESET);

        CliUtil.printInfo("Leave field empty to keep current value");
        CliUtil.printEmptyLine();

        CliUtil.printBold("Current First Name: " + existing.getFirstName());
        CliUtil.printBold("New First Name: ");
        String firstName = scanner.nextLine();
        if (firstName.isBlank()) firstName = existing.getFirstName();

        CliUtil.printBold("Current Last Name: " + existing.getLastName());
        CliUtil.printBold("New Last Name: ");
        String lastName = scanner.nextLine();
        if (lastName.isBlank()) lastName = existing.getLastName();

        CliUtil.printBold("Current Salary: " + existing.getSalary());
        CliUtil.printBold("New Salary: ");
        String salaryInput = scanner.nextLine();
        long salary = salaryInput.isBlank() ? existing.getSalary() : Long.parseLong(salaryInput);

        CliUtil.printBold("Current Active Status: " + existing.isActive());
        CliUtil.printBold("Is Active? (true/false): ");
        String activeInput = scanner.nextLine();
        boolean active = activeInput.isBlank() ? existing.isActive() : Boolean.parseBoolean(activeInput);

        CliUtil.printEmptyLine();
        CliUtil.printSectionWithIcon("CURRENT TERMS OF EMPLOYMENT", "📋");
        if (existing.getTermsOfEmployment().isEmpty()) {
            CliUtil.printInfo("  No terms defined");
        } else {
            existing.getTermsOfEmployment().forEach((k, v) -> 
                CliUtil.print("  " + CliUtil.BOLD + k + CliUtil.RESET + ": " + v));
        }

        CliUtil.printEmptyLine();
        CliUtil.printSectionWithIcon("UPDATE TERMS OF EMPLOYMENT", "📝");
        CliUtil.printInfo("Enter key-value pairs. Type 'done' when finished.");
        CliUtil.printInfo("To remove a term, enter its key with an empty value.");
        CliUtil.printEmptyLine();

        Map<String, Object> termsOfEmployment = new HashMap<>(existing.getTermsOfEmployment());
        while (true) {
            CliUtil.printBold("Key (or 'done'): ");
            String key = scanner.nextLine();
            if (key.equalsIgnoreCase("done")) break;

            CliUtil.printBold("Value: ");
            String value = scanner.nextLine();

            if (value.isEmpty()) {
                termsOfEmployment.remove(key);
                CliUtil.printSuccessWithCheckmark("Removed: " + key);
            } else {
                termsOfEmployment.put(key, value);
                CliUtil.printSuccessWithCheckmark("Updated: " + key + " = " + value);
            }
        }

        String resultJson = employeeService.updateEmployee(doneBy, israeliId, firstName, lastName, salary, termsOfEmployment, active);
        Response<String> resultResponse = JsonUtil.fromJson(resultJson, new TypeReference<Response<String>>() {});

        if (resultResponse.isError()) {
            printError(resultResponse.getErrorMessage());
        } else {
            printSuccess(resultResponse.getValue());
        }

        CliUtil.waitForEnter(scanner);
    }
    private void addRoleToEmployee() {
        printSectionHeader("Add Role to Employee");

        // Get all roles from service
        String responseJson = employeeService.getAllRoles();
        Response<List<String>> rolesResponse = JsonUtil.fromJson(responseJson, new TypeReference<Response<List<String>>>() {});

        if (rolesResponse.isError()) {
            printError("Error retrieving roles: " + rolesResponse.getErrorMessage());
            return;
        }

        List<String> allRolesList = rolesResponse.getValue();
        String[] allRoles = allRolesList.toArray(new String[0]);

        CliUtil.printBold("Employee Israeli ID: ");
        long israeliId = Long.parseLong(scanner.nextLine());

        try {
            // Get employee by ID
            String employeeResponseJson = employeeService.getEmployeeById(israeliId);
            Response<Employee> employeeResponse = JsonUtil.fromJson(employeeResponseJson, 
                new TypeReference<Response<Employee>>() {});

            if (employeeResponse.isError()) {
                printError("Error retrieving employee: " + employeeResponse.getErrorMessage());
                return;
            }

            Employee employee = employeeResponse.getValue();
            CliUtil.printSuccess("Employee: " + employee.getFirstName() + " " + employee.getLastName());

            CliUtil.printEmptyLine();
            CliUtil.printSectionWithIcon("CURRENT ROLES", "👤");
            if (employee.getRoles().isEmpty()) {
                CliUtil.printInfo("  No roles assigned");
            } else {
                List<String> roles = new ArrayList<>(employee.getRoles());
                CliUtil.printHierarchicalList(roles, "•", 2);
            }
        } catch (Exception e) {
            printError("Could not retrieve employee details: " + e.getMessage());
        }

        CliUtil.printEmptyLine();
        CliUtil.printSectionWithIcon("AVAILABLE ROLES", "🔑");
        List<String> rolesList = Arrays.asList(allRoles);
        CliUtil.printHierarchicalList(rolesList, "•", 2);

        CliUtil.printEmptyLine();
        CliUtil.printBold("Role Name to Add: ");
        String roleName = scanner.nextLine();

        // Confirm
        if (confirm("Confirm adding role '" + roleName + "' to employee #" + israeliId + "?")) {
            String resultJson = employeeService.addRoleToEmployee(doneBy, israeliId, roleName);
            Response<String> resultResponse = JsonUtil.fromJson(resultJson, new TypeReference<Response<String>>() {});

            if (resultResponse.isError()) {
                printError(resultResponse.getErrorMessage());
            } else {
                printSuccess(resultResponse.getValue());
            }
        } else {
            CliUtil.printOperationCancelled();
        }

        CliUtil.waitForEnter(scanner);
    }

    private void removeRoleFromEmployee() {
        printSectionHeader("Remove Role from Employee");

        CliUtil.printBold("Employee Israeli ID: ");
        long israeliId = Long.parseLong(scanner.nextLine());

        try {
            // Get employee by ID
            String employeeResponseJson = employeeService.getEmployeeById(israeliId);
            Response<Employee> employeeResponse = JsonUtil.fromJson(employeeResponseJson, 
                new TypeReference<Response<Employee>>() {});

            if (employeeResponse.isError()) {
                printError("Error retrieving employee: " + employeeResponse.getErrorMessage());
                return;
            }

            Employee employee = employeeResponse.getValue();
            CliUtil.printSuccess("Employee: " + employee.getFirstName() + " " + employee.getLastName());

            CliUtil.printEmptyLine();
            CliUtil.printSectionWithIcon("CURRENT ROLES", "👤");
            if (employee.getRoles().isEmpty()) {
                CliUtil.printInfo("  No roles assigned");
                CliUtil.waitForEnter(scanner);
                return;
            } else {
                List<String> roles = new ArrayList<>(employee.getRoles());
                CliUtil.printHierarchicalList(roles, "•", 2);
            }

            CliUtil.printEmptyLine();
            CliUtil.printBold("Role Name to Remove: ");
            String roleName = scanner.nextLine();

            // Confirm
            if (confirm("Confirm removing role '" + roleName + "' from employee #" + israeliId + "?")) {
                String resultJson = employeeService.removeRoleFromEmployee(doneBy, israeliId, roleName);
                Response<String> resultResponse = JsonUtil.fromJson(resultJson, new TypeReference<Response<String>>() {});

                if (resultResponse.isError()) {
                    printError(resultResponse.getErrorMessage());
                } else {
                    printSuccess(resultResponse.getValue());
                }
            } else {
                CliUtil.printOperationCancelled();
            }
        } catch (Exception e) {
            printError("Could not retrieve employee details: " + e.getMessage());
        }

        CliUtil.waitForEnter(scanner);
    }

    private void createRole() {
        printSectionHeader("Create Role");

        CliUtil.printBold("Role Name: ");
        String roleName = scanner.nextLine();

        CliUtil.printEmptyLine();
        CliUtil.printBold("Do you want to add permissions now? (yes/no): ");
        String addPermissions = scanner.nextLine();

        if (addPermissions.equalsIgnoreCase("yes")) {
            CliUtil.printEmptyLine();
            CliUtil.printSectionWithIcon("AVAILABLE PERMISSIONS", "🔑");

            // Get all permissions from service
            String permissionsResponseJson = employeeService.getAllPermissions();
            Response<List<String>> permissionsResponse = JsonUtil.fromJson(permissionsResponseJson, 
                new TypeReference<Response<List<String>>>() {});

            if (permissionsResponse.isError()) {
                printError("Error retrieving permissions: " + permissionsResponse.getErrorMessage());
                return;
            }

            List<String> permissionsList = permissionsResponse.getValue();

            if (permissionsList.isEmpty()) {
                CliUtil.printInfo("  No permissions defined in the system");
            } else {
                CliUtil.printHierarchicalList(permissionsList, "•", 2);
            }

            CliUtil.printEmptyLine();
            CliUtil.printSectionWithIcon("ADD PERMISSIONS TO ROLE", "📝");
            CliUtil.printInfo("Enter permissions one by one. Type 'done' when finished.");
            CliUtil.printEmptyLine();

            HashSet<String> permissions = new HashSet<>();
            while (true) {
                CliUtil.printBold("Permission (or 'done'): ");
                String permission = scanner.nextLine();
                if (permission.equalsIgnoreCase("done")) break;
                permissions.add(permission);
                CliUtil.printSuccessWithCheckmark("Added: " + permission);
            }

            // Confirm
            if (confirm("Confirm creating role '" + roleName + "' with " + permissions.size() + " permissions?")) {
                String resultJson = employeeService.createRoleWithPermissions(doneBy, roleName, permissions);
                Response<String> resultResponse = JsonUtil.fromJson(resultJson, new TypeReference<Response<String>>() {});

                if (resultResponse.isError()) {
                    printError(resultResponse.getErrorMessage());
                } else {
                    printSuccess(resultResponse.getValue());
                }
            } else {
                CliUtil.printOperationCancelled();
            }
        } else {
            // Confirm
            if (confirm("Confirm creating role '" + roleName + "' with no permissions?")) {
                // Create role without permissions
                String resultJson = employeeService.createRole(doneBy, roleName);
                Response<String> resultResponse = JsonUtil.fromJson(resultJson, new TypeReference<Response<String>>() {});

                if (resultResponse.isError()) {
                    printError(resultResponse.getErrorMessage());
                } else {
                    printSuccess(resultResponse.getValue());
                }
            } else {
                CliUtil.printOperationCancelled();
            }
        }

        CliUtil.waitForEnter(scanner);
    }

    private void addPermissionToRole() {
        printSectionHeader("Add Permission to Role");

        // Show roles
        CliUtil.printInfo("Available roles:");

        // Get all roles from service
        String rolesResponseJson = employeeService.getAllRoles();
        Response<List<String>> rolesResponse = JsonUtil.fromJson(rolesResponseJson, new TypeReference<Response<List<String>>>() {});

        if (rolesResponse.isError()) {
            printError("Error retrieving roles: " + rolesResponse.getErrorMessage());
            return;
        }

        List<String> rolesList = rolesResponse.getValue();
        CliUtil.printHierarchicalList(rolesList, "•", 2);

        CliUtil.printEmptyLine();
        CliUtil.printBold("Role Name: ");
        String roleName = scanner.nextLine();

        // Show permissions
        CliUtil.printEmptyLine();
        CliUtil.printInfo("Available permissions:");

        // Get all permissions from service
        String permissionsResponseJson = employeeService.getAllPermissions();
        Response<List<String>> permissionsResponse = JsonUtil.fromJson(permissionsResponseJson, new TypeReference<Response<List<String>>>() {});

        if (permissionsResponse.isError()) {
            printError("Error retrieving permissions: " + permissionsResponse.getErrorMessage());
            return;
        }

        List<String> permissionsList = permissionsResponse.getValue();
        CliUtil.printHierarchicalList(permissionsList, "•", 2);

        CliUtil.printEmptyLine();
        CliUtil.printBold("Permission Name: ");
        String permissionName = scanner.nextLine();

        // Confirm
        if (confirm("Confirm adding permission '" + permissionName + "' to role '" + roleName + "'?")) {
            String resultJson = employeeService.addPermissionToRole(doneBy, roleName, permissionName);
            Response<String> resultResponse = JsonUtil.fromJson(resultJson, new TypeReference<Response<String>>() {});

            if (resultResponse.isError()) {
                printError(resultResponse.getErrorMessage());
            } else {
                printSuccess(resultResponse.getValue());
            }
        } else {
            CliUtil.printOperationCancelled();
        }

        CliUtil.waitForEnter(scanner);
    }

    private void removePermissionFromRole() {
        printSectionHeader("Remove Permission from Role");

        CliUtil.printInfo("Available roles:");

        // Get all roles from service
        String rolesResponseJson = employeeService.getAllRoles();
        Response<List<String>> rolesResponse = JsonUtil.fromJson(rolesResponseJson, new TypeReference<Response<List<String>>>() {});

        if (rolesResponse.isError()) {
            printError("Error retrieving roles: " + rolesResponse.getErrorMessage());
            return;
        }

        List<String> rolesList = rolesResponse.getValue();
        CliUtil.printHierarchicalList(rolesList, "•", 2);

        CliUtil.printEmptyLine();
        CliUtil.printBold("Role Name: ");
        String roleName = scanner.nextLine();

        try {
            // Get role details from service
            String roleDetailsResponseJson = employeeService.getRoleDetails(roleName);
            Response<Map<String, HashSet<String>>> roleDetailsResponse = JsonUtil.fromJson(
                roleDetailsResponseJson, 
                new TypeReference<Response<Map<String, HashSet<String>>>>() {}
            );

            if (roleDetailsResponse.isError()) {
                printError("Error retrieving role details: " + roleDetailsResponse.getErrorMessage());
                return;
            }

            Map<String, HashSet<String>> roleDetails = roleDetailsResponse.getValue();
            if (roleDetails != null && !roleDetails.isEmpty()) {
                HashSet<String> permissions = roleDetails.get(roleName);

                CliUtil.printEmptyLine();
                CliUtil.printInfo("Current permissions for role '" + roleName + "':");

                if (permissions == null || permissions.isEmpty()) {
                    CliUtil.printInfo("  No permissions assigned to this role");
                    CliUtil.waitForEnter(scanner);
                    return;
                } else {
                    List<String> permissionsList = new ArrayList<>(permissions);
                    CliUtil.printHierarchicalList(permissionsList, "•", 2);
                }

                CliUtil.printEmptyLine();
                CliUtil.printBold("Permission Name to Remove: ");
                String permissionName = scanner.nextLine();

                // Confirm
                if (confirm("Confirm removing permission '" + permissionName + "' from role '" + roleName + "'?")) {
                    String resultJson = employeeService.removePermissionFromRole(doneBy, roleName, permissionName);
                    Response<String> resultResponse = JsonUtil.fromJson(resultJson, new TypeReference<Response<String>>() {});

                    if (resultResponse.isError()) {
                        printError(resultResponse.getErrorMessage());
                    } else {
                        printSuccess(resultResponse.getValue());
                    }
                } else {
                    CliUtil.printOperationCancelled();
                }
            } else {
                printError("Role '" + roleName + "' not found.");
            }
        } catch (Exception e) {
            printError("Error retrieving role details: " + e.getMessage());
        }

        CliUtil.waitForEnter(scanner);
    }

    /**
     * Displays all employees in the system with pagination
     */
    private void printAllEmployees() {
        try {
            // Get employees as JSON and parse
            String responseJson = employeeService.getAllEmployees();
            Response<List<DomainLayer.Employee>> response = JsonUtil.fromJson(responseJson, 
                new TypeReference<Response<List<DomainLayer.Employee>>>() {});

            if (response.isError()) {
                printError("Error retrieving employees: " + response.getErrorMessage());
                return;
            }

            List<DomainLayer.Employee> employeeList = response.getValue();

            // Define how many employees to show per page
            final int ITEMS_PER_PAGE = 5;

            // Use the pagination utility to display employees
            CliUtil.displayPaginatedList(
                "All Employees",
                employeeList,
                ITEMS_PER_PAGE,
                employee -> {
                    // Format each employee for display
                    String status = employee.isActive() ? CliUtil.greenString("Active") : CliUtil.redString("Inactive");
                    return String.format("ID: %-11s | Name: %-28s | Status: %s",
                        employee.getIsraeliId(),
                        employee.getFirstName() + " " + employee.getLastName(),
                        status);
                },
                scanner
            );
        } catch (Exception e) {
            printError("Error retrieving employees: " + e.getMessage());
        }
    }

    /**
     * Displays detailed information about a specific employee
     * 
     * @param israeliId The ID of the employee to display
     */
    private void printEmployeeDetails(long israeliId) {
        printSectionHeader("Employee Details");

        try {
            // Get employee by ID
            String employeeResponseJson = employeeService.getEmployeeById(israeliId);
            Response<Employee> employeeResponse = JsonUtil.fromJson(employeeResponseJson, 
                new TypeReference<Response<Employee>>() {});

            if (employeeResponse.isError()) {
                printError("Error retrieving employee: " + employeeResponse.getErrorMessage());
                CliUtil.waitForEnter(scanner);
                return;
            }

            Employee employee = employeeResponse.getValue();

            // Create headers for the table sections
            List<String> headers = Arrays.asList(
                "EMPLOYEE INFORMATION",
                "ROLES & PERMISSIONS",
                "TERMS OF EMPLOYMENT"
            );

            // Create content for each section
            Map<String, List<String[]>> content = new HashMap<>();

            // Employee Information section
            List<String[]> employeeInfo = new ArrayList<>();
            employeeInfo.add(new String[]{"ID:", String.valueOf(employee.getIsraeliId())});
            employeeInfo.add(new String[]{"Name:", employee.getFirstName() + " " + employee.getLastName()});
            employeeInfo.add(new String[]{"Salary:", String.valueOf(employee.getSalary())});

            String status = employee.isActive()
                ? CliUtil.greenString("Active") 
                : CliUtil.redString("Inactive");
            employeeInfo.add(new String[]{"Status:", status});
            employeeInfo.add(new String[]{"Start Date:", employee.getStartOfEmployment().toString()});
            content.put("EMPLOYEE INFORMATION", employeeInfo);

            // Roles & Permissions section
            List<String[]> rolesInfo = new ArrayList<>();
            rolesInfo.add(new String[]{"Roles:", ""});

            if (!employee.getRoles().isEmpty()) {
                for (String role : employee.getRoles()) {
                    rolesInfo.add(new String[]{"", "• " + role});
                }
            }
            content.put("ROLES & PERMISSIONS", rolesInfo);

            // Terms of Employment section
            List<String[]> termsInfo = new ArrayList<>();

            if (!employee.getTermsOfEmployment().isEmpty()) {
                for (Map.Entry<String, Object> entry : employee.getTermsOfEmployment().entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue().toString();

                    // Handle long values by splitting them
                    if (key.length() + value.length() > 45) {
                        termsInfo.add(new String[]{key + ":", value.substring(0, Math.min(value.length(), 34))});

                        // Add remaining value on next lines if needed
                        String remaining = value.substring(Math.min(value.length(), 34));
                        while (remaining.length() > 0) {
                            String chunk = remaining.substring(0, Math.min(remaining.length(), 49));
                            termsInfo.add(new String[]{chunk});
                            remaining = remaining.substring(Math.min(remaining.length(), 49));
                        }
                    } else {
                        termsInfo.add(new String[]{key + ":", value});
                    }
                }
            }
            content.put("TERMS OF EMPLOYMENT", termsInfo);

            // Create empty messages for each section
            Map<String, String> emptyMessages = new HashMap<>();
            emptyMessages.put("ROLES & PERMISSIONS", "No roles assigned");
            emptyMessages.put("TERMS OF EMPLOYMENT", "No terms of employment defined");

            // Print the formatted table
            CliUtil.printFormattedTable("Employee Details", headers, content, emptyMessages);

        } catch (Exception e) {
            printError("Employee not found or error retrieving details: " + e.getMessage());
        }

        CliUtil.waitForEnter(scanner);
    }
    //==========================================================================================
    // ROLE MANAGEMENT METHODS
    //==========================================================================================

    /**
     * Displays all roles in the system with pagination
     */
    private void printAllRoles() {
        try {
            // Get all roles from service
            String rolesResponseJson = employeeService.getAllRoles();
            Response<List<String>> rolesResponse = JsonUtil.fromJson(rolesResponseJson, 
                new TypeReference<Response<List<String>>>() {});

            if (rolesResponse.isError()) {
                printError("Error retrieving roles: " + rolesResponse.getErrorMessage());
                return;
            }

            List<String> rolesList = rolesResponse.getValue();

            // Define how many roles to show per page
            final int ITEMS_PER_PAGE = 5;

            // Use the pagination utility to display roles
            CliUtil.displayPaginatedList(
                "All Roles",
                rolesList,
                ITEMS_PER_PAGE,
                role -> role,  // Simple display function as roles are just strings
                scanner
            );

            // Display tip after exiting the paginated view
            CliUtil.printSectionWithIcon("Use 'View Role Details' to see permissions for each role","💡");
        } catch (Exception e) {
            printError("Error retrieving roles: " + e.getMessage());
        }
        CliUtil.waitForEnter(scanner);
    }

    /**
     * Displays detailed information about a specific role
     * 
     * @param roleName The name of the role to display
     */
    private void printRoleDetails(String roleName) {
        printSectionHeader("Role Details");

        try {
            // Get role details from service
            String roleDetailsResponseJson = employeeService.getRoleDetails(roleName);
            Response<Map<String, HashSet<String>>> roleDetailsResponse = JsonUtil.fromJson(
                roleDetailsResponseJson, 
                new TypeReference<Response<Map<String, HashSet<String>>>>() {}
            );

            if (roleDetailsResponse.isError()) {
                printError("Error retrieving role details: " + roleDetailsResponse.getErrorMessage());
                return;
            }

            Map<String, HashSet<String>> roleDetails = roleDetailsResponse.getValue();

            if (roleDetails != null && !roleDetails.isEmpty()) {
                // Create headers for the table sections
                List<String> headers = Arrays.asList(
                    "ROLE DETAILS",
                    "PERMISSIONS"
                );

                // Create content for each section
                Map<String, List<String[]>> content = new HashMap<>();

                // Role Details section
                List<String[]> roleInfo = new ArrayList<>();
                roleInfo.add(new String[]{"Role Name:", roleName});
                content.put("ROLE DETAILS", roleInfo);

                // Permissions section
                List<String[]> permissionsInfo = new ArrayList<>();
                HashSet<String> permissions = roleDetails.get(roleName);

                if (permissions != null && !permissions.isEmpty()) {
                    for (String permission : permissions) {
                        permissionsInfo.add(new String[]{"  • " + permission});
                    }
                }
                content.put("PERMISSIONS", permissionsInfo);

                // Create empty messages for each section
                Map<String, String> emptyMessages = new HashMap<>();
                emptyMessages.put("PERMISSIONS", "No permissions assigned to this role");

                // Print the formatted table
                CliUtil.printFormattedTable("Role Details", headers, content, emptyMessages);
            } else {
                printError("Role '" + roleName + "' not found.");
            }
        } catch (Exception e) {
            printError("Error retrieving role details: " + e.getMessage());
        }

        CliUtil.waitForEnter(scanner);
    }

    //==========================================================================================
    // PERMISSION MANAGEMENT METHODS
    //==========================================================================================

    /**
     * Displays all permissions in the system with pagination
     */
    private void printAllPermissions() {
        try {
            // Get all permissions from service
            String permissionsResponseJson = employeeService.getAllPermissions();
            Response<List<String>> permissionsResponse = JsonUtil.fromJson(permissionsResponseJson, 
                new TypeReference<Response<List<String>>>() {});

            if (permissionsResponse.isError()) {
                printError("Error retrieving permissions: " + permissionsResponse.getErrorMessage());
                return;
            }

            List<String> permissionsList = permissionsResponse.getValue();

            // Define how many permissions to show per page
            final int ITEMS_PER_PAGE = 5;

            // Use the pagination utility to display permissions
            CliUtil.displayPaginatedList(
                "All Permissions",
                permissionsList,
                ITEMS_PER_PAGE,
                permission -> permission,  // Simple display function as permissions are just strings
                scanner
            );
        } catch (Exception e) {
            printError("Error retrieving permissions: " + e.getMessage());
        }
    }

    /**
     * Creates a new permission in the system
     */
    private void createPermission() {
        printSectionHeader("Create Permission");

        // Show permissions
        String permissionsResponseJson = employeeService.getAllPermissions();
        Response<List<String>> permissionsResponse = JsonUtil.fromJson(permissionsResponseJson, 
            new TypeReference<Response<List<String>>>() {});

        if (permissionsResponse.isError()) {
            printError("Error retrieving permissions: " + permissionsResponse.getErrorMessage());
            return;
        }

        List<String> existingPermissionsList = permissionsResponse.getValue();
        String[] existingPermissions = existingPermissionsList.toArray(new String[0]);
        if (existingPermissions.length > 0) {
            CliUtil.printInfo("Existing permissions in the system:");
            List<String> permissionsList = Arrays.asList(existingPermissions);
            CliUtil.printHierarchicalList(permissionsList, "•", 2);
            CliUtil.printEmptyLine();
        }

        CliUtil.printBold("New Permission Name: ");
        String permissionName = scanner.nextLine();

        // Confirm
        if (confirm("Confirm creating permission '" + permissionName + "'?")) {
            String resultJson = employeeService.createPermission(doneBy, permissionName);
            Response<String> resultResponse = JsonUtil.fromJson(resultJson, new TypeReference<Response<String>>() {});

            if (resultResponse.isError()) {
                printError(resultResponse.getErrorMessage());
            } else {
                printSuccess(resultResponse.getValue());
            }
        } else {
            CliUtil.printOperationCancelled();
        }

        CliUtil.waitForEnter(scanner);
    }

    /**
     * Clones an existing role with all its permissions
     */
    private void cloneRole() {
        printSectionHeader("Clone Role");

        // Show roles
        String rolesResponseJson = employeeService.getAllRoles();
        Response<List<String>> rolesResponse = JsonUtil.fromJson(rolesResponseJson, 
            new TypeReference<Response<List<String>>>() {});

        if (rolesResponse.isError()) {
            printError("Error retrieving roles: " + rolesResponse.getErrorMessage());
            return;
        }

        List<String> existingRolesList = rolesResponse.getValue();
        String[] existingRoles = existingRolesList.toArray(new String[0]);
        if (existingRoles.length == 0) {
            printError("No roles exist in the system to clone.");
            CliUtil.waitForEnter(scanner);
            return;
        }

        CliUtil.printInfo("Available roles to clone:");
        List<String> rolesList = Arrays.asList(existingRoles);
        CliUtil.printHierarchicalList(rolesList, "•", 2);
        CliUtil.printEmptyLine();

        CliUtil.printBold("Source Role Name: ");
        String existingRoleName = scanner.nextLine();

        try {
            // Get role details
            String roleDetailsResponseJson = employeeService.getRoleDetails(existingRoleName);
            Response<Map<String, HashSet<String>>> roleDetailsResponse = JsonUtil.fromJson(
                roleDetailsResponseJson, 
                new TypeReference<Response<Map<String, HashSet<String>>>>() {}
            );

            if (roleDetailsResponse.isError()) {
                printError("Error retrieving role details: " + roleDetailsResponse.getErrorMessage());
                CliUtil.waitForEnter(scanner);
                return;
            }

            Map<String, HashSet<String>> roleDetails = roleDetailsResponse.getValue();
            if (roleDetails == null || roleDetails.isEmpty()) {
                printError("Source role '" + existingRoleName + "' not found.");
                CliUtil.waitForEnter(scanner);
                return;
            }

            // Show permissions that will be cloned
            HashSet<String> permissions = roleDetails.get(existingRoleName);
            CliUtil.printEmptyLine();
            CliUtil.printInfo("Permissions that will be cloned:");
            if (permissions == null || permissions.isEmpty()) {
                CliUtil.printInfo("  No permissions in source role");
            } else {
                List<String> permissionsList = new ArrayList<>(permissions);
                CliUtil.printHierarchicalList(permissionsList, "•", 2);
            }
        } catch (Exception e) {
            printError("Error retrieving role details: " + e.getMessage());
            CliUtil.waitForEnter(scanner);
            return;
        }

        CliUtil.printEmptyLine();
        CliUtil.printBold("New Role Name: ");
        String newRoleName = scanner.nextLine();

        // Confirm
        if (confirm("Confirm cloning role '" + existingRoleName + "' to new role '" + newRoleName + "'?")) {
            String resultJson = employeeService.cloneRole(doneBy, existingRoleName, newRoleName);
            Response<String> resultResponse = JsonUtil.fromJson(resultJson, new TypeReference<Response<String>>() {});

            if (resultResponse.isError()) {
                printError(resultResponse.getErrorMessage());
            } else {
                printSuccess(resultResponse.getValue());
            }
        } else {
            CliUtil.printOperationCancelled();
        }

        CliUtil.waitForEnter(scanner);
    }
}
