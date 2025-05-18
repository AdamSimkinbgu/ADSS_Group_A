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

   public static final Scanner scanner = new Scanner(System.in);
   private final ObjectMapper mapper = new ObjectMapper();

   public AppCLI(String dataPath) {
      // Initialize controllers
      // Initialize the facades and services
      SupplierFacade supplierFacade = new SupplierFacade();
      AgreementFacade agreementFacade = new AgreementFacade();
      OrderFacade orderFacade = new OrderFacade(supplierFacade);
      AgreementService agreementService = new AgreementService(agreementFacade, supplierFacade);
      SupplierService supplierService = new SupplierService(supplierFacade);
      OrderService orderService = new OrderService(orderFacade);
      SystemService systemService = new SystemService(supplierFacade, orderFacade, agreementFacade);
      this.SAContoller = new SupplierAggregateController(this, supplierService, agreementService, orderService);
      this.suppliersController = new SupplierAndAgreementController(this, supplierService);
      this.ordersController = new OrderController(this, orderService);
      this.systemController = new SystemController(dataPath, systemService, this);

      run();
   }

   private void run() {
      // @TODO: Add a loop to keep the application running until the user chooses to
      // exit
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

   @Override
   public void dispatchResponse(ServiceResponse<?> res) {
      if (res.isSuccess()) {
         showMessage("Operation successful: " + res.getValue());
      } else {
         showError("Operation failed: " + res.getError());
      }
   }

}
