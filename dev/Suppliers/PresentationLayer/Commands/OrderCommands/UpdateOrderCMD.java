package Suppliers.PresentationLayer.Commands.OrderCommands;

import java.util.concurrent.atomic.AtomicInteger;
import Suppliers.DTOs.OrderDTO;
import Suppliers.PresentationLayer.CommandInterface;
import Suppliers.PresentationLayer.Forms.OrderForm;
import Suppliers.PresentationLayer.View;
import Suppliers.ServiceLayer.OrderService;
import Suppliers.ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;

public class UpdateOrderCMD implements CommandInterface {
    private final View view;
    private final OrderService orderService;
    private final OrderForm form;

    public UpdateOrderCMD(View view, OrderService orderService, OrderForm form) {
        this.view = view;
        this.orderService = orderService;
        this.form = form;
    }

    @Override
    public void execute() {
        String idStr = view.readLine("Enter existing Order ID to update:");
        int orderId;
        try {
            orderId = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            view.showError("Order ID must be a number.");
            return;
        }

        ServiceResponse<OrderDTO> fetchRes = orderService.getOrderById(orderId);
        if (!fetchRes.isSuccess() || fetchRes.getValue() == null) {
            view.showError("Order with ID " + orderId + " not found.");
            return;
        }
        OrderDTO existingOrder = fetchRes.getValue();

        form.fillUpdate(existingOrder).ifPresent(updatedDto -> {
            try {
                ServiceResponse<?> res = orderService.updateOrder(String.valueOf(updatedDto));
                if (res.isSuccess()) {
                    view.showMessage("-- Order updated successfully --\n" + updatedDto);
                } else {
                    view.showError("-- Failed to update order --");
                    AtomicInteger counter = new AtomicInteger(1);
                    res.getErrors().forEach(error ->
                            view.showError(counter.getAndIncrement() + ". " + error)
                    );
                }
            } catch (Exception e) {
                view.showError("Error updating order: " + e.getMessage());
            }
        });
    }
}
