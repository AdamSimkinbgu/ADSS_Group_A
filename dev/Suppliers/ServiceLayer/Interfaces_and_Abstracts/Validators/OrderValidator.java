package Suppliers.ServiceLayer.Interfaces_and_Abstracts.Validators;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import Suppliers.DTOs.OrderDTO;
import Suppliers.DTOs.OrderItemLineDTO;
import Suppliers.ServiceLayer.Interfaces_and_Abstracts.IValidator;
import Suppliers.ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;

public class OrderValidator implements IValidator<OrderDTO> {

    @Override
    public ServiceResponse<List<String>> validateCreateDTO(OrderDTO target) {
        List<String> errors = new ArrayList<>();

        if (target == null) {
            errors.add("OrderDTO cannot be null");
            return ServiceResponse.fail(errors);
        }

        // 1) supplierId must be positive
        if (target.getSupplierId() <= 0) {
            errors.add("Supplier ID must be a positive integer");
        }

        // 2) supplierName: not null/empty, length <= 50, only letters, numbers, spaces
        if (target.getSupplierName() == null) {
            errors.add("Supplier name cannot be null");
        } else if (target.getSupplierName().isEmpty()) {
            errors.add("Supplier name cannot be empty");
        } else if (!target.getSupplierName().matches("^[a-zA-Z0-9 ]{1,50}$")) {
            errors.add("Supplier name must be 1–50 characters long and can contain only letters, numbers, and spaces");
        }

        // 3) orderDate: not null, must be today or future
        if (target.getOrderDate() == null) {
            errors.add("Order date cannot be null");
        } else if (target.getOrderDate().isBefore(LocalDate.now())) {
            errors.add("Order date must be today or in the future");
        }

        // 4) creationDate: not null, should be <= now
        if (target.getCreationDate() == null) {
            errors.add("Creation date cannot be null");
        } else if (target.getCreationDate().isAfter(LocalDate.now())) {
            errors.add("Creation date cannot be in the future");
        }

        // 5) address: not null, and individual fields not null/empty
        if (target.getAddress() == null) {
            errors.add("Address cannot be null");
        } else {
            // we assume Address has getStreet(), getCity(), getBuildingNumber()
            if (target.getAddress().getStreet() == null || target.getAddress().getStreet().isEmpty()) {
                errors.add("Address street cannot be null or empty");
            }
            if (target.getAddress().getCity() == null || target.getAddress().getCity().isEmpty()) {
                errors.add("Address city cannot be null or empty");
            }
            if (target.getAddress().getBuildingNumber() == null || target.getAddress().getBuildingNumber().isEmpty()) {
                errors.add("Address building number cannot be null or empty");
            }
        }

        // 6) contactPhoneNumber: not null/empty, pattern "05X-XXX-XXXX"
        if (target.getContactPhoneNumber() == null) {
            errors.add("Contact phone number cannot be null");
        } else if (target.getContactPhoneNumber().isEmpty()) {
            errors.add("Contact phone number cannot be empty");
        } else if (!target.getContactPhoneNumber().matches("^05\\d-\\d{3}-\\d{4}$")) {
            errors.add("Contact phone number must be in the format 05X-XXX-XXXX");
        }

        // 7) items list: not null, must have at least one entry
        if (target.getItems() == null) {
            errors.add("Order items list cannot be null");
        } else if (target.getItems().isEmpty()) {
            errors.add("Order must contain at least one item");
        } else {
            for (OrderItemLineDTO line : target.getItems()) {
                // a) productId > 0
                if (line.getProductId() <= 0) {
                    errors.add("Each order item must have a positive productId");
                }
                // b) supplierProductCatalogNumber > 0
                if (line.getSupplierProductCatalogNumber().isEmpty()) {
                    errors.add("Each order item must have a non-empty supplierProductCatalogNumber");
                }
                // c) quantity > 0
                if (line.getQuantity() <= 0) {
                    errors.add("Each order item must have a positive quantity");
                }
                // d) unitPrice > 0
                if (line.getUnitPrice() == null) {
                    errors.add("Each order item must have a unitPrice");
                } else if (line.getUnitPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                    errors.add("Each order item unitPrice must be greater than zero");
                }
                // e) productName not null/empty
                if (line.getProductName() == null || line.getProductName().isEmpty()) {
                    errors.add("Each order item must have a non-empty product name");
                }
                // f) discount between 0 and 1 (inclusive)
                if (line.getDiscount() == null) {
                    errors.add("Each order item must have a discount (0.0–1.0)");
                } else {
                    java.math.BigDecimal d = line.getDiscount();
                    if (d.compareTo(java.math.BigDecimal.ZERO) < 0 || d.compareTo(java.math.BigDecimal.ONE) > 0) {
                        errors.add("Each order item discount must be between 0.0 and 1.0");
                    }
                }
            }
        }

        // 8) status: not null
        if (target.getStatus() == null) {
            errors.add("Order status cannot be null");
        }

        return errors.isEmpty() ? ServiceResponse.ok(null) : ServiceResponse.fail(errors);
    }

    @Override
    public ServiceResponse<List<String>> validateUpdateDTO(OrderDTO target) {
        // For simplicity, reuse the same rules as create
        return validateCreateDTO(target);
    }

    @Override
    public ServiceResponse<?> validateRemoveDTO(int id) {
        return id > 0 ? ServiceResponse.ok(null)
                : ServiceResponse.fail(List.of("Invalid order ID"));
    }

    @Override
    public ServiceResponse<?> validateGetDTO(int id) {
        return id > 0 ? ServiceResponse.ok(null)
                : ServiceResponse.fail(List.of("Invalid order ID"));
    }
}
