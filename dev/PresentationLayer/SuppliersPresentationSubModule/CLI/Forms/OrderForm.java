package PresentationLayer.SuppliersPresentationSubModule.CLI.Forms;

import java.time.LocalDate;
import java.util.HashMap;

import DTOs.SuppliersModuleDTOs.OrderInfoDTO;
import PresentationLayer.SuppliersPresentationSubModule.CLI.InteractiveForm;
import PresentationLayer.SuppliersPresentationSubModule.CLI.View;

public class OrderForm extends InteractiveForm<OrderInfoDTO> {

    public OrderForm(View view) {
        super(view);
    }

    @Override
    protected OrderInfoDTO build() throws Cancelled {
        view.showMessage("Creating a new Order... (enter 'cancel' to cancel)");
        view.showMessage("Please fill in the following details:");
        LocalDate orderDate = askDate("Enter the order date (DD-MM-YYYY):");
        HashMap<Integer, Integer> products = askForOrderProducts(new HashMap<>());

        return new OrderInfoDTO(
                orderDate,
                products);
    }

    @Override
    protected OrderInfoDTO update(OrderInfoDTO dto) throws Cancelled {
        view.showMessage("Updating Order... (enter 'cancel' to cancel)");
        view.showMessage("Current details: " + dto);
        if (askBoolean("Do you want to update the order date? (y/n):")) {
            dto.setOrderDate(askDate("Enter the new order date (DD-MM-YYYY):"));
        }
        if (askBoolean("Do you want to update the products? (y/n):")) {
            HashMap<Integer, Integer> products = askForOrderProducts(dto.getProducts());
            dto.setProducts(products);
        }
        return dto;
    }

}
