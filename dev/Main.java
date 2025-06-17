import java.util.Scanner;

import DTOs.SuppliersModuleDTOs.Enums.InitializeState;
import DataAccessLayer.SuppliersDAL.util.Database;
import PresentationLayer.GUI.AppLauncher;
import PresentationLayer.InventoryPresentationSubModule.PresentationMenu;
import PresentationLayer.SuppliersPresentationSubModule.CLI.AppCLI;

public class Main {
   private static Scanner scanner = new Scanner(System.in);

   public static void main(String[] args) {
      if (args.length == 0) {
         System.out.println("No arguments provided. Testing GUI mode.");
         new AppLauncher().run(new String[] {}); // start GUI mode by default
         return;
      } else if (args.length > 1) {
         System.out.println(
               "Too many arguments provided. Usage: java -jar target/superLee-1.0.0-jar-with-dependencies.jar <gui|cli>");
         return;
      }
      String mode = args[0].toLowerCase();
      if (!mode.equals("gui") && !mode.equals("cli")) {
         System.out
               .println("Invalid argument. Usage: java -jar target/superLee-1.0.0-jar-with-dependencies.jar <gui|cli>");
         return;
      }
      switch (mode) {
         case "gui":
            new AppLauncher().run(new String[] {}); // currently, no arguments are needed
            break;
         case "cli":
            startCLI();
            break;
         default:
            System.out.println("Invalid mode. Please use 'gui' or 'cli'.");
            break;
      }
   }

   private static void startCLI() {
      System.out.println("Welcome to the Supplier-Inventory Management System!");
      InitializeState startupState = requestStartupStateFromUser();
      AppCLI appCLI = new AppCLI(startupState);
      PresentationMenu presentationMenu = new PresentationMenu();
      presentationMenu.Initialize(startupState);
      integrateModules(presentationMenu, appCLI);
      boolean showedStatistics = false;
      while (true) {
         System.out.println("\nMain Menu:");
         System.out.println("1. Supplier Management");
         System.out.println("2. Inventory Management");
         System.out.println("3. Exit");
         String choice = appCLI.readLine("Choose an option: ");
         switch (choice) {
            case "1":
               // showedStatistics = askShowStatisticsOnce(showedStatistics);
               appCLI.start();
               break;
            case "2":
               // askShowStatisticsOnce(showedStatistics);
               presentationMenu.Menu();
               break;
            case "3":
               System.out.println("Exiting the application.");
               return;
            default:
               System.out.println("Invalid option, please try again.");
         }
      }
   }

   public static InitializeState requestStartupStateFromUser() {
      System.out.println("Please select the startup state:");
      System.out.println("1. Current state - Load existing data from the database 'as is'.");
      System.out.println("2. Default state - Clear the datebase and start with default data (as in the instructions).");
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

   public static void integrateModules(PresentationMenu pm, AppCLI app) {
      app.integration();
      pm.Integration();
   }

   public static boolean askShowStatisticsOnce(boolean showedStatistics) {
      if (!showedStatistics) {
         System.out.println("Would you like to see the current state of the database? (yes/no)");
         String showCurrentState = scanner.nextLine().trim().toLowerCase();
         if (showCurrentState.equals("yes")) {
            Database.provideStatisticsAboutTheCurrentStateOfTheWholeDatabase();
            return true;
         }
      }
      return false;
   }
}
