// AgreementService.java
package ServiceLayer;

import java.util.HashMap;
import java.util.function.Function;

import com.fasterxml.jackson.core.JsonProcessingException;
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
      try {
         ObjectNode root = (ObjectNode) objectMapper.readTree(json);
         String supplierId = root.path("supplierId").asText(null);
         if (supplierId == null) {
            throw new IllegalArgumentException("supplierId is missing");
         }

         String lookup = objectMapper.createObjectNode()
               .put("supplierId", supplierId).toString();
         Supplier sup = supplierFacade.getSupplier(lookup);
         if (sup == null) {
            resp = new ServiceResponse<>(false,
                  "No supplier found with ID: " + supplierId);
            return serialize(resp);
         }

         root.put("supplierName", sup.getName());
         agreementFacade.createAgreement(root.toString());
         resp = new ServiceResponse<>(true, "");
      } catch (JsonProcessingException e) {
         resp = new ServiceResponse<>(false,
               "Invalid JSON payload: " + e.getOriginalMessage());
      } catch (IllegalArgumentException e) {
         resp = new ServiceResponse<>(false, e.getMessage());
      } catch (Exception e) {
         resp = new ServiceResponse<>(false,
               "Failed to create agreement: " + e.getMessage());
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
      ServiceResponse<Boolean> resp;
      try {
         boolean deleted = agreementFacade.removeAgreement(json);
         if (deleted) {
            resp = new ServiceResponse<>(true, "");
         } else {
            resp = new ServiceResponse<>(false, "No agreement with ID: " + json);
         }
      } catch (IllegalArgumentException e) {
         resp = new ServiceResponse<>(false, "Invalid Agreement ID format: " + json);
      } catch (Exception e) {
         resp = new ServiceResponse<>(false, e.getMessage());
      }
      return serialize(resp);
   }

   private String getAgreement(String json) {
      ServiceResponse<Agreement> resp;
      try {
         Agreement agreement = agreementFacade.getAgreement(json);
         if (agreement != null) {
            resp = new ServiceResponse<>(agreement, "");
         } else {
            resp = new ServiceResponse<>(null, "No agreement with ID: " + json);
         }
      } catch (IllegalArgumentException e) {
         resp = new ServiceResponse<>(null, "Invalid Agreement ID format: " + json);
      } catch (Exception e) {
         resp = new ServiceResponse<>(null, e.getMessage());
      }
      return serialize(resp);
   }

}