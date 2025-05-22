package DomainLayer;

import java.security.InvalidParameterException;
import java.util.*;

import DomainLayer.Classes.Supplier;
import DomainLayer.Classes.SupplierProduct;

import DTOs.SupplierDTO;
import DTOs.SupplierProductDTO;

public class SupplierFacade {
   private final Map<Integer, Supplier> suppliers = new HashMap<>();
   private final Map<Integer, Set<SupplierProduct>> supplierProducts = new HashMap<>();

   public Supplier createSupplier(SupplierDTO supplierDTO) {
      if (supplierDTO == null) {
         throw new InvalidParameterException("SupplierDTO cannot be null");
      }
      Supplier supplier = new Supplier(supplierDTO);
      if (suppliers.values().stream()
            .anyMatch(existingSupplier -> existingSupplier.getTaxNumber().equalsIgnoreCase(supplier.getTaxNumber()))) {
         throw new IllegalArgumentException("Supplier with the same tax number already exists");
      }
      // insert info into runtime memory
      suppliers.put(supplier.getSupplierId(), supplier);
      supplierProducts.put(supplier.getSupplierId(), new HashSet<>());
      Set<SupplierProduct> products = supplierProducts.computeIfAbsent(supplier.getSupplierId(), k -> new HashSet<>());
      for (SupplierProductDTO product : supplierDTO.getProducts()) {
         products.add(new SupplierProduct(product));
      }
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

   public SupplierDTO updateSupplier(SupplierDTO supplierDTO, int supplierID) {
      if (supplierDTO == null) {
         throw new InvalidParameterException("SupplierDTO cannot be null");
      }
      Supplier supplier = suppliers.get(supplierID);
      if (supplier == null) {
         throw new IllegalArgumentException("Supplier not found");
      }
      // check what needs to be updated and update it
      List<BeanPatch<SupplierDTO, Supplier, ?>> rules = List.of(
            BeanPatch.of(SupplierDTO::getName, Supplier::getName, Supplier::setName),
            BeanPatch.of(SupplierDTO::getTaxNumber, Supplier::getTaxNumber, Supplier::setTaxNumber),
            BeanPatch.of(SupplierDTO::getAddress, Supplier::getAddress, Supplier::setAddress),
            BeanPatch.of(SupplierDTO::getSelfSupply, Supplier::getSelfSupply, Supplier::setSelfSupply),
            BeanPatch.of(SupplierDTO::getSupplyDays, Supplier::getSupplyDays, Supplier::setSupplyDays),
            BeanPatch.of(SupplierDTO::getPaymentDetails, Supplier::getPaymentDetails, Supplier::setPaymentDetails),
            BeanPatch.of(SupplierDTO::getContactsList, Supplier::getContacts, Supplier::setContacts),
            BeanPatch.of(SupplierDTO::getProductsList, Supplier::getProducts, Supplier::setProducts),
            BeanPatch.of(SupplierDTO::getAgreements, Supplier::getAgreements, Supplier::setAgreements));

      // ─── single loop, no reflection ───
      rules.forEach(rule -> rule.apply(supplierDTO, supplier));
      return new SupplierDTO(supplier);
   }

   public SupplierDTO getSupplierDTO(int supplierID) {
      Supplier supplier = suppliers.get(supplierID);
      if (supplier == null) {
         throw new IllegalArgumentException("Supplier not found");
      }
      return new SupplierDTO(supplier);
   }

   public void addProductToSupplier(int supplierID, SupplierProductDTO product) {
      if (product == null) {
         throw new IllegalArgumentException("Product cannot be null");
      }
      Supplier supplier = suppliers.get(supplierID);
      if (supplier == null) {
         throw new IllegalArgumentException("Supplier not found");
      }
      SupplierProduct supplierProduct = new SupplierProduct(product);
      Set<SupplierProduct> products = supplierProducts.computeIfAbsent(supplierID, k -> new HashSet<>());
      supplier.addProduct(supplierProduct); // TODO: change this to only add id
      products.add(supplierProduct);
   }

   public boolean updateProductOnSupplier(String json) {
      return false; // TODO: Implement this method
   }

   public void removeProductFromSupplier(int supplierID, SupplierProductDTO product) {
      if (product == null) {
         throw new IllegalArgumentException("Product cannot be null");
      }
      Supplier supplier = suppliers.get(supplierID);
      if (supplier == null) {
         throw new IllegalArgumentException("Supplier not found");
      }
      Set<SupplierProduct> products = supplierProducts.get(supplierID);
      if (products != null) {
         products.removeIf(p -> p.getProductId() == product.productId());
         supplier.removeProduct(product.productId());
      }

   }

   public List<SupplierProductDTO> listProductsForSupplier(int supplierID) {
      return SupplierProductDTO.fromSupplierProductList(supplierProducts.get(supplierID).stream().toList());
   }

   public void addAgreementToSupplier(int supplierID, int agreementID) {
      Supplier supplier = suppliers.get(supplierID);
      if (supplier == null) {
         throw new IllegalArgumentException("Supplier not found");
      }
      supplier.addAgreement(agreementID);
   }

   public List<SupplierDTO> getAllSuppliers() {
      List<SupplierDTO> supplierDTOs = new ArrayList<>();
      for (Supplier supplier : suppliers.values()) {
         SupplierDTO dto = new SupplierDTO(supplier);
         supplierDTOs.add(dto);
      }
      return supplierDTOs;
   }

   public void addProductToSupplierProductMap(int supplierID, SupplierProductDTO product) {
      if (product == null) {
         throw new IllegalArgumentException("Product cannot be null");
      }
      SupplierProduct supplierProduct = new SupplierProduct(product);
      Set<SupplierProduct> products = supplierProducts.computeIfAbsent(supplierID, k -> new HashSet<>());
      products.add(supplierProduct);
   }

   public void removeProductFromSupplierProductMap(int supplierID, SupplierProductDTO product) {
      if (product == null) {
         throw new IllegalArgumentException("Product cannot be null");
      }
      Set<SupplierProduct> products = supplierProducts.get(supplierID);
      if (products != null) {
         products.removeIf(p -> p.getProductId() == product.productId());
      }
   }

}
