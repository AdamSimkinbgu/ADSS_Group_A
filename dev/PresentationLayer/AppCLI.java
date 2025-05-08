package PresentationLayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import DomainLayer.AgreementFacade;
import DomainLayer.OrderFacade;
import PresentationLayer.Controllers.OrderController;
import PresentationLayer.Controllers.SupplierAggregateController;
import PresentationLayer.Controllers.SupplierAndAgreementController;
import PresentationLayer.Controllers.SystemController;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import DomainLayer.SupplierFacade;
import ServiceLayer.*;
import ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;

public class AppCLI implements View {
   private OrderController ordersController;
   private SupplierAndAgreementController suppliersController;
   private SystemController systemController;
   private SupplierAggregateController SAContoller;
   private String userInput;
   private static final int TITLE = 0;
   private static final String CMD_MAIN = "0";
   private static final String CMD_SUPPLIER = "1";
   private static final String CMD_ORDER = "2";
   private static final String CMD_SYSTEM = "3";
   private static final String CMD_AGGREGATECONT = "4";
   private static final String CMD_EXIT = "5";
   private static final String CMD_INVALID = "?";
   private String selectedModule = CMD_SYSTEM;
   public static final Scanner scanner = new Scanner(System.in);
   // private final Map<String, Runnable> moduleMenuSelection = new HashMap<>();
   private final Map<String, Runnable> moduleSelection = new HashMap<>();
   private final ObjectMapper mapper = new ObjectMapper();

   public static void main(String[] args) {
      // we should change this so that the data path is optional
      String[] fakeargs = new String[] { "fake/data/path" }; // For testing purposes
      if (fakeargs.length != 1 || fakeargs[0].isEmpty()) {
         System.err.println("Please provide the data path as an argument.");
         System.exit(1);
      } else if (fakeargs[0].equals("help") && fakeargs.length == 1) {
         System.out.println("Help: Provide the data path as an argument.");
         System.out.println("Usage: java AppCLI <dataPath>");
         System.out.println("Example: java AppCLI /path/to/data");
         System.exit(0);
      } else if (!fakeargs[0].isEmpty() && fakeargs.length == 1) {
         System.out.println("Data path provided: " + fakeargs[0]);
      }

      AppCLI app = new AppCLI(fakeargs[0]);
      app.run();
   }

   public AppCLI(String dataPath) {
      // Initialize controllers
      // Initialize the facades and services
      SupplierFacade supplierFacade = new SupplierFacade();
      AgreementFacade agreementFacade = new AgreementFacade();
      OrderFacade orderFacade = new OrderFacade(supplierFacade);
      AgreementService agreementService = new AgreementService(agreementFacade, supplierFacade);
      SupplierService supplierService = new SupplierService(supplierFacade, agreementService);
      OrderService orderService = new OrderService(orderFacade);
      SystemService systemService = new SystemService(supplierFacade, orderFacade, agreementFacade);
      this.SAContoller = new SupplierAggregateController(this, supplierService, agreementService, orderService);
      this.suppliersController = new SupplierAndAgreementController(this, supplierService);
      this.ordersController = new OrderController(this, orderService);
      this.systemController = new SystemController(dataPath, systemService, this);
      moduleSelection.put(CMD_MAIN, () -> System.out.println("Returning to the main menu..."));
      moduleSelection.put(CMD_SUPPLIER, suppliersController::handleModuleMenu);
      moduleSelection.put(CMD_ORDER, ordersController::handleModuleMenu);
      moduleSelection.put(CMD_SYSTEM, systemController::handleModuleMenu);
      moduleSelection.put(CMD_AGGREGATECONT, SAContoller::handleModuleMenu);
      moduleSelection.put(CMD_EXIT, () -> {
         System.out.println("Exiting the system...");
         System.exit(0);
      });
      moduleSelection.put(CMD_INVALID, () -> {
         System.out.println("Invalid choice. Please try again.");
      });
   }

   @Override
   public void showOptions(String title, List<String> options) {
      showMessage(title);
      for (int i = 1; i < options.size(); i++) {
         showMessage(i + ". " + options.get(i));
      }
   }

   private void run() {
      showMessage("CLI started. Welcome!");
      moduleSelection.get(selectedModule).run();
      List<String> mainMenu = this.showMenu();
      while (true) {
         showOptions(mainMenu.get(TITLE), mainMenu);
         this.handleChoice();
      }
   }

   @Override
   public void showMessage(String message) {
      System.out.println(message);
   }

   public <T> void dispatchResponse(String rawJson, Class<T> valueType) {
      try {
         ServiceResponse<T> resp = mapper.readValue(
               rawJson,
               new TypeReference<ServiceResponse<T>>() {
               });

         if (resp.getError() == null || resp.getError().isEmpty()) {
            // success: show the object
            String pretty = mapper.writerWithDefaultPrettyPrinter()
                  .writeValueAsString(resp.getValue());
            this.showMessage("Success: " + pretty);
         } else {
            // failure: show only the error
            this.showError("Failure: " + resp.getError());
         }
      } catch (Exception e) {
         this.showError("Error:" + "Invalid response format: " + e.getMessage());
      }
   }

   @Override
   public String readLine() {
      return scanner.nextLine();
   }

   @Override
   public String readLine(String message) {
      showMessage(message);
      return scanner.nextLine();
   }

   public void showError(String message) {
      System.err.println(message);
   }

   private List<String> showMenu() {
      return List.of(
            "Please select a module to make actions in",
            "Suppliers and Agreements",
            "Orders",
            "System",
            "SupplierAggregateController - MOST UPDATED",
            "Exit");
   }

   private void handleChoice() {
      selectedModule = readLine();
      invokeModuleChoice(userInput);
   }

   private void invokeModuleChoice(String userRequest) {
      moduleSelection.getOrDefault(selectedModule, moduleSelection.get("?")).run();
   }
}
