package PresentationLayer.SuppliersPresentationSubModule.CLI.Commands.OrderCommands;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import DTOs.SuppliersModuleDTOs.CatalogProductDTO;
import DTOs.SuppliersModuleDTOs.PeriodicOrderDTO;
import PresentationLayer.SuppliersPresentationSubModule.CLI.CommandInterface;
import PresentationLayer.SuppliersPresentationSubModule.CLI.View;
import PresentationLayer.SuppliersPresentationSubModule.CLI.Forms.PeriodicOrderForm;
import ServiceLayer.SuppliersServiceSubModule.OrderService;
import ServiceLayer.SuppliersServiceSubModule.SupplierService;
import ServiceLayer.SuppliersServiceSubModule.Interfaces_and_Abstracts.ServiceResponse;

public class UpdatePeriodicOrderCMD implements CommandInterface {
    private final View view;
    private final OrderService orderService;
    private final SupplierService supplierService;
    private final PeriodicOrderForm form;

    public UpdatePeriodicOrderCMD(View view, OrderService orderService, SupplierService supplierService) {
        this.view = view;
        this.orderService = orderService;
        this.supplierService = supplierService;
        this.form = new PeriodicOrderForm(view);
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

        form.fillUpdate(existing).ifPresent(updatedDto -> {
            try {
                ServiceResponse<?> res = orderService.updatePeriodicOrder(updatedDto);
                if (res.isSuccess()) {
                    view.showMessage("-- Periodic order updated successfully --\n" + updatedDto);
                } else {
                    view.showError("-- Failed to update periodic order --");
                    AtomicInteger counter = new AtomicInteger(1);
                    res.getErrors().forEach(error -> view.showError(counter.getAndIncrement() + ". " + error));
                }
            } catch (Exception e) {
                view.showError("Error updating periodic order: " + e.getMessage());
            }
        });
    }
}
