package Suppliers.PresentationLayer.Commands.OrderCommands;

import java.util.List;

import Suppliers.DTOs.OrderDTO;
import Suppliers.PresentationLayer.CommandInterface;
import Suppliers.PresentationLayer.View;
import Suppliers.ServiceLayer.OrderService;
import Suppliers.ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;

public class ViewAllPeriodicOrdersForTodayCMD implements CommandInterface {
   private final View view;
   private final OrderService orderService;

   public ViewAllPeriodicOrdersForTodayCMD(View view, OrderService orderService) {
      this.view = view;
      this.orderService = orderService;
   }

   public void execute() {
      view.showMessage("Fetching all periodic orders for today...");
      ServiceResponse<List<OrderDTO>> response = orderService.getAllPeriodicOrdersForToday();

      if (response.isSuccess() && response.getValue() != null) {
         List<OrderDTO> orders = response.getValue();
         if (orders.isEmpty()) {
            view.showMessage("No periodic orders found for today.");
         } else {
            view.showMessage("Periodic orders for today:");
            orders.forEach(order -> view.showMessage(order.toString()));
         }
      } else {
         view.showError("Failed to fetch periodic orders: " + response.getErrors());
      }
   }
}
