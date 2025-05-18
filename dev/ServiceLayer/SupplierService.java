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
import ServiceLayer.Interfaces_and_Abstracts.IService;
import ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;

/**
 * Service layer for Supplier operations, wrapping all responses in a
 * ServiceResponse<T> envelope.
 */
public class SupplierService extends BaseService {
   private final SupplierFacade supplierFacade;

   public SupplierService(SupplierFacade facade) {
      this.supplierFacade = facade;
   }

   public ServiceResponse<?> createSupplier(SupplierDTO supplierDTO) {
      return ServiceResponse.fail(List.of("Not implemented"));
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

   public ServiceResponse<?> getAllSuppliers(String id) {
      return ServiceResponse.fail(List.of("Not implemented"));
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
