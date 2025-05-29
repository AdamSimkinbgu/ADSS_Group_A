package DomainLayer;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.util.*;

import DomainLayer.Classes.Supplier;
import DomainLayer.Classes.SupplierProduct;
import DTOs.AddressDTO;
import DTOs.PaymentDetailsDTO;
import DTOs.SupplierDTO;
import DTOs.SupplierProductDTO;
import DTOs.Enums.DayofWeek;
import DTOs.Enums.PaymentMethod;
import DTOs.Enums.PaymentTerm;

public class SupplierFacade {
   private static String configPath;
   private static int nextSupplierID;
   private static int nextProductID;
   private final Map<Integer, Supplier> suppliers = new HashMap<>();
   private final Map<Integer, Set<SupplierProduct>> supplierProducts = new HashMap<>(); // used to find all the products
                                                                                        // for a supplier
   private final Map<SupplierProduct, List<Integer>> supplierProductMap = new HashMap<>(); // used to find all the
                                                                                           // suppliers for a product
   private final Set<SupplierProduct> productCagalog = new HashSet<>(); // used to find all the products available in
                                                                        // the system

   public SupplierFacade(boolean initialize, String configJson) {
      if (configJson == null) {
         configPath = "config.json";
      } else {
         configPath = configJson;
      }
      if (initialize) {
         initialize(); // will become "loadFromDB()" in the future
      }

      // Initialize the next IDs
      nextSupplierID = suppliers.keySet().stream().max(Integer::compareTo).orElse(0) + 1;
      nextProductID = productCagalog.stream().mapToInt(SupplierProduct::getProductId).max().orElse(0) + 1;
   }

   private void initialize() {
      Supplier supplier1 = new Supplier(nextSupplierID++, "Supplier 1", "512345678",
            new AddressDTO("Street 1", "City 1", "Building 1"),
            new PaymentDetailsDTO("123456", PaymentMethod.CREDIT_CARD, PaymentTerm.N30),
            true, EnumSet.of(DayofWeek.MONDAY, DayofWeek.WEDNESDAY), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
      Supplier supplier2 = new Supplier(nextSupplierID++, "Supplier 2", "587654321",
            new AddressDTO("Street 2", "City 2", "Building 2"),
            new PaymentDetailsDTO("654321", PaymentMethod.CASH, PaymentTerm.N60),
            false, EnumSet.of(DayofWeek.TUESDAY, DayofWeek.THURSDAY), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
      Supplier supplier3 = new Supplier(nextSupplierID++, "Supplier 3", "518273645",
            new AddressDTO("Street 3", "City 3", "Building 3"),
            new PaymentDetailsDTO("162534", PaymentMethod.CASH_ON_DELIVERY, PaymentTerm.N60),
            true, EnumSet.of(DayofWeek.FRIDAY), new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
      suppliers.put(0, supplier1);
      suppliers.put(1, supplier2);
      suppliers.put(2, supplier3);
      // Add products to suppliers
      SupplierProduct product1 = new SupplierProduct(nextProductID++, "123456", "Product 1", new BigDecimal("10.00"),
            new BigDecimal("1.0"), 30, "Manufacturer 1");
      SupplierProduct product2 = new SupplierProduct(nextProductID++, "654321", "Product 2", new BigDecimal("20.00"),
            new BigDecimal("2.0"), 60, "Manufacturer 2");
      SupplierProduct product3 = new SupplierProduct(nextProductID++, "789012", "Product 3", new BigDecimal("30.00"),
            new BigDecimal("3.0"), 90, "Manufacturer 3");
      supplier1.addProduct(product1.getProductId());
      supplier1.addProduct(product2.getProductId());
      supplier2.addProduct(product2.getProductId());
      supplier2.addProduct(product3.getProductId());
      supplier3.addProduct(product1.getProductId());
      supplier3.addProduct(product3.getProductId());
      // Add products to supplierProducts map
      supplierProducts.computeIfAbsent(supplier1.getSupplierId(), k -> new HashSet<>()).add(product1);
      supplierProducts.computeIfAbsent(supplier1.getSupplierId(), k -> new HashSet<>()).add(product2);
      supplierProducts.computeIfAbsent(supplier2.getSupplierId(), k -> new HashSet<>()).add(product2);
      supplierProducts.computeIfAbsent(supplier2.getSupplierId(), k -> new HashSet<>()).add(product3);
      supplierProducts.computeIfAbsent(supplier3.getSupplierId(), k -> new HashSet<>()).add(product1);
      supplierProducts.computeIfAbsent(supplier3.getSupplierId(), k -> new HashSet<>()).add(product3);
      // Add products to productCatalog
      productCagalog.add(product1);
      productCagalog.add(product2);
      productCagalog.add(product3);
      // Add products to supplierProductMap
      supplierProductMap.computeIfAbsent(product1, k -> new ArrayList<>()).add(supplier1.getSupplierId());
      supplierProductMap.computeIfAbsent(product2, k -> new ArrayList<>()).add(supplier1.getSupplierId());
      supplierProductMap.computeIfAbsent(product2, k -> new ArrayList<>()).add(supplier2.getSupplierId());
      supplierProductMap.computeIfAbsent(product3, k -> new ArrayList<>()).add(supplier2.getSupplierId());
      supplierProductMap.computeIfAbsent(product1, k -> new ArrayList<>()).add(supplier3.getSupplierId());
      supplierProductMap.computeIfAbsent(product3, k -> new ArrayList<>()).add(supplier3.getSupplierId());
      // debug print
      System.out.println("Initialized suppliers: " + suppliers);
      System.out.println("Initialized supplier products: " + supplierProducts);
      System.out.println("Initialized supplier product map: " + supplierProductMap);
      System.out.println("Initialized product catalog: " + productCagalog);
      System.out.println("Next supplier ID: " + nextSupplierID);
      System.out.println("Next product ID: " + nextProductID);
   }

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
         SupplierProduct actualProduct = new SupplierProduct(product);
         // -VVV- this is used to find all the products for a supplier
         products.add(actualProduct);
         // -VVV- this is used to find all the suppliers for a product
         supplierProductMap.computeIfAbsent(actualProduct, k -> new ArrayList<>()).add(supplier.getSupplierId());
         // -VVV- this is used to find all the products available in the system
         productCagalog.add(actualProduct);
      }
      // debug print
      System.out.println("Supplier created: " + supplier);
      System.out.println("Supplier products: " + products);
      System.out.println("Supplier product map: " + supplierProductMap);
      System.out.println("Product catalog: " + productCagalog);
      return supplier;
   }

   public boolean removeSupplier(int supplierID) {
      Supplier supplier = suppliers.remove(supplierID);
      if (supplier == null) {
         return false;
      }
      supplierProducts.remove(supplierID);
      for (SupplierProduct product : supplierProducts.get(supplierID)) {
         List<Integer> suppliersForProduct = supplierProductMap.get(product);
         if (suppliersForProduct != null) {
            suppliersForProduct.removeIf(id -> id == supplierID);
            if (suppliersForProduct.isEmpty()) {
               supplierProductMap.remove(product);
               productCagalog.remove(product);
            }
         }
      }
      return true;
   }

   public void updateSupplier(SupplierDTO supplierDTO, int supplierID) {
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
            BeanPatch.of(SupplierDTO::getContactsInfoList, Supplier::getContacts, Supplier::setContacts),
            BeanPatch.of(SupplierDTO::getProductIDs, Supplier::getProducts, Supplier::setProducts),
            BeanPatch.of(SupplierDTO::getAgreements, Supplier::getAgreements, Supplier::setAgreements));

      // ─── single loop, no reflection ───
      rules.forEach(rule -> rule.apply(supplierDTO, supplier));
   }

