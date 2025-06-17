package ServiceLayer.SuppliersServiceSubModule.Interfaces_and_Abstracts.Validators;

import java.util.ArrayList;
import java.util.List;

import DTOs.SuppliersModuleDTOs.AgreementDTO;
import DTOs.SuppliersModuleDTOs.BillofQuantitiesItemDTO;
import ServiceLayer.SuppliersServiceSubModule.Interfaces_and_Abstracts.BaseValidator;
import ServiceLayer.SuppliersServiceSubModule.Interfaces_and_Abstracts.IValidator;
import ServiceLayer.SuppliersServiceSubModule.Interfaces_and_Abstracts.ServiceResponse;

public class AgreementValidator extends BaseValidator implements IValidator<AgreementDTO> {

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
         if (!areDatesValid(target.getAgreementStartDate(), target.getAgreementEndDate())) {
            errors.add("Agreement start date must be before end date and start date cannot be before today");
         }

         if (target.getBillOfQuantitiesItems() == null) {
            errors.add("Bill of Quantities items cannot be null");
         } else if (!target.getBillOfQuantitiesItems().isEmpty()) {
            for (BillofQuantitiesItemDTO item : target.getBillOfQuantitiesItems()) {
               if (!isBillofQuantitiesItemValid(item)) {
                  errors.add("Item " + item.getProductName() + ": " + item.getProductId()
                        + " is not valid. It must contain a valid product ID, quantity, and discount percentage (0-1)");
               }
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
