package Util;

import DomainLayer.enums.ShiftType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;

/**
 * Utility class for CLI operations with common methods for printing, formatting, and user interaction.
 * This class centralizes UI-related functionality used across different CLI classes.
 */
public class CliUtil {
    // ANSI color codes
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String BOLD = "\u001B[1m";
    public static final String GRAY = "\u001B[37m";

    // Properties
    public static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");



    public static String greenString(String message){return GREEN + message + RESET;}
    public static String redString(String message){return RED + message + RESET;}

    /**
     * Prints an empty line
     */
    public static void printEmptyLine() {
        System.out.println();
    }

    public static void print(String massage){System.out.println(massage);}

    /**
     * Prints a breadcrumb navigation path
     * 
     * @param path The navigation path to display
     */
    public static void printBreadcrumb(String path) {
        System.out.println(BLUE + "Location: " + RESET + path);
        printEmptyLine();
    }

    /**
     * Prints a success message with a checkmark
     * 
     * @param message The success message to display
     */
    public static void printSuccessWithCheckmark(String message) {
        System.out.println(GREEN + "✓ " + message + RESET);
    }

    /**
     * Prints a section title with an icon
     * 
     * @param title The title to display
     * @param icon The icon to display before the title
     */
    public static void printSectionWithIcon(String title, String icon) {
        System.out.println(CYAN + icon + " " + BOLD + title + RESET);
    }

    /**
     * Prints a message in bold
     * @param message To print in bold
     */
    public static void printBold(String message){ System.out.println(BOLD + message + RESET);}

    /**
     * Prints a numbered list of options
     * 
     * @param options The list of options to display
     * @param startNumber The starting number for the list
     */
    public static void printNumberedList(List<String> options, int startNumber) {
        for (int i = 0; i < options.size(); i++) {
            System.out.println((i + startNumber) + ". " + options.get(i));
        }
    }

    /**
     * Prints a hierarchical list with indentation
     * 
     * @param items The list of items to display
     * @param prefix The prefix to use for each item (e.g., "• ", "- ")
     * @param indentation The number of spaces to indent
     */
    public static void printHierarchicalList(List<String> items, String prefix, int indentation) {
        String indent = " ".repeat(indentation);
        for (String item : items) {
            System.out.println(indent + prefix + item);
        }
    }

    /**
     * Prints a prompt for user input
     * 
     * @param prompt The prompt to display
     */
    public static void printPrompt(String prompt) {
        System.out.print(CYAN + "======>" + RESET + " " + prompt);
    }

    /**
     * Prints a "Press Enter to return" message
     * 
     * @param menuName The name of the menu to return to
     * @param scanner The scanner to use for input
     */
    public static void printReturnPrompt(String menuName, Scanner scanner) {
        System.out.println();
        System.out.println(YELLOW + "Press Enter to return to " + menuName + "..." + RESET);
        scanner.nextLine();
    }

    /**
     * Prints a message about returning to a menu
     * 
     * @param menuName The name of the menu to return to
     */
    public static void printReturnMessage(String menuName) {
        System.out.println(BOLD + YELLOW + "Returning to " + menuName + "..." + RESET);
    }