   public SupplierDTO getSupplierDTO(int supplierID) {
      Supplier supplier = suppliers.get(supplierID);
      if (supplier == null) {
         throw new IllegalArgumentException("Supplier not found");
      }
      SupplierDTO supplierDTO = new SupplierDTO(supplier);
      // add products to the DTO
      List<SupplierProductDTO> productDTOs = getSupplierProducts(supplierID);
      supplierDTO.setProducts(productDTOs);
      return supplierDTO;
   }

   private List<SupplierProductDTO> getSupplierProducts(int supplierID) {
      Set<SupplierProduct> products = supplierProducts.get(supplierID);
      if (products == null) {
         return Collections.emptyList();
      }
      return SupplierProductDTO.fromSupplierProductList(products.stream().toList());
   }

   public void addProductToSupplier(SupplierProductDTO product, int supplierID) {
      if (product == null) {
         throw new IllegalArgumentException("Product cannot be null");
      }
      Supplier supplier = suppliers.get(supplierID);
      if (supplier == null) {
         throw new IllegalArgumentException("Supplier not found");
      }
      product.setProductId(nextProductID++);
      SupplierProduct supplierProduct = new SupplierProduct(product);
      Set<SupplierProduct> products = supplierProducts.computeIfAbsent(supplierID, k -> new HashSet<>());
      supplier.addProduct(supplierProduct.getProductId());
      products.add(supplierProduct);
      supplierProductMap.computeIfAbsent(supplierProduct, k -> new ArrayList<>()).add(supplierID);
      productCagalog.add(supplierProduct);
      // debug print
      System.out.println("Product added to supplier: " + supplier);
      System.out.println("Supplier products for supplier ID " + supplierID + ": " + supplierProducts.get(supplierID));
      System.out.println("Supplier product map for product ID " + supplierProduct.getProductId() + ": "
            + supplierProductMap.get(supplierProduct));
      System.out.println("Product catalog: " + productCagalog);
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
         products.removeIf(p -> p.getProductId() == product.getProductId());
         supplier.removeProduct(product.getProductId());
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
         supplierDTOs.add(getSupplierDTO(supplier.getSupplierId()));
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
         products.removeIf(p -> p.getProductId() == product.getProductId());
      }
   }

   public boolean checkSupplierExists(int supplierID) {
      return suppliers.containsKey(supplierID);
   }

}
