package PresentationLayer;

import DomainLayer.SystemFactory;
import PresentationLayer.EmployeeSubModule.CLI.HR_MainCLI;
import PresentationLayer.TransportPresentation.MainTranSysCLI;
import Util.CliUtil;
import Util.Database;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainCLI {

    private static final Scanner scanner = new Scanner(System.in);

    private static boolean minimalMode = false;

    public static void start() throws IOException {
        try {
            minimalMode = CliUtil.confirm("Do you want to start with minimal data?", scanner);
            Database.init(minimalMode);

            CliUtil.printWelcomeBanner("Welcome to SuperLee System Assignment 2", LocalDate.now().toString(),
                    "Not Logged In");

            while (true) {
                ExitAction action = loginAndRoute();
                if (action == ExitAction.EXIT_PROGRAM) {
                    CliUtil.printInfo("Thanks for using the system! Exiting...");
                    break;
                }
                // else: action == LOGOUT, loop continues and presents login again
            }
        } catch (IOException | SQLException e) {
            CliUtil.printError("Error: " + e.getMessage());
        }
    }

    // Enum to signal whether to logout or exit completely
    private enum ExitAction {
        LOGOUT,
        EXIT_PROGRAM
    }

    private static ExitAction loginAndRoute() throws IOException, SQLException {
        CliUtil.printSectionHeader("Login", false, "" );
        CliUtil.printTip("Enter 0 to exit the program.");
        long userId = CliUtil.getLongInput("Please enter your ID: ", scanner);
        if (userId == 0) {
            return ExitAction.EXIT_PROGRAM; // User chose to exit
        }
        SystemFactory factory = new SystemFactory();
//        boolean minimalMode = config.LOAD_DATA_FROM_DB;
        boolean canAccessTransportModule = false;

        // System Factory creates the Modules components
        SystemFactory.EmployeeModuleComponents employeeComponents = factory.createEmployeeModule(minimalMode);
        SystemFactory.TransportModuleComponents transportComponents = factory.createTransportModule(employeeComponents, minimalMode);


        if (!employeeComponents.getEmployeeService().isEmployeeActive(userId)) {
            CliUtil.printError("User ID cannot access the system.");
            return ExitAction.LOGOUT;
        }
        if (employeeComponents.getEmployeeService().canAccessTransportModule(userId))
            canAccessTransportModule = true;

        if (!canAccessTransportModule) {
            HR_MainCLI mainCLI = factory.createEmployeeCLI(
                    employeeComponents.getEmployeeService(),
                    employeeComponents.getShiftService(),
                    userId
            );
            mainCLI.start();
            // After finishing HR_MainCLI, just return LOGOUT (i.e., return to login screen)
            return ExitAction.LOGOUT;
        } else {
            return mainMenuLoop(factory, employeeComponents, transportComponents, userId);
        }
    }

    private static ExitAction mainMenuLoop(SystemFactory factory, SystemFactory.EmployeeModuleComponents employeeComponents, SystemFactory.TransportModuleComponents transportComponents, long userId) throws IOException {
        while (true) {
            CliUtil.printSectionHeader("Main Menu", true, "SuperLee System");
            // List of options for the main menu
            List<String> options = new ArrayList<>();
            options.add("Employee Module");
            options.add("Transport Module");
            options.add("Exit");
            // Print the options with numbering
            CliUtil.printNumberedList(options, 1);

            int choice = CliUtil.getMenuChoice("Enter your choice (1-" + options.size() + "): ", 1, options.size(), scanner);
            switch (choice) {
                case 1:
                    CliUtil.printInfo("Starting Employee Module...");
                    HR_MainCLI employeeCLI = factory.createEmployeeCLI(
                            employeeComponents.getEmployeeService(),
                            employeeComponents.getShiftService(),
                            userId
                    );
                    employeeCLI.start();
                    break;
                case 2:
                    CliUtil.printInfo("Starting Transport Module...");
                    MainTranSysCLI mainTranSysCLI = factory.createTransportCLI(
                            transportComponents.getTruckService(),
                            transportComponents.getTransportService(),
                            transportComponents.getSiteService(),
                            transportComponents.getStartUpService(),
                            transportComponents.getEmployeeIntegrationService(),
                            transportComponents.getoM()
                    );
                    mainTranSysCLI.transportModuleStartup(userId);
                    break;
                case 3:
                    if (logoutOrExitPrompt()) {
                        // true: user wants to exit program
                        return ExitAction.EXIT_PROGRAM;
                    } else {
                        // false: user wants to logout & return to main login screen
                        return ExitAction.LOGOUT;
                    }
                default:
                    CliUtil.printError("Invalid choice. Please try again.");
                    break;
            }
        }
    }

    // Ask the user: Do you want to log out, or exit program?
    private static boolean logoutOrExitPrompt() {
        CliUtil.printSectionHeader("Logout or Exit", false, "SuperLee System");
        CliUtil.printBold("Do you want to log out or exit the program? ");
        int subChoice = CliUtil.getMenuChoice("type 1 to log out, or 2 to exit the program: ", 1, 2, scanner);
        return subChoice == 2;
    }
}