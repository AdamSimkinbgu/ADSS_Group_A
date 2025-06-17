package PresentationLayer.SuppliersPresentationSubModule.CLI.Commands.OrderCommands;

import DTOs.SuppliersModuleDTOs.Enums.OrderStatus;
import PresentationLayer.SuppliersPresentationSubModule.CLI.CommandInterface;
import PresentationLayer.SuppliersPresentationSubModule.CLI.View;
import ServiceLayer.SuppliersServiceSubModule.OrderService;
import ServiceLayer.SuppliersServiceSubModule.Interfaces_and_Abstracts.ServiceResponse;

public class AdvanceOrderStatusCMD implements CommandInterface {
   private final View view;
   private final OrderService orderService;

   public AdvanceOrderStatusCMD(View view, OrderService orderService) {
      this.view = view;
      this.orderService = orderService;
   }

   @Override
   public void execute() {
      String idStr = view.readLine("Enter Order ID to advance status:");
      int orderId;
      try {
         orderId = Integer.parseInt(idStr);
      } catch (NumberFormatException e) {
         view.showError("Order ID must be a number.");
         return;
      }
      ServiceResponse<?> orderResponse = orderService.getOrderById(orderId);
      if (!orderResponse.isSuccess()) {
         view.showError("Failed to retrieve order with ID " + orderId);
         orderResponse.getErrors().forEach(view::showError);
         return;
      }
      if (orderResponse.getValue() == null) {
         view.showError("No order found with ID " + orderId);
         return;
      }
      // Display the current order status
      view.showMessage("Current order status: " + orderResponse.getValue().toString());
      view.showMessage("You can advance the order to the next status in the following sequence:");
      view.showMessage(
            "1. PENDING -> 2. ON_DELIVERY -> 3. DELIVERED [ -> 4. COMPLETED is triggered by the inventory system]");
      // get the orderstatus to advance to
      String statusStr;
      while (true) {
         statusStr = view.readLine("Enter the status to advance to (e.g., 'delivered', 'received', etc.):");
         if (statusStr != null && !statusStr.trim().isEmpty()) {
            try {
               OrderStatus.valueOf(statusStr.trim().toUpperCase());
               break; // Valid status, exit the loop
            } catch (IllegalArgumentException e) {
               view.showError("Invalid status. Please enter a valid status.");
            }

         } else {
            view.showError("Status cannot be empty. Please enter a valid status.");
         }
      }
      OrderStatus status = OrderStatus.valueOf(statusStr.trim().toUpperCase());
      try {
         ServiceResponse<?> res = orderService.advanceOrderStatus(orderId, status);
         if (res.isSuccess()) {
            view.showMessage("-- Order status advanced successfully --");
         } else {
            view.showError("-- Failed to advance order status --");
            res.getErrors().forEach(err -> view.showError(err));
         }
      } catch (Exception e) {
         view.showError("Error advancing order status: " + e.getMessage());
      }
   }

}