    /**
     * Prints a formatted table with a title, headers, and content
     * 
     * @param title The title of the table
     * @param headers List of section headers to display in the table
     * @param content Map of content rows, where each key is a header and each value is a list of content items
     * @param emptyMessages Map of messages to display when a section is empty
     */
    public static void printFormattedTable(String title, List<String> headers, 
                                          Map<String, List<String[]>> content,
                                          Map<String, String> emptyMessages) {
        final int TABLE_WIDTH = 53;
        final String TOP_BORDER = BLUE + "┌" + "─".repeat(TABLE_WIDTH - 2) + "┐" + RESET;
        final String MIDDLE_BORDER = BLUE + "├" + "─".repeat(TABLE_WIDTH - 2) + "┤" + RESET;
        final String BOTTOM_BORDER = BLUE + "└" + "─".repeat(TABLE_WIDTH - 2) + "┘" + RESET;

        // Print table title if provided
        if (title != null && !title.isEmpty()) {
            System.out.println(TOP_BORDER);
            String centeredTitle = centerText(title, TABLE_WIDTH - 2);
            System.out.println(BLUE + "│" + RESET + BOLD + YELLOW + centeredTitle + RESET + BLUE + "│" + RESET);
        } else {
            System.out.println(TOP_BORDER);
        }

        // Print each section
        for (int i = 0; i < headers.size(); i++) {
            String header = headers.get(i);

            // Print section header
            System.out.println(MIDDLE_BORDER);
            String centeredHeader = centerText(header, TABLE_WIDTH - 2);
            System.out.println(BLUE + "│" + RESET + BOLD + YELLOW + centeredHeader + RESET + BLUE + "│" + RESET);
            System.out.println(MIDDLE_BORDER);

            List<String[]> sectionContent = content.get(header);

            // Check if section is empty
            if (sectionContent == null || sectionContent.isEmpty()) {
                String emptyMessage = emptyMessages.getOrDefault(header, "No data available");
                System.out.printf(BLUE + "│ " + RESET + "%-" + (TABLE_WIDTH - 4) + "s " + BLUE + "│%n" + RESET, 
                    YELLOW + "  " + emptyMessage + RESET);
                continue;
            }

            // Print section content
            for (String[] row : sectionContent) {
                if (row.length == 2) {
                    // Two-column format (label: value)
                    System.out.printf(BLUE + "│ " + RESET + BOLD + "%-15s" + RESET + "│ %-" + (TABLE_WIDTH - 22) + "s " + BLUE + "│%n" + RESET, 
                        row[0], row[1]);
                } else if (row.length == 1) {
                    // Single-column format (full width)
                    System.out.printf(BLUE + "│ " + RESET + "%-" + (TABLE_WIDTH - 4) + "s " + BLUE + "│%n" + RESET, row[0]);
                }
            }
        }

        // Print bottom border
        System.out.println(BOTTOM_BORDER);
    }

    /**
     * Centers text within a given width
     * 
     * @param text The text to center
     * @param width The width to center within
     * @return The centered text
     */
    private static String centerText(String text, int width) {
        if (text.length() >= width) {
            return text;
        }

        int padding = (width - text.length()) / 2;
        return " ".repeat(padding) + text + " ".repeat(width - text.length() - padding);
    }


    /**
     * Prints an error message with formatting
     *
     * @param message The error message to display
     */
    public static void printError(String message) {
        System.out.println(RED + "❌ ERROR: " + message + RESET);
    }

    /**
     * Displays an error message with numbered options for retry, return, or cancel
     * 
     * @param message The error message to display
     * @param scanner The scanner to use for input
     * @return An integer indicating the user's choice: 1 for retry, 2 for return to previous menu, 0 for cancel
     */
    public static int handleError(String message, Scanner scanner) {
        printError(message);
        printEmptyLine();
        System.out.println("Please select:");
        System.out.println(YELLOW + "1" + RESET + ". Try again");
        System.out.println(YELLOW + "2" + RESET + ". Return to previous menu");
        System.out.println(YELLOW + "0" + RESET + ". Cancel operation");

        printEmptyLine();
        printPrompt("Enter your choice: ");

        while (true) {
            try {
                String input = scanner.nextLine();
                int choice = Integer.parseInt(input);

                if (choice == 0 || choice == 1 || choice == 2) {
                    return choice;
                } else {
                    printError("Invalid choice. Please enter 0, 1, or 2.");
                }
            } catch (NumberFormatException e) {
                printError("Please enter a valid number.");
            }
        }
    }

    /**
     * Prints a tip or hint message with formatting
     *
     * @param message The tip message to display
     */
    public static void printTip(String message) {
        System.out.println(YELLOW + "💡 TIP: " + message + RESET);
    }

    /**
     * Prints a warning message with formatting
     *
     * @param message The warning message to display
     */
    public static void printWarning(String message) {
        System.out.println(YELLOW + "⚠️ WARNING: " + message + RESET);
    }

