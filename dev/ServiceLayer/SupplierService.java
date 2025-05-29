package ServiceLayer;

import java.util.List;

import DTOs.SupplierDTO;
import DTOs.SupplierProductDTO;
import DomainLayer.Classes.Supplier;
import DomainLayer.SupplierFacade;
import ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;
import ServiceLayer.Interfaces_and_Abstracts.Validators.ProductValidator;
import ServiceLayer.Interfaces_and_Abstracts.Validators.SupplierValidator;

/**
 * Service layer for Supplier operations, wrapping all responses in a
 * ServiceResponse<T> envelope.
 */
public class SupplierService extends BaseService {
   private final SupplierFacade supplierFacade;
   private final SupplierValidator supplierValidator = new SupplierValidator();
   private final ProductValidator productValidator = new ProductValidator();

   public SupplierService(SupplierFacade facade) {
      this.supplierFacade = facade;

   }

   public ServiceResponse<?> createSupplier(SupplierDTO supplierDTO) {
      ServiceResponse<List<String>> response = supplierValidator.validateCreateDTO(supplierDTO);
      if (response.isSuccess()) {
         try {
            Supplier supplier = supplierFacade.createSupplier(supplierDTO);
            return ServiceResponse.ok("Supplier created successfully with ID: " + supplier.getSupplierId());
         } catch (Exception e) {
            return ServiceResponse.fail(List.of("Failed to create supplier: " + e.getMessage()));
         }
      } else {
         return ServiceResponse.fail(response.getErrors());
      }
   }

   public ServiceResponse<?> updateSupplier(SupplierDTO supplierDTO, int supplierID) {
      ServiceResponse<List<String>> response = supplierValidator.validateUpdateDTO(supplierDTO);
      if (response.isSuccess()) {
         try {
            supplierFacade.updateSupplier(supplierDTO, supplierID);
            return ServiceResponse.ok("Supplier with ID " + supplierID + " updated successfully");
         } catch (Exception e) {
            return ServiceResponse.fail(List.of("Failed to update supplier: " + e.getMessage()));
         }
      } else {
         return ServiceResponse.fail(response.getErrors());
      }
   }

   public ServiceResponse<?> removeSupplier(int id) {
      ServiceResponse<?> response = supplierValidator.validateRemoveDTO(id);
      if (response.isSuccess()) {
         try {
            boolean removed = supplierFacade.removeSupplier(id);
            if (removed) {
               return ServiceResponse.ok("Supplier with ID " + id + " removed successfully");
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

   public ServiceResponse<SupplierDTO> getSupplierByID(int supplierID) {
      ServiceResponse<?> response = supplierValidator.validateGetDTO(supplierID);
      if (response.isSuccess()) {
         try {
            SupplierDTO supplier = supplierFacade.getSupplierDTO(supplierID);
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

   public ServiceResponse<?> checkSupplierExists(int supplierID) {
      ServiceResponse<?> response = supplierValidator.validateGetDTO(supplierID);
      if (response.isSuccess()) {
         try {
            boolean exists = supplierFacade.checkSupplierExists(supplierID);
            if (exists) {
               return ServiceResponse.ok("Supplier with ID " + supplierID + " exists");
            } else {
               return ServiceResponse.fail(List.of("Supplier with ID " + supplierID + " does not exist"));
            }
         } catch (Exception e) {
            return ServiceResponse.fail(List.of("Failed to check supplier existence: " + e.getMessage()));
         }
      } else {
         return ServiceResponse.fail(response.getErrors());
      }
   }

   public ServiceResponse<?> addProductToSupplier(SupplierProductDTO productDTO, int supplierID) {
      ServiceResponse<?> response = productValidator.validateCreateDTO(productDTO);
      if (response.isSuccess()) {
         try {
            // Validate that the supplier exists before adding the product
            ServiceResponse<?> supplierResponse = supplierValidator.validateGetDTO(supplierID);
            if (!supplierResponse.isSuccess()) {
               return ServiceResponse.fail(supplierResponse.getErrors());
            }
            SupplierDTO supplier = supplierFacade.getSupplierDTO(supplierID);
            if (supplier == null) {
               return ServiceResponse.fail(List.of("Supplier with ID " + supplierID + " does not exist"));
            }
            supplierFacade.addProductToSupplierAndMemory(supplierID, productDTO);
            return ServiceResponse.ok("Product added successfully to supplier with ID " + supplierID);
         } catch (Exception e) {
            return ServiceResponse.fail(List.of("Failed to add product: " + e.getMessage()));
         }
      } else {
         return ServiceResponse.fail(response.getErrors());
      }

   }

   public ServiceResponse<?> updateProduct(String json) {
      return ServiceResponse.fail(List.of("Not implemented")); // TODO: Implement this method
   }

   public ServiceResponse<?> removeProduct(int productID, int supplierID) {
      if (productID < 0) {
         return ServiceResponse.fail(List.of("Product ID must be a positive integer"));
      }
      if (supplierID < 0) {
         return ServiceResponse.fail(List.of("Supplier ID must be a positive integer"));
      }
      try {
         supplierFacade.removeProductFromSupplierAndMemory(supplierID, productID);
         return ServiceResponse
               .ok("Product with ID " + productID + " removed successfully from supplier with ID " + supplierID);
      } catch (Exception e) {
         return ServiceResponse.fail(List.of("Failed to remove product: " + e.getMessage()));
      }
   }

   public ServiceResponse<?> listProducts(int supplierID) {
      ServiceResponse<?> response = supplierValidator.validateGetDTO(supplierID);
      if (response.isSuccess()) {
         try {
            List<SupplierProductDTO> products = supplierFacade.getSupplierProducts(supplierID);
            return ServiceResponse.ok(products);
         } catch (Exception e) {
            return ServiceResponse.fail(List.of("Failed to retrieve products: " + e.getMessage()));
         }
      } else {
         return ServiceResponse.fail(response.getErrors());
      }
   }
}
