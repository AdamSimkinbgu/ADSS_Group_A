package PresentationLayer.EmployeeSubModule.CLI;

import DTOs.EmployeeDTO;
import DTOs.ShiftDTO;
import ServiceLayer.exception.ServiceException;
import Util.CliUtil;
import ServiceLayer.EmployeeSubModule.EmployeeService;
import ServiceLayer.EmployeeSubModule.ShiftService;
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
        CliUtil.printWelcomeBanner("Employee Availability Management", String.valueOf(LocalDate.now()), formatEmployeeDisplay(doneBy));
    }

    public boolean process() {
        try {
            // check if the availability board is blocked on weekends
            if (shiftService.isWeekendBlocked()) {
                System.out.println("🚫 Availability update is blocked on weekends.");
                return false;
            }
            LocalDate startDate = Week.getNextSunday(LocalDate.now());
            Week week = Week.from(startDate);
            Set<ShiftDTO> weekShifts = shiftService.getShiftsByWeek(doneBy, week);

            if (weekShifts.isEmpty()) {
                CliUtil.printError("🚫 No shifts available for the upcoming week.");
                return false;
            }

            List<ShiftDTO> shiftsList = new ArrayList<>(weekShifts);
            Comparator<ShiftDTO> byDateThenType = Comparator
                    .comparing(ShiftDTO::getShiftDate)
                    .thenComparing(ShiftDTO::getShiftType);
            shiftsList.sort(byDateThenType);

            CliUtil.printSectionHeader("Employee Weekly Availability", false, "SYSTEM");

            System.out.printf(CliUtil.BOLD + "%-4s %-15s| %-13s| %-13s|\n" + CliUtil.RESET, "No.", "Day", "Type", "Available");
            System.out.println(CliUtil.GRAY + "--------------------------------------------------" + CliUtil.RESET);

            // Mapping of number to shift
            Map<Integer, ShiftDTO> numberedShifts = new LinkedHashMap<>();
            int index = 1;
            for (ShiftDTO shift : shiftsList) {
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
                CliUtil.printPrompt("Enter the number(s) of the shift(s) to update (comma-separated, 'all' for all shifts, or X to exit): ");
                String input = scanner.nextLine().trim().toLowerCase();

                if (input.equals("x")) {
                    break;
                }

                try {
                    Set<Integer> selectedNumbers = new HashSet<>();

                    // Handle 'all' option
                    if (input.equals("all")) {
                        selectedNumbers.addAll(numberedShifts.keySet());
                    } else {
                        // Parse comma-separated numbers
                        String[] numberStrings = input.split(",");
                        for (String numStr : numberStrings) {
                            int num = Integer.parseInt(numStr.trim());
                            if (!numberedShifts.containsKey(num)) {
                                CliUtil.printError("Invalid shift number: " + num + ". Skipping.");
                                continue;
                            }
                            selectedNumbers.add(num);
                        }
                    }

                    if (selectedNumbers.isEmpty()) {
                        CliUtil.printError("No valid shift numbers provided. Try again.");
                        continue;
                    }

                    // Get availability status for all selected shifts
                    String availabilityInput;
                    while (true) {
                        CliUtil.printPrompt("Mark availability for " + selectedNumbers.size() + " selected shift(s) (Y for available / N for not available): ");
                        availabilityInput = scanner.nextLine().trim().toLowerCase();

                        if (availabilityInput.equals("y") || availabilityInput.equals("n")) {
                            break;
                        } else {
                            CliUtil.printError("Invalid input. Please enter Y or N.");
                        }
                    }

                    // Process all selected shifts
                    int successCount = 0;
                    for (int selectedNumber : selectedNumbers) {
                        ShiftDTO selectedShift = numberedShifts.get(selectedNumber);

                        try {
                            if (availabilityInput.equals("y")) {
                                shiftService.markEmployeeAvailable(doneBy, selectedShift.getId());
                                successCount++;
                            } else if (availabilityInput.equals("n")) {
                                shiftService.removeEmployeeAvailability(doneBy, selectedShift.getId());
                                successCount++;
                            }
                        } catch (Exception e) {
                            CliUtil.printError("Error updating shift #" + selectedNumber + ": " + e.getMessage());
                        }
                    }

                    if (successCount > 0) {
                        CliUtil.printSuccessWithCheckmark("Successfully updated " + successCount + " out of " + selectedNumbers.size() + " shifts.");

                        // Refresh the display to show updated availabilities
                        return true;
                    }

                } catch (NumberFormatException e) {
                    CliUtil.printError("Please enter valid numbers separated by commas, 'all', or X to exit.");
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

}
