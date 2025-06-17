package Suppliers.PresentationLayer.Commands.OrderCommands;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import Suppliers.DTOs.CatalogProductDTO;
import Suppliers.DTOs.OrderDTO;
import Suppliers.DTOs.OrderInfoDTO;
import Suppliers.PresentationLayer.CommandInterface;
import Suppliers.PresentationLayer.Forms.OrderForm;
import Suppliers.PresentationLayer.View;
import Suppliers.ServiceLayer.OrderService;
import Suppliers.ServiceLayer.SupplierService;
import Suppliers.ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;

public class UpdateOrderCMD implements CommandInterface {
    private final View view;
    private final OrderService orderService;
    private final SupplierService supplierService;
    private final OrderForm form;

    public UpdateOrderCMD(View view, OrderService orderService, SupplierService supplierService) {
        this.view = view;
        this.orderService = orderService;
        this.supplierService = supplierService;
        this.form = new OrderForm(view);
    }

    @Override
    public void execute() {
        view.showMessage("You can update an existing order by providing its ID.");
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

        ServiceResponse<List<CatalogProductDTO>> catalogProducts = supplierService.getAllProducts();
        if (!catalogProducts.isSuccess() || catalogProducts.getValue() == null) {
            view.showError("Failed to fetch catalog products.");
            return;
        }
        List<CatalogProductDTO> products = catalogProducts.getValue();
        if (products.isEmpty()) {
            view.showError(
                    "No products available in the catalog. Please take in mind that adding products that are not in the catalog will not be saved.");
        }
        view.showMessage("Available products in the catalog:");
        AtomicInteger productCounter = new AtomicInteger(1);
        String format = "%-7s";
        products.forEach(product -> view.showMessage(
                String.format(format, productCounter.getAndIncrement() + ". ") + product.toString()));

        form.fillUpdate(new OrderInfoDTO(existingOrder)).ifPresent(updatedDto -> {
            try {
                ServiceResponse<?> res = orderService.updateOrder(updatedDto);
                if (res.isSuccess()) {
                    view.showMessage("-- Order updated successfully --\n" + updatedDto);
                } else {
                    view.showError("-- Failed to update order --");
                    AtomicInteger counter = new AtomicInteger(1);
                    res.getErrors().forEach(error -> view.showError(counter.getAndIncrement() + ". " + error));
                }
            } catch (Exception e) {
                view.showError("Error updating order: " + e.getMessage());
            }
        });
    }
}
