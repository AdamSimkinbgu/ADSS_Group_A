package ServiceLayer;

import java.util.List;

import DTOs.AgreementDTO;
import DomainLayer.AgreementFacade;
import DomainLayer.SupplierController;
import ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;
import ServiceLayer.Interfaces_and_Abstracts.Validators.AgreementValidator;

public class AgreementService extends BaseService {
   private final SupplierController supplierController;
   private final AgreementValidator agreementValidator;

   public AgreementService(SupplierController supplierController) {
      this.supplierController = supplierController;
      this.agreementValidator = new AgreementValidator();
   }

   public ServiceResponse<?> createAgreement(AgreementDTO agreementDTO) {
      ServiceResponse<?> validationResponse = agreementValidator.validateCreateDTO(agreementDTO);
      if (!validationResponse.isSuccess()) {
         return ServiceResponse.fail(validationResponse.getErrors());
      }
      try {
         AgreementDTO actualAgreementDTO = supplierController.createAgreement(agreementDTO);
         return ServiceResponse.ok(actualAgreementDTO);
      } catch (Exception e) {
         return ServiceResponse.fail(List.of("Failed to create agreement: " + e.getMessage()));
      }
   }

   public ServiceResponse<?> updateAgreement(int agreementID, AgreementDTO updatedAgreement) {
      ServiceResponse<?> validationResponse = agreementValidator.validateUpdateDTO(updatedAgreement);
      if (!validationResponse.isSuccess()) {
         return ServiceResponse.fail(validationResponse.getErrors());
      }
      try {
         supplierController.updateAgreement(agreementID, updatedAgreement);
         return ServiceResponse.ok("Agreement updated successfully");
      } catch (Exception e) {
         return ServiceResponse.fail(List.of("Failed to update agreement: " + e.getMessage()));
      }
   }

   public ServiceResponse<?> removeAgreement(int agreementID, int supplierID) {
      if (agreementID < 0) {
         return ServiceResponse.fail(List.of("Agreement ID must be a positive integer"));
      }
      if (supplierID < 0) {
         return ServiceResponse.fail(List.of("Supplier ID must be a positive integer"));
      }
      try {
         boolean removed = supplierController.removeAgreement(agreementID, supplierID);
         if (removed) {
            return ServiceResponse.ok("Agreement removed successfully");
         } else {
            return ServiceResponse
                  .fail(List.of("Failed to remove agreement: Agreement not found or supplier mismatch"));
         }
      } catch (Exception e) {
         return ServiceResponse.fail(List.of("Failed to remove agreement: " + e.getMessage()));
      }
   }

   public ServiceResponse<AgreementDTO> getAgreement(int agreementID) {
      ServiceResponse<?> validationResponse = agreementValidator.validateGetDTO(agreementID);
      if (!validationResponse.isSuccess()) {
         return ServiceResponse.fail(validationResponse.getErrors());
      }
      try {
         AgreementDTO agreement = supplierController.getAgreement(agreementID);
         if (agreement == null) {
            return ServiceResponse.fail(List.of("Agreement not found for ID: " + agreementID));
         }
         return ServiceResponse.ok(agreement);
      } catch (Exception e) {
         return ServiceResponse.fail(List.of("Failed to retrieve agreement: " + e.getMessage()));
      }
   }

   public ServiceResponse<?> getAllAgreements(String json) {
      return ServiceResponse.fail(List.of("Not implemented"));
   }

   public ServiceResponse<?> checkAgreementExists(String lookupJson) {
      return ServiceResponse.fail(List.of("Not implemented"));
   }

   public ServiceResponse<List<AgreementDTO>> getAgreementsBySupplierId(int supplierId) {
      if (supplierId < 0) {
         return ServiceResponse.fail(List.of("Supplier ID must be a positive integer"));
      }
      try {
         List<AgreementDTO> agreements = supplierController.getAgreementsBySupplierId(supplierId);
         if (agreements.isEmpty()) {
            return ServiceResponse.fail(List.of("No agreements found for supplier ID: " + supplierId));
         }
         return ServiceResponse.ok(agreements);
      } catch (Exception e) {
         return ServiceResponse.fail(List.of("Failed to retrieve agreements: " + e.getMessage()));
      }
   }

}