package PresentationLayer.EmployeeSubModule.CLI;

import DTOs.EmployeeDTO;
import DTOs.RoleDTO;
import Util.CliUtil;
import ServiceLayer.EmployeeSubModule.EmployeeService;
import ServiceLayer.exception.ServiceException;

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
import java.util.Set;

public class EmployeeCLI {

    // Properties
    private final DateTimeFormatter dateFormatter = CliUtil.dateFormatter;
    private final EmployeeService employeeService;
    private final Scanner scanner;
    private final long doneBy;

    // Helper methods for DTO deserialization
    private EmployeeDTO deserializeEmployee(String serialized) {
        return EmployeeDTO.deserialize(serialized);
    }

    private EmployeeDTO[] deserializeEmployees(String[] serialized) {
        EmployeeDTO[] employees = new EmployeeDTO[serialized.length];
        for (int i = 0; i < serialized.length; i++) {
            employees[i] = deserializeEmployee(serialized[i]);
        }
        return employees;
    }

    private RoleDTO deserializeRole(String serialized) {
        return RoleDTO.deserialize(serialized);
    }

    private RoleDTO[] deserializeRoles(String[] serialized) {
        RoleDTO[] roles = new RoleDTO[serialized.length];
        for (int i = 0; i < serialized.length; i++) {
            roles[i] = deserializeRole(serialized[i]);
        }
        return roles;
    }


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
            menuOptions.add(CliUtil.YELLOW + optionNumber++ + CliUtil.RESET + ". Deactivate/Reactivate Employee");
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

        menuOptions.add(CliUtil.YELLOW + "0" + CliUtil.RESET + ". Back To Main Menu");

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

            // Handle the exit option (0)
            if (choiceNum == 0) {
                CliUtil.printReturnMessage("main menu");
                return false;
            }

