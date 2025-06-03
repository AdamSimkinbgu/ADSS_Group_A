package Suppliers.ServiceLayer.Interfaces_and_Abstracts.Validators;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Suppliers.DTOs.PeriodicOrderDTO;
import Suppliers.ServiceLayer.Interfaces_and_Abstracts.IValidator;
import Suppliers.ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;

public class PeriodicOrderValidator implements IValidator<PeriodicOrderDTO> {

    @Override
    public ServiceResponse<List<String>> validateCreateDTO(PeriodicOrderDTO target) {
        List<String> errors = new ArrayList<>();

        if (target == null) {
            errors.add("PeriodicOrderDTO cannot be null");
            return ServiceResponse.fail(errors);
        }

        // 1) deliveryDay must not be null
        if (target.getDeliveryDay() == null) {
            errors.add("Delivery day cannot be null");
        }

        // 2) productsInOrder: not null, at least one entry, and each key/value positive
        Map<Integer, Integer> map = target.getProductsInOrder();
        if (map == null) {
            errors.add("ProductsInOrder cannot be null");
        } else if (map.isEmpty()) {
            errors.add("At least one product must be included in a periodic order");
        } else {
            for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
                Integer productId = entry.getKey();
                Integer quantity  = entry.getValue();
                if (productId == null || productId <= 0) {
                    errors.add("Each productId in productsInOrder must be a positive integer");
                }
                if (quantity == null || quantity <= 0) {
                    errors.add("Quantity for product " + productId + " must be a positive integer");
                }
            }
        }

        // 3) isActive is a primitive boolean; no need to validate null

        return errors.isEmpty() ? ServiceResponse.ok(null) : ServiceResponse.fail(errors);
    }

    @Override
    public ServiceResponse<List<String>> validateUpdateDTO(PeriodicOrderDTO target) {
        // Reuse creation rules for update
        return validateCreateDTO(target);
    }

    @Override
    public ServiceResponse<?> validateRemoveDTO(int id) {
        return id > 0 ? ServiceResponse.ok(null)
                : ServiceResponse.fail(List.of("Invalid periodic order ID"));
    }

    @Override
    public ServiceResponse<?> validateGetDTO(int id) {
        return id > 0 ? ServiceResponse.ok(null)
                : ServiceResponse.fail(List.of("Invalid periodic order ID"));
    }
}
