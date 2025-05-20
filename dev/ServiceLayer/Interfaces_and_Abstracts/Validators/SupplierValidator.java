package ServiceLayer.Interfaces_and_Abstracts.Validators;

import java.util.ArrayList;
import java.util.List;

import DTOs.SupplierDTO;
import ServiceLayer.Interfaces_and_Abstracts.IValidator;
import ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;

public class SupplierValidator implements IValidator<SupplierDTO> {
   @Override
   public ServiceResponse<List<String>> validate(SupplierDTO target) {
      List<String> errors = new ArrayList<String>();
      if (target == null) {
         errors.add("SupplierDTO cannot be null");
      } else {
         if (target.getName() == null) {
            errors.add("Supplier name cannot be null");
         } else if (target.getName().isEmpty()) {
            errors.add("Supplier name cannot be empty");
         } else if (!target.getName().matches("^[a-zA-Z ]{3,50}$")) {
            errors.add(
                  "Supplier name must be between 3 and 50 characters long and can only contain letters, numbers, and spaces");
         }
         if (target.getTaxNumber() == null) {
            errors.add("Supplier tax number cannot be null");
         } else if (target.getTaxNumber().isEmpty()) {
            errors.add("Supplier tax number cannot be empty");
         } else if (!target.getTaxNumber().matches("^5\\d{8}$")) {
            errors.add("Supplier tax number must start with 5 and be 9 digits long");
         }
         if (target.getAddress() == null) {
            errors.add("Supplier address cannot be null");
         } else {
            if (target.getAddress().street() == null) {
               errors.add("Supplier address street cannot be null");
            } else if (target.getAddress().street().isEmpty()) {
               errors.add("Supplier address street cannot be empty");
            } else if (!target.getAddress().street().matches("^[a-zA-Z0-9 ]+$")) {
               errors.add("Supplier address street can only contain letters, numbers, and spaces");
            }

            if (target.getAddress().city() == null) {
               errors.add("Supplier address city cannot be null");
            } else if (target.getAddress().city().isEmpty()) {
               errors.add("Supplier address city cannot be empty");
            } else if (!target.getAddress().city().matches("^[a-zA-Z ]+$")) {
               errors.add("Supplier address city can only contain letters and spaces");
            }
            if (target.getAddress().buildingNumber() == null) {
               errors.add("Supplier address building number cannot be null");
            } else if (target.getAddress().buildingNumber().isEmpty()) {
               errors.add("Supplier address building number cannot be empty");
            } else if (!target.getAddress().buildingNumber().matches("^[0-9]+$")) {
               errors.add("Supplier address building number can only contain numbers");
            }
         }
      }
      return errors.isEmpty() ? ServiceResponse.ok(null) : ServiceResponse.fail(errors);
   }
}