            if (choiceNum < 1 || choiceNum > max - 1) {
                printError("Invalid choice. Please enter a number between 0 and " + (max - 1) + ".");
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
//                CliUtil.printBold("Enter Role Name:");
//                String roleName = scanner.nextLine();
//                printRoleDetails(roleName);
                showRoleDetails();
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

            printError("Invalid choice. Please try again.");
            return true;
        } catch (NumberFormatException e) {
            printError("Please enter a valid input.");
            return true;
        }
    }

    private void showRoleDetails() {
        printSectionHeader("Role Details - Select role to view");
        // Show all roles
        CliUtil.printInfo("Current roles:");
        String[] roles = employeeService.getAllRoles();
        List<String> roleList = new ArrayList<>();
        List<RoleDTO> roleDTOList = new ArrayList<>();
        for (String role : roles) {
            RoleDTO roleDTO = deserializeRole(role);
            roleDTOList.add(roleDTO);
            roleList.add(roleDTO.getName());
        }
        CliUtil.printNumberedList(roleList, 1);
        CliUtil.printBold("Enter Role Number: ");
        int roleNumber = Integer.parseInt(scanner.nextLine());
        if (roleNumber < 1 || roleNumber > roleList.size()) {
            printError("Invalid choice.");
        }
        RoleDTO selectedRole = roleDTOList.get(roleNumber - 1);
        printSuccess("Role Details for " + CliUtil.YELLOW + selectedRole.getName() + CliUtil.RESET + ":");
        printRoleDetails(selectedRole.getName());
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
            employeeService.isEmployeeAuthorised(doneBy, permission);
            return true;
        } catch (Exception e) {
            //printError("Error checking permissions: " + e.getMessage());
            return false;
        }
    }


    //==========================================================================================
    // EMPLOYEE MANAGEMENT METHODS
    //==========================================================================================

    /**
     * Deactivates or reactivates an employee
     */
    private void deactivateEmployee() {
        printSectionHeader("Deactivate/Reactivate Employee");

        // Show all employees with numeric selection
        CliUtil.printInfo("Current employees:");
        EmployeeDTO[] employees = employeeService.getAllEmployeesAsDTO();

        // Separate active and inactive employees
        List<EmployeeDTO> activeEmployees = new ArrayList<>();
        List<EmployeeDTO> inactiveEmployees = new ArrayList<>();
        for (EmployeeDTO employee : employees) {
            if (employee.isActive()) {
                activeEmployees.add(employee);
            } else {
                inactiveEmployees.add(employee);
            }
        }

        // Ask user whether to deactivate or reactivate
        CliUtil.printEmptyLine();
        CliUtil.printBold("Do you want to: ");
        CliUtil.print("  1. Deactivate an employee");
        CliUtil.print("  2. Reactivate an employee");
        CliUtil.printEmptyLine();
        CliUtil.printBold("Enter your choice (1-2): ");

        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine());
            if (choice < 1 || choice > 2) {
                printError("Invalid choice. Operation cancelled.");
                CliUtil.waitForEnter(scanner);
                return;
            }
        } catch (NumberFormatException e) {
            printError("Please enter a valid number. Operation cancelled.");
            CliUtil.waitForEnter(scanner);
            return;
        }

        boolean isDeactivating = (choice == 1);
        List<EmployeeDTO> relevantEmployees = isDeactivating ? activeEmployees : inactiveEmployees;

        if (relevantEmployees.isEmpty()) {
            printError("No " + (isDeactivating ? "active" : "inactive") + " employees found to " + 
                      (isDeactivating ? "deactivate" : "reactivate") + ".");
            CliUtil.waitForEnter(scanner);
            return;
        }

        // Display relevant employees with numbers
        for (int i = 0; i < relevantEmployees.size(); i++) {
            EmployeeDTO employee = relevantEmployees.get(i);
            System.out.printf("  %d. ID: %-9d | Name: %-20s | Status: %s%n",
                i + 1,
                employee.getIsraeliId(),
                employee.getFullName(),
                employee.isActive() ? CliUtil.greenString("Active") : CliUtil.redString("Inactive"));
        }

        // Get employee selection by number
        CliUtil.printEmptyLine();
        CliUtil.printBold("Select employee to " + (isDeactivating ? "deactivate" : "reactivate") + " (enter number): ");
        int employeeIndex = -1;
        try {
            employeeIndex = Integer.parseInt(scanner.nextLine()) - 1;
            if (employeeIndex < 0 || employeeIndex >= relevantEmployees.size()) {
                printError("Invalid selection. Operation cancelled.");
                CliUtil.waitForEnter(scanner);
                return;
            }
        } catch (NumberFormatException e) {
            printError("Please enter a valid number. Operation cancelled.");
            CliUtil.waitForEnter(scanner);
            return;
        }

        EmployeeDTO selectedEmployee = relevantEmployees.get(employeeIndex);
        long israeliId = selectedEmployee.getIsraeliId();

        try {
            // Confirm
            if (confirm("Confirm " + (isDeactivating ? "deactivating" : "reactivating") + " employee '" +
                       selectedEmployee.getFullName() + "' (ID: " + israeliId + ")?")) {

                String result;
                if (isDeactivating) {
                    // Use the existing deactivateEmployee method
                    result = employeeService.deactivateEmployee(doneBy, israeliId);
                } else {
                    result = employeeService.reactivateEmployee(doneBy, israeliId);
                }

                if (result.contains("successfully")) {
                    printSuccess(result);
                } else {
                    printError(result);
                }
            } else {
                CliUtil.printOperationCancelled();
            }
        } catch (Exception e) {
            CliUtil.printError("An error occurred: " + e.getMessage());
        }

        CliUtil.waitForEnter(scanner);
    }

    private void printWelcomeBanner() {
        CliUtil.printWelcomeBanner("EMPLOYEE MANAGEMENT SYSTEM", LocalDate.now().toString(), formatEmployeeDisplay(doneBy));
    }
    /**
     * Formats employee display with name, number, and branch
     *
     * @param employeeId The ID of the employee
     * @return A formatted string with employee name, number, and branch
     */
    private String formatEmployeeDisplay(long employeeId) {
        try {
            EmployeeDTO employee = employeeService.getEmployeeByIdAsDTO(employeeId);
            String branch = employeeService.getEmployeeBranchName(employee.getBranchId()) != null ? " [" + employeeService.getEmployeeBranchName(employee.getBranchId()) + "]" : "";
            return employee.getFullName() + " (#" + employeeId + ")" + branch;
        } catch (ServiceException e) {
            // If we can't get the employee name, just return the ID
            return "Employee #" + employeeId;
        }
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
        boolean retry = true;

        while (retry) {
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

                CliUtil.printBold("Branch ID: ");
                long branch = Long.parseLong(scanner.nextLine());

                // Bank account information
                CliUtil.printEmptyLine();
                CliUtil.printSectionWithIcon("BANK ACCOUNT INFORMATION", "🏦");
                CliUtil.printInfo("Would you like to add bank account information? (y/n)");
                String addBankAccount = scanner.nextLine();

                long bankNumber = 0;
                long bankBranchNumber = 0;
                long bankAccountNumber = 0;
                boolean hasBankAccount = false;

                if (addBankAccount.equalsIgnoreCase("y") || addBankAccount.equalsIgnoreCase("yes")) {
                    hasBankAccount = true;
                    CliUtil.printBold("Bank Number: ");
                    bankNumber = Long.parseLong(scanner.nextLine());

                    CliUtil.printBold("Bank Branch Number: ");
                    bankBranchNumber = Long.parseLong(scanner.nextLine());

                    CliUtil.printBold("Bank Account Number: ");
                    bankAccountNumber = Long.parseLong(scanner.nextLine());
                }

                Map<String, Object> termsOfEmployment = new HashMap<>();
                CliUtil.printEmptyLine();
                CliUtil.printSectionWithIcon("TERMS OF EMPLOYMENT", "📋");
                CliUtil.printInfo("Enter key-value pairs. Type 'done' when finished.");
                CliUtil.printEmptyLine();

                while (true) {
                    CliUtil.printBold("Key (or 'done' or '0' to exit): ");
                    String key = scanner.nextLine();
                    if (key.equalsIgnoreCase("done") || key.equals("0")) break;

                    CliUtil.printBold("Value: ");
                    String value = scanner.nextLine();
                    termsOfEmployment.put(key, value);
                    CliUtil.printSuccessWithCheckmark("Added: " + key + " = " + value);
                }

                String result;
                if (hasBankAccount) {
                    result = employeeService.createEmployee(doneBy, israeliId, firstName, lastName, salary, termsOfEmployment, startDate, branch, bankNumber, bankBranchNumber, bankAccountNumber);
                } else {
                    result = employeeService.createEmployee(doneBy, israeliId, firstName, lastName, salary, termsOfEmployment, startDate, branch);
                }

                if (result.contains("successfully")) {
                    printSuccess(result);
                    retry = false; // Success, exit the retry loop
                } else {
                    int choice = CliUtil.handleError(result, scanner);
                    if (choice == 0) { // Cancel operation
                        CliUtil.printOperationCancelled();
                        retry = false;
                    } else if (choice == 2) { // Return to previous menu
                        retry = false;
                    }
                    // If choice == 1, retry is still true, so we'll loop again
                }
            } catch (NumberFormatException e) {
                int choice = CliUtil.handleError("Please enter valid numeric input.", scanner);
                if (choice == 0) { // Cancel operation
                    CliUtil.printOperationCancelled();
                    retry = false;
                } else if (choice == 2) { // Return to previous menu
                    retry = false;
                }
                // If choice == 1, retry is still true, so we'll loop again
            } catch (DateTimeParseException e) {
                int choice = CliUtil.handleError("Please enter date in format dd-MM-yyyy.", scanner);
                if (choice == 0) { // Cancel operation
                    CliUtil.printOperationCancelled();
                    retry = false;
                } else if (choice == 2) { // Return to previous menu
                    retry = false;
                }
                // If choice == 1, retry is still true, so we'll loop again
            } catch (Exception e) {
                int choice = CliUtil.handleError("An error occurred: " + e.getMessage(), scanner);
                if (choice == 0) { // Cancel operation
                    CliUtil.printOperationCancelled();
                    retry = false;
                } else if (choice == 2) { // Return to previous menu
                    retry = false;
                }
                // If choice == 1, retry is still true, so we'll loop again
            }
        }

        CliUtil.waitForEnter(scanner);
    }
    private void updateEmployee() {
        boolean retry = true;

        while (retry) {
            printSectionHeader("Update Employee");

            try {
                CliUtil.printBold("Israeli ID: ");
                long israeliId = Long.parseLong(scanner.nextLine());

                EmployeeDTO existing = employeeService.getEmployeeByIdAsDTO(israeliId);
                if (existing == null) {
                    int choice = CliUtil.handleError("Employee not found.", scanner);
                    if (choice == 0) { // Cancel operation
                        CliUtil.printOperationCancelled();
                        retry = false;
                    } else if (choice == 2) { // Return to previous menu
                        retry = false;
                    }
                    continue; // Skip the rest of this iteration
                }

                printSectionHeader("Edit Employee: " + CliUtil.YELLOW + existing.getFullName() + CliUtil.RESET);

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

                // Display numbered options for employee status
                CliUtil.print("Select employee status:");
                CliUtil.print(CliUtil.YELLOW + "1" + CliUtil.RESET + ". Active");
                CliUtil.print(CliUtil.YELLOW + "2" + CliUtil.RESET + ". Inactive");
                CliUtil.print(CliUtil.YELLOW + "0" + CliUtil.RESET + ". Cancel (keep current)");

                CliUtil.printPrompt("Enter your choice: ");
                String activeInput = scanner.nextLine();

                boolean active;
                if (activeInput.equals("0") || activeInput.isBlank()) {
                    // Keep current value
                    active = existing.isActive();
                } else if (activeInput.equals("1")) {
                    active = true;
                } else if (activeInput.equals("2")) {
                    active = false;
                } else {
                    // Invalid input, keep current value
                    int choice = CliUtil.handleError("Invalid choice. Keeping current status.", scanner);
                    if (choice == 0) { // Cancel operation
                        CliUtil.printOperationCancelled();
                        retry = false;
                        continue;
                    } else if (choice == 2) { // Return to previous menu
                        retry = false;
                        continue;
                    }
                    active = existing.isActive();
                }

                CliUtil.printEmptyLine();
                CliUtil.printSectionWithIcon("CURRENT TERMS OF EMPLOYMENT", "📋");
                if (existing.getTermsOfEmployment().isEmpty()) {
                    CliUtil.printInfo("  No terms defined");
                } else {
                    existing.getTermsOfEmployment().forEach((k, v) -> 
                        CliUtil.print("  " + CliUtil.BOLD + k + CliUtil.RESET + ": " + v));
                }

                // Display current bank account information
                CliUtil.printEmptyLine();
                CliUtil.printSectionWithIcon("CURRENT BANK ACCOUNT INFORMATION", "🏦");
                if (existing.getBankAccount() == null) {
                    CliUtil.printInfo("  No bank account information defined");
                } else {
                    CliUtil.print("  " + CliUtil.BOLD + "Bank Number: " + CliUtil.RESET + existing.getBankAccount().getBankNumber());
                    CliUtil.print("  " + CliUtil.BOLD + "Bank Branch Number: " + CliUtil.RESET + existing.getBankAccount().getBankBranchNumber());
                    CliUtil.print("  " + CliUtil.BOLD + "Bank Account Number: " + CliUtil.RESET + existing.getBankAccount().getBankAccountNumber());
                }

                CliUtil.printEmptyLine();
                CliUtil.printSectionWithIcon("UPDATE TERMS OF EMPLOYMENT", "📝");
                CliUtil.printInfo("Enter key-value pairs. Type 'done' when finished.");
                CliUtil.printInfo("To remove a term, enter its key with an empty value.");
                CliUtil.printEmptyLine();

                Map<String, Object> termsOfEmployment = new HashMap<>(existing.getTermsOfEmployment());
                while (true) {
                    CliUtil.printBold("Key (or 'done' or '0' to exit): ");
                    String key = scanner.nextLine();
                    if (key.equalsIgnoreCase("done") || key.equals("0")) break;

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

                // First update the employee information
                String result = employeeService.updateEmployee(doneBy, israeliId, firstName, lastName, salary, termsOfEmployment, active);

                if (!result.contains("successfully")) {
                    int choice = CliUtil.handleError(result, scanner);
                    if (choice == 0) { // Cancel operation
                        CliUtil.printOperationCancelled();
                        retry = false;
                        continue;
                    } else if (choice == 2) { // Return to previous menu
                        retry = false;
                        continue;
                    }
                } else {
                    printSuccess(result);
                }

                // Bank account information - separate update
                CliUtil.printEmptyLine();
                CliUtil.printSectionWithIcon("UPDATE BANK ACCOUNT INFORMATION", "🏦");
                CliUtil.printInfo("What would you like to do with bank account information?");
                CliUtil.print(CliUtil.YELLOW + "1" + CliUtil.RESET + ". Update bank account information");
                CliUtil.print(CliUtil.YELLOW + "2" + CliUtil.RESET + ". Remove bank account information");
                CliUtil.print(CliUtil.YELLOW + "0" + CliUtil.RESET + ". Skip (no changes to bank account)");

                CliUtil.printPrompt("Enter your choice: ");
                String bankAccountChoice = scanner.nextLine();

                if (bankAccountChoice.equals("1")) {
                    // Update bank account information
                    CliUtil.printBold("Bank Number: ");
                    String bankNumberInput = scanner.nextLine();
                    long bankNumber = bankNumberInput.isBlank() && existing.getBankAccount() != null ? 
                        existing.getBankAccount().getBankNumber() : 
                        Long.parseLong(bankNumberInput);

                    CliUtil.printBold("Bank Branch Number: ");
                    String bankBranchNumberInput = scanner.nextLine();
                    long bankBranchNumber = bankBranchNumberInput.isBlank() && existing.getBankAccount() != null ? 
                        existing.getBankAccount().getBankBranchNumber() : 
                        Long.parseLong(bankBranchNumberInput);

                    CliUtil.printBold("Bank Account Number: ");
                    String bankAccountNumberInput = scanner.nextLine();
                    long bankAccountNumber = bankAccountNumberInput.isBlank() && existing.getBankAccount() != null ? 
                        existing.getBankAccount().getBankAccountNumber() : 
                        Long.parseLong(bankAccountNumberInput);

                    String bankResult = employeeService.updateEmployeeBankAccount(doneBy, israeliId, 
                                                                                bankNumber, bankBranchNumber, bankAccountNumber);

                    if (bankResult.contains("successfully")) {
                        printSuccess(bankResult);
                    } else {
                        CliUtil.handleError(bankResult, scanner);
                    }
                } else if (bankAccountChoice.equals("2")) {
                    // Remove bank account information
                    if (existing.getBankAccount() == null) {
                        CliUtil.printInfo("No bank account information to remove.");
                    } else {
                        String bankResult = employeeService.removeEmployeeBankAccount(doneBy, israeliId);

                        if (bankResult.contains("successfully")) {
                            printSuccess(bankResult);
                        } else {
                            CliUtil.handleError(bankResult, scanner);
                        }
                    }
                } else if (!bankAccountChoice.equals("0")) {
                    CliUtil.printInfo("Invalid choice. Skipping bank account update.");
                }

                retry = false; // Exit the retry loop after handling bank account
            } catch (NumberFormatException e) {
                int choice = CliUtil.handleError("Please enter valid numeric input.", scanner);
                if (choice == 0) { // Cancel operation
                    CliUtil.printOperationCancelled();
                    retry = false;
                } else if (choice == 2) { // Return to previous menu
                    retry = false;
                }
                // If choice == 1, retry is still true, so we'll loop again
            } catch (Exception e) {
                int choice = CliUtil.handleError("An error occurred: " + e.getMessage(), scanner);
                if (choice == 0) { // Cancel operation
                    CliUtil.printOperationCancelled();
                    retry = false;
                } else if (choice == 2) { // Return to previous menu
                    retry = false;
                }
                // If choice == 1, retry is still true, so we'll loop again
            }
        }

        CliUtil.waitForEnter(scanner);
    }
    private void addRoleToEmployee() {
        boolean retry = true;

        while (retry) {
            printSectionHeader("Add Role to Employee");

            // Get all roles
            RoleDTO[] allRoles = deserializeRoles(employeeService.getAllRoles());
            if (allRoles.length == 0) {
                int choice = CliUtil.handleError("No roles defined in the system", scanner);
                if (choice == 0 || choice == 2) { // Cancel operation or return to previous menu
                    retry = false;
                }
                continue; // Skip the rest of this iteration
            }

            try {
                // Get employee ID
                CliUtil.printBold("Employee Israeli ID: ");
                long israeliId = Long.parseLong(scanner.nextLine());

                // Get employee details
                EmployeeDTO employee = employeeService.getEmployeeByIdAsDTO(israeliId);
                if (employee == null) {
                    int choice = CliUtil.handleError("Employee not found.", scanner);
                    if (choice == 0) { // Cancel operation
                        CliUtil.printOperationCancelled();
                        retry = false;
                    } else if (choice == 2) { // Return to previous menu
                        retry = false;
                    }
                    continue; // Skip the rest of this iteration
                }

                CliUtil.printSuccess("Employee: " + employee.getFullName());

                // Display current roles
                CliUtil.printEmptyLine();
                CliUtil.printSectionWithIcon("CURRENT ROLES", "👤");
                if (employee.getRoles().isEmpty()) {
                    CliUtil.printInfo("  No roles assigned");
                } else {
                    List<String> roles = new ArrayList<>(employee.getRoles());
                    CliUtil.printHierarchicalList(roles, "•", 2);
                }

                // Display available roles
                CliUtil.printEmptyLine();
                CliUtil.printSectionWithIcon("AVAILABLE ROLES", "🔑");
                List<String> rolesList = Arrays.asList(Arrays.stream(allRoles).map(RoleDTO::getName).toArray(String[]::new));
                CliUtil.printNumberedList(rolesList, 1);

                // Get role selection
                CliUtil.printEmptyLine();
                CliUtil.printBold("Role Number to Add (or 0 to cancel): ");
                String roleInput = scanner.nextLine();

                // Check for cancel
                if (roleInput.equals("0")) {
                    CliUtil.printOperationCancelled();
                    retry = false;
                    continue;
                }

                int roleNumber = Integer.parseInt(roleInput);
                if (roleNumber < 1 || roleNumber > rolesList.size()) {
                    int choice = CliUtil.handleError("Invalid role number. Please enter a number between 1 and " + rolesList.size() + ".", scanner);
                    if (choice == 0) { // Cancel operation
                        CliUtil.printOperationCancelled();
                        retry = false;
                    } else if (choice == 2) { // Return to previous menu
                        retry = false;
                    }
                    continue; // Skip the rest of this iteration
                }

                String roleName = rolesList.get(roleNumber - 1);

                // Confirm
                boolean confirmed = confirm("Confirm adding role '" + roleName + "' to employee #" + israeliId + "?");
                if (confirmed) {
                    String result = employeeService.addRoleToEmployee(doneBy, israeliId, roleName);

                    if (result.contains("successfully")) {
                        printSuccess(result);
                        retry = false; // Success, exit the retry loop
                    } else {
                        int choice = CliUtil.handleError(result, scanner);
                        if (choice == 0) { // Cancel operation
                            CliUtil.printOperationCancelled();
                            retry = false;
                        } else if (choice == 2) { // Return to previous menu
                            retry = false;
                        }
                        // If choice == 1, retry is still true, so we'll loop again
                    }
                } else {
                    CliUtil.printOperationCancelled();
                    retry = false;
                }
            } catch (NumberFormatException e) {
                int choice = CliUtil.handleError("Please enter a valid number.", scanner);
                if (choice == 0) { // Cancel operation
                    CliUtil.printOperationCancelled();
                    retry = false;
                } else if (choice == 2) { // Return to previous menu
                    retry = false;
                }
                // If choice == 1, retry is still true, so we'll loop again
            } catch (IndexOutOfBoundsException e) {
                int choice = CliUtil.handleError("Invalid role number. Please try again.", scanner);
                if (choice == 0) { // Cancel operation
                    CliUtil.printOperationCancelled();
                    retry = false;
                } else if (choice == 2) { // Return to previous menu
                    retry = false;
                }
                // If choice == 1, retry is still true, so we'll loop again
            } catch (Exception e) {
                int choice = CliUtil.handleError("An error occurred: " + e.getMessage(), scanner);
                if (choice == 0) { // Cancel operation
                    CliUtil.printOperationCancelled();
                    retry = false;
                } else if (choice == 2) { // Return to previous menu
                    retry = false;
                }
                // If choice == 1, retry is still true, so we'll loop again
            }
        }

        CliUtil.waitForEnter(scanner);
    }

    private void removeRoleFromEmployee() {
        boolean retry = true;

        while (retry) {
            printSectionHeader("Remove Role from Employee");

            try {
                // Get employee ID
                CliUtil.printBold("Employee Israeli ID (or 0 to cancel): ");
                String idInput = scanner.nextLine();

                // Check for cancel
                if (idInput.equals("0")) {
                    CliUtil.printOperationCancelled();
                    retry = false;
                    continue;
                }

                long israeliId = Long.parseLong(idInput);

                // Get employee details
                EmployeeDTO employee = employeeService.getEmployeeByIdAsDTO(israeliId);
                if (employee == null) {
                    int choice = CliUtil.handleError("Employee not found.", scanner);
                    if (choice == 0) { // Cancel operation
                        CliUtil.printOperationCancelled();
                        retry = false;
                    } else if (choice == 2) { // Return to previous menu
                        retry = false;
                    }
                    continue; // Skip the rest of this iteration
                }

                CliUtil.printSuccess("Employee: " + employee.getFullName());

                // Display current roles
                CliUtil.printEmptyLine();
                CliUtil.printSectionWithIcon("CURRENT ROLES", "👤");
                if (employee.getRoles().isEmpty()) {
                    int choice = CliUtil.handleError("No roles assigned to this employee.", scanner);
                    if (choice == 0 || choice == 2) { // Cancel operation or return to previous menu
                        retry = false;
                    }
                    continue; // Skip the rest of this iteration
                } else {
                    List<String> roles = new ArrayList<>(employee.getRoles());
                    CliUtil.printHierarchicalList(roles, "•", 2);
                }

                // Get role to remove
                CliUtil.printEmptyLine();
                CliUtil.printBold("Role Name to Remove (or 'cancel' to cancel): ");
                String roleName = scanner.nextLine();

                // Check for cancel
                if (roleName.equalsIgnoreCase("cancel")) {
                    CliUtil.printOperationCancelled();
                    retry = false;
                    continue;
                }

                // Confirm
                if (confirm("Confirm removing role '" + roleName + "' from employee #" + israeliId + "?")) {
                    String result = employeeService.removeRoleFromEmployee(doneBy, israeliId, roleName);

                    if (result.contains("successfully")) {
                        printSuccess(result);
                        retry = false; // Success, exit the retry loop
                    } else {
                        int choice = CliUtil.handleError(result, scanner);
                        if (choice == 0) { // Cancel operation
                            CliUtil.printOperationCancelled();
                            retry = false;
                        } else if (choice == 2) { // Return to previous menu
                            retry = false;
                        }
                        // If choice == 1, retry is still true, so we'll loop again
                    }
                } else {
                    CliUtil.printOperationCancelled();
                    retry = false;
                }
            } catch (NumberFormatException e) {
                int choice = CliUtil.handleError("Please enter a valid number for the employee ID.", scanner);
                if (choice == 0) { // Cancel operation
                    CliUtil.printOperationCancelled();
                    retry = false;
                } else if (choice == 2) { // Return to previous menu
                    retry = false;
                }
                // If choice == 1, retry is still true, so we'll loop again
            } catch (Exception e) {
                int choice = CliUtil.handleError("An error occurred: " + e.getMessage(), scanner);
                if (choice == 0) { // Cancel operation
                    CliUtil.printOperationCancelled();
                    retry = false;
                } else if (choice == 2) { // Return to previous menu
                    retry = false;
                }
                // If choice == 1, retry is still true, so we'll loop again
            }
        }

        CliUtil.waitForEnter(scanner);
    }

    private void createRole() {
        printSectionHeader("Create Role");

        CliUtil.printBold("Role Name: ");
        String roleName = scanner.nextLine();

        CliUtil.printEmptyLine();

        // Display numbered options for adding permissions
        CliUtil.print("Do you want to add permissions now?");
        CliUtil.print(CliUtil.YELLOW + "1" + CliUtil.RESET + ". Yes");
        CliUtil.print(CliUtil.YELLOW + "2" + CliUtil.RESET + ". No");
        CliUtil.print(CliUtil.YELLOW + "0" + CliUtil.RESET + ". Cancel");

        CliUtil.printPrompt("Enter your choice: ");
        String addPermissions = scanner.nextLine();

        if (addPermissions.equals("0")) {
            CliUtil.printOperationCancelled();
            CliUtil.waitForEnter(scanner);
            return;
        } else if (addPermissions.equals("1")) {
            CliUtil.printEmptyLine();
            CliUtil.printSectionWithIcon("AVAILABLE PERMISSIONS", "🔑");
            String[] allPermissions = employeeService.getAllPermissions();

            if (allPermissions.length == 0) {
                CliUtil.printInfo("  No permissions defined in the system");
            } else {
                List<String> permissionsList = Arrays.asList(allPermissions);
                CliUtil.printHierarchicalList(permissionsList, "•", 2);
            }

            CliUtil.printEmptyLine();
            CliUtil.printSectionWithIcon("ADD PERMISSIONS TO ROLE", "📝");
            CliUtil.printInfo("Enter permissions one by one. Type 'done' when finished.");
            CliUtil.printEmptyLine();

            HashSet<String> permissions = new HashSet<>();
            while (true) {
                CliUtil.printBold("Permission (or 'done' or '0' to exit): ");
                String permission = scanner.nextLine();
                if (permission.equalsIgnoreCase("done") || permission.equals("0")) break;
                permissions.add(permission);
                CliUtil.printSuccessWithCheckmark("Added: " + permission);
            }

            // Confirm
            if (confirm("Confirm creating role '" + roleName + "' with " + permissions.size() + " permissions?")) {
                String result = employeeService.createRoleWithPermissions(doneBy, roleName, permissions);

                if (result.contains("successfully")) {
                    printSuccess(result);
                } else {
                    printError(result);
                }
            } else {
                CliUtil.printOperationCancelled();
            }
        } else if (addPermissions.equals("2")) {
            // Confirm
            if (confirm("Confirm creating role '" + roleName + "' with no permissions?")) {
                // Create role without permissions
                String result = employeeService.createRole(doneBy, roleName);

                if (result.contains("successfully")) {
                    printSuccess(result);
                } else {
                    printError(result);
                }
            } else {
                CliUtil.printOperationCancelled();
            }
        } else {
            // Invalid input
            printError("Invalid choice. Operation cancelled.");
            CliUtil.waitForEnter(scanner);
            return;
        }

        CliUtil.waitForEnter(scanner);
    }

    private void addPermissionToRole() {
        printSectionHeader("Add Permission to Role");

        // Show roles with numeric selection
        CliUtil.printInfo("Available roles:");
        RoleDTO[] allRoles = employeeService.getAllRolesAsDTO();

        // Display roles with numbers
        for (int i = 0; i < allRoles.length; i++) {
            CliUtil.printInfo("  " + (i + 1) + ". " + allRoles[i].getName());
        }

        // Get role selection by number
        CliUtil.printEmptyLine();
        CliUtil.printBold("Select role (enter number): ");
        int roleIndex = -1;
        try {
            roleIndex = Integer.parseInt(scanner.nextLine()) - 1;
            if (roleIndex < 0 || roleIndex >= allRoles.length) {
                printError("Invalid selection. Operation cancelled.");
                CliUtil.waitForEnter(scanner);
                return;
            }
        } catch (NumberFormatException e) {
            printError("Please enter a valid number. Operation cancelled.");
            CliUtil.waitForEnter(scanner);
            return;
        }

        String roleName = allRoles[roleIndex].getName();

        // Show permissions with numeric selection
        CliUtil.printEmptyLine();
        CliUtil.printInfo("Available permissions:");
        String[] allPermissions = employeeService.getAllPermissions();

        // Display permissions with numbers
        for (int i = 0; i < allPermissions.length; i++) {
            CliUtil.printInfo("  " + (i + 1) + ". " + allPermissions[i]);
        }

        // Get permission selection by number
        CliUtil.printEmptyLine();
        CliUtil.printBold("Select permission (enter number): ");
        int permissionIndex = -1;
        try {
            permissionIndex = Integer.parseInt(scanner.nextLine()) - 1;
            if (permissionIndex < 0 || permissionIndex >= allPermissions.length) {
                printError("Invalid selection. Operation cancelled.");
                CliUtil.waitForEnter(scanner);
                return;
            }
        } catch (NumberFormatException e) {
            printError("Please enter a valid number. Operation cancelled.");
            CliUtil.waitForEnter(scanner);
            return;
        }

        String permissionName = allPermissions[permissionIndex];

        // Confirm
        if (confirm("Confirm adding permission '" + permissionName + "' to role '" + roleName + "'?")) {
            String result = employeeService.addPermissionToRole(doneBy, roleName, permissionName);

            if (result.contains("successfully")) {
                printSuccess(result);
            } else {
                printError(result);
            }
        } else {
            CliUtil.printOperationCancelled();
        }

        CliUtil.waitForEnter(scanner);
    }

    private void removePermissionFromRole() {
        printSectionHeader("Remove Permission from Role");

        // Show roles with numeric selection
        CliUtil.printInfo("Available roles:");
        RoleDTO[] allRoles = employeeService.getAllRolesAsDTO();

        // Display roles with numbers
        for (int i = 0; i < allRoles.length; i++) {
            CliUtil.printInfo("  " + (i + 1) + ". " + allRoles[i].getName());
        }

        // Get role selection by number
        CliUtil.printEmptyLine();
        CliUtil.printBold("Select role (enter number): ");
        int roleIndex = -1;
        try {
            roleIndex = Integer.parseInt(scanner.nextLine()) - 1;
            if (roleIndex < 0 || roleIndex >= allRoles.length) {
                printError("Invalid selection. Operation cancelled.");
                CliUtil.waitForEnter(scanner);
                return;
            }
        } catch (NumberFormatException e) {
            printError("Please enter a valid number. Operation cancelled.");
            CliUtil.waitForEnter(scanner);
            return;
        }

        RoleDTO roleDTO = allRoles[roleIndex];
        String roleName = roleDTO.getName();
        Set<String> permissions = roleDTO.getPermissions();

        CliUtil.printEmptyLine();
        CliUtil.printInfo("Current permissions for role '" + roleName + "':");

        if (permissions == null || permissions.isEmpty()) {
            CliUtil.printInfo("  No permissions assigned to this role");
            CliUtil.waitForEnter(scanner);
            return;
        } else {
            List<String> permissionsList = new ArrayList<>(permissions);

            // Display permissions with numbers
            for (int i = 0; i < permissionsList.size(); i++) {
                CliUtil.printInfo("  " + (i + 1) + ". " + permissionsList.get(i));
            }

            // Get permission selection by number
            CliUtil.printEmptyLine();
            CliUtil.printBold("Select permission to remove (enter number): ");
            int permissionIndex = -1;
            try {
                permissionIndex = Integer.parseInt(scanner.nextLine()) - 1;
                if (permissionIndex < 0 || permissionIndex >= permissionsList.size()) {
                    printError("Invalid selection. Operation cancelled.");
                    CliUtil.waitForEnter(scanner);
                    return;
                }
            } catch (NumberFormatException e) {
                printError("Please enter a valid number. Operation cancelled.");
                CliUtil.waitForEnter(scanner);
                return;
            }

            String permissionName = permissionsList.get(permissionIndex);

            // Confirm
            if (confirm("Confirm removing permission '" + permissionName + "' from role '" + roleName + "'?")) {
                String result = employeeService.removePermissionFromRole(doneBy, roleName, permissionName);

                if (result.contains("successfully")) {
                    printSuccess(result);
                } else {
                    printError(result);
                }
            } else {
                CliUtil.printOperationCancelled();
            }
        }

        CliUtil.waitForEnter(scanner);
    }

    /**
     * Displays all employees in the system with pagination
     * If the user has VIEW_EMPLOYEE permission, shows all employees
     * Otherwise, shows only employees for the user's branch
     */
    private void printAllEmployees() {
        List<EmployeeDTO> employeeList;

        // Check if user has permission to view all employees
        if (hasPermission("VIEW_EMPLOYEE")) {
            // User has management permission, show all employees
            String[] serializedEmployees = employeeService.getAllEmployees();
            EmployeeDTO[] employees = deserializeEmployees(serializedEmployees);
            employeeList = Arrays.asList(employees);
        } else {
            // Regular user, show only employees for their branch
            try {
                // Get employee's branch ID
                EmployeeDTO employee = employeeService.getEmployeeByIdAsDTO(doneBy);
                long branchId = employee.getBranchId();

                // Get employees for this branch
                String[] serializedEmployees = employeeService.getAllEmployeesByBranch(branchId);
                EmployeeDTO[] employees = deserializeEmployees(serializedEmployees);
                employeeList = Arrays.asList(employees);
            } catch (Exception e) {
                printError("Error retrieving employees: " + e.getMessage());
                waitForEnter();
                return;
            }
        }

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
                String branch = employeeService.getEmployeeBranchName(employee.getIsraeliId()) != null ? employeeService.getEmployeeBranchName(employee.getIsraeliId()) : "None";
                return String.format("ID: %-11d | Name: %-28s | Branch: %-15s | Status: %s",
                    employee.getIsraeliId(),
                    employee.getFullName(),
                    branch,
                    status);
            },
            scanner
        );
    }

    /**
     * Displays detailed information about a specific employee
     * 
     * @param israeliId The ID of the employee to display
     */
    private void printEmployeeDetails(long israeliId) {
        printSectionHeader("Employee Details");

        try {
            EmployeeDTO employee = employeeService.getEmployeeByIdAsDTO(israeliId);

            // Create headers for the table sections
            List<String> headers = Arrays.asList(
                "EMPLOYEE INFORMATION",
                "ROLES & PERMISSIONS",
                "TERMS OF EMPLOYMENT",
                "BANK ACCOUNT INFORMATION"
            );

            // Create content for each section
            Map<String, List<String[]>> content = new HashMap<>();

            // Employee Information section
            List<String[]> employeeInfo = new ArrayList<>();
            employeeInfo.add(new String[]{"ID:", String.valueOf(employee.getIsraeliId())});
            employeeInfo.add(new String[]{"Name:", employee.getFullName()});
            employeeInfo.add(new String[]{"Salary:", String.valueOf(employee.getSalary())});

            String status = employee.isActive()
                ? CliUtil.greenString("Active") 
                : CliUtil.redString("Inactive");
            employeeInfo.add(new String[]{"Status:", status});
            employeeInfo.add(new String[]{"Start Date:", employee.getStartOfEmployment().toString()});
            employeeInfo.add(new String[]{"Branch:", employeeService.getEmployeeBranchName(employee.getIsraeliId()) != null ? employeeService.getEmployeeBranchName(employee.getIsraeliId()) : "None"});
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

            // Bank Account Information section
            List<String[]> bankAccountInfo = new ArrayList<>();
            if (employee.getBankAccount() != null) {
                bankAccountInfo.add(new String[]{"Bank Number:", String.valueOf(employee.getBankAccount().getBankNumber())});
                bankAccountInfo.add(new String[]{"Branch Number:", String.valueOf(employee.getBankAccount().getBankBranchNumber())});
                bankAccountInfo.add(new String[]{"Account Number:", String.valueOf(employee.getBankAccount().getBankAccountNumber())});
            }
            content.put("BANK ACCOUNT INFORMATION", bankAccountInfo);

            // Create empty messages for each section
            Map<String, String> emptyMessages = new HashMap<>();
            emptyMessages.put("ROLES & PERMISSIONS", "No roles assigned");
            emptyMessages.put("TERMS OF EMPLOYMENT", "No terms of employment defined");
            emptyMessages.put("BANK ACCOUNT INFORMATION", "No bank account information defined");

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
        // Convert array to list for pagination
        List<String> rolesList = Arrays.asList(employeeService.getAllRoles());
        List<RoleDTO> rolesDTOList = new ArrayList<>();
        for (String role : rolesList) {
            RoleDTO roleDTO = RoleDTO.deserialize(role);
            if (roleDTO != null) {
                rolesDTOList.add(roleDTO);
            }
        }


        // Define how many roles to show per page
        final int ITEMS_PER_PAGE = 10;

        // Use the pagination utility to display roles
        CliUtil.displayPaginatedList(
            "All Roles", rolesDTOList, ITEMS_PER_PAGE, role -> role.getName(), scanner);

        // Display tip after exiting the paginated view
        CliUtil.printSectionWithIcon("Use 'View Role Details' to see permissions for each role","💡");
        CliUtil.waitForEnter(scanner);
    }

    /**
     * Displays detailed information about a specific role
     * 
     * @param roleName The name of the role to display
     */
    private void printRoleDetails(String roleName) {

        try {
            RoleDTO roleDTO = employeeService.getRoleDTO(roleName);

            if (roleDTO != null) {
                // Create headers for the table sections
                List<String> headers = Arrays.asList(
                    "ROLE DETAILS",
                    "PERMISSIONS"
                );

                // Create content for each section
                Map<String, List<String[]>> content = new HashMap<>();

                // Role Details section
                List<String[]> roleInfo = new ArrayList<>();
                roleInfo.add(new String[]{"Role Name:", roleDTO.getName()});
                content.put("ROLE DETAILS", roleInfo);

                // Permissions section
                List<String[]> permissionsInfo = new ArrayList<>();
                Set<String> permissions = roleDTO.getPermissions();

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
        // Convert array to list for pagination
        List<String> permissionsList = Arrays.asList(employeeService.getAllPermissions());

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
    }

    /**
     * Creates a new permission in the system
     */
    private void createPermission() {
        printSectionHeader("Create Permission");

        // Show permissions
        String[] existingPermissions = employeeService.getAllPermissions();
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
            String result = employeeService.createPermission(doneBy, permissionName);

            if (result.contains("successfully")) {
                printSuccess(result);
            } else {
                printError(result);
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
        String[] existingRoles = employeeService.getAllRoles();
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
            RoleDTO roleDTO = employeeService.getRoleDetailsAsDTO(existingRoleName);
            if (roleDTO == null) {
                printError("Source role '" + existingRoleName + "' not found.");
                CliUtil.waitForEnter(scanner);
                return;
            }

            // Show permissions that will be cloned
            Set<String> permissions = roleDTO.getPermissions();
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
            String result = employeeService.cloneRole(doneBy, existingRoleName, newRoleName);

            if (result.contains("successfully")) {
                printSuccess(result);
            } else {
                CliUtil.printOperationCancelled();
            }
        } else {
            CliUtil.printOperationCancelled();
        }

        CliUtil.waitForEnter(scanner);
    }
}
