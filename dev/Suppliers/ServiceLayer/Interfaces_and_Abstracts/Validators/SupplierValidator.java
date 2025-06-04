package Suppliers.ServiceLayer.Interfaces_and_Abstracts.Validators;

import java.util.ArrayList;
import java.util.List;

import Suppliers.DTOs.ContactInfoDTO;
import Suppliers.DTOs.SupplierDTO;
import Suppliers.ServiceLayer.Interfaces_and_Abstracts.IValidator;
import Suppliers.ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;

public class SupplierValidator implements IValidator<SupplierDTO> {
   @Override
   public ServiceResponse<List<String>> validateCreateDTO(SupplierDTO target) {
      List<String> errors = new ArrayList<String>();
      if (target == null) {
         errors.add("SupplierDTO cannot be null");
      } else {
         if (target.getName() == null) {
            errors.add("Supplier name cannot be null");
         } else if (target.getName().isEmpty()) {
            errors.add("Supplier name cannot be empty");
         } else if (!target.getName().matches("^[a-zA-Z ]{1,50}$")) {
            errors.add(
                  "Supplier name must be between 1 and 50 characters long and can only contain letters, numbers, and spaces");
         }
         if (target.getTaxNumber() == null) {
            errors.add("Supplier tax number cannot be null");
         } else if (target.getTaxNumber().isEmpty()) {
            errors.add("Supplier tax number cannot be empty");
         } else if (!target.getTaxNumber().matches("^5\\d{8}$")) {
            errors.add("Supplier tax number must start with 5 and be 9 digits long");
         }
         if (target.getAddressDTO() == null) {
            errors.add("Supplier address cannot be null");
         } else {
            if (target.getAddressDTO().getStreet() == null) {
               errors.add("Supplier address street cannot be null");
            } else if (target.getAddressDTO().getStreet().isEmpty()) {
               errors.add("Supplier address street cannot be empty");
            }
            if (target.getAddressDTO().getCity() == null) {
               errors.add("Supplier address city cannot be null");
            } else if (target.getAddressDTO().getCity().isEmpty()) {
               errors.add("Supplier address city cannot be empty");
            }
            if (target.getAddressDTO().getBuildingNumber() == null) {
               errors.add("Supplier address building number cannot be null");
            } else if (target.getAddressDTO().getBuildingNumber().isEmpty()) {
               errors.add("Supplier address building number cannot be empty");
            }
         }
         // selfSupply and supplyDays are are a must by their nature as their types
         // are primitive
         if (target.getPaymentDetails() == null) {
            errors.add("Supplier payment details cannot be null");
         } else {
            if (target.getPaymentDetails().getBankAccountNumber() == null) {
               errors.add("Supplier payment details bank account number cannot be null");
            } else if (target.getPaymentDetails().getBankAccountNumber().isEmpty()) {
               errors.add("Supplier payment details bank account number cannot be empty");
            } else if (!target.getPaymentDetails().getBankAccountNumber().matches("^[0-9]{6}"))
               errors.add("Supplier payment details bank account number must be 6 digits long");
            if (target.getPaymentDetails().getPaymentMethod() == null) {
               errors.add("Supplier payment details payment method cannot be null");
            }
            if (target.getPaymentDetails().getPaymentTerm() == null) {
               errors.add("Supplier payment details payment term cannot be null");
            }
         }
         if (target.getContactsInfoDTOList() == null) {
            errors.add("Supplier contact person cannot be null");
         } else {
            for (ContactInfoDTO contact : target.getContactsInfoDTOList()) {
               if (contact.getName() == null) {
                  errors.add("Supplier contact person name cannot be null");
               } else if (contact.getName().isEmpty()) {
                  errors.add("Supplier contact person name cannot be empty");
               } else if (!contact.getName().matches("^[a-zA-Z ]{1,50}$")) {
                  errors.add(
                        "Supplier contact person name must be between 1 and 50 characters long and can only contain letters, numbers, and spaces");
               }
               if (contact.getPhone() == null) {
                  errors.add("Supplier contact person phone number cannot be null");
               } else if (contact.getPhone().isEmpty()) {
                  errors.add("Supplier contact person phone number cannot be empty");
               } else if (!contact.getPhone().matches("^05\\d-\\d{3}-\\d{4}$")) {
                  if (contact.getPhone().matches("^05\\d{8}$"))
                     contact.setPhone(contact.getPhone().substring(0, 3) + "-" + contact.getPhone().substring(3, 7)
                           + "-" + contact.getPhone().substring(7));
                  else
                     errors.add(
                           "Supplier contact person phone number must start with 05 and be in the format 05X-XXX-XXXX");
               }
               if (contact.getEmail() == null) {
                  errors.add("Supplier contact person email cannot be null");
               } else if (contact.getEmail().isEmpty()) {
                  errors.add("Supplier contact person email cannot be empty");
               } else if (!contact.getEmail().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
                  errors.add(
                        "Please enter a valid email address (e.g. user@example.com). It may only use letters, numbers and . _ % + – before the @, and must include a domain with a valid extension (like .com, .net).");
               }
            }
         }
         if (target.getProducts() == null) {
            errors.add("Supplier products cannot be null");
         }
         if (target.getAgreements() == null) {
            errors.add("Supplier agreements cannot be null");
         }
      }
      return errors.isEmpty() ? ServiceResponse.ok(null) : ServiceResponse.fail(errors);
   }

   @Override
   public ServiceResponse<List<String>> validateUpdateDTO(SupplierDTO target) {
      return validateCreateDTO(target);
      // Reusing the create validation logic for update
      // as the same rules apply.
   }

   @Override
   public ServiceResponse<?> validateRemoveDTO(int id) {
      return id >= 0 ? ServiceResponse.ok(null) : ServiceResponse.fail(List.of("Invalid supplier ID"));
   }

   @Override
   public ServiceResponse<?> validateGetDTO(int id) {
      return id >= 0 ? ServiceResponse.ok(null) : ServiceResponse.fail(List.of("Invalid supplier ID"));
   }
}
