package Suppliers.ServiceLayer.Interfaces_and_Abstracts.Validators;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Suppliers.DTOs.OrderDTO;
import Suppliers.DTOs.OrderItemLineDTO;
import Suppliers.DTOs.PeriodicOrderDTO;
import Suppliers.ServiceLayer.Interfaces_and_Abstracts.BaseValidator;
import Suppliers.ServiceLayer.Interfaces_and_Abstracts.IValidator;
import Suppliers.ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;

public class OrderValidator extends BaseValidator implements IValidator<OrderDTO> {

    @Override
    public ServiceResponse<List<String>> validateCreateDTO(OrderDTO target) {
        List<String> errors = new ArrayList<>();

        if (target == null) {
            errors.add("OrderDTO cannot be null");
            return ServiceResponse.fail(errors);
        }

        if (isDateInPast(target.getOrderDate())) {
            errors.add("Order date cannot be null and must be today or in the future");
        }

        if (!isValidAddress(target.getAddress())) {
            errors.add("Address cannot be null and must have non-empty street, city, and building number");
        } else {
            target.setAddress(normalizeAddress(target.getAddress()));
        }

        if (!isValidPhone(target.getContactPhoneNumber())) {
            errors.add("Contact phone number must be in the format 05X-XXX-XXXX");
        } else {
            target.setContactPhoneNumber(normalizePhone(target.getContactPhoneNumber()));
        }

        // 7) items list: not null, must have at least one entry
        if (target.getItems() == null) {
            errors.add("Order items list cannot be null");
        } else if (target.getItems().isEmpty()) {
            errors.add("Order must contain at least one item");
        } else {
            for (OrderItemLineDTO line : target.getItems()) {
                if (line.getProductId() <= 0) {
                    errors.add("Each order item must have a positive productId");
                }
                if (line.getQuantity() <= 0) {
                    errors.add("Each order item must have a positive quantity");
                }
            }
        }

        return errors.isEmpty() ? ServiceResponse.ok(null) : ServiceResponse.fail(errors);
    }

    @Override
    public ServiceResponse<List<String>> validateUpdateDTO(OrderDTO target) {
        List<String> errors = new ArrayList<>();

        if (target == null) {
            errors.add("OrderDTO cannot be null");
            return ServiceResponse.fail(errors);
        } else {
            if (target.getOrderId() <= 0) {
                errors.add("Order ID must be a positive integer to be updated");
            }
            if (target.getSupplierId() <= 0) {
                errors.add("Supplier ID must be a positive integer");
            }
            if (isDateInPast(target.getOrderDate())) {
                errors.add("Order date cannot be null and must be today or in the future");
            }

            if (!isValidAddress(target.getAddress())) {
                errors.add("Address cannot be null and must have non-empty street, city, and building number");
            } else {
                target.setAddress(normalizeAddress(target.getAddress()));
            }
            if (!isValidPhone(target.getContactPhoneNumber())) {
                errors.add("Contact phone number must be in the format 05X-XXX-XXXX");
            } else {
                target.setContactPhoneNumber(normalizePhone(target.getContactPhoneNumber()));
            }
            if (target.getItems() == null || target.getItems().isEmpty()) {
                errors.add("Order must contain at least one item");
            } else {
                for (OrderItemLineDTO line : target.getItems()) {
                    if (line.getProductId() <= 0) {
                        errors.add("Each order item must have a positive productId");
                    }
                    if (line.getSupplierProductCatalogNumber().isEmpty()) {
                        errors.add("Each order item must have a non-empty supplierProductCatalogNumber");
                    }
                    if (line.getQuantity() <= 0) {
                        errors.add("Each order item must have a positive quantity");
                    }
                    if (line.getUnitPrice() == null || line.getUnitPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                        errors.add("Each order item unitPrice must be greater than zero");
                    }
                    if (line.getProductName() == null || line.getProductName().isEmpty()) {
                        errors.add("Each order item must have a non-empty product name");
                    }
                    if (line.getDiscount() == null || line.getDiscount().compareTo(java.math.BigDecimal.ZERO) < 0
                            || line.getDiscount().compareTo(java.math.BigDecimal.ONE) > 0) {
                        errors.add("Each order item discount must be between 0.0 and 1.0");
                    }
                }
            }
        }
        return errors.isEmpty() ? ServiceResponse.ok(null) : ServiceResponse.fail(errors);

    }

    @Override
    public ServiceResponse<List<String>> validateRemoveDTO(int id) {
        return id > 0 ? ServiceResponse.ok(null)
                : ServiceResponse.fail(List.of("Invalid order ID"));
    }

    @Override
    public ServiceResponse<List<String>> validateGetDTO(int id) {
        return id > 0 ? ServiceResponse.ok(null)
                : ServiceResponse.fail(List.of("Invalid order ID"));
    }

    public ServiceResponse<List<String>> validateCreateDTO(PeriodicOrderDTO periodicOrderDTO) {
        List<String> errors = new ArrayList<>();

        if (periodicOrderDTO == null) {
            errors.add("PeriodicOrderDTO cannot be null");
        } else {
            if (periodicOrderDTO.getDeliveryDay() == null) {
                errors.add("Delivery day cannot be null");
            } // Assuming DeliveryDay is an enum, you might want to check if it's a valid day
            if (periodicOrderDTO.getProductsInOrder() == null || periodicOrderDTO.getProductsInOrder().isEmpty()) {
                errors.add("Periodic order must contain at least one product");
            } else {
                for (Map.Entry<Integer, Integer> entry : periodicOrderDTO.getProductsInOrder().entrySet()) {
                    Integer productId = entry.getKey();
                    Integer quantity = entry.getValue();
                    if (productId == null || productId <= 0) {
                        errors.add("Product ID must be a positive integer");
                    }
                    if (quantity == null || quantity <= 0) {
                        errors.add("Quantity must be a positive integer");
                    }
                }
            }
        }
        return errors.isEmpty() ? ServiceResponse.ok(null) : ServiceResponse.fail(errors);

    }

    public ServiceResponse<List<String>> validateUpdateDTO(PeriodicOrderDTO updatedDto) {
        List<String> errors = new ArrayList<>();

        if (updatedDto == null) {
            errors.add("PeriodicOrderDTO cannot be null");
            return ServiceResponse.fail(errors);
        }

        // Validate delivery day
        if (updatedDto.getDeliveryDay() == null) {
            errors.add("Delivery day cannot be null");
        }

        // Validate products in order
        if (updatedDto.getProductsInOrder() == null || updatedDto.getProductsInOrder().isEmpty()) {
            errors.add("Periodic order must contain at least one product");
        } else {
            for (Map.Entry<Integer, Integer> entry : updatedDto.getProductsInOrder().entrySet()) {
                Integer productId = entry.getKey();
                Integer quantity = entry.getValue();
                if (productId == null || productId <= 0) {
                    errors.add("Product ID must be a positive integer");
                }
                if (quantity == null || quantity <= 0) {
                    errors.add("Quantity must be a positive integer");
                }
            }
        }

        return errors.isEmpty() ? ServiceResponse.ok(null) : ServiceResponse.fail(errors);
    }
}
