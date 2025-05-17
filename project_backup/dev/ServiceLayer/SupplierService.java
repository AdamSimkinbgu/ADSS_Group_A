package ServiceLayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.fasterxml.jackson.databind.JsonNode;

import DomainLayer.Classes.Supplier;
import DomainLayer.Classes.SupplierProduct;
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
      serviceFunctions.put("addProduct", this::addProduct);
      serviceFunctions.put("updateProduct", this::updateProduct);
      serviceFunctions.put("removeProduct", this::removeProduct);
      serviceFunctions.put("listProducts", this::listProducts);
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
      ServiceResponse<Void> validation = validateJsonPayload(creationJson, Supplier.class);
      if (validation.getError() != null && !validation.getError().isEmpty()) {
         return ServiceResponse.error("Invalid JSON payload: " + validation.getError());
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
         ServiceResponse<Void> patchCheck = validatePatch(updateJson, Supplier.class);
         if (patchCheck.getError() != null && !patchCheck.getError().isEmpty()) {
            return ServiceResponse.error("Invalid JSON payload: " + patchCheck.getError());
         }
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

   private ServiceResponse<?> addProduct(String json) {
      // validate the JSON payload against SupplierProduct setters
      var v = validatePatchViaSetters(json, SupplierProduct.class);
      if (v.isFailure())
         return v;

      try {
         boolean ok = supplierFacade.addProductToSupplier(json);
         return ServiceResponse.ok(ok);
      } catch (Exception e) {
         return ServiceResponse.error(e.getMessage());
      }
   }

   private ServiceResponse<?> updateProduct(String json) {
      var v = validatePatchViaSetters(json, SupplierProduct.class);
      if (v.isFailure())
         return v;

      try {
         boolean ok = supplierFacade.updateProductOnSupplier(json);
         return ServiceResponse.ok(ok);
      } catch (Exception e) {
         return ServiceResponse.error(e.getMessage());
      }
   }

   private ServiceResponse<?> removeProduct(String json) {
      try {
         boolean ok = supplierFacade.removeProductFromSupplier(json);
         return ServiceResponse.ok(ok);
      } catch (Exception e) {
         return ServiceResponse.error(e.getMessage());
      }
   }

   private ServiceResponse<?> listProducts(String json) {
      try {
         var list = supplierFacade.listProductsForSupplier(json);
         return ServiceResponse.ok(list);
      } catch (Exception e) {
         return ServiceResponse.error(e.getMessage());
      }
   }
}
