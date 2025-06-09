package Suppliers.PresentationLayer.Commands.OrderCommands;

import Suppliers.PresentationLayer.CommandInterface;
import Suppliers.PresentationLayer.View;
import Suppliers.ServiceLayer.OrderService;

public class ExecutePeriodicOrdersForThisWeekCMD implements CommandInterface {
   private final View view;
   private final OrderService orderService;

   public ExecutePeriodicOrdersForThisWeekCMD(View view, OrderService orderService) {
      this.view = view;
      this.orderService = orderService;
   }

   @Override
   public void execute() {
      try {
         orderService.executePeriodicOrdersForThisWeek();
         view.showMessage("Periodic orders for this week have been executed successfully.");
      } catch (Exception e) {
         view.showError("Failed to execute periodic orders for this week: " + e.getMessage());
      }
   }

}