    /**
     * Prints an operation cancelled message
     */
    public static void printOperationCancelled() {
        System.out.println(YELLOW + "Operation cancelled." + RESET);
    }

    /**
     * Prints a formatted role assignment with count
     * 
     * @param role The role name
     * @param assigned The number of assigned employees
     * @param required The number of required employees
     */
    public static void printRoleAssignment(String role, int assigned, int required) {
        System.out.printf("  • %-15s: %d/%d assigned%n", role, assigned, required);
    }

    /**
     * Prints a list of role assignments with counts
     * 
     * @param roleAssignments Map of role names to arrays containing [assigned, required] counts
     */
    public static void printRoleAssignments(Map<String, int[]> roleAssignments) {
        for (Map.Entry<String, int[]> entry : roleAssignments.entrySet()) {
            String role = entry.getKey();
            int[] counts = entry.getValue();
            printRoleAssignment(role, counts[0], counts[1]);
        }
    }

    /**
     * Prints an employee name with indentation
     * 
     * @param employeeName The employee name to display
     * @param indentation The number of spaces to indent
     */
    public static void printEmployee(String employeeName, int indentation) {
        String indent = " ".repeat(indentation);
        System.out.println(indent + "- " + employeeName);
    }

    /**
     * Prints an info message with formatting
     *
     * @param message The info message to display
     */
    public static void printInfo(String message) {
        System.out.println(YELLOW + message + RESET);
    }

    /**
     * Prints a section title with options header
     * 
     * @param title The title to display
     */
    public static void printOptionsHeader(String title) {
        System.out.println();
        System.out.println(CYAN + title + RESET);
    }

    /**
     * Prints a numbered list of options with additional information
     * 
     * @param options The list of options to display
     * @param infoProvider A function that provides additional information for each option
     * @param startNumber The starting number for the list
     */
    public static <T> void printNumberedListWithInfo(List<T> options, Function<T, String> infoProvider, int startNumber) {
        for (int i = 0; i < options.size(); i++) {
            T option = options.get(i);
            System.out.printf("  %d. %s%n", i + startNumber, infoProvider.apply(option));
        }
    }

    /**
     * Prints a success message with formatting
     *
     * @param message The success message to display
     */
    public static void printSuccess(String message) {
        System.out.println(GREEN + "✅ SUCCESS: " + message + RESET);
    }

    /**
     * Waits for the user to press Enter to continue
     * 
     * @param scanner The scanner to use for input
     */
    public static void waitForEnter(Scanner scanner) {
        System.out.println(YELLOW + "Press Enter to continue..." + RESET);
        scanner.nextLine();
    }

    /**
     * Gets user confirmation for an action
     * 
     * @param message The confirmation message to display
     * @param scanner The scanner to use for input
     * @return true if confirmed, false otherwise
     */
    public static boolean confirm(String message, Scanner scanner) {
        System.out.println();
        System.out.print(YELLOW + message + " (1=yes/0=no): " + RESET);
        String input = scanner.nextLine();
        return input.equals("1") || input.equalsIgnoreCase("y") || input.equalsIgnoreCase("yes");
    }

    /**
     * Gets a long value from user input
     * 
     * @param prompt The prompt to display
     * @param scanner The scanner to use for input
     * @return The long value entered by the user
     */
    public static long getLongInput(String prompt, Scanner scanner) {
        while (true) {
            try {
                System.out.print(BOLD + prompt + RESET);
                return Long.parseLong(scanner.nextLine());
            } catch (NumberFormatException e) {
                printError("Please enter a valid input.");
            }
        }
    }

