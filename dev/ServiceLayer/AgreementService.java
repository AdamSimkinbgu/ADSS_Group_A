// AgreementService.java
package ServiceLayer;

import java.util.HashMap;
import java.util.function.Function;

import com.fasterxml.jackson.databind.node.ObjectNode;

import DomainLayer.AgreementFacade;
import DomainLayer.SupplierFacade;
import ServiceLayer.Interfaces_and_Abstracts.IService;
import ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;
import DomainLayer.Classes.Agreement;
import DomainLayer.Classes.Supplier;

public class AgreementService extends BaseService implements IService {
   private final HashMap<String, Function<String, String>> serviceFunctions = new HashMap<>();
   private final AgreementFacade agreementFacade;
   private final SupplierFacade supplierFacade;

   public AgreementService(AgreementFacade agreementFacade, SupplierFacade supplierFacade) {
      this.agreementFacade = agreementFacade;
      this.supplierFacade = supplierFacade;
      serviceFunctions.put("addAgreement", this::addAgreement);
      serviceFunctions.put("updateAgreement", this::updateAgreement);
      serviceFunctions.put("removeAgreement", this::removeAgreement);
      serviceFunctions.put("getAgreement", this::getAgreement);
      serviceFunctions.put("?", this::commandDoesNotExist);
   }

   @Override
   public String execute(String serviceOption, String data) {
      Function<String, String> fn = serviceFunctions.getOrDefault(serviceOption, this::commandDoesNotExist);
      return fn.apply(data);
   }

   private String addAgreement(String json) {
      ServiceResponse<Boolean> resp;
      Supplier thatOneSupplier;
      try {
         thatOneSupplier = supplierFacade.getSupplier(objectMapper.readValue(json, ObjectNode.class).toString());
         // exists so add the name to the json
         json = injectKeyValueOnJsonString(json, "supplierName", thatOneSupplier.getName());
         System.out.println("Supplier exists: " + thatOneSupplier);
         System.out.println("JSON after injection: " + json);
      } catch (Exception e) {
         return serialize(new ServiceResponse<>(null, "supplierId not found or bad format"));
      }

      try {
         agreementFacade.createAgreement(json);
         resp = new ServiceResponse<>(true, "");
      } catch (Exception e) {
         resp = new ServiceResponse<>(false, e.getMessage());
      }
      return serialize(resp);
   }

   private String updateAgreement(String json) {
      ServiceResponse<Agreement> resp;
      try {
         // TODO: implement update logic
         resp = new ServiceResponse<>(null, "Not implemented yet");
      } catch (Exception e) {
         resp = new ServiceResponse<>(null, e.getMessage());
      }
      return serialize(resp);
   }

   private String removeAgreement(String json) {
      ServiceResponse<Agreement> resp;
      try {
         // TODO: implement update logic
         resp = new ServiceResponse<>(null, "Not implemented yet");
      } catch (Exception e) {
         resp = new ServiceResponse<>(null, e.getMessage());
      }
      return serialize(resp);
   }

   private String getAgreement(String json) {
      ServiceResponse<Agreement> resp;
      try {
         // TODO: implement update logic
         resp = new ServiceResponse<>(null, "Not implemented yet");
      } catch (Exception e) {
         resp = new ServiceResponse<>(null, e.getMessage());
      }
      return serialize(resp);
   }

}