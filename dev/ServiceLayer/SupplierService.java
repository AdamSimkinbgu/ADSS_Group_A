package ServiceLayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import DomainLayer.Classes.Agreement;
import DomainLayer.Classes.Supplier;
import DomainLayer.SupplierFacade;
import ServiceLayer.Interfaces_and_Abstracts.IService;
import ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;

/**
 * Service layer for Supplier operations, wrapping all responses in a
 * ServiceResponse<T> envelope.
 */
public class SupplierService extends BaseService implements IService {
   private final SupplierFacade supplierFacade;

   public SupplierService(SupplierFacade facade, AgreementService agreementService) {
      this.supplierFacade = facade;
      serviceFunctions.put("addSupplier", this::addSupplier);
      serviceFunctions.put("updateSupplier", this::updateSupplier);
      serviceFunctions.put("removeSupplier", this::removeSupplier);
      serviceFunctions.put("getSupplierDetails", this::getSupplierDetails);
      serviceFunctions.put("getAllSuppliers", this::getAllSuppliers);
      serviceFunctions.put("checkSupplierExists", this::checkSupplierExists);
      serviceFunctions.put("addAgreement", agreementService::addAgreement);
      serviceFunctions.put("updateAgreement", agreementService::updateAgreement);
      serviceFunctions.put("removeAgreement", agreementService::removeAgreement);
      serviceFunctions.put("getAgreement", agreementService::getAgreement);
      serviceFunctions.put("getAllAgreements", agreementService::getAllAgreements);
      serviceFunctions.put("checkAgreementExists", agreementService::checkAgreementExists);
      serviceFunctions.put("?", this::commandDoesNotExist);
   }

   @Override
   public ServiceResponse<?> execute(String serviceOption, String data) {
      Function<String, ServiceResponse<?>> fn = serviceFunctions.getOrDefault(serviceOption, this::commandDoesNotExist);
      return fn.apply(data);
   }

   // example function
   private ServiceResponse<?> addSupplier(String creationJson) {
      // make sure json is good
      ServiceResponse<Boolean> pre = validateBinding(creationJson, Supplier.class);
      // if not, return jackson error message
      if (pre.getValue() == null || !pre.getValue()) {
         return pre;
      }

      // use facade to try and add supplier
      ServiceResponse<String> resp;
      try {
         supplierFacade.addSupplier(creationJson);
         resp = ServiceResponse.ok("Supplier added successfully");
      } catch (Exception e) {
         resp = ServiceResponse.error("Failed to add supplier: " + e.getMessage());
      }
      return resp;
   }

   private ServiceResponse<?> updateSupplier(String updateJson) {
      ServiceResponse<Supplier> resp;
      try {
         JsonNode root = objectMapper.readTree(updateJson);
         String supplierId = "{\"supplierId\": \"" + root.path("supplierId").asText() + "\"}";

         Supplier existing = supplierFacade.getSupplier(supplierId);
         if (existing == null) {
            resp = ServiceResponse.error("Supplier not found: " + root.path("supplierId").asText());
         } else {
            // merge only the provided fields into the object
            objectMapper.readerForUpdating(existing).readValue(updateJson);

            // persist the updated object
            supplierFacade.updateSupplier(existing);

            resp = ServiceResponse.ok(existing);
         }
      } catch (Exception e) {
         resp = ServiceResponse.error("Update failed: " + e.getMessage());
      }
      return resp;
   }

   private ServiceResponse<?> removeSupplier(String id) {
      ServiceResponse<Boolean> resp;
      try {
         boolean deleted = supplierFacade.removeSupplier(id);
         if (deleted) {
            resp = ServiceResponse.ok(deleted);
         } else {
            resp = ServiceResponse.error("No supplier with ID: " + id);
         }
      } catch (Exception e) {
         resp = ServiceResponse.error(e.getMessage());
      }
      return resp;
   }

   private ServiceResponse<?> getSupplierDetails(String id) {
      ServiceResponse<Supplier> resp;
      try {
         Supplier supplier = supplierFacade.getSupplier(id);
         if (supplier != null) {
            resp = ServiceResponse.ok(supplier);
         } else {
            resp = ServiceResponse.error("No supplier with ID: " + id);
         }
      } catch (Exception e) {
         resp = ServiceResponse.error(e.getMessage());
      }
      return resp;
   }

   private ServiceResponse<?> getAllSuppliers(String id) {
      ServiceResponse<Map<String, String>> resp;
      try {
         List<Supplier> suppliers = supplierFacade.getSuppliersWithFullDetail();
         Map<String, String> suppliersMap = new HashMap<>();
         for (Supplier supplier : suppliers) {
            suppliersMap.put(supplier.getSupplierId().toString(), supplier.getName());
         }
         resp = ServiceResponse.ok(suppliersMap);
      } catch (Exception e) {
         resp = ServiceResponse.error(e.getMessage());
      }
      return resp;
   }

   private ServiceResponse<?> checkSupplierExists(String infoToCheck) {
      ServiceResponse<Boolean> resp;
      try {
         boolean exists = supplierFacade.supplierExists(infoToCheck);
         resp = ServiceResponse.ok(exists);
      } catch (Exception e) {
         resp = ServiceResponse.error(e.getMessage());
      }
      return resp;
   }
}