    /**
     * Prints a section header with a border.
     *
     * @param title The title of the section to be printed.
     * @param isMainMenu Whether this is a main menu header
     * @param menuType The type of menu (for customizing the icon and text)
     */
    public static void printSectionHeader(String title, boolean isMainMenu, String menuType) {
        String border = "┌─────────────────────────────────────────────┐";

        if (isMainMenu) {
            System.out.println(BOLD + CYAN + "⚙️ " + menuType + " OPTIONS:" + RESET);
        } else {
            String formatted = String.format("│ %1$-43s │", BOLD + PURPLE + title.toUpperCase() + RESET);
            System.out.println(BLUE + border + RESET);
            System.out.println(BLUE + formatted + RESET);
            System.out.println(BLUE + border.replace('┌', '└').replace('┐', '┘') + RESET);
        }
    }

    /**
     * Prints a welcome banner for the CLI
     * 
     * @param title The title to display in the banner
     * @param currentDate The current date to display
     * @param userInfo Information about the current user
     */
    public static void printWelcomeBanner(String title, String currentDate, String userInfo) {
        int totalWidth = 50; // width between the borders ║....║
        System.out.println(CYAN + "╔" + "═".repeat(totalWidth) + "╗" + RESET);
        System.out.println(CYAN + "║" + " ".repeat(totalWidth) + "║" + RESET);

        // Center the title
        int padding = (totalWidth - title.length()) / 2;
        String centeredTitle = " ".repeat(Math.max(0, padding)) + title + " ".repeat(Math.max(0, totalWidth - padding - title.length()));
        System.out.println(CYAN + "║" + RESET + BOLD + BLUE + centeredTitle + RESET + CYAN + "║" + RESET);

        System.out.println(CYAN + "║" + " ".repeat(totalWidth) + "║" + RESET);
        System.out.println(CYAN + "╠" + "═".repeat(totalWidth) + "╣" + RESET);

        // Welcome text
        String welcome = "Welcome!";
        padding = (totalWidth - welcome.length()) / 2;
        String centeredWelcome = " ".repeat(Math.max(0, padding)) + welcome + " ".repeat(Math.max(0, totalWidth - padding - welcome.length()));
        System.out.println(CYAN + "║" + RESET + YELLOW + centeredWelcome + RESET + CYAN + "║" + RESET);

        System.out.println(CYAN + "╚" + "═".repeat(totalWidth) + "╝" + RESET);

        System.out.println(GREEN + "Current date: " + RESET + currentDate);
        System.out.println(GREEN + "Logged in as: " + RESET + userInfo);
    }

