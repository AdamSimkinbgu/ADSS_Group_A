package Suppliers.PresentationLayer.Commands.OrderCommands;

import java.util.concurrent.atomic.AtomicInteger;
import Suppliers.DTOs.PeriodicOrderDTO;
import Suppliers.PresentationLayer.CommandInterface;
import Suppliers.PresentationLayer.Forms.PeriodicOrderForm;
import Suppliers.PresentationLayer.View;
import Suppliers.ServiceLayer.OrderService;
import Suppliers.ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;

public class UpdatePeriodicOrderCMD implements CommandInterface {
    private final View view;
    private final OrderService orderService;
    private final PeriodicOrderForm form;

    public UpdatePeriodicOrderCMD(View view, OrderService orderService, PeriodicOrderForm form) {
        this.view = view;
        this.orderService = orderService;
        this.form = form;
    }

    @Override
    public void execute() {
        String idStr = view.readLine("Enter Periodic Order ID to update:");
        int periodicOrderId;
        try {
            periodicOrderId = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            view.showError("Periodic Order ID must be a number.");
            return;
        }

        ServiceResponse<PeriodicOrderDTO> fetchRes = orderService.getPeriodicOrderById(periodicOrderId);
        if (!fetchRes.isSuccess() || fetchRes.getValue() == null) {
            view.showError("Periodic order with ID " + periodicOrderId + " not found.");
            return;
        }
        PeriodicOrderDTO existing = fetchRes.getValue();

        form.fillUpdate(existing).ifPresent(updatedDto -> {
            try {
                ServiceResponse<?> res = orderService.updatePeriodicOrder(updatedDto);
                if (res.isSuccess()) {
                    view.showMessage("-- Periodic order updated successfully --\n" + updatedDto);
                } else {
                    view.showError("-- Failed to update periodic order --");
                    AtomicInteger counter = new AtomicInteger(1);
                    res.getErrors().forEach(error ->
                            view.showError(counter.getAndIncrement() + ". " + error)
                    );
                }
            } catch (Exception e) {
                view.showError("Error updating periodic order: " + e.getMessage());
            }
        });
    }
}
