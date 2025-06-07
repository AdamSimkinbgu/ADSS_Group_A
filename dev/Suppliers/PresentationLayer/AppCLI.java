package Suppliers.PresentationLayer;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import Suppliers.DTOs.Enums.InitializeState;
import Suppliers.DomainLayer.*;
import Suppliers.PresentationLayer.CLIs.*;
import Suppliers.PresentationLayer.Commands.AgreementCommands.*;
import Suppliers.PresentationLayer.Commands.OrderCommands.*;
import Suppliers.PresentationLayer.Commands.ProductCommands.*;
import Suppliers.PresentationLayer.Commands.SupplierCommands.*;
import Suppliers.ServiceLayer.*;

public class AppCLI implements View {
   private final SupplierCLI supplierCLI;
   private final ProductCLI productCLI;
   private final AgreementCLI agreementCLI;
   private final OrderCLI orderCLI;
   private IntegrationService integrationService;
   public static final Scanner scanner = new Scanner(System.in);

   public AppCLI(InitializeState initializeState) {

      // Initialize the facades
      SupplierFacade supplierFacade = new SupplierFacade(initializeState);
      OrderFacade orderFacade = new OrderFacade(initializeState, supplierFacade);

      // Initialize the services
      AgreementService agreementService = new AgreementService(supplierFacade);
      SupplierService supplierService = new SupplierService(supplierFacade);
      OrderService orderService = new OrderService(orderFacade);
      IntegrationService.setIntegrationServiceInstance(supplierService, orderService);
      integrationService = IntegrationService.getIntegrationServiceInstance();
      // SystemService systemService = new SystemService(supplierFacade, orderFacade,
      // agreementFacade);

      // Initialize the commands
      Map<String, CommandInterface> supplierCommands = initializeSupplierCommands(supplierService);
      Map<String, CommandInterface> productCommands = initializeProductCommands(supplierService);
      Map<String, CommandInterface> agreementCommands = initializeAgreementCommands(agreementService, supplierService);
      Map<String, CommandInterface> orderCommands = initializeOrderCommands(orderService, supplierService);

      // Initialize the controllers
      this.supplierCLI = new SupplierCLI(this, supplierCommands);
      this.productCLI = new ProductCLI(this, productCommands);
      this.agreementCLI = new AgreementCLI(this, agreementCommands);
      this.orderCLI = new OrderCLI(this, orderCommands);
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
      commands.put("RemoveAgreementCMD", new RemoveAgreementCMD(this, agreementService));
      commands.put("ViewAllAgreementsForSupplierCMD", new ViewAgreementsBySupplierIdCMD(this, agreementService));
      return commands;
   }

   private Map<String, CommandInterface> initializeOrderCommands(OrderService orderService,
         SupplierService supplierService) {
      Map<String, CommandInterface> commands = new HashMap<>();

      commands.put("CreateOrderCMD", new CreateOrderCMD(this, orderService, supplierService));
      commands.put("UpdateOrderCMD", new UpdateOrderCMD(this, orderService, supplierService));
      commands.put("RemoveOrderCMD", new RemoveOrderCMD(this, orderService));
      commands.put("ViewAllOrdersCMD", new ViewAllOrdersCMD(this, orderService));
      // Periodic order commands
      commands.put("CreatePeriodicOrderCMD", new CreatePeriodicOrderCMD(this, orderService, supplierService));
      commands.put("UpdatePeriodicOrderCMD", new UpdatePeriodicOrderCMD(this, orderService, supplierService));
      commands.put("RemovePeriodicOrderCMD", new RemovePeriodicOrderCMD(this, orderService));
      commands.put("ViewAllPeriodicOrdersCMD", new ViewAllPeriodicOrdersCMD(this, orderService));
      commands.put("ViewAllPeriodicOrdersForTodayCMD", new ViewAllPeriodicOrdersForTodayCMD(this, orderService));
      return commands;
   }

   public void start() {
      while (true) {
         showMessage("Welcome to the Supplier Management System");
         showMessage("1. Supplier Management");
         showMessage("2. Product Management");
         showMessage("3. Agreement Management");
         showMessage("4. Order Management");
         showMessage("Type 'back' or '0' to quit.");

         String choice = readLine("Choose an option: ").toLowerCase();
         if (choice.equals("back") || choice.equals("0")) {
            showMessage(" === Exiting the Supplier Management System. Goodbye! ===");
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
               orderCLI.start();
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
      showMessage("--------------------------------------------------------------");
      showMessage(prompt);
      return scanner.nextLine();
   }

   public boolean integration() {
      return integrationService.setMainService();
   }
}
