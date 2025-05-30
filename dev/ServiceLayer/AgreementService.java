package ServiceLayer;

import java.util.List;

import DTOs.AgreementDTO;
import DomainLayer.AgreementFacade;
import DomainLayer.AgreementSupplierController;
import ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;
import ServiceLayer.Interfaces_and_Abstracts.Validators.AgreementValidator;

public class AgreementService extends BaseService {
   private final AgreementFacade agreementFacade;
   private final AgreementSupplierController agreementSupplierController;
   private final AgreementValidator agreementValidator;

   public AgreementService(AgreementFacade agreementFacade, AgreementSupplierController agreementSupplierController) {
      this.agreementFacade = agreementFacade;
      this.agreementSupplierController = agreementSupplierController;
      this.agreementValidator = new AgreementValidator();
   }

   public ServiceResponse<?> createAgreement(AgreementDTO agreementDTO) {
      ServiceResponse<?> validationResponse = agreementValidator.validateCreateDTO(agreementDTO);
      if (!validationResponse.isSuccess()) {
         return ServiceResponse.fail(validationResponse.getErrors());
      }
      try {
         AgreementDTO actualAgreementDTO = agreementSupplierController.createAgreement(agreementDTO);
         return ServiceResponse.ok(actualAgreementDTO);
      } catch (Exception e) {
         return ServiceResponse.fail(List.of("Failed to create agreement: " + e.getMessage()));
      }
   }

   public ServiceResponse<?> updateAgreement(String updateJson) {
      return ServiceResponse.fail(List.of("Not implemented"));
   }

   public ServiceResponse<?> removeAgreement(int agreementID, int supplierID) {
      if (agreementID < 0) {
         return ServiceResponse.fail(List.of("Agreement ID must be a positive integer"));
      }
      if (supplierID < 0) {
         return ServiceResponse.fail(List.of("Supplier ID must be a positive integer"));
      }
      try {
         boolean removed = agreementFacade.removeAgreement(agreementID, supplierID);
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

   public ServiceResponse<?> getAgreement(String json) {
      return ServiceResponse.fail(List.of("Not implemented"));
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
         List<AgreementDTO> agreements = agreementFacade.getAgreementsBySupplierId(supplierId);
         if (agreements.isEmpty()) {
            return ServiceResponse.fail(List.of("No agreements found for supplier ID: " + supplierId));
         }
         return ServiceResponse.ok(agreements);
      } catch (Exception e) {
         return ServiceResponse.fail(List.of("Failed to retrieve agreements: " + e.getMessage()));
      }
   }

}