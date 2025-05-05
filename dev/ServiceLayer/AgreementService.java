// AgreementService.java
package ServiceLayer;

import java.util.HashMap;
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
      serviceFunctions.put("?", this::commandDoesNotExist);
   }

   @Override
   public ServiceResponse<?> execute(String serviceOption, String data) {
      Function<String, ServiceResponse<?>> fn = serviceFunctions.getOrDefault(serviceOption, this::commandDoesNotExist);
      return fn.apply(data);
   }

   public ServiceResponse<?> addAgreement(String json) {
      ServiceResponse<Boolean> resp;
      try {
         ObjectNode root = (ObjectNode) objectMapper.readTree(json);
         String supplierId = root.path("supplierId").asText(null);
         if (supplierId == null) {
            resp = ServiceResponse.error("Missing supplierId in JSON payload");
            return resp;
         }

         String lookup = objectMapper.createObjectNode()
               .put("supplierId", supplierId).toString();
         Supplier sup = supplierFacade.getSupplier(lookup);
         if (sup == null) {
            resp = ServiceResponse.error(
                  "No supplier found with ID: " + supplierId);
            return resp;
         }

         root.put("supplierName", sup.getName());
         agreementFacade.createAgreement(root.toString());
         resp = ServiceResponse.ok(true);
      } catch (JsonProcessingException e) {
         resp = ServiceResponse.error(
               "Invalid JSON payload: " + e.getOriginalMessage());
      } catch (IllegalArgumentException e) {
         resp = ServiceResponse.error(e.getMessage());
      } catch (Exception e) {
         resp = ServiceResponse.error(
               "Failed to create agreement: " + e.getMessage());
      }
      return resp;
   }

   public ServiceResponse<?> updateAgreement(String updateJson) {
      ServiceResponse<Agreement> resp;
      try {
         // pull out and parse the ID
         JsonNode root = objectMapper.readTree(updateJson);
         String agreementId = "{\"agreementId\": \"" + root.path("agreementId").asText() + "\"}";

         // load existing
         Agreement existing = agreementFacade.getAgreementById(agreementId);
         if (existing == null) {
            resp = ServiceResponse.error("No agreement with ID: " + root.path("agreementId").asText());
         } else {
            // merge only provided fields
            objectMapper.readerForUpdating(existing)
                  .readValue(updateJson);

            // persist
            agreementFacade.updateAgreement(existing);
            resp = ServiceResponse.ok(existing);
         }
      } catch (Exception e) {
         resp = ServiceResponse.error("Update failed: " + e.getMessage());
      }
      return resp;
   }

   public ServiceResponse<?> removeAgreement(String json) {
      ServiceResponse<Boolean> resp;
      try {
         boolean deleted = agreementFacade.removeAgreement(json);
         if (deleted) {
            resp = ServiceResponse.ok(deleted);
         } else {
            resp = ServiceResponse.error("No agreement with ID: " + json);
         }
      } catch (IllegalArgumentException e) {
         resp = ServiceResponse.error("Invalid Agreement ID format: " + json);
      } catch (Exception e) {
         resp = ServiceResponse.error(e.getMessage());
      }
      return resp;
   }

   public ServiceResponse<?> getAgreement(String json) {
      ServiceResponse<Agreement> resp;
      try {
         Agreement agreement = agreementFacade.getAgreement(json);
         if (agreement != null) {
            resp = ServiceResponse.ok(agreement);
         } else {
            resp = ServiceResponse.error("No agreement with ID: " + json);
         }
      } catch (IllegalArgumentException e) {
         resp = ServiceResponse.error("Invalid Agreement ID format: " + json);
      } catch (Exception e) {
         resp = ServiceResponse.error(e.getMessage());
      }
      return resp;
   }

   public ServiceResponse<?> getAllAgreements(String json) {
      ServiceResponse<List<Agreement>> resp;
      try {
         List<Agreement> agreements = agreementFacade.getAgreementsWithFullDetail();
         resp = ServiceResponse.ok(agreements);
      } catch (Exception e) {
         resp = ServiceResponse.error(e.getMessage());
      }
      return resp;
   }

   public ServiceResponse<?> checkAgreementExists(String lookupJson) {
      ServiceResponse<Boolean> resp;
      try {
         JsonNode root = objectMapper.readTree(lookupJson);
         String agreementId = "{\"agreementId\": \"" + root.path("agreementId").asText() + "\"}";
         resp = ServiceResponse.ok(agreementFacade.getAgreementById(agreementId) != null);
      } catch (Exception e) {
         resp = ServiceResponse.error(e.getMessage());
      }
      return resp;
   }

}