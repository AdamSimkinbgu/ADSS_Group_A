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

   public SupplierService(SupplierFacade facade) {
      this.supplierFacade = facade;
   }

   @Override
   public ServiceResponse<?> execute(String serviceOption, String data) {
      return ServiceResponse.error("Not implemented");
   }

   // example function
   private ServiceResponse<?> addSupplier(String creationJson) {
      return ServiceResponse.error("Not implemented");
   }

   private ServiceResponse<?> updateSupplier(String updateJson) {
      return ServiceResponse.error("Not implemented");
   }

   private ServiceResponse<?> removeSupplier(String id) {
      return ServiceResponse.error("Not implemented");
   }

   private ServiceResponse<?> getSupplierDetails(String id) {
      return ServiceResponse.error("Not implemented");
   }

   private ServiceResponse<?> getAllSuppliers(String id) {
      return ServiceResponse.error("Not implemented");
   }

   private ServiceResponse<?> checkSupplierExists(String infoToCheck) {
      return ServiceResponse.error("Not implemented");
   }

   private ServiceResponse<?> addProduct(String json) {
      return ServiceResponse.error("Not implemented");
   }

   private ServiceResponse<?> updateProduct(String json) {
      return ServiceResponse.error("Not implemented");
   }

   private ServiceResponse<?> removeProduct(String json) {
      return ServiceResponse.error("Not implemented");
   }

   private ServiceResponse<?> listProducts(String json) {
      return ServiceResponse.error("Not implemented");
   }
}
