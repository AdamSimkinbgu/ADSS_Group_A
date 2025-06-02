package Suppliers.PresentationLayer;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import Inventory.Service.MainService;
import Suppliers.DTOs.Enums.InitializeState;
import Suppliers.DomainLayer.*;
import Suppliers.PresentationLayer.CLIs.*;
import Suppliers.PresentationLayer.Commands.AgreementCommands.*;
import Suppliers.PresentationLayer.Commands.ProductCommands.CreateProductCMD;
import Suppliers.PresentationLayer.Commands.ProductCommands.RemoveProductCMD;
import Suppliers.PresentationLayer.Commands.ProductCommands.UpdateProductCMD;
import Suppliers.PresentationLayer.Commands.ProductCommands.ViewAllProductsCMD;
import Suppliers.PresentationLayer.Commands.SupplierCommands.*;
import Suppliers.ServiceLayer.*;

public class AppCLI implements View {
   private final SupplierCLI supplierCLI;
   private final ProductCLI productCLI;
   private final AgreementCLI agreementCLI;
   private IntegrationService integrationService;

   public static final Scanner scanner = new Scanner(System.in);

   public AppCLI(InitializeState initializeState) {

      // Initialize the facades
      SupplierController supplierFacade = new SupplierController(initializeState);

      // OrderFacade orderFacade = new OrderFacade(supplierFacade);

      // Initialize the services
      AgreementService agreementService = new AgreementService(supplierFacade);
      SupplierService supplierService = new SupplierService(supplierFacade);
      integrationService = IntegrationService.getIntegrationServiceInstance();
      // OrderService orderService = new OrderService(orderFacade);
      // SystemService systemService = new SystemService(supplierFacade, orderFacade,
      // agreementFacade);

      // Initialize the commands
      Map<String, CommandInterface> supplierCommands = initializeSupplierCommands(supplierService);
      Map<String, CommandInterface> productCommands = initializeProductCommands(supplierService);
      Map<String, CommandInterface> agreementCommands = initializeAgreementCommands(agreementService, supplierService);
      // Map<String, CommandInterface> orderCommands =
      // initializeOrderCommands(orderFacade);

      // Initialize the controllers
      this.supplierCLI = new SupplierCLI(this, supplierCommands);
      this.productCLI = new ProductCLI(this, productCommands);
      this.agreementCLI = new AgreementCLI(this, agreementCommands);
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
      commands.put("UpdateProductCMD", new UpdateProductCMD(this, supplierService));
      commands.put("RemoveProductCMD", new RemoveProductCMD(this, supplierService));
      commands.put("ViewAllProductsCMD", new ViewAllProductsCMD(this, supplierService));
      return commands;
   }

   private Map<String, CommandInterface> initializeAgreementCommands(AgreementService agreementService,
         SupplierService supplierService) {
      Map<String, CommandInterface> commands = new HashMap<>();
      commands.put("CreateAgreementCMD", new CreateAgreementCMD(this, agreementService));
      commands.put("UpdateAgreementCMD", new UpdateAgreementCMD(this, agreementService));
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

   public void start() {
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
               supplierCLI.start();
               break;
            case "2":
               productCLI.start();
               break;
            case "3":
               agreementCLI.start();
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

   public boolean integration() {
      return integrationService.setMainService();
   }
}