    /**
     * Gets a shift type from user input using numbered options
     *
     * @param prompt The prompt to display
     * @return The ShiftType selected by the user
     */
    public static ShiftType getShiftTypeInput(String prompt, Scanner scanner) {
        while (true) {
            System.out.println(BOLD + prompt + RESET);
            System.out.println("1. MORNING");
            System.out.println("2. EVENING");
            System.out.print(CYAN + "======>" + RESET + " Enter your choice (1-2): ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1:
                        return ShiftType.MORNING;
                    case 2:
                        return ShiftType.EVENING;
                    default:
                        printError("Please enter a valid option (1-2).");
                }
            } catch (NumberFormatException e) {
                printError("Please enter a valid number.");
            }
        }
    }
    /**
     * Gets a date from user input using numbered options for common dates
     *
     * @param prompt The prompt to display
     * @return The LocalDate selected by the user
     */
    public static LocalDate getDateInput(String prompt, Scanner scanner) {
        LocalDate today = LocalDate.now();

        while (true) {
            System.out.println(BOLD + prompt + RESET);
            System.out.println("1. Today (" + today.format(dateFormatter) + ")");
            System.out.println("2. Tomorrow (" + today.plusDays(1).format(dateFormatter) + ")");
            System.out.println("3. Next week (" + today.plusDays(7).format(dateFormatter) + ")");
            System.out.println("4. Enter specific date (dd-mm-yyyy):");
            System.out.print(CYAN + "======>" + RESET + " Enter your choice (1-4): ");

            String input = scanner.nextLine();

            // Check if input is a date in the format dd-mm-yyyy
            if (input.matches("\\d{2}-\\d{2}-\\d{4}")) {
                try {
                    return LocalDate.parse(input, dateFormatter);
                } catch (Exception e) {
                    printError("Please enter a valid date in the format dd-mm-yyyy.");
                    continue;
                }
            }

            // Otherwise, try to parse as a menu choice
            try {
                int choice = Integer.parseInt(input);
                switch (choice) {
                    case 1:
                        return today;
                    case 2:
                        return today.plusDays(1);
                    case 3:
                        return today.plusDays(7);
                    case 4:
                        System.out.print(BOLD + "Enter date (dd-mm-yyyy): " + RESET);
                        input = scanner.nextLine();
                        try {
                            return LocalDate.parse(input, dateFormatter);
                        } catch (Exception e) {
                            printError("Please enter a valid date in the format dd-mm-yyyy.");
                            continue;
                        }
                    default:
                        printError("Please enter a valid option (1-4).");
                }
            } catch (NumberFormatException e) {
                printError("Please enter a valid number or date in the format dd-mm-yyyy.");
            } catch (Exception e) {
                printError("Please enter a valid date in the format dd-mm-yyyy.");
            }
        }
    }



    public static LocalTime getHourInput(String prompt, Scanner scanner) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                return parseFlexibleTime(input);
            } catch (DateTimeParseException e) {
                printError("Invalid time format. Try something like 9, 09:00, 5pm, or 5:00 PM.");
            }
        }
    }

    private static LocalTime parseFlexibleTime(String input) throws DateTimeParseException {
        input = input.trim().toLowerCase();

        String[] patterns = {
                "H",         // 9
                "HH",        // 09
                "H:mm",      // 9:00
                "HH:mm",     // 09:00
                "h a",       // 5 PM
                "h:mma",     // 5:00PM
                "h:mm a",    // 5:00 PM
                "hh:mm a"    // 05:00 PM
        };

        for (String pattern : patterns) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                return LocalTime.parse(input.toUpperCase(), formatter);
            } catch (DateTimeParseException ignored) {
                // Try next pattern
            }
        }
        throw new DateTimeParseException("Unrecognized time format", input, 0);
    }

    /**
     * Gets a menu choice from the user within a specified range
     *
     * @param prompt The prompt to display
     * @param min The minimum valid choice
     * @param max The maximum valid choice
     * @param scanner The scanner to use for input
     * @return The user's choice as an integer
     */
    public static int getMenuChoice(String prompt, int min, int max, Scanner scanner) {
        while (true) {
            try {
                System.out.print(BOLD + prompt + RESET);
                int choice = Integer.parseInt(scanner.nextLine());
                if (choice < min || choice > max) {
                    printError("Please enter a number between " + min + " and " + max + ".");
                    continue;
                }
                return choice;
            } catch (NumberFormatException e) {
                printError("Please enter a valid number.");
            }
        }
    }

    /**
     * Gets a positive integer from user input
     *
     * @param prompt The prompt to display
     * @param scanner The scanner to use for input
     * @return The positive integer entered by the user
     */
    public static int getPositiveIntInput(String prompt, Scanner scanner) {
        while (true) {
            try {
                System.out.print(BOLD + prompt + RESET);
                int value = Integer.parseInt(scanner.nextLine());
                if (value <= 0) {
                    printError("Please enter a positive number.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                printError("Please enter a valid number.");
            }
        }
    }

    /**
     * Creates a formatted table for display
     *
     * @param headers The table headers
     * @param data The table data
     * @param columnWidths The widths of each column
     * @return A string representation of the table
     */
    public static String createTable(String[] headers, List<String[]> data, int[] columnWidths) {
        StringBuilder table = new StringBuilder();

        // Create the header row
        table.append(createTableRow(headers, columnWidths, true));

        // Create a separator row
        table.append(createTableSeparator(columnWidths));

        // Create the data rows
        for (String[] row : data) {
            table.append(createTableRow(row, columnWidths, false));
        }

        return table.toString();
    }

    /**
     * Creates a row for the table
     *
     * @param cells The cell values
     * @param columnWidths The widths of each column
     * @param isHeader Whether this is a header row
     * @return A string representation of the row
     */
    private static String createTableRow(String[] cells, int[] columnWidths, boolean isHeader) {
        StringBuilder row = new StringBuilder();

        for (int i = 0; i < cells.length; i++) {
            String cell = cells[i];
            int width = columnWidths[i];

            // Format the cell with proper padding
            String format = "| %-" + width + "s ";
            row.append(String.format(format, cell));
        }

        row.append("|\n");

        return isHeader ? BOLD + row.toString() + RESET : row.toString();
    }

    /**
     * Creates a separator row for the table
     *
     * @param columnWidths The widths of each column
     * @return A string representation of the separator row
     */
    private static String createTableSeparator(int[] columnWidths) {
        StringBuilder separator = new StringBuilder();

        for (int width : columnWidths) {
            separator.append("+");
            for (int i = 0; i < width + 2; i++) {
                separator.append("-");
            }
        }

        separator.append("+\n");

        return separator.toString();
    }

    /**
     * Adds a visual divider to the output
     */
    public static void addDivider() {
        System.out.println("------------------------------------------------");
    }

    /**
     * Displays a paginated view of data
     *
     * @param title The title of the list
     * @param data The list of data to display
     * @param itemsPerPage The number of items to display per page
     * @param displayFunction A function that takes an item and returns a string representation
     * @param scanner The scanner to use for input
     * @param <T> The type of data in the list
     * @return true if the user wants to continue, false if they want to exit
     */
    public static <T> boolean displayPaginatedList(String title, List<T> data, int itemsPerPage, 
                                                 java.util.function.Function<T, String> displayFunction, 
                                                 Scanner scanner) {
        if (data == null || data.isEmpty()) {
            System.out.println(YELLOW + "No data to display." + RESET);
            waitForEnter(scanner);
            return true;
        }

        int currentPage = 0;
        int totalPages = (int) Math.ceil((double) data.size() / itemsPerPage);
        boolean viewing = true;

        while (viewing) {
            // Clear the console (optional, may not work in all environments)
            System.out.print("\033[H\033[2J");
            System.out.flush();

            // Display the title and pagination info
            printSectionHeader(title, false, "");
            System.out.println(BLUE + "Page " + (currentPage + 1) + " of " + totalPages + RESET);
            System.out.println();

            // Calculate start and end indices for the current page
            int startIndex = currentPage * itemsPerPage;
            int endIndex = Math.min(startIndex + itemsPerPage, data.size());

            // Display the items for the current page
            for (int i = startIndex; i < endIndex; i++) {
                T item = data.get(i);
                System.out.println((i - startIndex + 1) + ". " + displayFunction.apply(item));
            }

            // Display navigation options
            System.out.println();
            System.out.println(YELLOW + "Navigation:" + RESET);
            if (currentPage > 0) {
                System.out.println("P. Previous page");
            }
            if (currentPage < totalPages - 1) {
                System.out.println("N. Next page");
            }
            System.out.println("X. Exit list view");

            // Get user input
            System.out.print(BOLD + "Enter your choice: " + RESET);
            String choice = scanner.nextLine().trim().toUpperCase();

            switch (choice) {
                case "P":
                    if (currentPage > 0) {
                        currentPage--;
                    }
                    break;
                case "N":
                    if (currentPage < totalPages - 1) {
                        currentPage++;
                    }
                    break;
                case "X":
                    viewing = false;
                    break;
                default:
                    // Check if the user entered a number to select an item
                    try {
                        int selection = Integer.parseInt(choice);
                        if (selection >= 1 && selection <= (endIndex - startIndex)) {
                            // Handle item selection (optional)
                            int selectedIndex = startIndex + selection - 1;
                            System.out.println(CYAN + "Selected: " + displayFunction.apply(data.get(selectedIndex)) + RESET);
                            waitForEnter(scanner);
                        } else {
                            printError("Invalid selection. Please try again.");
                            waitForEnter(scanner);
                        }
                    } catch (NumberFormatException e) {
                        printError("Invalid choice. Please try again.");
                        waitForEnter(scanner);
                    }
                    break;
            }
        }

        return true;
    }
}
