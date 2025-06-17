package Suppliers.PresentationLayer.Commands.OrderCommands;

import java.util.List;

import Suppliers.DTOs.OrderDTO;
import Suppliers.PresentationLayer.CommandInterface;
import Suppliers.PresentationLayer.View;
import Suppliers.ServiceLayer.OrderService;
import Suppliers.ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;

public class DeliverOrderToInventoryCMD implements CommandInterface {
   private final View view;
   private final OrderService orderService;

   public DeliverOrderToInventoryCMD(View view, OrderService orderService) {
      this.view = view;
      this.orderService = orderService;
   }

   @Override
   public void execute() {
      ServiceResponse<List<OrderDTO>> ordersInDeliveredStatus = orderService.getOrdersInDeliveredStatus();
      if (!ordersInDeliveredStatus.isSuccess()) {
         view.showError("Failed to retrieve orders in delivered status.");
         ordersInDeliveredStatus.getErrors().forEach(view::showError);
         return;
      }
      if (ordersInDeliveredStatus.getValue() == null || ordersInDeliveredStatus.getValue().isEmpty()) {
         view.showMessage("No orders in delivered status to deliver to inventory.");
         return;
      }
      view.showMessage("=== Orders in Delivered Status ===");
      ordersInDeliveredStatus.getValue().forEach(order -> view.showMessage(order.toString()));
      view.showMessage("==================================");

      String idStr = view.readLine("Enter Order ID to deliver to inventory:");
      int orderId;
      try {
         orderId = Integer.parseInt(idStr);
      } catch (NumberFormatException e) {
         view.showError("Order ID must be a number.");
         return;
      }

      try {
         ServiceResponse<?> res = orderService.deliverOrderToInventory(orderId);
         if (res.isSuccess()) {
            view.showMessage("-- Order delivered to inventory successfully --");
         } else {
            view.showError("-- Failed to deliver order to inventory --");
            res.getErrors().forEach(err -> view.showError(err));
         }
      } catch (Exception e) {
         view.showError("Error delivering order to inventory: " + e.getMessage());
      }
   }

}
