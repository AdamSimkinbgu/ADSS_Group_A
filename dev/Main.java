import java.sql.Connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Inventory.Presentation.PresentationMenu;
import Suppliers.DataLayer.util.Database;
import Suppliers.PresentationLayer.AppCLI;

public class Main {
   private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

   public static void main(String[] args) {
      System.out.println("Welcome to the Supplier-Inventory Management System!");
      AppCLI appCLI = new AppCLI("data.json");
      PresentationMenu presentationMenu = new PresentationMenu();
      integrateModules(presentationMenu, appCLI);
      while (true) {
         System.out.println("1. Supplier Management");
         System.out.println("2. Inventory Management");
         System.out.println("3. Exit");
         String choice = appCLI.readLine("Choose an option: ");

         switch (choice) {
            case "1":
               appCLI.start();
               break;
            case "2":
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

   public static void integrateModules(PresentationMenu pm, AppCLI app) {
      app.integration();
      pm.Integration();
   }
}
