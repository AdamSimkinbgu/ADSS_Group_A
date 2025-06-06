package Suppliers.ServiceLayer.Interfaces_and_Abstracts.Validators;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import Suppliers.DTOs.SupplierProductDTO;
import Suppliers.ServiceLayer.Interfaces_and_Abstracts.BaseValidator;
import Suppliers.ServiceLayer.Interfaces_and_Abstracts.IValidator;
import Suppliers.ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;

public class ProductValidator extends BaseValidator implements IValidator<SupplierProductDTO> {

   @Override
   public ServiceResponse<?> validateCreateDTO(SupplierProductDTO target) {
      List<String> errors = new ArrayList<String>();
      if (target == null) {
         errors.add("SupplierProductDTO cannot be null");
      } else {
         if (target.getSupplierId() <= 0) {
            errors.add("Supplier ID must be greater than 0");
         }

         if (!isValidSupplierProductName(target.getName())) {
            errors.add(
                  "Product name must be between 1 and 50 characters long and can only contain letters, numbers, and spaces");
         } else {
            target.setName(normalizeName(target.getName()));
         }

         if (!isValidCatalogNumber(target.getSupplierCatalogNumber())) {
            errors.add(
                  "Supplier catalog number must be between 1 and 20 characters long and can only contain letters, numbers, and spaces");
         }

         if (target.getPrice() == null || target.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            errors.add("Product price cannot be null or negative");
         }

         if (target.getWeight() == null || target.getWeight().compareTo(BigDecimal.ZERO) < 0) {
            errors.add("Product weight cannot be null or negative");
         }

         if (target.getExpiresInDays() <= 0) {
            errors.add("Product expiration days must be greater than 0");
         }
      }
      return errors.isEmpty() ? ServiceResponse.ok(null) : ServiceResponse.fail(errors);
   }

   @Override
   public ServiceResponse<?> validateUpdateDTO(SupplierProductDTO target) {
      return validateCreateDTO(target); // Reuse create validation logic
   }

   @Override
   public ServiceResponse<?> validateRemoveDTO(int id) {
      List<String> errors = new ArrayList<>();
      if (id < 0) {
         errors.add("Product ID must be greater than 0");
      }
      return errors.isEmpty() ? ServiceResponse.ok(null) : ServiceResponse.fail(errors);
   }

   @Override
   public ServiceResponse<?> validateGetDTO(int id) {
      List<String> errors = new ArrayList<>();
      if (id < 0) {
         errors.add("Product ID must be a positive integer.");
      }
      return errors.isEmpty() ? ServiceResponse.ok(null) : ServiceResponse.fail(errors);
   }

}
