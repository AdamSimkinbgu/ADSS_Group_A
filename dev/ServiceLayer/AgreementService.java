// AgreementService.java
package ServiceLayer;

import java.util.List;

import DomainLayer.AgreementFacade;

import ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;

public class AgreementService extends BaseService {
   private final AgreementFacade agreementFacade;

   public AgreementService(AgreementFacade agreementFacade) {
      this.agreementFacade = agreementFacade;
   }

   public ServiceResponse<?> createAgreement(String json) {
      return ServiceResponse.fail(List.of("Not implemented"));
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