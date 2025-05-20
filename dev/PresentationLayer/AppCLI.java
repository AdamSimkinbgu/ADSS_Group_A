package PresentationLayer;

import java.util.Scanner;

import DomainLayer.AgreementFacade;
import DomainLayer.OrderFacade;
import com.fasterxml.jackson.databind.ObjectMapper;

import DomainLayer.SupplierFacade;
// import PresentationLayer.Controllers.SupplierController;
import ServiceLayer.*;

public class AppCLI implements View {
   // private final SupplierController supplierController;

   public static final Scanner scanner = new Scanner(System.in);
   private final ObjectMapper mapper = new ObjectMapper();

   public AppCLI(String dataPath) {
      SupplierFacade supplierFacade = new SupplierFacade();
      AgreementFacade agreementFacade = new AgreementFacade();
      OrderFacade orderFacade = new OrderFacade(supplierFacade);
      // AgreementService agreementService = new AgreementService(agreementFacade);
      SupplierService supplierService = new SupplierService(supplierFacade);
      OrderService orderService = new OrderService(orderFacade);
      SystemService systemService = new SystemService(supplierFacade, orderFacade, agreementFacade);
      // Initialize the controllers
      // this.supplierController = new SupplierController(this, supplierService);
      start();
   }

   private void start() {
      while (true) {
         showMessage("Welcome to the Supplier Management System");
         showMessage("1. Supplier Management");
         showMessage("2. Agreement Management");
         showMessage("3. Order Management");
         showMessage("Type 'exit' to quit.");

         String choice = readLine("Choose an option: ").toLowerCase();
         if (choice.equals("exit")) {
            break;
         }
         switch (choice) {
            case "1":
               // supplierController.start();
               break;
            case "2":
               // agreementController.start();
               break;
            case "3":
               // orderController.start();
               break;
            default:
               showError("Invalid option");
         }
      }
   }

   @Override
   public void showMessage(String message) {
      System.out.println(message);
   }

   @Override
   public void showError(String msg) {
      System.err.println(msg);
   }

   @Override
   public String readLine(String prompt) {
      showMessage(prompt);
      return scanner.nextLine();
   }

}
