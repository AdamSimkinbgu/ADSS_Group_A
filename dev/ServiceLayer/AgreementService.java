// AgreementService.java
package ServiceLayer;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import DomainLayer.AgreementFacade;
import DomainLayer.SupplierFacade;
import ServiceLayer.Interfaces_and_Abstracts.IService;
import ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;
import DomainLayer.Classes.Agreement;
import DomainLayer.Classes.Supplier;

public class AgreementService extends BaseService implements IService {
   private final AgreementFacade agreementFacade;
   private final SupplierFacade supplierFacade;

   public AgreementService(AgreementFacade agreementFacade, SupplierFacade supplierFacade) {
      this.agreementFacade = agreementFacade;
      this.supplierFacade = supplierFacade;
   }

   @Override
   public ServiceResponse<?> execute(String serviceOption, String data) {
      return ServiceResponse.fail(List.of("Not implemented"));
   }

   public ServiceResponse<?> addAgreement(String json) {
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