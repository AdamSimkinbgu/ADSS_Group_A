package PresentationLayer;

import ServiceLayer.EmployeeService;
import ServiceLayer.ShiftSL;
import ServiceLayer.ShiftService;
import ServiceLayer.exception.AuthorizationException;
import Util.Week;
import java.time.*;
import java.util.*;

public class AvailabilityCLI {
    private final ShiftService shiftService;
    private final EmployeeService employeeService;
    private final Scanner scanner;
    private final long doneBy;

    public AvailabilityCLI(ShiftService shiftService, EmployeeService employeeService , long doneBy) {
        this.shiftService = shiftService;
        this.employeeService = employeeService;
        this.scanner = new Scanner(System.in);
        this.doneBy = doneBy;
    }

    public void start() {
        printWelcomeBanner();
        boolean running = true;

        while (running) {
            running = process();
        }
    }

    private void printWelcomeBanner() {
        System.out.println("Welcome to the Employee Module!");
        System.out.println("You are logged in as: " + doneBy);
    }

    public boolean process() {
        try {
            if (isWeekendBlocked()) {
                System.out.println("🚫 Availability update is blocked on weekends.");
                return false;
            }
            LocalDate startDate = Week.getNextSunday(LocalDate.now());
            Week week = Week.from(startDate);
            Set<ShiftSL> weekShifts = shiftService.getShiftsByWeek(doneBy, week);

            if (weekShifts.isEmpty()) {
                CliUtil.printError("🚫 No shifts available for the upcoming week.");
                return false;
            }

            List<ShiftSL> shiftsList = new ArrayList<>(weekShifts);

            CliUtil.printSectionHeader("Employee Weekly Availability", false, "SYSTEM");

            System.out.printf(CliUtil.BOLD + "%-4s %-15s| %-13s| %-13s|\n" + CliUtil.RESET, "No.", "Day", "Type", "Available");
            System.out.println(CliUtil.GRAY + "--------------------------------------------------" + CliUtil.RESET);

            // Mapping of number to shift
            Map<Integer, ShiftSL> numberedShifts = new LinkedHashMap<>();
            int index = 1;
            for (ShiftSL shift : shiftsList) {
                LocalDate date = shift.getShiftDate();
                String dayStr = date.getDayOfWeek().toString().substring(0, 1).toUpperCase() + date.getDayOfWeek().toString().substring(1).toLowerCase();
                dayStr = dayStr.substring(0, 3); // Sun, Mon...

                boolean available = shiftService.isEmployeeAvailable(doneBy, shift.getId(), doneBy);
                String availabilityMark = available ? CliUtil.greenString("✔") : CliUtil.redString("✖");

                System.out.printf(CliUtil.YELLOW + "%-3d" + CliUtil.RESET + " %-15s| %-13s| %-13s|\n",
                        index, dayStr + " " + date, shift.getShiftType(), availabilityMark);

                numberedShifts.put(index, shift);
                index++;
            }

            CliUtil.printEmptyLine();

            // Selection loop
            while (true) {
                CliUtil.printPrompt("Enter the number of the shift to update (or X to exit): ");
                String input = scanner.nextLine().trim().toLowerCase();

                if (input.equals("x")) {
                    break;
                }

                try {
                    int selectedNumber = Integer.parseInt(input);
                    if (!numberedShifts.containsKey(selectedNumber)) {
                        CliUtil.printError("Invalid shift number. Try again.");
                        continue;
                    }

                    ShiftSL selectedShift = numberedShifts.get(selectedNumber);

                    while (true) {
                        CliUtil.printPrompt("Mark availability (Y for available / N for not available): ");
                        String availabilityInput = scanner.nextLine().trim().toLowerCase();

                        if (availabilityInput.equals("y")) {
                            shiftService.markEmployeeAvailable(doneBy, selectedShift.getId());
                            CliUtil.printSuccessWithCheckmark("Marked as available.");
                            break;
                        } else if (availabilityInput.equals("n")) {
                            shiftService.removeEmployeeAvailability(doneBy, selectedShift.getId());
                            CliUtil.printSuccessWithCheckmark("Marked as not available.");
                            break;
                        } else {
                            CliUtil.printError("Invalid input. Please enter Y or N.");
                        }
                    }

                } catch (NumberFormatException e) {
                    CliUtil.printError("Please enter a valid number or X to exit.");
                }
            }

            CliUtil.printSuccessWithCheckmark("✅ Availability updated successfully!");
            return false;

        } catch (Exception e) {
            CliUtil.printError("An error occurred: " + e.getMessage());
            return true;
        }
        //System.out.printf("%d. %-15s| %-13s| %-13s|%n", index, (shift.getShiftType() == ShiftType.MORNING ? dayStr + " " + date : ""), shift.getShiftType(), availabilityMark);

    }


            private boolean isWeekendBlocked() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        DayOfWeek day = now.getDayOfWeek();
        LocalTime time = now.toLocalTime();

        return (day == DayOfWeek.THURSDAY && time.isAfter(LocalTime.of(16, 0)))
                || day == DayOfWeek.FRIDAY
                || day == DayOfWeek.SATURDAY;
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
        } catch (AuthorizationException e) {
            printError(e.getMessage());
            return false;
        } catch (Exception e) {
            printError("Error checking permissions.");
            return false;
        }
    }
}
