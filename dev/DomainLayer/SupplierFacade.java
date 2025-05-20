package DomainLayer;

import java.security.InvalidParameterException;
import java.util.*;

import DomainLayer.Classes.Supplier;
import DomainLayer.Classes.SupplierProduct;

import DTOs.SupplierDTO;

public class SupplierFacade {
   private final Map<Integer, Supplier> suppliers = new HashMap<>();
   private final Map<Integer, List<SupplierProduct>> supplierProducts = new HashMap<>();

   public Supplier createSupplier(SupplierDTO supplierDTO) {
      if (supplierDTO == null) {
         throw new InvalidParameterException("SupplierDTO cannot be null");
      }
      Supplier supplier = new Supplier(supplierDTO);
      if (suppliers.values().stream()
            .anyMatch(existingSupplier -> existingSupplier.getName().equalsIgnoreCase(supplier.getName()))) {
         throw new IllegalArgumentException("Supplier with the same name already exists");
      }
      suppliers.put(supplier.getSupplierId(), supplier);
      return supplier;
   }

   public boolean removeSupplier(int supplierID) {
      Supplier supplier = suppliers.remove(supplierID);
      if (supplier == null) {
         return false;
      }
      supplierProducts.remove(supplierID);
      return true;
   }

   public boolean updateSupplier(Supplier updated) {
      return false; // TODO: Implement this method
   }

   public Supplier getSupplier(int supplierID) {
      Supplier supplier = suppliers.get(supplierID);
      if (supplier == null) {
         throw new IllegalArgumentException("Supplier not found");
      }
      return supplier;
   }

   public boolean supplierExists(String json) {
      return false; // TODO: Implement this method
   }

   public boolean addProductToSupplier(String json) {
      return false; // TODO: Implement this method

   }

   public boolean updateProductOnSupplier(String json) {
      return false; // TODO: Implement this method
   }

   public boolean removeProductFromSupplier(String json) {
      return false; // TODO: Implement this method
   }

   public List<SupplierProduct> listProductsForSupplier(String json) {
      return null; // TODO: Implement this method
   }

   public boolean addAgreementToSupplier(String supUpdate) {
      return false; // TODO: Implement this method
   }

   public List<SupplierDTO> getAllSuppliers() {
      List<SupplierDTO> supplierDTOs = new ArrayList<>();
      for (Supplier supplier : suppliers.values()) {
         SupplierDTO dto = new SupplierDTO(supplier);
         supplierDTOs.add(dto);
      }
      return supplierDTOs;
   }

}
