package PresentationLayer.EmployeeSubModule.CLI;

import DTOs.ShiftDTO;
import Util.CliUtil;
import ServiceLayer.EmployeeSubModule.ShiftService;
import ServiceLayer.EmployeeSubModule.EmployeeService;
import DTOs.EmployeeDTO;
import ServiceLayer.exception.AuthorizationException;
import ServiceLayer.exception.ServiceException;
import DomainLayer.enums.ShiftType;
import Util.Week;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class AssignmentCLI {

    // Properties
    private final ShiftService shiftService;
    private final EmployeeService employeeService;
    private final Scanner scanner;
    private final long doneBy;
    private static final DateTimeFormatter dateFormatter = CliUtil.dateFormatter;

    /**
     * Constructor for the Assignment Command Line Interface
     * 
     * @param shiftService The service layer for shift operations
     * @param employeeService The service layer for employee operations
     * @param doneBy The ID of the employee using the CLI
     */
    public AssignmentCLI(ShiftService shiftService, EmployeeService employeeService, long doneBy) {
        this.shiftService = shiftService;
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
            displayMenu();
            String choice = scanner.nextLine();
            running = processMenuChoice(choice);
        }
    }

    //==========================================================================================
    // MENU SYSTEM METHODS
    //==========================================================================================

    /**
     * Displays the main menu with options based on user permissions
     */
    private void displayMenu() {
        CliUtil.printEmptyLine();
        printSectionHeader("Assignment Management Menu");

        // Display breadcrumb navigation
        CliUtil.printBreadcrumb("Assignment Management");

        List<String> menuOptions = new ArrayList<>();
        int optionNumber = 1;

        if (hasPermission("ASSIGN_EMPLOYEE")) {
            menuOptions.add(CliUtil.YELLOW + optionNumber++ + CliUtil.RESET + ". Assign Employees to a Shift");
            menuOptions.add(CliUtil.YELLOW + optionNumber++ + CliUtil.RESET + ". Remove Employee from Shift");
            menuOptions.add(CliUtil.YELLOW + optionNumber++ + CliUtil.RESET + ". Back to Main Menu");
        }

        // Add indentation to menu options
        List<String> formattedOptions = new ArrayList<>();
        for (String option : menuOptions) {
            formattedOptions.add("  " + option);
        }

        // Print the menu options
        for (String option : formattedOptions) {
            CliUtil.print(option);
        }

        CliUtil.printEmptyLine();
        CliUtil.printPrompt("Enter your choice: ");
    }

    private boolean processMenuChoice(String choice) {
        CliUtil.printEmptyLine();

        try {
            int choiceNum = Integer.parseInt(choice);
            int currentOption = 1;

            if (choiceNum == currentOption++) {
                assignEmployeeToShift();
                return true;
            }

            if (choiceNum == currentOption++) {
                removeEmployeeFromShift();
                return true;
            }

            if (choiceNum == currentOption++) {
                printSuccess("Returning to Main Menu...");
                return false;
            }

            printError("Invalid choice. Please enter a number between 1 and 3.");
            return true;

        } catch (NumberFormatException e) {
            printError("Please enter a valid number.");
            return true;
        }
    }


    //==========================================================================================
    // UI UTILITY METHODS
    //==========================================================================================

    /**
     * Formats employee display with name, number, and branch
     *
     * @param employeeId The ID of the employee
     * @return A formatted string with employee name, number, and branch
     */
    private String formatEmployeeDisplay(long employeeId) {
        try {
            EmployeeDTO employee = employeeService.getEmployeeByIdAsDTO(employeeId);
            String branch = employeeService.getEmployeeBranchName(employeeId);
            return employee.getFullName() + " (#" + employeeId + ")" + branch;
        } catch (ServiceException e) {
            // If we can't get the employee name, just return the ID
            return "Employee #" + employeeId;
        }
    }


    /**
     * Formats employee display with name, number, branch, and roles to show availability
     *
     * @param employeeId The ID of the employee
     * @return A formatted string with the employee name, number, branch, and roles
     */
    private String formatUnassignedEmployeeDisplay(long employeeId) {
        try {
            EmployeeDTO employee = employeeService.getEmployeeByIdAsDTO(employeeId);
            String branch = employeeService.getEmployeeBranchName(employeeId);
            return employee.getFullName() + " (#" + employeeId + ") " + branch + " " + employee.getRoles().toString() + CliUtil.RESET;

        } catch (ServiceException e) {
            // If we can't get the employee name, just return the ID
            return "Employee #" + employeeId;
        }
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
     * Gets a date from user input using numbered options for common dates
     * 
     * @param prompt The prompt to display
     * @return The LocalDate selected by the user
     */
    private LocalDate getDateInput(String prompt) {
        return CliUtil.getDateInput(prompt, scanner);
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

    /**
     * Utility method to select a shift by date and type
     * Shows available shifts on the selected date to help the user make a selection
     * If the user has VIEW_SHIFT permission, shows all shifts
     * Otherwise, shows only shifts for the user's branch
     * 
     * @param headerText The header text to display
     * @param openOnly Whether to only show open shifts
     * @return The selected shift, or null if no shift was selected
     */
    private ShiftDTO selectShiftByDateAndType(String headerText, boolean openOnly) {
        printSectionHeader(headerText);

        // Get user's branch ID if they don't have management permission
        long userBranchId = -1;
        boolean isManagement = hasPermission("VIEW_SHIFT");

        if (!isManagement) {
            try {
                EmployeeDTO employee = employeeService.getEmployeeByIdAsDTO(doneBy);
                userBranchId = employee.getBranchId();
            } catch (Exception e) {
                printError("Error retrieving user branch: " + e.getMessage());
                waitForEnter();
                return null;
            }
        }

        // Get date from user
        LocalDate date = getDateInput("Select a date for the shift:");

        // Show available shifts on this date to help user make a selection
        try {
            List<ShiftDTO> shiftsList;

            if (isManagement) {
                // Management user can see all shifts
                String shifts = shiftService.getAllShiftsByDate(doneBy, date);
                shiftsList = ShiftDTO.deserializeList(shifts);
            } else {
                // Regular user can only see shifts for their branch
                String shifts = shiftService.getAllShiftsByDateBranch(doneBy, date, userBranchId);
                shiftsList = ShiftDTO.deserializeList(shifts);
            }

            ShiftDTO[] shiftsOnDate = shiftsList.toArray(new ShiftDTO[0]);

            // Filter shifts if openOnly is true
            if (openOnly) {
                List<ShiftDTO> openShifts = new ArrayList<>();
                for (ShiftDTO s : shiftsOnDate) {
                    if (s.isOpen()) {
                        openShifts.add(s);
                    }
                }

                if (openShifts.isEmpty()) {
                    printError("No open shifts found for the selected date: " + date.format(dateFormatter));
                    waitForEnter();
                    return null;
                }

                shiftsOnDate = openShifts.toArray(new ShiftDTO[0]);
            }

            if (shiftsOnDate.length == 0) {
                printError("No shifts found for the selected date: " + date.format(dateFormatter));
                waitForEnter();
                return null;
            }

            CliUtil.printSectionWithIcon("Available shifts on " + date.format(dateFormatter) + ":", "📅");

            List<String> shiftItems = new ArrayList<>();
            for (ShiftDTO s : shiftsOnDate) {
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
            ShiftDTO shift;

            if (isManagement) {
                // Management user can access all shifts
                shift = ShiftDTO.deserialize(shiftService.getShift(doneBy, date, shiftType));
            } else {
                // Regular user can only access shifts for their branch
                shift = ShiftDTO.deserialize(shiftService.getShiftByBranch(doneBy, date, shiftType, userBranchId));
            }

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
     * Checks if the current user has a specific permission
     * 
     * @param permission The permission to check
     * @return true if the user has the permission, false otherwise
     */
    private boolean hasPermission(String permission) {
        try {
            employeeService.isEmployeeAuthorised(doneBy, permission);
            return true;
        } catch (AuthorizationException e) {
            return false;
        } catch (Exception e) {
            CliUtil.printError("Error checking permissions: " + e.getMessage());
            return false;
        }
    }

    /**
     * Prints a welcome banner for the CLI
     */
    private void printWelcomeBanner() {
        CliUtil.printWelcomeBanner("ASSIGNMENT MANAGEMENT SYSTEM", LocalDate.now().toString(), formatEmployeeDisplay(doneBy));
    }

    /**
     * Prints a section header with a border.
     *
     * @param title - The title of the section to be printed.
     */
    private void printSectionHeader(String title) {
        boolean isMainMenu = title.equalsIgnoreCase("Assignment Management Menu");
        CliUtil.printSectionHeader(title, isMainMenu, "ASSIGNMENT");
    }

    //==========================================================================================
    // ASSIGNMENT MANAGEMENT METHODS
    //==========================================================================================

    private void assignFromUpcomingWeek() {
        LocalDate today = LocalDate.now();
        LocalDate nextSunday = Week.getNextSunday(LocalDate.now());

        List<ShiftDTO> shifts = getShiftsInRange(nextSunday, nextSunday.plusDays(4));
        if (shifts.isEmpty()) {
            printError("No open shifts found for the upcoming week (Sunday-Thursday).");
            waitForEnter();
            return;
        }

        ShiftDTO selectedShift = selectShiftFromList(shifts);
        if (selectedShift != null) {
            assignEmployeesToSpecificShift(selectedShift);
        }
    }

    private void assignBySpecificDate() {
        LocalDate date = CliUtil.getDateInput("Enter the specific date:", scanner);
        List<ShiftDTO> shifts = getShiftsInRange(date, date);

        if (shifts.isEmpty()) {
            printError("No open shifts found on selected date.");
            CliUtil.waitForEnter(scanner);
            return;
        }

        ShiftDTO selectedShift = selectShiftFromList(shifts);
        if (selectedShift != null) {
            assignEmployeesToSpecificShift(selectedShift);
        }
    }

    private void assignFromPreviousWeeks() {
        LocalDate date = CliUtil.getDateInput("Enter a date from the past:", scanner);
        LocalDate weekStart = date.with(DayOfWeek.SUNDAY);
        List<ShiftDTO> shifts = getShiftsInRange(weekStart, weekStart.plusDays(6));

        if (shifts.isEmpty()) {
            CliUtil.printError("No open shifts found for that week.");
            CliUtil.waitForEnter(scanner);
            return;
        }

        ShiftDTO selectedShift = selectShiftFromList(shifts);
        if (selectedShift != null) {
            assignEmployeesToSpecificShift(selectedShift);
        }
    }

    private List<ShiftDTO> getShiftsInRange(LocalDate start, LocalDate end) {
        List<ShiftDTO> shifts = new ArrayList<>();
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            try {
                ShiftDTO[] dayShifts = ShiftDTO.deserializeList(shiftService.getAllShiftsByDate(doneBy, date)).toArray(new ShiftDTO[0]);
                for (ShiftDTO shift : dayShifts) {
                    if (shift.isOpen()) {
                        shifts.add(shift);
                    }
                }
            } catch (ServiceException ignored) {}
        }
        shifts.sort(Comparator.comparing(ShiftDTO::getShiftDate).thenComparing(ShiftDTO::getShiftType));
        return shifts;
    }

    private ShiftDTO selectShiftFromList(List<ShiftDTO> shifts) {
        CliUtil.printOptionsHeader("Select a shift:");
        CliUtil.printNumberedListWithInfo(shifts, shift ->
                String.format("%s %s (ID: %d)", shift.getShiftDate(), shift.getShiftType(), shift.getId()), 1);

        int choice = CliUtil.getMenuChoice("Enter your choice (1-" + shifts.size() + "): ", 1, shifts.size(), scanner);
        return shifts.get(choice - 1);
    }

    private void assignEmployeesToSpecificShift(ShiftDTO shift) {
        CliUtil.printEmptyLine();
        printSectionHeader("Assign Employees to Shift");

        try {
            Set<Long> availableEmployees = shift.getAvailableEmployees();
            EmployeeDTO[] allEmployees = employeeService.getAllEmployeesAsDTO();

            CliUtil.printSectionWithIcon("Available Employees:", "👥");
            for (EmployeeDTO emp : allEmployees) {
                boolean isAvailable = availableEmployees.contains(emp.getIsraeliId());
                String availability = isAvailable ? CliUtil.greenString("[Available]") : CliUtil.redString("[Unavailable]");
                String roles = String.join(", ", emp.getRoles());
                CliUtil.print(String.format("  • %s (ID: %d) %s | Roles: %s", emp.getFullName(), emp.getIsraeliId(), availability, roles));
            }

            CliUtil.printInfo("Assignment functionality coming next... (extend here)");
            CliUtil.waitForEnter(scanner);

        } catch (ServiceException e) {
            printError("Error retrieving employees: " + e.getMessage());
            CliUtil.waitForEnter(scanner);
        }
    }





//==========================================================================================
    /**
     * Displays the shift management submenu and processes user choices
     */
    private void shiftManagement() {
        boolean stayInMenu = true;

        while (stayInMenu) {
            printSectionHeader("Shift Management");

            // Display breadcrumb navigation
            CliUtil.printBreadcrumb("Assignment Management > Shift Management");

            CliUtil.printOptionsHeader("Select a shift management option:");
            List<String> shiftOptions = new ArrayList<>();
            shiftOptions.add("Assign Employees to Shift");
            shiftOptions.add("Remove Employee from Shift");
            shiftOptions.add("Modify Required Roles");
            shiftOptions.add("Create Full Week of Shifts (Sunday-Saturday)");
            shiftOptions.add("Back to Assignment Management Menu");
            CliUtil.printNumberedList(shiftOptions, 1);

            CliUtil.printEmptyLine();

            int choice = CliUtil.getMenuChoice("Enter your choice (1-5): ", 1, 5, scanner);

            switch (choice) {
                case 1:
                    assignEmployeeToShift();
                    break;
                case 2:
                    removeEmployeeFromShift();
                    break;
                case 3:
                    modifyRequiredRoles();
                    break;
                case 4:
                    createFullWeekShifts();
                    break;
                case 5:
                    CliUtil.printReturnMessage("Assignment Management Menu");
                    stayInMenu = false;
                    break;
            }

            // If we're staying in the menu and just completed an operation, wait for user input
            if (stayInMenu) {
                CliUtil.printEmptyLine();
                CliUtil.printInfo("Press Enter to continue or type 'back' to return to Assignment Management Menu...");
                String input = scanner.nextLine().trim().toLowerCase();
                if (input.equals("back")) {
                    stayInMenu = false;
                }
            }
        }
    }

    /**
     * Creates a full week of shifts from Sunday to Saturday
     */
    private void createFullWeekShifts() {
        printSectionHeader("Create Full Week of Shifts");

        if (!hasPermission("CREATE_SHIFT")) {
            printError("You don't have permission to create shifts.");
            waitForEnter();
            return;
        }

        // start date (must be a Sunday)
        LocalDate startDate = null;
        boolean validStartDate = false;

        while (!validStartDate) {
            startDate = getDateInput("Select the Sunday start date for the week:");

            if (startDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
                printError("The start date must be a Sunday. Selected date is " + startDate.getDayOfWeek() + ".");
                CliUtil.print("Please select a Sunday date.");
            } else {
                validStartDate = true;
            }
        }

        // Get roles required
        Map<String, Integer> rolesRequired = new HashMap<>();
        rolesRequired.put("Shift Manager", 1);
        boolean addingRoles = true;

        CliUtil.printSectionWithIcon("Define the roles required for all shifts in this week:", "📋");

        while (addingRoles) {
            // Display current roles
            if (!rolesRequired.isEmpty()) {
                CliUtil.printEmptyLine();
                CliUtil.printSectionWithIcon("Current roles defined:", "👥");
                Map<String, int[]> roleAssignments = new HashMap<>();
                for (Map.Entry<String, Integer> entry : rolesRequired.entrySet()) {
                    roleAssignments.put(entry.getKey(), new int[]{0, entry.getValue()});
                }
                CliUtil.printRoleAssignments(roleAssignments);
            }

            // Get available roles
            Set<String> allRoles;
            try {
                allRoles = Arrays.stream(shiftService.getRoles(doneBy).split(", ")).collect(Collectors.toSet());
            } catch (Exception e) {
                printError("Error retrieving available roles: " + e.getMessage());
                waitForEnter();
                return;
            }

            // Filter out roles already added
            List<String> availableRoles = new ArrayList<>();
            for (String role : allRoles) {
                if (!rolesRequired.containsKey(role)) {
                    availableRoles.add(role);
                }
            }

            CliUtil.printEmptyLine();
            CliUtil.printOptionsHeader("What would you like to do?");
            List<String> roleOptions = new ArrayList<>();
            roleOptions.add("Add a role");
            roleOptions.add("Finish adding roles and create shifts");
            CliUtil.printNumberedList(roleOptions, 1);

            if (rolesRequired.isEmpty()) {
                CliUtil.printWarning("You must add at least one role before creating shifts.");
            }

            int choice = CliUtil.getMenuChoice("Enter your choice (1-2): ", 1, 2, scanner);

            if (choice == 2) {
                if (rolesRequired.isEmpty()) {
                    printError("You must add at least one role before creating shifts.");
                    continue;
                }
                addingRoles = false;
                continue;
            }

            // No more roles to add
            if (availableRoles.isEmpty()) {
                printError("No more roles available to add.");
                if (!rolesRequired.isEmpty()) {
                    addingRoles = false;
                }
                continue;
            }

            // Display available roles
            CliUtil.printEmptyLine();
            CliUtil.printOptionsHeader("Available roles:");
            CliUtil.printNumberedList(availableRoles, 1);

            int roleChoice = CliUtil.getMenuChoice("Enter your choice (1-" + availableRoles.size() + "): ", 1, availableRoles.size(), scanner);

            // Get the selected role
            String selectedRole = availableRoles.get(roleChoice - 1);

            // Get the number of employees required for this role
            int requiredCount = CliUtil.getPositiveIntInput("Enter number of employees required for " + selectedRole + ": ", scanner);

            // Add the role to the map
            rolesRequired.put(selectedRole, requiredCount);
            CliUtil.printSuccessWithCheckmark("Role '" + selectedRole + "' added with " + requiredCount + " required employees.");
        }

        // Confirm creation
        CliUtil.printEmptyLine();
        CliUtil.printSectionWithIcon("Summary of shifts to be created:", "📅");

        List<String> summaryItems = new ArrayList<>();
        summaryItems.add("Week starting: " + startDate.format(dateFormatter) + " (Sunday)");
        summaryItems.add("Week ending: " + startDate.plusDays(6).format(dateFormatter) + " (Saturday)");
        summaryItems.add("Morning shifts: Sunday through Saturday");
        summaryItems.add("Evening shifts: Sunday through Thursday and Saturday (no Friday evening)");
        summaryItems.add("Total shifts: 13 (7 morning shifts, 6 evening shifts)");
        CliUtil.printHierarchicalList(summaryItems, "• ", 0);

        CliUtil.printEmptyLine();
        CliUtil.printSectionWithIcon("Roles required for each shift:", "👥");

        Map<String, int[]> roleAssignments = new HashMap<>();
        for (Map.Entry<String, Integer> entry : rolesRequired.entrySet()) {
            roleAssignments.put(entry.getKey(), new int[]{0, entry.getValue()});
        }
        CliUtil.printRoleAssignments(roleAssignments);

        if (!confirm("Do you want to create these shifts?")) {
            CliUtil.printOperationCancelled();
            waitForEnter();
            return;
        }

        // Create the shifts
        try {
            String result = shiftService.createWeeklyShifts(doneBy, startDate, rolesRequired);

            if (result.startsWith("Error:")) {
                printError(result);
            } else {
                printSuccess(result);
                CliUtil.printSuccessWithCheckmark("Created 13 shifts for the week of " + startDate.format(dateFormatter) + ".");
            }
        } catch (Exception e) {
            printError("Error creating shifts: " + e.getMessage());
        }

        waitForEnter();
    }

    /**
     * Displays all shifts in the system with pagination
     * If the user has VIEW_SHIFT permission, shows all shifts
     * Otherwise, shows only shifts for the user's branch
     */
    private void viewAllShifts() {
        List<ShiftDTO> shiftList;

        // Check if user has permission to view all shifts
        if (hasPermission("VIEW_SHIFT")) {
            // Management user can see all shifts
            shiftList = Arrays.asList(ShiftDTO.deserialize(shiftService.getAllShifts(doneBy)));
        } else {
            // Regular user can only see shifts for their branch
            try {
                // Get employee's branch ID
                EmployeeDTO employee = employeeService.getEmployeeByIdAsDTO(doneBy);
                long branchId = employee.getBranchId();

                // Get shifts for this branch
                shiftList = ShiftDTO.deserializeList(shiftService.getAllShiftsByBranch(doneBy, branchId));
            } catch (Exception e) {
                printError("Error retrieving shifts: " + e.getMessage());
                waitForEnter();
                return;
            }
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
            List<ShiftDTO> shiftsList;

            // Check if user has permission to view all shifts
            if (hasPermission("VIEW_SHIFT")) {
                // Management user can see all shifts
                String shifts = shiftService.getAllShiftsByDate(doneBy, date);
                shiftsList = ShiftDTO.deserializeList(shifts);
            } else {
                // Regular user can only see shifts for their branch
                try {
                    // Get employee's branch ID
                    EmployeeDTO employee = employeeService.getEmployeeByIdAsDTO(doneBy);
                    long branchId = employee.getBranchId();

                    // Get shifts for this branch
                    String shifts = shiftService.getAllShiftsByDateBranch(doneBy, date, branchId);
                    shiftsList = ShiftDTO.deserializeList(shifts);
                } catch (Exception e) {
                    printError("Error retrieving shifts: " + e.getMessage());
                    waitForEnter();
                    return;
                }
            }

            ShiftDTO[] shiftsOnDate = shiftsList.toArray(new ShiftDTO[0]);

            if (shiftsOnDate.length == 0) {
                printError("No shifts found for the selected date: " + date.format(dateFormatter));
                waitForEnter();
                return;
            }

            CliUtil.printSectionWithIcon("Available shifts on " + date.format(dateFormatter) + ":", "📅");

            List<String> shiftItems = new ArrayList<>();
            for (ShiftDTO s : shiftsOnDate) {
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
            ShiftDTO shift;

            // Check if user has permission to view all shifts
            if (hasPermission("VIEW_SHIFT")) {
                // Management user can access all shifts
                shift = ShiftDTO.deserialize(shiftService.getShift(doneBy, date, shiftType));
            } else {
                // Regular user can only access shifts for their branch
                try {
                    // Get employee's branch ID
                    EmployeeDTO employee = employeeService.getEmployeeByIdAsDTO(doneBy);
                    long branchId = employee.getBranchId();

                    // Get shift for this branch
                    shift = ShiftDTO.deserialize(shiftService.getShiftByBranch(doneBy, date, shiftType, branchId));
                } catch (Exception e) {
                    printError("Error retrieving shift: " + e.getMessage());
                    waitForEnter();
                    return;
                }
            }

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
     * Assigns employees to a shift
     * Allows selecting multiple employees to assign to different roles in the shift
     */
    private void assignEmployeeToShift() {
        if (!hasPermission("ASSIGN_EMPLOYEE")) {
            printError("You don't have permission to assign employees to shifts.");
            waitForEnter();
            return;
        }

        // Select a shift by date and type (only open shifts)
        ShiftDTO shift = selectShiftByDateAndType("Assign Employees to Shift", true);
        if (shift == null) {
            return; // User cancelled or no shift was found
        }

        long shiftId = shift.getId();
        try {

            // Display roles and their requirements
            CliUtil.printEmptyLine();
            CliUtil.printSectionWithIcon("ROLES REQUIRED", "👥");

            Map<String, Integer> rolesRequired = shift.getRolesRequired();
            Map<String, Set<Long>> assignedEmployees = shift.getAssignedEmployees();

            if (rolesRequired.isEmpty()) {
                printError("No roles defined for this shift.");
                waitForEnter();
                return;
            }

            // Continue assigning employees until the user is done
            boolean continuingAssignments = true;
            while (continuingAssignments) {
                // Refresh shift data
                shift = ShiftDTO.deserialize(shiftService.getShiftById(doneBy, shiftId));
                rolesRequired = shift.getRolesRequired();
                assignedEmployees = shift.getAssignedEmployees();

                // Display current assignment status
                CliUtil.printEmptyLine();
                CliUtil.printSectionWithIcon("CURRENT ROLE ASSIGNMENTS", "👥");

                List<String> availableRoles = new ArrayList<>();
                boolean allRolesStaffed = true;

                for (Map.Entry<String, Integer> entry : rolesRequired.entrySet()) {
                    String role = entry.getKey();
                    int required = entry.getValue();
                    Set<Long> assigned = assignedEmployees.getOrDefault(role, new HashSet<>());

                    CliUtil.printRoleAssignment(role, assigned.size(), required);

                    if (assigned.size() < required) {
                        availableRoles.add(role);
                        allRolesStaffed = false;
                    }

                    // Display employees assigned to this role
                    if (!assigned.isEmpty()) {
                        for (Long empId : assigned) {
                            CliUtil.printEmployee(formatEmployeeDisplay(empId), 4);
                        }
                    }
                }

                if (allRolesStaffed) {
                    CliUtil.printSuccessWithCheckmark("All roles are fully staffed for this shift.");
                    waitForEnter();
                    return;
                }

                CliUtil.printOptionsHeader("What would you like to do?");
                List<String> options = new ArrayList<>();
                options.add("Assign an employee to a role");
                options.add("Finish assignments");
                CliUtil.printNumberedList(options, 1);

                int assignChoice = CliUtil.getMenuChoice("Enter your choice (1-2): ", 1, 2, scanner);

                if (assignChoice == 2) {
                    shiftService.isAssignedManager(doneBy, shiftId);
                    CliUtil.printInfo("Finishing assignments.");
                    continuingAssignments = false;
                    continue;
                }

                // Select role using numbered options
                CliUtil.printOptionsHeader("Available roles:");

                if (availableRoles.isEmpty()) {
                    printError("No roles available for assignment.");
                    continue;
                }

                // Display roles as numbered options
                final Map<String, Integer> finalRolesRequired = rolesRequired;
                final Map<String, Set<Long>> finalAssignedEmployees = assignedEmployees;
                CliUtil.printNumberedListWithInfo(availableRoles, role -> {
                    int required = finalRolesRequired.get(role);
                    int assigned = finalAssignedEmployees.containsKey(role) ? 
                                  finalAssignedEmployees.get(role).size() : 0;
                    return role + " (" + assigned + "/" + required + " assigned)";
                }, 1);

                CliUtil.printEmptyLine();
                int roleChoice = CliUtil.getMenuChoice("Select Role (1-" + availableRoles.size() + "): ", 1, availableRoles.size(), scanner);

                // Get the selected role
                String role = availableRoles.get(roleChoice - 1);

                if (assignedEmployees.containsKey(role) && 
                    assignedEmployees.get(role).size() >= rolesRequired.get(role)) {
                    printError("This role is already fully staffed.");
                    continue;
                }

                // Display available employees for this shift
                List<Set<Long>> unAssignedEmployees;

                // Check if user has permission to view all employees
                if (hasPermission("MANAGEMENT")) {
                    // Management user can see all employees
                    unAssignedEmployees = shiftService.getUnassignedManager(doneBy, shiftId);
                } else {
                    // Regular user can only see employees for their branch
                    try {
                        // Get employee's branch ID
                        EmployeeDTO employee = employeeService.getEmployeeByIdAsDTO(doneBy);
                        long branchId = employee.getBranchId();

                        // Get employees for this branch
                        unAssignedEmployees = shiftService.getUnassignedManagerByBranch(doneBy, shiftId, branchId);
                    } catch (Exception e) {
                        printError("Error retrieving employees: " + e.getMessage());
                        waitForEnter();
                        return;
                    }
                }

                Set<Long> availableEmployees = unAssignedEmployees.get(1);
                Set<Long> unavailableEmployees = unAssignedEmployees.get(0);
                CliUtil.printEmptyLine();
                CliUtil.printSectionWithIcon("UNASSIGNED EMPLOYEES FOR THIS SHIFT", "👥");

                long employeeId;

                if (availableEmployees.isEmpty() && unavailableEmployees.isEmpty()) {
                    CliUtil.printInfo("  No employees are available for this shift.");
                    CliUtil.printEmptyLine();
                    CliUtil.printOptionsHeader("Would you like to:");
                    List<String> manualOptions = new ArrayList<>();
                    manualOptions.add("Enter an employee ID manually");
                    manualOptions.add("Cancel and return to role selection");
                    CliUtil.printNumberedList(manualOptions, 1);

                    int manualChoice = CliUtil.getMenuChoice("Enter your choice (1-2): ", 1, 2, scanner);
                    if (manualChoice != 1) {
                        continue;
                    }

                    // Manual entry
                    CliUtil.printEmptyLine();
                    employeeId = getLongInput("Enter Employee ID to assign: ");
                } else {
                    // Convert set to list for indexed access
                    List<Long> availableEmployeesList = new ArrayList<>(availableEmployees);
                    List<Long> unavailableEmployeesList = new ArrayList<>(unavailableEmployees);

                    CliUtil.printOptionsHeader("Available employees:");
                    // Display employees as numbered options
                    if (availableEmployeesList.isEmpty()) {
                        CliUtil.printInfo("  No employees are available for this shift.");
                    }else {
                        CliUtil.printNumberedListWithInfo(availableEmployeesList, this::formatUnassignedEmployeeDisplay, 1);
                    }
                    CliUtil.printEmptyLine();
                    CliUtil.printOptionsHeader("Unavailable employees:");
                    if (unavailableEmployees.isEmpty()) {
                        CliUtil.printInfo("  No employees are unavailable for this shift.");
                    }else {
                        CliUtil.printNumberedListWithInfo(unavailableEmployeesList, this::formatUnassignedEmployeeDisplay, availableEmployeesList.size() + 1);
                    }
                    CliUtil.printEmptyLine();
                    CliUtil.printOptionsHeader("Options:");
                    List<Long> employeeList = new ArrayList<>(availableEmployeesList);
                    employeeList.addAll(unavailableEmployees);
                    List<String> empOptions = new ArrayList<>();
                    empOptions.add("1-" + employeeList.size() + ". Select an employee from the list");
                    empOptions.add((employeeList.size() + 1) + ". Enter an employee ID manually");
                    CliUtil.printHierarchicalList(empOptions, "", 0);

                    int empChoice = CliUtil.getMenuChoice("Enter your choice (1-" + (employeeList.size() + 1) + "): ", 1, employeeList.size() + 1, scanner);

                    if (empChoice >= 1 && empChoice <= employeeList.size()) {
                        // Selected from list
                        employeeId = employeeList.get(empChoice - 1);
                    } else {
                        // Manual entry
                        CliUtil.printEmptyLine();
                        employeeId = getLongInput("Enter Employee ID to assign: ");
                    }
                }

                // Check if employee is available
                boolean isAvailable = shiftService.isEmployeeAvailable(doneBy, shiftId, employeeId);

                if (!isAvailable) {
                    CliUtil.printWarning(formatEmployeeDisplay(employeeId) + " is not marked as available for this shift.");
                    if (confirm("Do you want to assign this employee anyway?")) {
                        // Continue with assignment
                    } else {
                        CliUtil.printOperationCancelled();
                        continue;
                    }
                }

                // Check if employee is already assigned to this shift
                boolean isAssigned = shiftService.isEmployeeAssigned(doneBy, shiftId, employeeId);

                if (isAssigned) {
                    // Find which role the employee is already assigned to
                    String assignedRole = null;
                    for (Map.Entry<String, Set<Long>> entry : assignedEmployees.entrySet()) {
                        if (entry.getValue().contains(employeeId)) {
                            assignedRole = entry.getKey();
                            break;
                        }
                    }

                    if (assignedRole != null && assignedRole.equals(role)) {
                        printError(formatEmployeeDisplay(employeeId) + " is already assigned to this role in this shift.");
                        continue;
                    } else if (assignedRole != null) {
                        printError(formatEmployeeDisplay(employeeId) + " is already assigned to role '" + assignedRole + "' in this shift.");
                        CliUtil.printWarning("An employee cannot be assigned to multiple roles in the same shift.");
                        continue;
                    } else {
                        printError(formatEmployeeDisplay(employeeId) + " is already assigned to this shift.");
                        continue;
                    }
                }

                // Confirm assignment
                if (confirm("Confirm assigning " + formatEmployeeDisplay(employeeId) + " to role '" + role + "' for shift on " + 
                        shift.getShiftDate() + " (" + shift.getShiftType() + ")?")) {

                    String result = shiftService.assignEmployeeToRole(doneBy, shiftId, employeeId, role);

                    if (result.contains("successfully")) {
                        printSuccess(result);
                    } else {
                        printError(result);
                    }
                } else {
                    CliUtil.printOperationCancelled();
                }
            }
            if (shiftService.isAssignedManager(doneBy, shiftId).contains("Error")) {
                CliUtil.printWarning("ATTN: No shift manager assigned for this shift.");
            }

        } catch (Exception e) {
            printError("Error assigning employee: " + e.getMessage());
        }

        waitForEnter();
    }

    /**
     * Removes an employee from a shift
     */
    private void removeEmployeeFromShift() {
        if (!hasPermission("ASSIGN_EMPLOYEE")) {
            printError("You don't have permission to remove employees from shifts.");
            waitForEnter();
            return;
        }

        // Select a shift by date and type (only open shifts)
        ShiftDTO shift = selectShiftByDateAndType("Remove Employee from Shift", true);
        if (shift == null) {
            return; // User cancelled or no shift was found
        }

        long shiftId = shift.getId();
        try {

            // Display current assignments
            CliUtil.printEmptyLine();
            CliUtil.printSectionWithIcon("CURRENT ASSIGNMENTS", "👥");

            Map<String, Set<Long>> assignedEmployees = shift.getAssignedEmployees();
            boolean hasAssignments = false;

            for (Map.Entry<String, Set<Long>> entry : assignedEmployees.entrySet()) {
                String role = entry.getKey();
                Set<Long> employees = entry.getValue();

                if (!employees.isEmpty()) {
                    hasAssignments = true;
                    CliUtil.printBold("  " + role + ":");

                    for (Long employeeId : employees) {
                        CliUtil.printEmployee(formatEmployeeDisplay(employeeId), 4);
                    }
                }
            }

            if (!hasAssignments) {
                printError("No employees are assigned to this shift.");
                waitForEnter();
                return;
            }

            // Select employee to remove
            long employeeId = getLongInput("Enter Employee ID to remove: ");

            // Check if employee is assigned to this shift
            boolean isAssigned = shiftService.isEmployeeAssigned(doneBy, shiftId, employeeId);

            if (!isAssigned) {
                printError(formatEmployeeDisplay(employeeId) + " is not assigned to this shift.");
                waitForEnter();
                return;
            }

            // Find which role the employee is assigned to
            String assignedRole = null;
            for (Map.Entry<String, Set<Long>> entry : assignedEmployees.entrySet()) {
                if (entry.getValue().contains(employeeId)) {
                    assignedRole = entry.getKey();
                    break;
                }
            }

            // Confirm removal
            if (confirm("Confirm removing " + formatEmployeeDisplay(employeeId) + " from role '" + assignedRole + "' for shift on " + 
                    shift.getShiftDate() + " (" + shift.getShiftType() + ")?")) {

                String result = shiftService.removeAssignment(doneBy, shiftId, assignedRole, employeeId);

                if (result.contains("successfully")) {
                    printSuccess(result);
                } else {
                    printError(result);
                }
            } else {
                CliUtil.printOperationCancelled();
            }

        } catch (Exception e) {
            printError("Error removing employee: " + e.getMessage());
        }

        waitForEnter();
    }

    /**
     * Allows users to modify the required roles for a shift
     */
    private void modifyRequiredRoles() {
        if (!hasPermission("ASSIGN_EMPLOYEE")) {
            printError("You don't have permission to modify required roles for shifts.");
            waitForEnter();
            return;
        }

        // Select a shift by date and type (only open shifts)
        ShiftDTO shift = selectShiftByDateAndType("Modify Required Roles", true);
        if (shift == null) {
            return; // User cancelled or no shift was found
        }

        long shiftId = shift.getId();
        try {

            // Display current required roles
            Map<String, Integer> rolesRequired = shift.getRolesRequired();

            boolean continuingModifications = true;
            while (continuingModifications) {
                // Refresh shift data
                shift = ShiftDTO.deserialize(shiftService.getShiftById(doneBy, shiftId));
                rolesRequired = shift.getRolesRequired();

                CliUtil.printEmptyLine();
                CliUtil.printSectionWithIcon("CURRENT REQUIRED ROLES", "👥");

                if (rolesRequired.isEmpty()) {
                    CliUtil.printInfo("  No roles defined for this shift.");
                } else {
                    Map<String, int[]> roleAssignments = new HashMap<>();
                    for (Map.Entry<String, Integer> entry : rolesRequired.entrySet()) {
                        roleAssignments.put(entry.getKey(), new int[]{0, entry.getValue()});
                    }
                    CliUtil.printRoleAssignments(roleAssignments);
                }

                CliUtil.printEmptyLine();
                CliUtil.printOptionsHeader("What would you like to do?");
                List<String> modifyOptions = new ArrayList<>();
                modifyOptions.add("Add a new required role");
                modifyOptions.add("Modify an existing required role");
                modifyOptions.add("Remove a required role");
                modifyOptions.add("Finish modifications");
                CliUtil.printNumberedList(modifyOptions, 1);

                int modifyChoice = CliUtil.getMenuChoice("Enter your choice (1-4): ", 1, 4, scanner);

                switch (modifyChoice) {
                    case 1: // Add a new required role
                        addRequiredRole(shiftId, rolesRequired);
                        break;
                    case 2: // Modify an existing required role
                        modifyExistingRole(shiftId, rolesRequired);
                        break;
                    case 3: // Remove a required role
                        removeRequiredRole(shiftId, rolesRequired);
                        break;
                    case 4: // Finish modifications
                        CliUtil.printInfo("Finishing modifications.");
                        continuingModifications = false;
                        break;
                }
            }

        } catch (Exception e) {
            printError("Error modifying required roles: " + e.getMessage());
        }

        waitForEnter();
    }

    /**
     * Adds a new required role to a shift
     * 
     * @param shiftId The ID of the shift
     * @param currentRoles The current required roles for the shift
     */
    private void addRequiredRole(long shiftId, Map<String, Integer> currentRoles) {
        try {
            // Get all available roles
            String[] allRoles = shiftService.getRoles(doneBy).split(", ");

            // Filter out roles that are already required
            List<String> availableRoles = new ArrayList<>();
            for (String role : allRoles) {
                if (!currentRoles.containsKey(role)) {
                    availableRoles.add(role);
                }
            }

            if (availableRoles.isEmpty()) {
                printError("No additional roles available to add.");
                return;
            }

            // Display available roles as numbered options
            CliUtil.printEmptyLine();
            CliUtil.printOptionsHeader("Available roles to add:");
            CliUtil.printNumberedList(availableRoles, 1);

            int roleChoice = CliUtil.getMenuChoice("Enter your choice (1-" + availableRoles.size() + "): ", 1, availableRoles.size(), scanner);

            // Get the selected role
            String selectedRole = availableRoles.get(roleChoice - 1);

            // Get the number of employees required for this role
            int requiredCount = CliUtil.getPositiveIntInput("Enter number of employees required for " + selectedRole + ": ", scanner);

            // Add the required role
            String result = shiftService.updateRolesRequired(doneBy, shiftId, selectedRole, requiredCount);

            if (result.contains("success")) {
                printSuccess("Role '" + selectedRole + "' added with " + requiredCount + " required employees.");
            } else {
                printError("Failed to add role: " + result);
            }

        } catch (Exception e) {
            printError("Error adding required role: " + e.getMessage());
        }
    }

    /**
     * Modifies an existing required role for a shift
     * 
     * @param shiftId The ID of the shift
     * @param currentRoles The current required roles for the shift
     */
    private void modifyExistingRole(long shiftId, Map<String, Integer> currentRoles) {
        try {
            if (currentRoles.isEmpty()) {
                printError("No roles defined for this shift to modify.");
                return;
            }

            // Display current roles as numbered options
            CliUtil.printEmptyLine();
            CliUtil.printOptionsHeader("Select a role to modify:");

            List<String> rolesList = new ArrayList<>(currentRoles.keySet());
            CliUtil.printNumberedListWithInfo(rolesList, role -> 
                String.format("%-15s: %d required", role, currentRoles.get(role)), 1);

            int roleChoice = CliUtil.getMenuChoice("Enter your choice (1-" + rolesList.size() + "): ", 1, rolesList.size(), scanner);

            // Get the selected role
            String selectedRole = rolesList.get(roleChoice - 1);
            int currentCount = currentRoles.get(selectedRole);

            // Get the new number of employees required for this role
            int newCount = CliUtil.getPositiveIntInput("Enter new number of employees required for " + selectedRole + " (currently " + currentCount + "): ", scanner);

            // Update the required role
            String result = shiftService.updateRolesRequired(doneBy, shiftId, selectedRole, newCount);

            if (result.contains("success")) {
                printSuccess("Role '" + selectedRole + "' updated from " + currentCount + " to " + newCount + " required employees.");
            } else {
                printError("Failed to update role: " + result);
            }

        } catch (Exception e) {
            printError("Error modifying required role: " + e.getMessage());
        }
    }

    /**
     * Removes a required role from a shift
     * 
     * @param shiftId The ID of the shift
     * @param currentRoles The current required roles for the shift
     */
    private void removeRequiredRole(long shiftId, Map<String, Integer> currentRoles) {
        try {
            if (currentRoles.isEmpty()) {
                printError("No roles defined for this shift to remove.");
                return;
            }

            // Display current roles as numbered options
            CliUtil.printEmptyLine();
            CliUtil.printOptionsHeader("Select a role to remove:");

            List<String> rolesList = new ArrayList<>(currentRoles.keySet());
            CliUtil.printNumberedListWithInfo(rolesList, role -> 
                String.format("%-15s: %d required", role, currentRoles.get(role)), 1);

            int roleChoice = CliUtil.getMenuChoice("Enter your choice (1-" + rolesList.size() + "): ", 1, rolesList.size(), scanner);

            // Get the selected role
            String selectedRole = rolesList.get(roleChoice - 1);

            // Confirm removal
            if (confirm("Are you sure you want to remove the role '" + selectedRole + "' from this shift?")) {
                // Remove the required role
                String result = shiftService.removeRoleRequired(doneBy, shiftId, selectedRole);

                if (result.contains("success")) {
                    printSuccess("Role '" + selectedRole + "' removed from shift requirements.");
                } else {
                    printError("Failed to remove role: " + result);
                }
            } else {
                CliUtil.printOperationCancelled();
            }

        } catch (Exception e) {
            printError("Error removing required role: " + e.getMessage());
        }
    }

    private void viewEmployeeAssignments() {
        printSectionHeader("Employee Assignments");

        // Display breadcrumb navigation
        CliUtil.printBreadcrumb("Assignment Management > View Employee Assignments");

        long employeeId = getLongInput("Enter Employee ID: ");

        try {
            // Get employee name for display
            String employeeName = formatEmployeeDisplay(employeeId);

            // Get shifts assigned to the employee
            ShiftDTO[] shifts = ShiftDTO.deserializeList(shiftService.getShiftsByEmployee(doneBy, employeeId)).toArray(new ShiftDTO[0]);

            if (shifts.length == 0) {
                CliUtil.printInfo("No shifts assigned to " + employeeName + ".");
                waitForEnter();
                return;
            }

            // Convert array to list for pagination
            List<ShiftDTO> shiftList = Arrays.asList(shifts);

            // Define how many shifts to show per page
            final int ITEMS_PER_PAGE = 5;

            // Use the pagination utility to display shifts
            CliUtil.displayPaginatedList(
                "Shifts Assigned to " + employeeName,
                shiftList,
                ITEMS_PER_PAGE,
                shift -> {
                    // Find which role the employee is assigned to
                    String assignedRole = "Unknown";
                    for (Map.Entry<String, Set<Long>> entry : shift.getAssignedEmployees().entrySet()) {
                        if (entry.getValue().contains(employeeId)) {
                            assignedRole = entry.getKey();
                            break;
                        }
                    }

                    // Return formatted string representation of the shift
                    return String.format("ID: %d | Date: %s | Type: %s | Role: %s",
                        shift.getId(),
                        shift.getShiftDate().toString(),
                        shift.getShiftType().toString(),
                        assignedRole);
                },
                scanner
            );

        } catch (Exception e) {
            printError("Error retrieving employee assignments: " + e.getMessage());
            waitForEnter();
        }
    }
}
