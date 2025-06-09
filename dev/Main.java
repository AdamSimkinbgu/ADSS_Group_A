import java.util.Scanner;

import Inventory.Presentation.PresentationMenu;
import Suppliers.DTOs.Enums.InitializeState;
import Suppliers.DataLayer.util.Database;
import Suppliers.PresentationLayer.AppCLI;

public class Main {
   private static Scanner scanner = new Scanner(System.in);

   public static void main(String[] args) {
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
