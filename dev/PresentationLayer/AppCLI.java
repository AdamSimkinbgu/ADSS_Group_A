package PresentationLayer;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import DomainLayer.*;
import PresentationLayer.CLIs.*;
import PresentationLayer.CLIs.Commands.AgreementCommands.*;
import PresentationLayer.CLIs.Commands.ProductCommands.CreateProductCMD;
import PresentationLayer.CLIs.Commands.ProductCommands.RemoveProductCMD;
import PresentationLayer.CLIs.Commands.SupplierCommands.*;
import PresentationLayer.CLIs.Controllers.*;
import ServiceLayer.*;

public class AppCLI implements View {
   private final SupplierController supplierController;
   private final ProductController productController;
   private final AgreementController agreementController;

   public static final Scanner scanner = new Scanner(System.in);

   public AppCLI(String configJson) {

      // Initialize the facades
      SupplierFacade supplierFacade = new SupplierFacade(true, configJson);
      AgreementFacade agreementFacade = new AgreementFacade();
      // OrderFacade orderFacade = new OrderFacade(supplierFacade);

      // Initialize the services
      AgreementService agreementService = new AgreementService(agreementFacade);
      SupplierService supplierService = new SupplierService(supplierFacade);
      // OrderService orderService = new OrderService(orderFacade);
      // SystemService systemService = new SystemService(supplierFacade, orderFacade,
      // agreementFacade);

      // Initialize the commands
      Map<String, CommandInterface> supplierCommands = initializeSupplierCommands(supplierService);
      Map<String, CommandInterface> productCommands = initializeProductCommands(supplierService);
      // Map<String, CommandInterface> agreementCommands =
      // initializeAgreementCommands(agreementService, supplierService);
      // Map<String, CommandInterface> orderCommands =
      // initializeOrderCommands(orderFacade);
      // Map<String, CommandInterface> crossSystemCommands =
      // initializeCrossSystemCommands(
      // supplierService, agreementService, orderFacade);

      // Initialize the controllers
      this.supplierController = new SupplierController(this, supplierCommands);
      this.productController = new ProductController(this, productCommands);
      this.agreementController = new AgreementController(this, agreementService, supplierService);
      start();
   }

   private Map<String, CommandInterface> initializeSupplierCommands(SupplierService supplierService) {
      Map<String, CommandInterface> commands = new HashMap<>();
      commands.put("CreateSupplierCMD", new CreateSupplierCMD(this, supplierService));
      commands.put("UpdateSupplierCMD", new UpdateSupplierCMD(this, supplierService));
      commands.put("RemoveSupplierCMD", new RemoveSupplierCMD(this, supplierService));
      commands.put("ViewAllSuppliersCMD", new ViewAllSuppliersCMD(this, supplierService));
      return commands;
   }

   private Map<String, CommandInterface> initializeProductCommands(SupplierService supplierService) {
      Map<String, CommandInterface> commands = new HashMap<>();
      commands.put("CreateProductCMD", new CreateProductCMD(this, supplierService));
      // commands.put("UpdateProductCMD", new UpdateProductCMD(this,
      // supplierService));
      commands.put("RemoveProductCMD", new RemoveProductCMD(this, supplierService));
      // commands.put("ViewAllProductsCMD", new ViewAllProductsCMD(this,
      // supplierService));
      return commands;
   }

   private Map<String, CommandInterface> initializeAgreementCommands(AgreementService agreementService,
         SupplierService supplierService) {
      Map<String, CommandInterface> commands = new HashMap<>();
      commands.put("CreateAgreementCMD", new CreateAgreementCMD(this, agreementService, supplierService));
      // commands.put("UpdateAgreementCMD", new UpdateAgreementCMD(this,
      // agreementService));
      commands.put("RemoveAgreementCMD", new RemoveAgreementCMD(this, agreementService, supplierService));
      commands.put("ViewAllAgreementsCMD", new ViewAgreementsBySupplierIdCMD(this, agreementService));
      return commands;
   }

   private Map<String, CommandInterface> initializeOrderCommands(OrderFacade orderFacade) {
      Map<String, CommandInterface> commands = new HashMap<>();
      // commands.put("CreateOrderCMD", new CreateOrderCMD(this, orderFacade));
      // commands.put("UpdateOrderCMD", new UpdateOrderCMD(this, orderFacade));
      // commands.put("RemoveOrderCMD", new RemoveOrderCMD(this, orderFacade));
      // commands.put("ViewAllOrdersCMD", new ViewAllOrdersCMD(this, orderFacade));
      return commands;
   }

   private Map<String, CommandInterface> initializeCrossSystemCommands(SupplierService supplierService,
         AgreementService agreementService, OrderFacade orderFacade) {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'initializeCrossSystemCommands'");
   }

   private void start() {
      while (true) {
         showMessage("Welcome to the Supplier Management System");
         showMessage("1. Supplier Management");
         showMessage("2. Product Management");
         showMessage("3. Agreement Management");
         showMessage("4. Order Management");
         showMessage("Type 'exit' to quit.");

         String choice = readLine("Choose an option: ").toLowerCase();
         if (choice.equals("exit")) {
            break;
         }
         switch (choice) {
            case "1":
               supplierController.start();
               break;
            case "2":
               productController.start();
               break;
            case "3":
               agreementController.start();
               break;
            case "4":
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
