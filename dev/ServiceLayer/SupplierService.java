package ServiceLayer;

import java.util.List;

import DTOs.SupplierDTO;
import DomainLayer.Classes.Supplier;
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
      ServiceResponse<List<String>> response = supplierValidator.validateCreateDTO(supplierDTO);
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

   public ServiceResponse<?> removeSupplier(int id) {
      ServiceResponse<?> response = supplierValidator.validateRemoveDTO(id);
      if (response.isSuccess()) {
         try {
            boolean removed = supplierFacade.removeSupplier(id);
            if (removed) {
               return ServiceResponse.ok("Supplier removed successfully");
            } else {
               return ServiceResponse.fail(List.of("Supplier not found"));
            }
         } catch (Exception e) {
            return ServiceResponse.fail(List.of("Failed to remove supplier: " + e.getMessage()));
         }
      } else {
         return ServiceResponse.fail(response.getErrors());
      }
   }

   public ServiceResponse<?> getSupplierDetails(int id) {
      ServiceResponse<?> response = supplierValidator.validateGetDTO(id);
      if (response.isSuccess()) {
         try {
            Supplier supplier = supplierFacade.getSupplier(id);
            if (supplier != null) {
               return ServiceResponse.ok(supplier);
            } else {
               return ServiceResponse.fail(List.of("Supplier not found"));
            }
         } catch (Exception e) {
            return ServiceResponse.fail(List.of("Failed to retrieve supplier: " + e.getMessage()));
         }
      } else {
         return ServiceResponse.fail(response.getErrors());
      }
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
      return ServiceResponse.fail(List.of("Not implemented")); // TODO: Implement this method
   }

   public ServiceResponse<?> addProduct(String json) {
      return ServiceResponse.fail(List.of("Not implemented")); // TODO: Implement this method
   }

   public ServiceResponse<?> updateProduct(String json) {
      return ServiceResponse.fail(List.of("Not implemented")); // TODO: Implement this method
   }

   public ServiceResponse<?> removeProduct(String json) {
      return ServiceResponse.fail(List.of("Not implemented")); // TODO: Implement this method
   }

   public ServiceResponse<?> listProducts(String json) {
      return ServiceResponse.fail(List.of("Not implemented")); // TODO: Implement this method
   }
}
