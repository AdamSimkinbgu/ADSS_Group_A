package ServiceLayer.SuppliersServiceSubModule.Interfaces_and_Abstracts.Validators;

import java.util.ArrayList;
import java.util.List;

import DTOs.SuppliersModuleDTOs.ContactInfoDTO;
import DTOs.SuppliersModuleDTOs.SupplierDTO;
import ServiceLayer.SuppliersServiceSubModule.Interfaces_and_Abstracts.BaseValidator;
import ServiceLayer.SuppliersServiceSubModule.Interfaces_and_Abstracts.IValidator;
import ServiceLayer.SuppliersServiceSubModule.Interfaces_and_Abstracts.ServiceResponse;

public class SupplierValidator extends BaseValidator implements IValidator<SupplierDTO> {
   @Override
   public ServiceResponse<List<String>> validateCreateDTO(SupplierDTO target) {
      List<String> errors = new ArrayList<String>();
      if (target == null) {
         errors.add("SupplierDTO cannot be null");
      } else {
         if (!isValidName(target.getName())) {
            errors.add(
                  "Supplier name must be between 1 and 50 characters long and can only contain letters, numbers, and spaces");
         } else {
            target.setName(normalizeName(target.getName()));
         }

         if (!isValidTaxNumber(target.getTaxNumber())) {
            errors.add("Supplier tax number must be 9 digits long");
         }

         if (!isValidAddress(target.getAddressDTO())) {
            errors.add("Supplier address must not be null and must contain a street, city, and postal code");
         } else {
            target.setAddress(normalizeAddress(target.getAddressDTO()));
         }

         // self supply is a boolean, so no validation needed

         if (target.getLeadSupplyDays() < 0) {
            errors.add("Supplier lead supply days must be 0 or greater");
         }

         if (!isValidPaymentDetails(target.getPaymentDetailsDTO())) {
            errors.add(
                  "Supplier payment details must not be null and must contain a valid band account number and payment method/terms");
         } else {
            target.setPaymentDetails(normalizePaymentDetails(target.getPaymentDetailsDTO()));
         }
         List<ContactInfoDTO> contactsNormalized = new ArrayList<>();
         for (ContactInfoDTO contact : target.getContactsInfoDTOList()) {
            if (!isValidContactInfo(contact)) {
               errors.add("Supplier contact " + contact.getName()
                     + " is invalid. It must contain a valid name, email, and phone number.");
            } else {
               contactsNormalized.add(normalizeContactInfo(contact));
            }
            target.setContacts(contactsNormalized);
         }
      }
      return errors.isEmpty() ? ServiceResponse.ok(null) : ServiceResponse.fail(errors);
   }

   @Override
   public ServiceResponse<List<String>> validateUpdateDTO(SupplierDTO target) {
      List<String> errors = new ArrayList<String>();
      if (target == null) {
         errors.add("SupplierDTO cannot be null");
      } else {
         if (target.getId() <= 0) {
            errors.add(
                  "Supplier id is invalid for update");
         } else if (!isValidName(target.getName())) {
            errors.add(
                  "Supplier name must be between 1 and 50 characters long and can only contain letters, numbers, and spaces");
         } else {
            target.setName(normalizeName(target.getName()));
         }

         if (!isValidTaxNumber(target.getTaxNumber())) {
            errors.add("Supplier tax number must be 9 digits long");
         }

         if (!isValidAddress(target.getAddressDTO())) {
            errors.add("Supplier address must not be null and must contain a street, city, and postal code");
         } else {
            target.setAddress(normalizeAddress(target.getAddressDTO()));
         }

         // self supply is a boolean, so no validation needed

         if (target.getLeadSupplyDays() < 0) {
            errors.add("Supplier lead supply days must be 0 or greater");
         }

         if (!isValidPaymentDetails(target.getPaymentDetailsDTO())) {
            errors.add(
                  "Supplier payment details must not be null and must contain a valid band account number and payment method/terms");
         } else {
            target.setPaymentDetails(normalizePaymentDetails(target.getPaymentDetailsDTO()));
         }
         List<ContactInfoDTO> contactsNormalized = new ArrayList<>();
         for (ContactInfoDTO contact : target.getContactsInfoDTOList()) {
            if (!isValidContactInfo(contact)) {
               errors.add("Supplier contact " + contact.getName()
                     + " is invalid. It must contain a valid name, email, and phone number.");
            } else {
               contactsNormalized.add(normalizeContactInfo(contact));
            }
            target.setContacts(contactsNormalized);
         }
      }
      return errors.isEmpty() ? ServiceResponse.ok(null) : ServiceResponse.fail(errors);

   }

   @Override
   public ServiceResponse<?> validateRemoveDTO(int id) {
      List<String> errors = new ArrayList<>();
      if (id < 0) {
         errors.add("Supplier ID must be a positive integer.");
      }
      return errors.isEmpty() ? ServiceResponse.ok(null) : ServiceResponse.fail(errors);
   }

   @Override
   public ServiceResponse<?> validateGetDTO(int id) {
      List<String> errors = new ArrayList<>();
      if (id < 0) {
         errors.add("Supplier ID must be a positive integer.");
      }
      return errors.isEmpty() ? ServiceResponse.ok(null) : ServiceResponse.fail(errors);
   }
}
