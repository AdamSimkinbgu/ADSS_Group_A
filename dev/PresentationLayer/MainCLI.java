package PresentationLayer;

import DomainLayer.SystemFactory;
import PresentationLayer.EmployeeSubModule.CLI.HR_MainCLI;
import PresentationLayer.InventoryPresentationSubModule.PresentationMenu;
import PresentationLayer.SuppliersPresentationSubModule.CLI.AppCLI;
import PresentationLayer.TransportPresentation.MainTranSysCLI;
import Util.CliUtil;
import Util.Database;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import DTOs.SuppliersModuleDTOs.Enums.InitializeState;

public class MainCLI {

    private static final Scanner scanner = new Scanner(System.in);

    private static boolean minimalMode = false;

    private static AppCLI appCLI;
    private static PresentationMenu presentationMenu;

    public static void start() throws IOException {
        try {
            // Initialization of suppliers and inventory modules
            InitializeState startupState = requestStartupStateFromUser();
            appCLI = new AppCLI(startupState);
            presentationMenu = new PresentationMenu();
            presentationMenu.Initialize(startupState);
            integrateModules(presentationMenu, appCLI);

            // Initialization of hr and transport modules
            minimalMode = CliUtil.confirm("Do you want to start with minimal data?", scanner);
            Database.init(minimalMode);

            // starting point for the CLI
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
        CliUtil.printSectionHeader("Login", false, "");
        CliUtil.printTip("Enter 0 to exit the program.");
        long userId = CliUtil.getLongInput("Please enter your ID: ", scanner);
        if (userId == 0) {
            return ExitAction.EXIT_PROGRAM; // User chose to exit
        }
        SystemFactory factory = new SystemFactory();
        // boolean minimalMode = config.LOAD_DATA_FROM_DB;

        // System Factory creates the Modules components
        SystemFactory.EmployeeModuleComponents employeeComponents = factory.createEmployeeModule(minimalMode);
        SystemFactory.TransportModuleComponents transportComponents = factory.createTransportModule(employeeComponents,
                minimalMode);

        if (!employeeComponents.getEmployeeService().isEmployeeActive(userId)) {
            CliUtil.printError("User ID cannot access the system.");
            return ExitAction.LOGOUT;
        }

        boolean canAccessTransportModule = false;

        if (employeeComponents.getEmployeeService().canAccessTransportModule(userId))
            canAccessTransportModule = true;

        if (!canAccessTransportModule) {
            HR_MainCLI mainCLI = factory.createEmployeeCLI(
                    employeeComponents.getEmployeeService(),
                    employeeComponents.getShiftService(),
                    userId);
            mainCLI.start();
            // After finishing HR_MainCLI, just return LOGOUT (i.e., return to login screen)
            return ExitAction.LOGOUT;
        }

        else {
            return mainMenuLoop(factory, employeeComponents, transportComponents, userId);
        }
    }

    private static ExitAction mainMenuLoop(SystemFactory factory,
            SystemFactory.EmployeeModuleComponents employeeComponents,
            SystemFactory.TransportModuleComponents transportComponents, long userId) throws IOException {
        while (true) {
            CliUtil.printSectionHeader("Main Menu", true, "SuperLee System");
            // List of options for the main menu
            List<String> options = new ArrayList<>();
            options.add("Employee Module");
            options.add("Transport Module");
            options.add("Supplier Module");
            options.add("Inventory Module");
            options.add("Exit");
            // Print the options with numbering
            CliUtil.printNumberedList(options, 1);

            int choice = CliUtil.getMenuChoice("Enter your choice (1-" + options.size() + "): ", 1, options.size(),
                    scanner);
            switch (choice) {
                case 1:
                    CliUtil.printInfo("Starting Employee Module...");
                    HR_MainCLI employeeCLI = factory.createEmployeeCLI(
                            employeeComponents.getEmployeeService(),
                            employeeComponents.getShiftService(),
                            userId);
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
                            transportComponents.getoM());
                    mainTranSysCLI.transportModuleStartup(userId);
                    break;
                case 3:
                    CliUtil.printInfo("Starting Supplier Module...");
                    boolean canAccessSuppliers = employeeComponents.getEmployeeService()
                            .canAccessSuppliersModule(userId);
                    if (!canAccessSuppliers) {
                        CliUtil.printError("You do not have permission to access the Supplier Module.");
                        break;
                    }
                    appCLI.start();
                    break;
                case 4:
                    CliUtil.printInfo("Starting Inventory Module...");
                    boolean canAccessInventory = employeeComponents.getEmployeeService()
                            .canAccessInventoryModule(userId);
                    if (!canAccessInventory) {
                        CliUtil.printError("You do not have permission to access the Inventory Module.");
                        break;
                    }
                    presentationMenu.Menu();
                    break;
                case 5:
                    if (logoutOrExitPrompt()) {
                        // true: user wants to exit program
                        return ExitAction.EXIT_PROGRAM;
                    } else {
                        // false: user wants to logout & return to main login screen
                        return ExitAction.LOGOUT;
                    }
                default:
                    CliUtil.printError("Invalid choice. Please try again.");
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

    public static void integrateModules(PresentationMenu pm, AppCLI app) {
        app.integration();
        pm.Integration();
    }

    public static InitializeState requestStartupStateFromUser() {
        System.out.println("Please select the startup state for Suppliers/Inventory modules:");
        System.out.println("1. Current state - Load existing data from the database 'as is'.");
        System.out
                .println("2. Default state - Clear the datebase and start with default data (as in the instructions).");
        System.out.println("3. No data state - Clear the database and start with no data.");
        System.out.println("4. Exit the application.");
        System.out.print("Enter your choice (1-4): ");
        String choice = scanner.nextLine().trim();
        while (true) {
            switch (choice) {
                case "1":
                    return InitializeState.CURRENT_STATE;
                case "2":
                    return InitializeState.DEFAULT_STATE;
                case "3":
                    return InitializeState.NO_DATA_STATE;
                case "4":
                    System.out.println("Exiting the application.");
                    System.exit(0);
                    return null; // This line will never be reached, but is needed to satisfy the compiler.
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 4.");
                    System.out.print("Enter your choice (1-4): ");
                    choice = scanner.nextLine().trim();
            }
        }
    }
}