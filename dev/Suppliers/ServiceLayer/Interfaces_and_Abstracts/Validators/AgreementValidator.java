package Suppliers.ServiceLayer.Interfaces_and_Abstracts.Validators;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import Suppliers.DTOs.AgreementDTO;
import Suppliers.DTOs.BillofQuantitiesItemDTO;
import Suppliers.ServiceLayer.Interfaces_and_Abstracts.IValidator;
import Suppliers.ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;

public class AgreementValidator implements IValidator<AgreementDTO> {

   @Override
   public ServiceResponse<?> validateCreateDTO(AgreementDTO target) {
      List<String> errors = new ArrayList<>();
      // Example validation logic
      if (target == null) {
         errors.add("AgreementDTO cannot be null");
      } else {
         if (target.getSupplierId() < 0) {
            errors.add("Supplier ID must be a positive integer");
         }
         if (target.getAgreementStartDate() == null) {
            errors.add("Start date cannot be null");
         }
         if (target.getAgreementEndDate() == null) {
            errors.add("End date cannot be null");
         } else if (target.getAgreementEndDate().isBefore(target.getAgreementStartDate())) {
            errors.add("End date cannot be before start date");
         }
      }
      if (target.getBillOfQuantitiesItems() == null) {
         errors.add("Bill of Quantities items cannot be null");
      } else if (!target.getBillOfQuantitiesItems().isEmpty()) {
         for (BillofQuantitiesItemDTO item : target.getBillOfQuantitiesItems()) {
            if (item == null) {
               errors.add("Bill of Quantities item cannot be null");
               continue; // Skip to the next item
            }
            if (item.getProductId() < 0) {
               errors.add("Product ID in Bill of Quantities item must be a positive integer");
            }
            if (item.getQuantity() <= 0) {
               errors.add("Quantity in Bill of Quantities item must be greater than 0");
            }
            if (item.getDiscountPercent().compareTo(new BigDecimal(0)) == -1
                  || item.getDiscountPercent().compareTo(new BigDecimal("100")) == 1) {
               errors.add("Discount percentage must be between 0 and 100");
            }
         }

      }
      return errors.isEmpty() ? ServiceResponse.ok(null) : ServiceResponse.fail(errors);
   }

   @Override
   public ServiceResponse<?> validateUpdateDTO(AgreementDTO target) {
      return validateCreateDTO(target); // Reuse create validation logic
   }

   @Override
   public ServiceResponse<?> validateRemoveDTO(int id) {
      if (id < 0) {
         return ServiceResponse.fail(List.of("Agreement ID must be a positive integer"));
      }
      return ServiceResponse.ok(null);
   }

   @Override
   public ServiceResponse<?> validateGetDTO(int id) {
      if (id < 0) {
         return ServiceResponse.fail(List.of("Agreement ID must be a positive integer"));
      }
      return ServiceResponse.ok(null);
   }
}
