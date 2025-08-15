package PresentationLayer.SuppliersPresentationSubModule.CLI.Commands.OrderCommands;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import DTOs.SuppliersModuleDTOs.CatalogProductDTO;
import PresentationLayer.SuppliersPresentationSubModule.CLI.CommandInterface;
import PresentationLayer.SuppliersPresentationSubModule.CLI.View;
import PresentationLayer.SuppliersPresentationSubModule.CLI.Forms.PeriodicOrderForm;
import ServiceLayer.SuppliersServiceSubModule.OrderService;
import ServiceLayer.SuppliersServiceSubModule.SupplierService;
import ServiceLayer.SuppliersServiceSubModule.Interfaces_and_Abstracts.ServiceResponse;

public class CreatePeriodicOrderCMD implements CommandInterface {
    private final View view;
    private final OrderService orderService;
    private final SupplierService supplierService;
    private final PeriodicOrderForm form;

    public CreatePeriodicOrderCMD(View view, OrderService orderService, SupplierService supplierService) {
        this.view = view;
        this.orderService = orderService;
        this.supplierService = supplierService;
        this.form = new PeriodicOrderForm(view);
    }

    @Override
    public void execute() {
        ServiceResponse<List<CatalogProductDTO>> catalogProducts = supplierService.getAllProducts();
        if (!catalogProducts.isSuccess() || catalogProducts.getValue() == null) {
            view.showError("Failed to fetch catalog products.");
            return;
        }
        List<CatalogProductDTO> products = catalogProducts.getValue();
        if (products.isEmpty()) {
            view.showError(
                    "No products available in the catalog. Please add products before creating a periodic order.");
            return;
        }
        view.showMessage("Available products in the catalog:");
        AtomicInteger productCounter = new AtomicInteger(1);
        String format = "%-7s";
        products.forEach(product -> view.showMessage(
                String.format(format, productCounter.getAndIncrement() + ". ") + product.toString()));

        form.fillBuild().ifPresent(periodicOrderDTO -> {
            try {
                ServiceResponse<?> res = orderService.createPeriodicOrder(periodicOrderDTO);
                if (res.isSuccess()) {
                    view.showMessage("-- Periodic order created successfully --\n" +
                            periodicOrderDTO);
                } else {
                    view.showError("-- Failed to create periodic order --");
                    AtomicInteger counter = new AtomicInteger(1);
                    res.getErrors().forEach(error -> view.showError(counter.getAndIncrement() + ". " + error));
                }
            } catch (Exception e) {
                view.showError("Error creating periodic order: " + e.getMessage());
            }
        });
    }
}
