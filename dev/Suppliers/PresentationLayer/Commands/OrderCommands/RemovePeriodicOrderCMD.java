package Suppliers.PresentationLayer.Commands.OrderCommands;

import Suppliers.PresentationLayer.CommandInterface;
import Suppliers.PresentationLayer.View;
import Suppliers.ServiceLayer.OrderService;
import Suppliers.ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;

public class RemovePeriodicOrderCMD implements CommandInterface {
    private final View view;
    private final OrderService orderService;

    public RemovePeriodicOrderCMD(View view, OrderService orderService) {
        this.view = view;
        this.orderService = orderService;
    }

    @Override
    public void execute() {
        String idStr = view.readLine("Enter Periodic Order ID to remove:");
        int periodicOrderId;
        try {
            periodicOrderId = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            view.showError("Periodic Order ID must be a number.");
            return;
        }

        try {
            ServiceResponse<?> res = orderService.removePeriodicOrder(periodicOrderId);
            if (res.isSuccess()) {
                view.showMessage("-- Periodic order removed successfully --");
            } else {
                view.showError("-- Failed to remove periodic order --");
                res.getErrors().forEach(err -> view.showError(err));
            }
        } catch (Exception e) {
            view.showError("Error removing periodic order: " + e.getMessage());
        }
    }
}
