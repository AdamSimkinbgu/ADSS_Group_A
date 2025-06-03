package Suppliers.PresentationLayer.Forms;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import Suppliers.DTOs.AddressDTO;
import Suppliers.DTOs.OrderDTO;
import Suppliers.DTOs.OrderItemLineDTO;
import Suppliers.DTOs.Enums.OrderStatus;
import Suppliers.DomainLayer.Classes.Address;
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

        // 1) Supplier ID
        int supplierId = askInt("Enter the supplier ID:");

        // 2) Supplier Name
        String supplierName = askNonEmpty("Enter the supplier name:");

        // 3) Order Date (must be today or in the future)
        LocalDate orderDate;
        while (true) {
            String orderDateStr = askNonEmpty("Enter the order date (DD-MM-YYYY):");
            try {
                orderDate = askDate(orderDateStr);
                if (orderDate.isBefore(LocalDate.now())) {
                    view.showError("Order date must be today or in the future. Please enter a valid date.");
                    continue;
                }
                break;
            } catch (Exception e) {
                view.showError("Invalid date format. Please use DD-MM-YYYY.");
            }
        }

        // 4) Creation Date (set to now)
        LocalDate creationDate = LocalDate.now();

        //
        // ─── 5) ADDRESS (use AddressDTO) ───────────────────────────────────────────
        //
        view.showMessage("Enter the delivery address details:");
        String street = askNonEmpty("Enter street:");
        String city   = askNonEmpty("Enter city:");
        String buildingNumber = askNonEmpty("Enter building number:");

        // Build an AddressDTO, then convert to domain Address:
        AddressDTO addressDTO = new AddressDTO(street, city, buildingNumber);
        Address address = AddressDTO.toAddress(addressDTO);

        // 6) Contact Phone Number
        String contactPhoneNumber = askNonEmpty("Enter contact phone number:");

        // 7) Order Items (unchanged)
        List<OrderItemLineDTO> items = new ArrayList<>();
        while (true) {
            String more = askNonEmpty("Do you want to add an Order Item? (y/n):").trim().toLowerCase();
            if (more.startsWith("n")) {
                break;
            }

            int productId = askInt("Enter product ID:");
            int supplierCatalogNumber = askInt("Enter supplier product catalog number:");
            int quantity = askInt("Enter quantity:");

            BigDecimal unitPrice;
            while (true) {
                String unitPriceStr = askNonEmpty("Enter unit price:");
                try {
                    unitPrice = new BigDecimal(unitPriceStr);
                    break;
                } catch (NumberFormatException e) {
                    view.showError("Unit price must be a number.");
                }
            }

            String productName = askNonEmpty("Enter product name:");

            // Optional discount:
            BigDecimal discount = BigDecimal.ZERO;
            String hasDiscount = askNonEmpty("Is there a discount for this item? (y/n):").trim().toLowerCase();
            if (hasDiscount.startsWith("y")) {
                while (true) {
                    String discountStr = askNonEmpty("Enter discount percentage (0–100):");
                    try {
                        BigDecimal perc = new BigDecimal(discountStr);
                        if (perc.compareTo(BigDecimal.ZERO) < 0 || perc.compareTo(new BigDecimal("100")) > 0) {
                            view.showError("Discount must be between 0 and 100.");
                            continue;
                        }
                        BigDecimal hundred = new BigDecimal("100");
                        discount = hundred.subtract(perc).divide(hundred);
                        break;
                    } catch (NumberFormatException e) {
                        view.showError("Discount must be a number.");
                    }
                }
            }

            OrderItemLineDTO line = new OrderItemLineDTO(
                    -1,                     // orderID (will be set later)
                    supplierCatalogNumber,
                    quantity,
                    unitPrice,
                    productName
            );
            line.setProductId(productId);
            line.setDiscount(discount);
            items.add(line);
        }

        // 8) Order Status (default to SENT)
        OrderStatus status = OrderStatus.SENT;

        // 9) Build and return the OrderDTO
        OrderDTO dto = new OrderDTO(
                -1,                 // orderId (database will assign later)
                supplierId,
                supplierName,
                orderDate,
                creationDate,
                address,            // domain-layer Address from AddressDTO.toAddress(...)
                contactPhoneNumber,
                items,
                status
        );
        return dto;
    }

    @Override
    protected OrderDTO update(OrderDTO dto) throws Cancelled {
        view.showMessage("Updating Order... (enter 'cancel' to cancel)");
        view.showMessage("Current details: " + dto);

        String fieldToUpdate = askNonEmpty(
                "What do you want to change? " +
                        "(supplierId, supplierName, orderDate, address, contactPhoneNumber, status, items):"
        ).trim().toLowerCase();

        switch (fieldToUpdate) {
            case "supplierid":
                int newSupplierId = askInt("Enter new supplier ID:");
                dto.setSupplierId(newSupplierId);
                break;

            case "suppliername":
                String newSupplierName = askNonEmpty("Enter new supplier name:");
                dto.setSupplierName(newSupplierName);
                break;

            case "orderdate":
                LocalDate newOrderDate;
                while (true) {
                    String s = askNonEmpty("Enter new order date (DD-MM-YYYY):");
                    try {
                        newOrderDate = askDate(s);
                        if (newOrderDate.isBefore(LocalDate.now())) {
                            view.showError("Order date must be today or in the future.");
                            continue;
                        }
                        break;
                    } catch (Exception e) {
                        view.showError("Invalid date format. Use DD-MM-YYYY.");
                    }
                }
                dto.setOrderDate(newOrderDate);
                break;

            case "address":
                // Convert existing Address to AddressDTO to display its current values:
                AddressDTO currentDto = AddressDTO.fromAddress(dto.getAddress());
                view.showMessage("Current address is:\n" + currentDto);

                // Now re-prompt all three fields:
                view.showMessage("Enter new address details (press Enter to keep existing):");
                String newStreet = askNonEmpty("Enter street (" + currentDto.getStreet() + "):");
                String newCity   = askNonEmpty("Enter city (" + currentDto.getCity() + "):");
                String newBuildingNumber = askNonEmpty(
                        "Enter building number (" + currentDto.getBuildingNumber() + "):"
                );

                // Build a new AddressDTO, then convert back to Address:
                AddressDTO updatedDto = new AddressDTO(newStreet, newCity, newBuildingNumber);
                Address updatedAddress = AddressDTO.toAddress(updatedDto);
                dto.setAddress(updatedAddress);
                break;

            case "contactphonenumber":
                String newPhone = askNonEmpty("Enter new contact phone number:");
                dto.setContactPhoneNumber(newPhone);
                break;

            case "status":
                String statusStr = askNonEmpty(
                        "Enter new status (e.g., SENT, CONFIRMED, DELIVERED, CANCELED):"
                ).trim().toUpperCase();
                try {
                    OrderStatus newStatus = OrderStatus.valueOf(statusStr);
                    dto.setStatus(newStatus);
                } catch (IllegalArgumentException e) {
                    view.showError("Invalid status. Please enter a valid OrderStatus value.");
                }
                break;

            case "items":
                String action = askNonEmpty("Do you want to add or remove an item? (add/remove):")
                        .trim()
                        .toLowerCase();
                if (action.equals("add")) {
                    int productId = askInt("Enter product ID:");
                    int supplierCatalogNumber = askInt("Enter supplier product catalog number:");
                    int quantity = askInt("Enter quantity:");

                    BigDecimal unitPrice;
                    while (true) {
                        String unitPriceStr = askNonEmpty("Enter unit price:");
                        try {
                            unitPrice = new BigDecimal(unitPriceStr);
                            break;
                        } catch (NumberFormatException e) {
                            view.showError("Unit price must be a number.");
                        }
                    }

                    String productName = askNonEmpty("Enter product name:");

                    BigDecimal discount = BigDecimal.ZERO;
                    String hasDiscount = askNonEmpty("Is there a discount? (y/n):").trim().toLowerCase();
                    if (hasDiscount.startsWith("y")) {
                        while (true) {
                            String discountStr = askNonEmpty("Enter discount percentage (0–100):");
                            try {
                                BigDecimal perc = new BigDecimal(discountStr);
                                BigDecimal hundred = new BigDecimal("100");
                                discount = hundred.subtract(perc).divide(hundred);
                                break;
                            } catch (NumberFormatException e) {
                                view.showError("Discount must be a number.");
                            }
                        }
                    }

                    OrderItemLineDTO newItem = new OrderItemLineDTO(
                            -1,
                            supplierCatalogNumber,
                            quantity,
                            unitPrice,
                            productName
                    );
                    newItem.setProductId(productId);
                    newItem.setDiscount(discount);
                    dto.getItems().add(newItem);

                } else if (action.equals("remove")) {
                    int prodToRemove = askInt("Enter the product ID to remove from items:");
                    dto.getItems().removeIf(item -> item.getProductId() == prodToRemove);
                } else {
                    view.showError("Invalid action. Use 'add' or 'remove'.");
                }
                break;

            default:
                view.showError(
                        "Invalid field. Choose one of: supplierId, supplierName, orderDate, address, contactPhoneNumber, status, items."
                );
                break;
        }

        return dto;
    }
}
