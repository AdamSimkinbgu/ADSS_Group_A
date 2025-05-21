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

   public ServiceResponse<?> removeAgreement(String json) {
      return ServiceResponse.fail(List.of("Not implemented"));
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

}