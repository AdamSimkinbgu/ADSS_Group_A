package ServiceLayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.fasterxml.jackson.databind.JsonNode;

import DTOs.SupplierDTO;
import DomainLayer.Classes.Supplier;
import DomainLayer.Classes.SupplierProduct;
import DomainLayer.SupplierFacade;
import ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;
import ServiceLayer.Interfaces_and_Abstracts.Validators.SupplierValidator;

/**
 * Service layer for Supplier operations, wrapping all responses in a
 * ServiceResponse<T> envelope.
 */
public class SupplierService extends BaseService {
   private final SupplierFacade supplierFacade;
   private final SupplierValidator supplierValidator = new SupplierValidator();

   public SupplierService(SupplierFacade facade) {
      this.supplierFacade = facade;

   }

   public ServiceResponse<?> createSupplier(SupplierDTO supplierDTO) {
      ServiceResponse<List<String>> response = supplierValidator.validate(supplierDTO);
      if (response.isSuccess()) {
         try {
            Supplier supplier = supplierFacade.createSupplier(supplierDTO);
            return ServiceResponse.ok(supplier);
         } catch (Exception e) {
            return ServiceResponse.fail(List.of("Failed to create supplier: " + e.getMessage()));
         }
      } else {
         return ServiceResponse.fail(response.getErrors());
      }
   }

   public ServiceResponse<?> updateSupplier(String updateJson) {
      return ServiceResponse.fail(List.of("Not implemented"));
   }

   public ServiceResponse<?> removeSupplier(String id) {
      return ServiceResponse.fail(List.of("Not implemented"));
   }

   public ServiceResponse<?> getSupplierDetails(String id) {
      return ServiceResponse.fail(List.of("Not implemented"));
   }

   public ServiceResponse<List<SupplierDTO>> getAllSuppliers() {
      try {
         List<SupplierDTO> suppliers = supplierFacade.getAllSuppliers();
         return ServiceResponse.ok(suppliers);
      } catch (Exception e) {
         return ServiceResponse.fail(List.of("Failed to retrieve suppliers: " + e.getMessage()));
      }
   }

   public ServiceResponse<?> checkSupplierExists(String infoToCheck) {
      return ServiceResponse.fail(List.of("Not implemented"));
   }

   public ServiceResponse<?> addProduct(String json) {
      return ServiceResponse.fail(List.of("Not implemented"));
   }

   public ServiceResponse<?> updateProduct(String json) {
      return ServiceResponse.fail(List.of("Not implemented"));
   }

   public ServiceResponse<?> removeProduct(String json) {
      return ServiceResponse.fail(List.of("Not implemented"));
   }

   public ServiceResponse<?> listProducts(String json) {
      return ServiceResponse.fail(List.of("Not implemented"));
   }
}
