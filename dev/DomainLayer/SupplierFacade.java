package DomainLayer;

import java.security.InvalidParameterException;
import java.util.*;

import DomainLayer.Classes.Supplier;
import DomainLayer.Classes.SupplierProduct;

import DTOs.SupplierDTO;

public class SupplierFacade {
   private final Map<UUID, Supplier> suppliers = new HashMap<>();
   private final Map<UUID, SupplierProduct> supplierProducts = new HashMap<>();

   public Supplier createSupplier(SupplierDTO supplierDTO) {
      if (supplierDTO == null) {
         throw new InvalidParameterException("SupplierDTO cannot be null");
      }

      Supplier supplier = new Supplier(supplierDTO.getName(), supplierDTO.getTaxNumber(),
            supplierDTO.getAddress(), supplierDTO.getPaymentDetails(),
            supplierDTO.getContacts(), supplierDTO.getProducts(), supplierDTO.getAgreements());
      suppliers.put(supplier.getSupplierId(), supplier);
      return supplier;
   }

   public boolean removeSupplier(String json) {
      return false; // TODO: Implement this method
   }

   public boolean updateSupplier(Supplier updated) {
      return false; // TODO: Implement this method
   }

   public Supplier getSupplier(String jsonOfID) {
      return null; // TODO: Implement this method
   }

   public boolean supplierExists(String json) {
      return false; // TODO: Implement this method
   }

   public List<Supplier> getSuppliersWithFullDetail() {
      return new ArrayList<>(suppliers.values());
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
}
