package PresentationLayer.EmployeeSubModule.CLI;

import DomainLayer.enums.ShiftType;
import DTOs.EmployeeDTO;
import DTOs.ShiftDTO;
import DomainLayer.exception.ShiftNotFoundException;
import Util.CliUtil;
import ServiceLayer.EmployeeSubModule.EmployeeService;
import ServiceLayer.EmployeeSubModule.ShiftService;
import ServiceLayer.exception.ServiceException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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
            menuOptions.add(CliUtil.YELLOW + optionNumber++ + CliUtil.RESET + ". View Current Shift");
            menuOptions.add(YELLOW + optionNumber++ + RESET + ". View Shift Details");
            menuOptions.add(YELLOW + optionNumber++ + RESET + ". View My Shifts");
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
                    viewCurrentShift();
                    return true;
                }

                if (choiceNum == currentOption++) {
                    viewShiftDetails();
                    return true;
                }

                if (choiceNum == currentOption++) {
                    viewEmployeeShifts();
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

    private void viewCurrentShift() {
        printSectionHeader("Current Shift");

        // Display breadcrumb navigation
        CliUtil.printBreadcrumb("Shift Management > View Current Shift");

        try {
            // Get current shift by date and type
            ShiftDTO shift = shiftService.getCurrentShift(doneBy);
            if (shift == null) {
                return; // User cancelled or no shift found
            }

            CliUtil.printSuccessWithCheckmark("Found current shift: " + shift.getShiftDate().format(dateFormatter) + " " + shift.getShiftType() + " (ID: " + shift.getId() + ")");
            CliUtil.printEmptyLine();

            // Display shift details
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
            table.add(new String[]{"Time", LocalTime.now().toString()});

            Map<String, List<String[]>> content = new HashMap<>();
            content.put("Shift Data", table);
            content.put("Assigned Employees", shift.getAssignedEmployees().values().stream()
                .map(employees -> employees.stream()
                    .map(employeeId -> new String[]{formatEmployeeDisplay(employeeId)})
                    .collect(Collectors.toList()))
                .flatMap(List::stream)
                .collect(Collectors.toList()));

            CliUtil.printFormattedTable("Current Shift Details", List.of("Shift Data", "Assigned Employees"), content, Map.of());

        } catch (Exception e) {
            printError("Unexpected error: " + e.getMessage());
        }

        waitForEnter();
    }

    private void printWelcomeBanner() {
        CliUtil.printWelcomeBanner("SHIFT MANAGEMENT SYSTEM", LocalDate.now().toString(), formatEmployeeDisplay(doneBy));
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
        } catch (ServiceLayer.exception.AuthorizationException e) {
            //printError("Error checking permissions: " + e.getMessage());
            return false;
        } catch (Exception e) {
            printError("Error checking permissions: " + e.getMessage());
            return false;
        }
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
            String branch = employeeService.getEmployeeBranchName(employee.getIsraeliId()) != null ? " [" + employeeService.getEmployeeBranchName(employee.getIsraeliId()) + "]" : "";
            return employee.getFullName() + " (#" + employeeId + ")" + branch;
        } catch (ServiceException e) {
            // If we can't get the employee name, just return the ID
            return "Employee #" + employeeId;
        }
    }

    /**
     * Utility method to select a shift by date and type
     * Shows available shifts on the selected date to help user make a selection
     * If the user has VIEW_SHIFT permission, shows all shifts
     * Otherwise, shows only shifts for the user's branch
     *
     * @param openOnly Whether to only show open shifts
     * @return The selected shift, or null if no shift was selected
     */
    private ShiftDTO selectShiftByDateAndType(String header, boolean openOnly) {
        printSectionHeader(header);
        LocalDate date = null;
        ShiftDTO[] shiftsOnDate = new ShiftDTO[0];

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

        while (true) {
            // Get date from user
            date = getDateInput("Select a date for the shift:");

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

                shiftsOnDate = shiftsList.toArray(new ShiftDTO[0]);

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
                    boolean tryAgain = confirm("Would you like to try another date?");
                    if (!tryAgain) {
                        CliUtil.printReturnPrompt("Shift Management Menu", scanner);
                        return null;
                    }
                    continue;
                }

                CliUtil.printSectionWithIcon("Available shifts on " + date.format(dateFormatter) + ":", "📅");

                List<String> shiftItems = new ArrayList<>();
                for (ShiftDTO s : shiftsOnDate) {
                    String status = s.isOpen() ? "(Open)" : "(Closed)";
                    shiftItems.add(s.getShiftType() + " shift " + status + " (ID: " + s.getId() + ")");
                }
                CliUtil.printHierarchicalList(shiftItems, "• ", 2);
                CliUtil.printEmptyLine();
                break; // Exit loop if we have shifts
            } catch (ShiftNotFoundException e) {
                printError(e.getMessage());
                boolean tryAgain = confirm("Would you like to try another date?");
                if (!tryAgain) {
                    CliUtil.printReturnPrompt("Shift Management Menu", scanner);
                    return null;
                }
            } catch (Exception e) {
                printError("Unexpected error: " + e.getMessage());
                CliUtil.printReturnPrompt("Shift Management Menu", scanner);
                return null;
            }
        }

        // Get shift type from user
        ShiftType shiftType = getShiftTypeInput("Select the shift type:");

        try {
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

    private LocalTime getHourInput(String prompt) {
        return CliUtil.getHourInput(prompt, scanner);
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

    /**
     * Gets a Sunday date from user input by showing a list of upcoming Sundays
     *
     * @param prompt The prompt to display
     * @return The LocalDate of the selected Sunday
     */
    private LocalDate getSundayDateInput(String prompt) {
        LocalDate today = LocalDate.now();

        // Find the next Sunday
        LocalDate nextSunday = today;
        while (nextSunday.getDayOfWeek().getValue() != 7) { // 7 is Sunday in ISO-8601
            nextSunday = nextSunday.plusDays(1);
        }

        // Create a list of the next 4 Sundays
        List<LocalDate> upcomingSundays = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            upcomingSundays.add(nextSunday.plusWeeks(i));
        }

        while (true) {
            System.out.println(CliUtil.BOLD + prompt + CliUtil.RESET);

            // Display the upcoming Sundays
            for (int i = 0; i < upcomingSundays.size(); i++) {
                System.out.println((i + 1) + ". Sunday (" + upcomingSundays.get(i).format(dateFormatter) + ")");
            }

            // Option to enter a specific date
            System.out.println((upcomingSundays.size() + 1) + ". Enter another Sunday date (dd-mm-yyyy):");

            System.out.print(CliUtil.CYAN + "======>" + CliUtil.RESET + " Enter your choice (1-" + (upcomingSundays.size() + 1) + "): ");

            String input = scanner.nextLine();

            // Check if input is a date in the format dd-mm-yyyy
            if (input.matches("\\d{2}-\\d{2}-\\d{4}")) {
                try {
                    LocalDate date = LocalDate.parse(input, dateFormatter);
                    // Verify it's a Sunday
                    if (date.getDayOfWeek().getValue() == 7) {
                        return date;
                    } else {
                        printError("The date entered is not a Sunday. Please enter a Sunday date.");
                    }
                } catch (Exception e) {
                    printError("Please enter a valid date in the format dd-mm-yyyy.");
                }
                continue;
            }

            // Otherwise, try to parse as a menu choice
            try {
                int choice = Integer.parseInt(input);
                if (choice >= 1 && choice <= upcomingSundays.size()) {
                    return upcomingSundays.get(choice - 1);
                } else if (choice == upcomingSundays.size() + 1) {
                    System.out.print(CliUtil.BOLD + "Enter Sunday date (dd-mm-yyyy): " + CliUtil.RESET);
                    input = scanner.nextLine();
                    try {
                        LocalDate date = LocalDate.parse(input, dateFormatter);
                        // Verify it's a Sunday
                        if (date.getDayOfWeek().getValue() == 7) {
                            return date;
                        } else {
                            printError("The date entered is not a Sunday. Please enter a Sunday date.");
                        }
                    } catch (Exception e) {
                        printError("Please enter a valid date in the format dd-mm-yyyy.");
                    }
                } else {
                    printError("Please enter a valid option (1-" + (upcomingSundays.size() + 1) + ").");
                }
            } catch (NumberFormatException e) {
                printError("Please enter a valid number or date in the format dd-mm-yyyy.");
            }
        }
    }



    //===============================================================================
    // Shift Management Methods
    //===============================================================================

    /**
     * Displays all shifts in the system with pagination
     * If the user has VIEW_SHIFT permission, shows all shifts
     * Otherwise, shows only shifts for the user's branch
     */
    private void viewAllShifts() {
        List<ShiftDTO> shiftList;

        // Check if user has permission to view all shifts
        if (hasPermission("MANAGEMENT")) {
            // User has management permission, show all shifts
            shiftList = ShiftDTO.deserializeList(shiftService.getAllShifts(doneBy));
        } else {
            // Regular user, show only shifts for their branch
            try {
                // Get employee's branch ID from employee service
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
        final int ITEMS_PER_PAGE = 12;

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
        CliUtil.printBreadcrumb("Shift Management > View Shift Details");

        try {
            // Get shift by date and type
            ShiftDTO shift = selectShiftByDateAndType("Select Shift to View", false);
            if (shift == null) {
                return; // User cancelled or no shift found
            }
            CliUtil.printSuccessWithCheckmark("Found shift: " + shift.getShiftDate().format(dateFormatter) + " " + shift.getShiftType() + " (ID: " + shift.getId() + ")");
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

        } catch (Exception e) {
            printError("Unexpected error: " + e.getMessage());
        }

        CliUtil.printReturnPrompt("Shift Management Menu", scanner);
    }



    /**
     * Updates an existing shift
     */
    private void updateShift() {
        printSectionHeader("Update Shift");

        // Display breadcrumb navigation
        CliUtil.printBreadcrumb("Shift Management > Update Shift Details");

        // Select a shift to update
        ShiftDTO shift = selectShiftByDateAndType( "Select Shift to Update", false);
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
            LocalTime startHour = shift.getStartHour();
            LocalTime endHour = shift.getEndHour();

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
                startHour = getHourInput("Enter new start hour:");
                endHour = getHourInput("Enter new end hour:");
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
                String result = shiftService.updateShift(doneBy, shift.getId(), shiftType, date, isOpen, hasManager, startHour,endHour ,LocalDate.now());
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

        // Display breadcrumb navigation
        CliUtil.printBreadcrumb("Shift Management > Delete Shift");

        // Select a shift to delete
        ShiftDTO shift = selectShiftByDateAndType("Select Shift to Delete", false);
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

        // Display breadcrumb navigation
        CliUtil.printBreadcrumb("Shift Management > Create Shift");

        try {
            // Get basic shift information
            CliUtil.printSectionWithIcon("BASIC SHIFT INFORMATION", "📅");

            LocalDate date = getDateInput("Enter shift date:");
            ShiftType shiftType = getShiftTypeInput("Enter shift type:");

            // Check if shift already exists
            try {
                ShiftDTO existingShift = ShiftDTO.deserialize(shiftService.getShift(doneBy, date, shiftType));
                if (existingShift != null) {
                    printError("A " + shiftType + " shift already exists for " + date.format(dateFormatter));
                    CliUtil.printTip("You can edit the existing shift using the 'Edit Shifts' option.");
                    waitForEnter();
                    return;
                }
            } catch (Exception e) {
                // No shift exists, continue with creation
            }

            // Get shift hours
            LocalTime startHour = getHourInput("Enter start hour:");
            LocalTime endHour = getHourInput("Enter end hour:");


            // Get roles required
            CliUtil.printEmptyLine();
            CliUtil.printSectionWithIcon("ROLES REQUIRED", "👥");
            CliUtil.printInfo("Enter the number of employees required for each role (0 for none)");
            CliUtil.printEmptyLine();

            Map<String, Integer> rolesRequired = new HashMap<>();
            Set<String> roles = Arrays.stream(shiftService.getRoles(doneBy).split(", ")).collect(Collectors.toSet());

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

            boolean isManagerShift = false;
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
                    startHour,
                    endHour,
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

        // Display breadcrumb navigation
        CliUtil.printBreadcrumb("Shift Management > Add Weekly Shifts");

        try {
            // Get start date for the week
            CliUtil.printSectionWithIcon("WEEKLY SHIFT CREATION", "📅");
            CliUtil.printInfo("This will create morning and evening shifts for an entire week");
            CliUtil.printEmptyLine();

            LocalDate startDate;
            startDate = getSundayDateInput("Select a Sunday to start the week:");


            // Get roles required
            CliUtil.printEmptyLine();
            CliUtil.printSectionWithIcon("DEFAULT ROLES REQUIRED", "👥");
            CliUtil.printInfo("Enter the default number of employees required for each role");
            CliUtil.printInfo("These values will be used for all shifts in the week");
            CliUtil.printEmptyLine();

            Map<String, Integer> rolesRequired = new HashMap<>();
            Set<String> roles = Arrays.stream(shiftService.getRoles(doneBy).split(", ")).collect(Collectors.toSet());


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

    /**
     * Displays the list of shifts assigned to the currently logged-in employee.
     * If the employee has no assigned shifts, an error message will be displayed.
     * Shifts are shown with pagination, allowing the user to navigate through
     * a limited number of entries per page.
     */
    private void viewEmployeeShifts() {

        try {
            // Get shifts for logged in employee
            List<ShiftDTO> employeeShifts = ShiftDTO.deserializeList(shiftService.getShiftByEmployee(doneBy, doneBy));

            if (employeeShifts.isEmpty()) {
                printError("You are not assigned to any shifts.");
                waitForEnter();
                return;
            }

            // Display shifts with pagination
            final int ITEMS_PER_PAGE = 5;

            CliUtil.displayPaginatedList(
                    "My Assigned Shifts",
                    employeeShifts,
                    ITEMS_PER_PAGE,
                    shift -> {
                        String openStatus = shift.isOpen() ? CliUtil.greenString("Open") : CliUtil.redString("Closed");

                        // Get employee's roles in this shift
                        List<String> employeeRoles = new ArrayList<>();
                        for (Map.Entry<String, Set<Long>> entry : shift.getAssignedEmployees().entrySet()) {
                            if (entry.getValue().contains(doneBy)) {
                                employeeRoles.add(entry.getKey());
                            }
                        }

                        return String.format("ID: %s | Date: %s | Type: %s | Status: %s | My Roles: %s",
                                shift.getId(),
                                shift.getShiftDate().toString(),
                                shift.getShiftType().toString(),
                                openStatus,
                                String.join(", ", employeeRoles));
                    },
                    scanner
            );

        } catch (Exception e) {
            printError("Error retrieving your shifts: " + e.getMessage());
            waitForEnter();
        }
    }
}
