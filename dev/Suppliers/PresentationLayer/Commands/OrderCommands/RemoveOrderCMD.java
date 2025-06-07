package Suppliers.PresentationLayer.Commands.OrderCommands;

import Suppliers.PresentationLayer.CommandInterface;
import Suppliers.PresentationLayer.View;
import Suppliers.ServiceLayer.OrderService;
import Suppliers.ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;

public class RemoveOrderCMD implements CommandInterface {
    private final View view;
    private final OrderService orderService;

    public RemoveOrderCMD(View view, OrderService orderService) {
        this.view = view;
        this.orderService = orderService;
    }

    @Override
    public void execute() {
        String idStr = view.readLine("Enter Order ID to remove:");
        int orderId;
        try {
            orderId = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            view.showError("Order ID must be a number.");
            return;
        }

        try {
            ServiceResponse<?> res = orderService.removeOrder(orderId);
            if (res.isSuccess()) {
                view.showMessage("-- Order removed successfully --");
            } else {
                view.showError("-- Failed to remove order --");
                res.getErrors().forEach(err -> view.showError(err));
            }
        } catch (Exception e) {
            view.showError("Error removing order: " + e.getMessage());
        }
    }
}
