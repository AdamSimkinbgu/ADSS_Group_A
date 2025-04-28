package PresentationLayer;

import DomainLayer.Employee;
import DomainLayer.Shift;
import DomainLayer.enums.ShiftType;
import ServiceLayer.EmployeeSL;
import ServiceLayer.EmployeeService;
import ServiceLayer.ShiftSL;
import ServiceLayer.ShiftService;
import ServiceLayer.exception.ServiceException;
import ServiceLayer.response.Response;
import ServiceLayer.util.JsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ShiftCLI {
    // ANSI color codes
    private static final String RESET = CliUtil.RESET;
    private static final String YELLOW = CliUtil.YELLOW;

    // Properties
    private final ShiftService shiftService;
    private final EmployeeService employeeService;
    private final Scanner scanner;
    private final long doneBy;
    private static final DateTimeFormatter dateFormatter = CliUtil.dateFormatter;

    /**
     * Constructor for ShiftCLI
     * @param shiftService ShiftService
     * @param employeeService EmployeeService
     * @param doneBy User ID
     */
    public ShiftCLI(ShiftService shiftService, EmployeeService employeeService, long doneBy) {
        this.shiftService = shiftService;
        this.employeeService = employeeService;
        this.scanner = new Scanner(System.in);
        this.doneBy = doneBy;
    }

    public void start() {
        printWelcomeBanner();
        boolean running = true;

        while (running) {
            int max = displayMenu();
            String choice = scanner.nextLine().trim();
            running = processMenuChoice(choice,max);
        }
    }

    private int displayMenu() {
        CliUtil.printEmptyLine();
        printSectionHeader("Main Menu");
        CliUtil.printBreadcrumb("Shift Management");

        List<String> menuOptions = new ArrayList<>();
        int optionNumber = 1;

        if (hasPermission("VIEW_SHIFT")) {
            menuOptions.add(CliUtil.YELLOW + optionNumber++ + CliUtil.RESET + ". View All Shifts");
            menuOptions.add(YELLOW + optionNumber++ + RESET + ". View Shift Details");
        }
        if (hasPermission("CREATE_SHIFT")) {
            menuOptions.add(CliUtil.YELLOW + optionNumber++ + CliUtil.RESET + ". Add Shift");
            menuOptions.add(CliUtil.YELLOW + optionNumber++ + CliUtil.RESET + ". Add Weekly Shifts");
        }
        if (hasPermission("EDIT_SHIFT")) {
            menuOptions.add(CliUtil.YELLOW + optionNumber++ + CliUtil.RESET + ". Edit Shifts");
        }
        if (hasPermission("REMOVE_SHIFT")) {
            menuOptions.add(CliUtil.YELLOW + optionNumber++ + CliUtil.RESET + ". Delete Shifts");
        }

        menuOptions.add(CliUtil.YELLOW + optionNumber + CliUtil.RESET + ". Back To Main Menu");

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

            // View All Shifts
            if (hasPermission("VIEW_SHIFT")) {
                if (choiceNum == currentOption++) {
                    viewAllShifts();
                    return true;
                }

                if (choiceNum == currentOption++) {
                    viewShiftDetails();
                    return true;
                }
            }

            // Create Shift options
            if (hasPermission("CREATE_SHIFT")) {
                if (choiceNum == currentOption++) {
                    createShift();
                    return true;
                }

                if (choiceNum == currentOption++) {
                    addWeeklyShifts();
                    return true;
                }
            }

            // Edit Shift
            if (hasPermission("EDIT_SHIFT")) {
                if (choiceNum == currentOption++) {
                    updateShift();
                    return true;
                }
            }

            // Delete Shift
            if (hasPermission("REMOVE_SHIFT")) {
                if (choiceNum == currentOption++) {
                    deleteShift();
                    return true;
                }
            }

            // Back to main menu
            if (choiceNum == currentOption) {
                CliUtil.printReturnMessage("main menu");
                return false;
            }

            printError("Invalid choice. Please try again.");
            return true;

        } catch (NumberFormatException e) {
            printError("Please enter a valid number.");
            return true;
        }
    }

    private void printWelcomeBanner() {
        CliUtil.printWelcomeBanner("SHIFT MANAGEMENT SYSTEM", LocalDate.now().toString(), "Employee #" + doneBy);
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

    /**
     * Formats employee display with name and number
     *
     * @param employeeId The ID of the employee
     * @return A formatted string with employee name and number
     */
    private String formatEmployeeDisplay(long employeeId) {
        try {
            String responseJson = employeeService.getEmployeeById(employeeId);
            Response<Employee> response = JsonUtil.fromJson(responseJson, new TypeReference<Response<Employee>>() {});

            if (response.isError()) {
                return "Employee #" + employeeId;
            }

            Employee employee = response.getValue();
            return employee.getFirstName() + " " + employee.getLastName() + " (#" + employeeId + ")";
        } catch (Exception e) {
            // If we can't get the employee name, just return the ID
            return "Employee #" + employeeId;
        }
    }

    /**
     * Utility method to select a shift by date and type
     * Shows available shifts on the selected date to help user make a selection
     *
     * @param headerText The header text to display
     * @param openOnly Whether to only show open shifts
     * @return The selected shift, or null if no shift was selected
     */
    private Shift selectShiftByDateAndType(String headerText, boolean openOnly) {
        printSectionHeader(headerText);

        // Get date from user
        LocalDate date = getDateInput("Select a date for the shift:");

        // Show available shifts on this date to help user make a selection
        try {
            String shiftsResponseJson = shiftService.getAllShiftsByDate(doneBy, date);
            Response<List<Shift>> shiftsResponse = JsonUtil.fromJson(shiftsResponseJson, new TypeReference<Response<List<Shift>>>() {});

            if (shiftsResponse.isError()) {
                printError("Error retrieving shifts: " + shiftsResponse.getErrorMessage());
                waitForEnter();
                return null;
            }

            List<Shift> shiftsOnDate = shiftsResponse.getValue();

            // Filter shifts if openOnly is true
            if (openOnly) {
                List<Shift> openShifts = new ArrayList<>();
                for (Shift s : shiftsOnDate) {
                    if (s.isOpen()) {
                        openShifts.add(s);
                    }
                }

                if (openShifts.isEmpty()) {
                    printError("No open shifts found for the selected date: " + date.format(dateFormatter));
                    waitForEnter();
                    return null;
                }

                shiftsOnDate = openShifts;
            }

            if (shiftsOnDate.isEmpty()) {
                printError("No shifts found for the selected date: " + date.format(dateFormatter));
                waitForEnter();
                return null;
            }

            CliUtil.printSectionWithIcon("Available shifts on " + date.format(dateFormatter) + ":", "📅");

            List<String> shiftItems = new ArrayList<>();
            for (Shift s : shiftsOnDate) {
                String status = s.isOpen() ? "(Open)" : "(Closed)";
                shiftItems.add(s.getShiftType() + " shift " + status + " (ID: " + s.getId() + ")");
            }
            CliUtil.printHierarchicalList(shiftItems, "• ", 2);
            CliUtil.printEmptyLine();
        } catch (Exception e) {
            // If there's an error getting shifts by date, continue with manual selection
            CliUtil.printError("Could not retrieve shifts for the selected date. Please select a shift type manually.");
        }

        // Get shift type from user
        ShiftType shiftType = getShiftTypeInput("Select the shift type:");

        try {
            // Get shift by date and type
            String shiftResponseJson = shiftService.getShift(doneBy, date, shiftType);
            Response<Shift> shiftResponse = JsonUtil.fromJson(shiftResponseJson, new TypeReference<Response<Shift>>() {});

            if (shiftResponse.isError()) {
                printError("Error retrieving shift: " + shiftResponse.getErrorMessage());
                waitForEnter();
                return null;
            }

            Shift shift = shiftResponse.getValue();

            // Check if shift is open if openOnly is true
            if (openOnly && !shift.isOpen()) {
                printError("The selected shift is closed for modifications.");
                waitForEnter();
                return null;
            }

            CliUtil.printSuccessWithCheckmark("Selected shift: " + date.format(dateFormatter) + " " + shiftType + " (ID: " + shift.getId() + ")");
            CliUtil.printEmptyLine();

            return shift;
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                printError("No " + shiftType + " shift found for date " + date.format(dateFormatter));
            } else {
                printError("Error retrieving shift: " + e.getMessage());
            }
            waitForEnter();
            return null;
        }
    }

    /**
     * Prints a section header with a border.
     *
     * @param title - The title of the section to be printed.
     */
    private void printSectionHeader(String title) {
        boolean isMainMenu = title.equalsIgnoreCase("Main Menu");
        CliUtil.printSectionHeader(title, isMainMenu, "SHIFTS");
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
     * Gets user confirmation for an action
     *
     * @param message The confirmation message to display
     * @return true if confirmed, false otherwise
     */
    private boolean confirm(String message) {
        return CliUtil.confirm(message, scanner);
    }

    /**
     * Prints an error message with formatting
     *
     * @param message The error message to display
     */
    private void printError(String message) {
        CliUtil.printError(message);
    }

    /**
     * Gets a date from user input using numbered options for common dates
     *
     * @param prompt The prompt to display
     * @return The LocalDate selected by the user
     */
    private LocalDate getDateInput(String prompt) {
        return CliUtil.getDateInput(prompt, scanner);
    }

    private String getHoursInput(String prompt) {
        return CliUtil.getHoursInput(prompt, scanner);
    }


    /**
     * Waits for the user to press Enter to continue
     */
    private void waitForEnter() {
        CliUtil.waitForEnter(scanner);
    }

    /**
     * Gets a shift type from user input using numbered options
     *
     * @param prompt The prompt to display
     * @return The ShiftType selected by the user
     */
    private ShiftType getShiftTypeInput(String prompt) {
        return CliUtil.getShiftTypeInput(prompt, scanner);
    }



    //===============================================================================
    // Shift Management Methods
    //===============================================================================

    /**
     * Displays all shifts in the system with pagination
     */
    private void viewAllShifts() {
        try {
            // Get all shifts from service
            String shiftsResponseJson = shiftService.getAllShifts(doneBy);
            Response<List<Shift>> shiftsResponse = JsonUtil.fromJson(shiftsResponseJson, new TypeReference<Response<List<Shift>>>() {});

            if (shiftsResponse.isError()) {
                printError("Error retrieving shifts: " + shiftsResponse.getErrorMessage());
                waitForEnter();
                return;
            }

            List<Shift> shiftList = shiftsResponse.getValue();

            if (shiftList.isEmpty()) {
                printError("No shifts found in the system.");
                waitForEnter();
                return;
            }

            // Define how many shifts to show per page
            final int ITEMS_PER_PAGE = 5;

            // Use the pagination utility to display shifts
            CliUtil.displayPaginatedList(
                    "All Shifts",
                    shiftList,
                    ITEMS_PER_PAGE,
                    shift -> {
                        // Format each shift for display
                        String openStatus = shift.isOpen() ? CliUtil.greenString("Open") : CliUtil.redString("Closed");

                        // Calculate total assigned vs required employees
                        int totalAssigned = 0;
                        int totalRequired = 0;

                        for (Map.Entry<String, Set<Long>> entry : shift.getAssignedEmployees().entrySet()) {
                            totalAssigned += entry.getValue().size();
                        }

                        for (Integer required : shift.getRolesRequired().values()) {
                            totalRequired += required;
                        }

                        String assignmentStatus = totalAssigned + "/" + totalRequired;

                        // Return formatted string representation of the shift
                        return String.format("ID: %s | Date: %s | Type: %s | Status: %s | Assigned/Required: %s",
                                shift.getId(),
                                shift.getShiftDate().toString(),
                                shift.getShiftType().toString(),
                                openStatus,
                                assignmentStatus);
                    },
                    scanner
            );
        } catch (Exception e) {
            printError("Error retrieving shifts: " + e.getMessage());
            waitForEnter();
        }
    }

    /**
     * Displays detailed information about a specific shift
     * User selects a shift by entering date and shift type
     */
    private void viewShiftDetails() {
        printSectionHeader("Shift Details");

        // Display breadcrumb navigation
        CliUtil.printBreadcrumb("Assignment Management > View Shift Details");

        // Get date from user
        LocalDate date = getDateInput("Select a date for the shift you want to view:");

        // Show available shifts on this date to help user make a selection
        try {
            String shiftsResponseJson = shiftService.getAllShiftsByDate(doneBy, date);
            Response<List<Shift>> shiftsResponse = JsonUtil.fromJson(shiftsResponseJson, new TypeReference<Response<List<Shift>>>() {});

            if (shiftsResponse.isError()) {
                printError("Error retrieving shifts: " + shiftsResponse.getErrorMessage());
                waitForEnter();
                return;
            }

            List<Shift> shiftsOnDate = shiftsResponse.getValue();

            if (shiftsOnDate.isEmpty()) {
                printError("No shifts found for the selected date: " + date.format(dateFormatter));
                waitForEnter();
                return;
            }

            CliUtil.printSectionWithIcon("Available shifts on " + date.format(dateFormatter) + ":", "📅");

            List<String> shiftItems = new ArrayList<>();
            for (Shift s : shiftsOnDate) {
                shiftItems.add(s.getShiftType() + " shift (ID: " + s.getId() + ")");
            }
            CliUtil.printHierarchicalList(shiftItems, "• ", 2);
            CliUtil.printEmptyLine();
        } catch (Exception e) {
            // If there's an error getting shifts by date, continue with manual selection
            CliUtil.printError("Could not retrieve shifts for the selected date. Please select a shift type manually.");
        }

        // Get shift type from user
        ShiftType shiftType = getShiftTypeInput("Select the shift type:");

        try {
            // Get shift by date and type
            String shiftResponseJson = shiftService.getShift(doneBy, date, shiftType);
            Response<Shift> shiftResponse = JsonUtil.fromJson(shiftResponseJson, new TypeReference<Response<Shift>>() {});

            if (shiftResponse.isError()) {
                printError("Error retrieving shift: " + shiftResponse.getErrorMessage());
                waitForEnter();
                return;
            }

            Shift shift = shiftResponse.getValue();

            CliUtil.printSuccessWithCheckmark("Found shift: " + date.format(dateFormatter) + " " + shiftType + " (ID: " + shift.getId() + ")");
            CliUtil.printEmptyLine();

            // Prepare data for the formatted table
            List<String> headers = new ArrayList<>();
            headers.add("SHIFT INFORMATION");
            headers.add("ROLES & ASSIGNMENTS");
            headers.add("AVAILABLE EMPLOYEES");

            Map<String, List<String[]>> content = new HashMap<>();
            Map<String, String> emptyMessages = new HashMap<>();

            // Shift information section
            List<String[]> shiftInfo = new ArrayList<>();
            shiftInfo.add(new String[]{"ID:", String.valueOf(shift.getId())});
            shiftInfo.add(new String[]{"Date:", shift.getShiftDate().toString()});
            shiftInfo.add(new String[]{"Type:", shift.getShiftType().toString()});
            shiftInfo.add(new String[]{"Hours:", shift.getHours()});

            String status = shift.isOpen()
                    ? CliUtil.greenString("Open")
                    : CliUtil.redString("Closed");
            shiftInfo.add(new String[]{"Status:", status});

            String managerStatus = shift.isAssignedShiftManager()
                    ? CliUtil.greenString("Assigned")
                    : CliUtil.redString("Not Assigned");
            shiftInfo.add(new String[]{"Shift Manager:", managerStatus});

            content.put("SHIFT INFORMATION", shiftInfo);

            // Roles & assignments section
            List<String[]> rolesInfo = new ArrayList<>();
            Map<String, Integer> rolesRequired = shift.getRolesRequired();
            Map<String, Set<Long>> assignedEmployees = shift.getAssignedEmployees();

            if (rolesRequired.isEmpty()) {
                emptyMessages.put("ROLES & ASSIGNMENTS", "No roles defined for this shift");
            } else {
                for (Map.Entry<String, Integer> entry : rolesRequired.entrySet()) {
                    String role = entry.getKey();
                    int required = entry.getValue();
                    Set<Long> assigned = assignedEmployees.getOrDefault(role, new HashSet<>());

                    rolesInfo.add(new String[]{role + ":", assigned.size() + "/" + required + " assigned"});

                    if (!assigned.isEmpty()) {
                        for (Long employeeId : assigned) {
                            rolesInfo.add(new String[]{"", "• " + formatEmployeeDisplay(employeeId)});
                        }
                    }
                }
            }
            content.put("ROLES & ASSIGNMENTS", rolesInfo);

            // Available employees section
            List<String[]> availableInfo = new ArrayList<>();
            Set<Long> availableEmployees = shift.getAvailableEmployees();

            if (availableEmployees.isEmpty()) {
                emptyMessages.put("AVAILABLE EMPLOYEES", "No employees available for this shift");
            } else {
                for (Long employeeId : availableEmployees) {
                    availableInfo.add(new String[]{"  • " + formatEmployeeDisplay(employeeId)});
                }
            }
            content.put("AVAILABLE EMPLOYEES", availableInfo);

            // Display the formatted table
            CliUtil.printFormattedTable(null, headers, content, emptyMessages);

        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                printError("No " + shiftType + " shift found for date " + date.format(dateFormatter));
                CliUtil.printTip("Check if the shift exists using 'View All Shifts' option first.");
            } else {
                printError("Error retrieving shift details: " + e.getMessage());
            }
        } catch (Exception e) {
            printError("Unexpected error: " + e.getMessage());
        }

        CliUtil.printReturnPrompt("Assignment Management Menu", scanner);
    }



    /**
     * Updates an existing shift
     */
    private void updateShift() {
        printSectionHeader("Update Shift");

        // Select a shift to update
        Shift shift = selectShiftByDateAndType("Select Shift to Update", false);
        if (shift == null) {
            return; // User cancelled or no shift found
        }

        try {
            // Display current shift details
            List<String[]> table = new ArrayList<>();
            table.add(new String[]{"ID", String.valueOf(shift.getId())});
            table.add(new String[]{"Date", shift.getShiftDate().toString()});
            table.add(new String[]{"Type", shift.getShiftType().toString()});
            table.add(new String[]{"Hours", shift.getHours()});

            String openStatus = shift.isOpen() 
                ? CliUtil.greenString("Open") 
                : CliUtil.redString("Closed");
            table.add(new String[]{"Status", openStatus});



            String managerStatus = shift.isAssignedShiftManager() 
                ? CliUtil.greenString("Assigned") 
                : CliUtil.redString("Not Assigned");
            table.add(new String[]{"Shift Manager", managerStatus});

            CliUtil.printFormattedTable("Current Shift Details", List.of("Shift Data"), Map.of("Shift Data", table), Map.of());
            CliUtil.printEmptyLine();

            // Get updated values
            boolean isOpen = shift.isOpen();
            boolean hasManager = shift.isAssignedShiftManager();
            ShiftType shiftType = shift.getShiftType();
            LocalDate date = shift.getShiftDate();
            String hours = shift.getHours();

            // Option to change shift type
            if (confirm("Do you want to change the shift type?")) {
                CliUtil.printInfo("Current type: " + shiftType);
                shiftType = getShiftTypeInput("Select new shift type:");
            }

            // Option to change date
            if (confirm("Do you want to change the shift date?")) {
                CliUtil.printInfo("Current date: " + date.format(dateFormatter));
                date = getDateInput("Select new date:");
            }

            if (confirm("Do you want to change the shift hours?")) {
                CliUtil.printInfo("Current hours: " + shift.getHours());
                String newHours = scanner.nextLine().trim();
                hours = getHoursInput("Select new hours:");
            }

            // Option to change open status
            if (confirm("Do you want to change open status?")) {
                CliUtil.printInfo("Current status: " + (isOpen ? "Open" : "Closed"));
                isOpen = confirm("Mark shift as OPEN?");
            }

            // Option to change manager status
            if (confirm("Do you want to change manager status?")) {
                CliUtil.printInfo("Current manager status: " + (hasManager ? "Assigned" : "Not Assigned"));
                hasManager = confirm("Is a shift manager assigned?");
            }

            // Option to update roles required
            if (confirm("Do you want to update roles required?")) {
                CliUtil.printSectionWithIcon("CURRENT ROLES REQUIRED", "👥");
                Map<String, Integer> rolesRequired = shift.getRolesRequired();

                if (rolesRequired.isEmpty()) {
                    CliUtil.printInfo("  No roles defined for this shift");
                } else {
                    for (Map.Entry<String, Integer> entry : rolesRequired.entrySet()) {
                        CliUtil.print("  • " + entry.getKey() + ": " + entry.getValue());
                    }
                }

                CliUtil.printEmptyLine();
                CliUtil.printSectionWithIcon("UPDATE ROLES REQUIRED", "📝");

                Map<String, Integer> updatedRoles = new HashMap<>(rolesRequired);
                for (String role : updatedRoles.keySet()) {
                    int current = updatedRoles.get(role);
                    CliUtil.printPrompt(role + " (current: " + current + "): ");
                    String input = scanner.nextLine().trim();

                    if (!input.isEmpty()) {
                        try {
                            int newValue = Integer.parseInt(input);
                            updatedRoles.put(role, newValue);
                            CliUtil.printSuccessWithCheckmark("Updated " + role + " to " + newValue);
                        } catch (NumberFormatException e) {
                            printError("Invalid input for " + role + ". Must be a number.");
                        }
                    }
                }

                // Apply role updates
                if (confirm("Apply these role changes?")) {
                    for (Map.Entry<String, Integer> entry : updatedRoles.entrySet()) {
                        try {
                            shiftService.updateRolesRequired(doneBy, shift.getId(), entry.getKey(), entry.getValue());
                        } catch (Exception e) {
                            printError("Failed to update role " + entry.getKey() + ": " + e.getMessage());
                        }
                    }
                    printSuccess("Roles updated successfully");
                } else {
                    CliUtil.printOperationCancelled();
                }
            }

            // Confirm all changes
            CliUtil.printEmptyLine();
            if (confirm("Apply all changes to this shift?")) {
                String result = shiftService.updateShift(doneBy, shift.getId(), shiftType, date, isOpen, hasManager, hours,LocalDate.now());
                if (result.contains("successfully")) {
                    printSuccess(result);
                } else {
                    printError(result);
                }
            } else {
                CliUtil.printOperationCancelled();
            }

        } catch (Exception e) {
            printError("Error updating shift: " + e.getMessage());
        }

        waitForEnter();
    }

    /**
     * Deletes an existing shift
     */
    private void deleteShift() {
        printSectionHeader("Delete Shift");

        // Select a shift to delete
        Shift shift = selectShiftByDateAndType("Select Shift to Delete", false);
        if (shift == null) {
            return; // User cancelled or no shift found
        }

        try {
            // Display shift details for confirmation
            List<String[]> table = new ArrayList<>();
            table.add(new String[]{"ID", String.valueOf(shift.getId())});
            table.add(new String[]{"Date", shift.getShiftDate().toString()});
            table.add(new String[]{"Type", shift.getShiftType().toString()});

            String openStatus = shift.isOpen() 
                ? CliUtil.greenString("Open") 
                : CliUtil.redString("Closed");
            table.add(new String[]{"Status", openStatus});

            // Check if shift has assigned employees
            boolean hasAssignedEmployees = false;
            for (Set<Long> employees : shift.getAssignedEmployees().values()) {
                if (!employees.isEmpty()) {
                    hasAssignedEmployees = true;
                    break;
                }
            }

            if (hasAssignedEmployees) {
                table.add(new String[]{"Warning", CliUtil.redString("This shift has assigned employees!")});
            }

            CliUtil.printFormattedTable("Shift To Delete", List.of("Shift Details"), Map.of("Shift Details", table), Map.of());
            CliUtil.printEmptyLine();

            // Double confirmation for shifts with assigned employees
            if (hasAssignedEmployees) {
                CliUtil.printWarning("This shift has assigned employees. Deleting it will remove all assignments.");
                if (!confirm("Are you ABSOLUTELY SURE you want to delete this shift?")) {
                    CliUtil.printOperationCancelled();
                    waitForEnter();
                    return;
                }
            }

            // Final confirmation
            if (confirm("Are you sure you want to delete this shift?")) {
                String result = shiftService.removeShift(doneBy, shift.getShiftDate(), shift.getShiftType());
                if (result.contains("successfully")) {
                    printSuccess(result);
                } else {
                    printError(result);
                }
            } else {
                CliUtil.printOperationCancelled();
            }

        } catch (Exception e) {
            printError("Error deleting shift: " + e.getMessage());
        }

        waitForEnter();
    }

    /**
     * Creates a new shift
     */
    private void createShift() {
        printSectionHeader("Create Shift");

        try {
            // Get basic shift information
            CliUtil.printSectionWithIcon("BASIC SHIFT INFORMATION", "📅");

            LocalDate date = getDateInput("Enter shift date:");
            ShiftType shiftType = getShiftTypeInput("Enter shift type:");

            // Check if shift already exists
            try {
                String shiftResponseJson = shiftService.getShift(doneBy, date, shiftType);
                Response<Shift> shiftResponse = JsonUtil.fromJson(shiftResponseJson, new TypeReference<Response<Shift>>() {});

                if (!shiftResponse.isError()) {
                    printError("A " + shiftType + " shift already exists for " + date.format(dateFormatter));
                    CliUtil.printTip("You can edit the existing shift using the 'Edit Shifts' option.");
                    waitForEnter();
                    return;
                }
            } catch (Exception e) {
                // No shift exists, continue with creation
            }

            // Get shift hours
            String hours = getHoursInput("Enter shift hours (e.g., 9:00-17:00):");


            // Get roles required
            CliUtil.printEmptyLine();
            CliUtil.printSectionWithIcon("ROLES REQUIRED", "👥");
            CliUtil.printInfo("Enter the number of employees required for each role (0 for none)");
            CliUtil.printEmptyLine();

            Map<String, Integer> rolesRequired = new HashMap<>();
            String rolesResponseJson = shiftService.getRoles(doneBy);
            Response<List<String>> rolesResponse = JsonUtil.fromJson(rolesResponseJson, new TypeReference<Response<List<String>>>() {});

            if (rolesResponse.isError()) {
                printError("Error retrieving roles: " + rolesResponse.getErrorMessage());
                waitForEnter();
                return;
            }

            List<String> rolesList = rolesResponse.getValue();
            Set<String> roles = new HashSet<>(rolesList);

            if (roles.isEmpty()) {
                printError("No roles defined in the system. Please create roles first.");
                waitForEnter();
                return;
            }

            for (String role : roles) {
                CliUtil.printPrompt("Required number for '" + role + "': ");
                try {
                    int count = Integer.parseInt(scanner.nextLine().trim());
                    if (count > 0) {
                        rolesRequired.put(role, count);
                        CliUtil.printSuccessWithCheckmark("Added: " + role + " x" + count);
                    }
                } catch (NumberFormatException e) {
                    printError("Invalid number for " + role + ". Must be a number.");
                    CliUtil.printPrompt("Required number for '" + role + "': ");
                    try {
                        int count = Integer.parseInt(scanner.nextLine().trim());
                        if (count > 0) {
                            rolesRequired.put(role, count);
                            CliUtil.printSuccessWithCheckmark("Added: " + role + " x" + count);
                        }
                    } catch (NumberFormatException ex) {
                        CliUtil.printWarning("Invalid number again, skipping " + role);
                    }
                }
            }

            if (rolesRequired.isEmpty()) {
                CliUtil.printWarning("No roles were added to this shift.");
                if (!confirm("Continue creating shift with no roles?")) {
                    CliUtil.printOperationCancelled();
                    waitForEnter();
                    return;
                }
            }

            // Get shift settings
            CliUtil.printEmptyLine();
            CliUtil.printSectionWithIcon("SHIFT SETTINGS", "⚙️");

            boolean isManagerShift = confirm("Is this a manager shift?");
            boolean isOpen = confirm("Is the shift open for assignments?");

            // Initialize empty collections for new shift
            Map<String, Set<Long>> assignedEmployees = new HashMap<>();
            Set<Long> availableEmployees = new HashSet<>();

            // Confirm creation
            CliUtil.printEmptyLine();
            if (confirm("Create this shift?")) {
                String result = shiftService.createShift(
                    doneBy, 
                    shiftType, 
                    date, 
                    rolesRequired, 
                    assignedEmployees, 
                    availableEmployees, 
                    isManagerShift, 
                    isOpen,
                    hours,
                    LocalDate.now()
                );
                if (result.contains("successfully")) {
                    printSuccess(result);
                } else {
                    printError(result);
                }
            } else {
                CliUtil.printOperationCancelled();
            }

        } catch (Exception e) {
            printError("Error creating shift: " + e.getMessage());
        }

        waitForEnter();
    }

    /**
     * Creates shifts for an entire week
     */
    private void addWeeklyShifts() {
        printSectionHeader("Add Weekly Shifts");

        try {
            // Get start date for the week
            CliUtil.printSectionWithIcon("WEEKLY SHIFT CREATION", "📅");
            CliUtil.printInfo("This will create morning and evening shifts for an entire week");
            CliUtil.printEmptyLine();

            LocalDate startDate = getDateInput("Enter the first day of the week:");

            // Get roles required
            CliUtil.printEmptyLine();
            CliUtil.printSectionWithIcon("DEFAULT ROLES REQUIRED", "👥");
            CliUtil.printInfo("Enter the default number of employees required for each role");
            CliUtil.printInfo("These values will be used for all shifts in the week");
            CliUtil.printEmptyLine();

            Map<String, Integer> rolesRequired = new HashMap<>();
            String rolesResponseJson = shiftService.getRoles(doneBy);
            Response<List<String>> rolesResponse = JsonUtil.fromJson(rolesResponseJson, new TypeReference<Response<List<String>>>() {});

            if (rolesResponse.isError()) {
                printError("Error retrieving roles: " + rolesResponse.getErrorMessage());
                waitForEnter();
                return;
            }

            List<String> rolesList = rolesResponse.getValue();
            Set<String> roles = new HashSet<>(rolesList);

            if (roles.isEmpty()) {
                printError("No roles defined in the system. Please create roles first.");
                waitForEnter();
                return;
            }

            for (String role : roles) {
                CliUtil.printPrompt("Default number for '" + role + "': ");
                try {
                    int number = Integer.parseInt(scanner.nextLine().trim());
                    if (number > 0) {
                        rolesRequired.put(role, number);
                        CliUtil.printSuccessWithCheckmark("Added: " + role + " x" + number);
                    }
                } catch (NumberFormatException e) {
                    printError("Invalid number for " + role + ". Must be a number.");
                    CliUtil.printPrompt("Default number for '" + role + "': ");
                    try {
                        int number = Integer.parseInt(scanner.nextLine().trim());
                        if (number > 0) {
                            rolesRequired.put(role, number);
                            CliUtil.printSuccessWithCheckmark("Added: " + role + " x" + number);
                        }
                    } catch (NumberFormatException ex) {
                        CliUtil.printWarning("Invalid number again, skipping " + role);
                    }
                }
            }

            if (rolesRequired.isEmpty()) {
                CliUtil.printWarning("No roles were added to the shifts.");
                if (!confirm("Continue creating weekly shifts with no roles?")) {
                    CliUtil.printOperationCancelled();
                    waitForEnter();
                    return;
                }
            }

            // Confirm creation
            CliUtil.printEmptyLine();
            CliUtil.printInfo("This will create 14 shifts (morning and evening) for the week starting " + 
                startDate.format(dateFormatter));

            if (confirm("Create these shifts?")) {
                String result = shiftService.createWeeklyShifts(doneBy, startDate, rolesRequired);
                if (result.contains("successfully")) {
                    printSuccess(result);
                } else {
                    printError(result);
                }
            } else {
                CliUtil.printOperationCancelled();
            }

        } catch (Exception e) {
            printError("Error creating weekly shifts: " + e.getMessage());
        }

        waitForEnter();
    }
}
