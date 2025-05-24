package ServiceLayer.Interfaces_and_Abstracts.Validators;

import java.util.ArrayList;
import java.util.List;

import DTOs.SupplierProductDTO;
import ServiceLayer.Interfaces_and_Abstracts.IValidator;
import ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;

public class ProductValidator implements IValidator<SupplierProductDTO> {

   @Override
   public ServiceResponse<?> validateCreateDTO(SupplierProductDTO target) {
      List<String> errors = new ArrayList<String>();
      if (target == null) {
         errors.add("SupplierProductDTO cannot be null");
      } else {
         if (target.getName() == null) {
            errors.add("Product name cannot be null");
         } else if (target.getName().isEmpty()) {
            errors.add("Product name cannot be empty");
         } else if (!target.getName().matches("^[a-zA-Z ]{1,50}$")) {
            errors.add(
                  "Product name must be between 1 and 50 characters long and can only contain letters, numbers, and spaces");
         }
         if (target.getSupplierCatalogNumber() == null) {
            errors.add("Supplier catalog number cannot be null");
         } else if (target.getSupplierCatalogNumber().isEmpty()) {
            errors.add("Supplier catalog number cannot be empty");
         }
         if (target.getPrice() == null) {
            errors.add("Product price cannot be null");
         }
         if (target.getWeight() == null) {
            errors.add("Product weight cannot be null");
         }
         if (target.getExpiresInDays() <= 0) {
            errors.add("Product expiration days must be greater than 0");
         }
      }
      return errors.isEmpty() ? ServiceResponse.ok(null) : ServiceResponse.fail(errors);
   }

   @Override
   public ServiceResponse<?> validateUpdateDTO(SupplierProductDTO target) {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'validateUpdateDTO'");
   }

   @Override
   public ServiceResponse<?> validateRemoveDTO(int id) {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'validateRemoveDTO'");
   }

   @Override
   public ServiceResponse<?> validateGetDTO(int id) {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'validateGetDTO'");
   }
    
   
}
