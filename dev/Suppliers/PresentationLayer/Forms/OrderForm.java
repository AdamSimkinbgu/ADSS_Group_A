package Suppliers.PresentationLayer.Forms;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import Suppliers.DTOs.AddressDTO;
import Suppliers.DTOs.OrderDTO;
import Suppliers.DTOs.OrderItemLineDTO;
import Suppliers.DTOs.Enums.OrderStatus;
import Suppliers.PresentationLayer.InteractiveForm;
import Suppliers.PresentationLayer.View;

public class OrderForm extends InteractiveForm<OrderDTO> {

    public OrderForm(View view) {
        super(view);
    }

    @Override
    protected OrderDTO build() throws Cancelled {
        view.showMessage("Creating a new Order... (enter 'cancel' to cancel)");
        view.showMessage("Please fill in the following details:");
        int supplierId = askInt("Enter the supplier ID:");
        LocalDate orderDate = askDate("Enter the order date (DD-MM-YYYY):");
        LocalDate creationDate = LocalDate.now();
        AddressDTO addressDTO = askAddress("Enter the delivery address details:");
        String contactPhoneNumber = askPhone("Enter contact phone number:");
        List<OrderItemLineDTO> items = new ArrayList<>();
        while (true) {
            boolean more = askBoolean("Do you want to add an Order Item? (y/n):");
            if (!more) {
                break;
            }

            int productId = askInt("Enter product ID:");
            int quantity = askInt("Enter quantity:");
            OrderItemLineDTO line = new OrderItemLineDTO(productId, quantity);
            items.add(line);
        }
        OrderStatus status = OrderStatus.SENT;
        OrderDTO dto = new OrderDTO(supplierId, orderDate, creationDate,
                addressDTO, contactPhoneNumber, items, status);
        return dto;
    }

    @Override
    protected OrderDTO update(OrderDTO dto) throws Cancelled {
        view.showMessage("Updating Order... (enter 'cancel' to cancel)");
        view.showMessage("Current details: " + dto);
        if (askBoolean("Do you want to update the order date? (y/n):")) {
            dto.setOrderDate(askDate("Enter the new order date (DD-MM-YYYY):"));
        }
        int orderLineId = askInt("Enter the Order Item Line ID to update:");
        for (OrderItemLineDTO item : dto.getItems()) {
            if (item.getOrderItemLineID() == orderLineId) {
                if (askBoolean("Do you want to update the quantity? (y/n):")) {
                    item.setQuantity(askInt("Enter the new quantity:"));
                }
                if (askBoolean("Do you want to update the unit price? (y/n):")) {
                    item.setUnitPrice(askBigDecimal("Enter the new unit price:"));
                }
                if (askBoolean("Do you want to update the discount? (y/n):")) {
                    item.setDiscount(askBigDecimal("Enter the new discount:"));
                }
                break;
            }
        }
        if (askBoolean("Do you want to update the delivery address? (y/n):")) {
            dto.setAddress(askAddress("Enter the new delivery address details:"));
        }
        if (askBoolean("Do you want to update the contact phone number? (y/n):")) {
            dto.setContactPhoneNumber(askPhone("Enter the new contact phone number:"));
        }
        return dto;
    }

}
