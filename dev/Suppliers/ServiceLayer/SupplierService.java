package Suppliers.ServiceLayer;

import java.util.List;

import Suppliers.DTOs.CatalogProductDTO;
import Suppliers.DTOs.SupplierDTO;
import Suppliers.DTOs.SupplierProductDTO;
import Suppliers.DomainLayer.Classes.Supplier;
import Suppliers.DomainLayer.SupplierController;
import Suppliers.ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;
import Suppliers.ServiceLayer.Interfaces_and_Abstracts.Validators.ProductValidator;
import Suppliers.ServiceLayer.Interfaces_and_Abstracts.Validators.SupplierValidator;

/**
 * Service layer for Supplier operations, wrapping all responses in a
 * ServiceResponse<T> envelope.
 */
public class SupplierService extends BaseService {
   private final SupplierController supplierFacade;
   private final SupplierValidator supplierValidator = new SupplierValidator();
   private final ProductValidator productValidator = new ProductValidator();

   public SupplierService(SupplierController facade) {
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

   public ServiceResponse<?> updateProduct(SupplierProductDTO productDTO, int supplierID) {
      ServiceResponse<?> response = productValidator.validateUpdateDTO(productDTO);
      if (response.isSuccess()) {
         try {
            // Validate that the supplier exists before updating the product
            ServiceResponse<?> supplierResponse = supplierValidator.validateGetDTO(supplierID);
            if (!supplierResponse.isSuccess()) {
               return ServiceResponse.fail(supplierResponse.getErrors());
            }
            SupplierDTO supplier = supplierFacade.getSupplierDTO(supplierID);
            if (supplier == null) {
               return ServiceResponse.fail(List.of("Supplier with ID " + supplierID + " does not exist"));
            }
            supplierFacade.updateProductInSupplierAndMemory(supplierID, productDTO);
            return ServiceResponse.ok("Product updated successfully for supplier with ID " + supplierID);
         } catch (Exception e) {
            return ServiceResponse.fail(List.of("Failed to update product: " + e.getMessage()));
         }
      } else {
         return ServiceResponse.fail(response.getErrors());
      }
   }

   public ServiceResponse<?> removeProduct(int productID, int supplierID) {
      if (!supplierValidator.validateGetDTO(supplierID).isSuccess()
            || !productValidator.validateGetDTO(productID).isSuccess()) {
         return ServiceResponse.fail(List.of("Invalid supplier or product ID"));
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

   public ServiceResponse<List<CatalogProductDTO>> getAllProducts() {
      try {
         List<CatalogProductDTO> products = supplierFacade.getProductCatalog();
         return ServiceResponse.ok(products);
      } catch (Exception e) {
         return ServiceResponse.fail(List.of("Failed to retrieve all products: " + e.getMessage()));
      }
   }

   public ServiceResponse<List<SupplierProductDTO>> getSupplierProducts(int supplierID) {
      ServiceResponse<?> response = supplierValidator.validateGetDTO(supplierID);
      if (response.isSuccess()) {
         try {
            List<SupplierProductDTO> products = supplierFacade.getSupplierProducts(supplierID);
            if (products != null && !products.isEmpty()) {
               return ServiceResponse.ok(products);
            } else {
               return ServiceResponse.fail(List.of("No products found for supplier with ID " + supplierID));
            }
         } catch (Exception e) {
            return ServiceResponse.fail(List.of("Failed to retrieve products: " + e.getMessage()));
         }
      } else {
         return ServiceResponse.fail(response.getErrors());
      }
   }
}
