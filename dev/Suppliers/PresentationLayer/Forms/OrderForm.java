package Suppliers.PresentationLayer.Forms;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Suppliers.DTOs.AddressDTO;
import Suppliers.DTOs.OrderDTO;
import Suppliers.DTOs.OrderInfoDTO;
import Suppliers.DTOs.OrderItemLineDTO;
import Suppliers.DTOs.Enums.OrderStatus;
import Suppliers.PresentationLayer.InteractiveForm;
import Suppliers.PresentationLayer.View;

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
