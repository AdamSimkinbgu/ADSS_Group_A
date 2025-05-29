package ServiceLayer;

import java.util.List;

import DTOs.AgreementDTO;
import DomainLayer.AgreementFacade;

import ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;

public class AgreementService extends BaseService {
   private final AgreementFacade agreementFacade;

   public AgreementService(AgreementFacade agreementFacade) {
      this.agreementFacade = agreementFacade;
   }

   public ServiceResponse<?> createAgreement(AgreementDTO agreementDTO) {
      if (agreementDTO == null) {
         return ServiceResponse.fail(List.of("AgreementDTO cannot be null"));
      }
      try {
         AgreementDTO actualAgreementDTO = agreementFacade.createAgreement(agreementDTO);
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