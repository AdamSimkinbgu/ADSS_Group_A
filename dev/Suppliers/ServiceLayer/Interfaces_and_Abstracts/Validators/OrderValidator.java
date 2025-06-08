package Suppliers.ServiceLayer.Interfaces_and_Abstracts.Validators;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Suppliers.DTOs.OrderInfoDTO;
import Suppliers.DTOs.PeriodicOrderDTO;
import Suppliers.ServiceLayer.Interfaces_and_Abstracts.BaseValidator;
import Suppliers.ServiceLayer.Interfaces_and_Abstracts.IValidator;
import Suppliers.ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;

public class OrderValidator extends BaseValidator implements IValidator<OrderInfoDTO> {

    @Override
    public ServiceResponse<List<String>> validateCreateDTO(OrderInfoDTO target) {
        List<String> errors = new ArrayList<>();

        if (target == null) {
            errors.add("OrderDTO cannot be null");
            return ServiceResponse.fail(errors);
        }

        if (isDateInPast(target.getOrderDate())) {
            errors.add("Order date cannot be null and must be today or in the future");
        }

        return errors.isEmpty() ? ServiceResponse.ok(null) : ServiceResponse.fail(errors);
    }

    @Override
    public ServiceResponse<List<String>> validateUpdateDTO(OrderInfoDTO target) {
        List<String> errors = new ArrayList<>();

        if (target == null) {
            errors.add("OrderDTO cannot be null");
            return ServiceResponse.fail(errors);
        } else {

            if (isDateInPast(target.getOrderDate())) {
                errors.add("Order date cannot be null and must be today or in the future");
            }

            if (target.getProducts() == null || target.getProducts().isEmpty()) {
                errors.add("Order must contain at least one item");
            } else {
                for (Map.Entry<Integer, Integer> entry : target.getProducts().entrySet()) {
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
