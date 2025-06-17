package PresentationLayer.SuppliersPresentationSubModule.CLI.Commands.OrderCommands;

import java.util.List;

import DTOs.SuppliersModuleDTOs.OrderDTO;
import PresentationLayer.SuppliersPresentationSubModule.CLI.CommandInterface;
import PresentationLayer.SuppliersPresentationSubModule.CLI.View;
import ServiceLayer.SuppliersServiceSubModule.OrderService;
import ServiceLayer.SuppliersServiceSubModule.Interfaces_and_Abstracts.ServiceResponse;

public class ViewAllOrdersCMD implements CommandInterface {
    private final View view;
    private final OrderService orderService;

    public ViewAllOrdersCMD(View view, OrderService orderService) {
        this.view = view;
        this.orderService = orderService;
    }

    @Override
    public void execute() {
        try {
            ServiceResponse<List<OrderDTO>> res = orderService.getAllOrders();
            if (res.isSuccess()) {
                List<OrderDTO> orders = res.getValue();
                if (orders == null || orders.isEmpty()) {
                    view.showMessage("No orders to display.");
                } else {
                    view.showMessage("=== All Orders ===");
                    for (OrderDTO dto : orders) {
                        view.showMessage(dto.toString());
                    }
                }
            } else {
                view.showError("Failed to retrieve orders:");
                res.getErrors().forEach(err -> view.showError(err));
            }
        } catch (Exception e) {
            view.showError("Error retrieving orders: " + e.getMessage());
        }
    }
}
