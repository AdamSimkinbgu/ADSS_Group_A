package PresentationLayer.SuppliersPresentationSubModule.CLI.Commands.OrderCommands;

import java.util.List;

import DTOs.SuppliersModuleDTOs.PeriodicOrderDTO;
import PresentationLayer.SuppliersPresentationSubModule.CLI.CommandInterface;
import PresentationLayer.SuppliersPresentationSubModule.CLI.View;
import ServiceLayer.SuppliersServiceSubModule.OrderService;
import ServiceLayer.SuppliersServiceSubModule.Interfaces_and_Abstracts.ServiceResponse;

public class ViewAllPeriodicOrdersCMD implements CommandInterface {
    private final View view;
    private final OrderService orderService;

    public ViewAllPeriodicOrdersCMD(View view, OrderService orderService) {
        this.view = view;
        this.orderService = orderService;
    }

    @Override
    public void execute() {
        try {
            ServiceResponse<List<PeriodicOrderDTO>> res = orderService.getAllPeriodicOrders();
            if (res.isSuccess()) {
                List<PeriodicOrderDTO> orders = res.getValue();
                if (orders == null || orders.isEmpty()) {
                    view.showMessage("No periodic orders to display.");
                } else {
                    view.showMessage("=== All Periodic Orders ===");
                    for (PeriodicOrderDTO dto : orders) {
                        view.showMessage(dto.toString());
                    }
                }
            } else {
                view.showError("Failed to retrieve periodic orders:");
                res.getErrors().forEach(err -> view.showError(err));
            }
        } catch (Exception e) {
            view.showError("Error retrieving periodic orders: " + e.getMessage());
        }
    }
}
