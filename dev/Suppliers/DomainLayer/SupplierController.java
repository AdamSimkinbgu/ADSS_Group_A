package Suppliers.DomainLayer;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Suppliers.DomainLayer.Classes.Agreement;
import Suppliers.DomainLayer.Classes.Supplier;
import Suppliers.DomainLayer.Classes.SupplierProduct;
import Suppliers.DomainLayer.Repositories.SuppliersAgreementsRepositoryImpl;
import Suppliers.DTOs.AddressDTO;
import Suppliers.DTOs.AgreementDTO;
import Suppliers.DTOs.BillofQuantitiesItemDTO;
import Suppliers.DTOs.CatalogProductDTO;
import Suppliers.DTOs.PaymentDetailsDTO;
import Suppliers.DTOs.SupplierDTO;
import Suppliers.DTOs.SupplierProductDTO;
import Suppliers.DTOs.Enums.PaymentMethod;
import Suppliers.DTOs.Enums.PaymentTerm;

public class SupplierController {
   private String configPath;
   private static int nextSupplierID;
   private static int nextProductID;
   private AgreementFacade agreementFacade;
   private final SuppliersAgreementsRepositoryImpl suppliersAgreementsRepo;
   private static final Logger LOGGER = LoggerFactory.getLogger(SupplierController.class);
   // private final SuppliersAgreementsRepositoryImpl agreementRepository;
   private final Map<Integer, Supplier> suppliers = new HashMap<>();
   // Map of supplier IDs to their products and prices
   private final Map<Integer, Map<Integer, SupplierProduct>> supplierIDsToTheirProductIDsAndTheirSpesification = new HashMap<>();

   private final Map<Integer, List<Integer>> productIDsToTheirSupplierIDs = new HashMap<>(); // used to find all the
   // suppliers for a product
   private final Set<CatalogProductDTO> productCagalog = new HashSet<>(); // used to find all the products available in
                                                                          // the system

   public SupplierController(boolean initialize, String configJson, AgreementFacade agreementFacade) {
      LOGGER.info("Initializing SupplierFacade with configJson: {}", configJson);
      this.suppliersAgreementsRepo = SuppliersAgreementsRepositoryImpl.getInstance();
      this.agreementFacade = agreementFacade;
      if (configJson == null) {
         this.configPath = "config.json";
      } else {
         this.configPath = configJson;
      }
      if (suppliersAgreementsRepo != null && initialize) {
         try {
            DBinitialize(); // will become "loadFromDB()" in the future
         } catch (SQLException e) {
            LOGGER.error("Error initializing database: {}", e.getMessage());
            throw new RuntimeException("Failed to initialize database", e);
         }
         LOGGER.info("Database initialized successfully.");
      } else if (initialize) {
         initialize(); // will become "loadFake()" in the future
         LOGGER.info("Database initialized with fake data.");
      } else {
         emptyDBinitialize();
         LOGGER.info("Database initialized with empty data.");
      }
   }

   private void emptyDBinitialize() {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'emptyDBinitialize'");
   }

   private void DBinitialize() throws SQLException {
      // Load suppliers and agreements from the database
      List<SupplierDTO> supplierDTOs = suppliersAgreementsRepo.getAllSuppliers();
      if (supplierDTOs != null) {
         // the agreements and products need to be fetched from the database for each
         // supplier
         for (SupplierDTO supplierDTO : supplierDTOs) {
            Supplier supplier = new Supplier(supplierDTO);
            suppliers.put(supplier.getSupplierId(), supplier);
            // Load products for the supplier
            List<SupplierProductDTO> products = suppliersAgreementsRepo
                  .getAllSupplierProductsById(supplier.getSupplierId());
            if (products != null) {
               Map<Integer, SupplierProduct> productsMap = new HashMap<>();
               for (SupplierProductDTO product : products) {
                  SupplierProduct supplierProduct = new SupplierProduct(product);
                  productsMap.put(supplierProduct.getProductId(), supplierProduct);
                  // Add the product to the catalog
                  productCagalog.add(new CatalogProductDTO(supplierProduct));
                  // Add the product to the product IDs to their supplier IDs map
                  productIDsToTheirSupplierIDs.computeIfAbsent(supplierProduct.getProductId(), k -> new ArrayList<>())
                        .add(supplier.getSupplierId());
               }
               supplierIDsToTheirProductIDsAndTheirSpesification.put(supplier.getSupplierId(), productsMap);
            }
            // Load agreements for the supplier
            List<AgreementDTO> agreements = suppliersAgreementsRepo
                  .getAllAgreementsForSupplier(supplier.getSupplierId());
            if (agreements != null) {
               for (AgreementDTO agreement : agreements) {
                  supplier.addAgreement(agreement.getAgreementId());
                  List<BillofQuantitiesItemDTO> items = suppliersAgreementsRepo.getBillOfQuantitiesItemsForAgreement(
                        agreement.getAgreementId());
                  if (items != null) {
                     agreement.setBillOfQuantitiesItems(items);
                  }
                  agreementFacade.createAgreement(agreement);
               }
            }
         }
      }
   }

   private void initialize() {
      Supplier supplier1 = new Supplier(nextSupplierID++, "Supplier 1", "512345678",
            new AddressDTO("Street 1", "City 1", "Building 1"),
            new PaymentDetailsDTO("123456", PaymentMethod.CREDIT_CARD, PaymentTerm.N30),
            true, EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY), 0, new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
      Supplier supplier2 = new Supplier(nextSupplierID++, "Supplier 2", "587654321",
            new AddressDTO("Street 2", "City 2", "Building 2"),
            new PaymentDetailsDTO("654321", PaymentMethod.CASH, PaymentTerm.N60),
            false, EnumSet.of(DayOfWeek.TUESDAY, DayOfWeek.THURSDAY), 1, new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
      Supplier supplier3 = new Supplier(nextSupplierID++, "Supplier 3", "518273645",
            new AddressDTO("Street 3", "City 3", "Building 3"),
            new PaymentDetailsDTO("162534", PaymentMethod.CASH_ON_DELIVERY, PaymentTerm.N60),
            true, EnumSet.of(DayOfWeek.FRIDAY), 1, new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
      try {
         suppliersAgreementsRepo.createSupplier(new SupplierDTO(supplier1));
         suppliersAgreementsRepo.createSupplier(new SupplierDTO(supplier2));
         suppliersAgreementsRepo.createSupplier(new SupplierDTO(supplier3));
      } catch (SQLException e) {
         LOGGER.error("Error creating suppliers in the database: {}", e.getMessage());
         throw new RuntimeException("Failed to create suppliers in the database", e);
      }
      suppliers.put(0, supplier1);
      suppliers.put(1, supplier2);
      suppliers.put(2, supplier3);
      // Add products to suppliers
      SupplierProduct product1 = new SupplierProduct(supplier1.getSupplierId(), nextProductID++, "123456",
            "Product 1", new BigDecimal("10.00"), new BigDecimal("1.0"), 30, "Manufacturer 1");
      SupplierProduct product2 = new SupplierProduct(supplier1.getSupplierId(), nextProductID++, "654321",
            "Product 2", new BigDecimal("20.00"), new BigDecimal("2.0"), 60, "Manufacturer 2");
      SupplierProduct product3 = new SupplierProduct(supplier2.getSupplierId(), nextProductID++, "789012",
            "Product 3", new BigDecimal("15.00"), new BigDecimal("1.5"), 45, "Manufacturer 3");
      SupplierProduct product4 = new SupplierProduct(supplier2.getSupplierId(), nextProductID++, "210987",
            "Product 4", new BigDecimal("25.00"), new BigDecimal("3.0"), 90, "Manufacturer 4");
      SupplierProduct product5 = new SupplierProduct(supplier3.getSupplierId(), nextProductID++, "345678",
            "Product 5", new BigDecimal("30.00"), new BigDecimal("2.5"), 15, "Manufacturer 5");
      SupplierProduct product6 = new SupplierProduct(supplier3.getSupplierId(), nextProductID++, "876543",
            "Product 6", new BigDecimal("40.00"), new BigDecimal("4.0"), 120, "Manufacturer 6");

      try {
         suppliersAgreementsRepo.createSupplierProduct(new SupplierProductDTO(product1));
         suppliersAgreementsRepo.createSupplierProduct(new SupplierProductDTO(product2));
         suppliersAgreementsRepo.createSupplierProduct(new SupplierProductDTO(product3));
         suppliersAgreementsRepo.createSupplierProduct(new SupplierProductDTO(product4));
         suppliersAgreementsRepo.createSupplierProduct(new SupplierProductDTO(product5));
         suppliersAgreementsRepo.createSupplierProduct(new SupplierProductDTO(product6));
      } catch (SQLException e) {
         LOGGER.error("Error creating products in the database: {}", e.getMessage());
         throw new RuntimeException("Failed to create products in the database", e);
      }
      // Add products to suppliers
      supplier1.addProduct(product1.getProductId());
      supplier1.addProduct(product2.getProductId());
      supplier1.addProduct(product3.getProductId());
      supplier2.addProduct(product3.getProductId());
      supplier2.addProduct(product4.getProductId());
      supplier3.addProduct(product5.getProductId());
      supplier3.addProduct(product6.getProductId());
      // Add products to supplierIDsToTheirProductIDsAndTheirPrice
      supplierIDsToTheirProductIDsAndTheirSpesification.put(supplier1.getSupplierId(), new HashMap<>());
      supplierIDsToTheirProductIDsAndTheirSpesification.get(supplier1.getSupplierId())
            .put(product1.getProductId(), product1);
      supplierIDsToTheirProductIDsAndTheirSpesification.get(supplier1.getSupplierId())
            .put(product2.getProductId(), product2);
      supplierIDsToTheirProductIDsAndTheirSpesification.get(supplier1.getSupplierId())
            .put(product3.getProductId(), product3);
      supplierIDsToTheirProductIDsAndTheirSpesification.put(supplier2.getSupplierId(), new HashMap<>());
      supplierIDsToTheirProductIDsAndTheirSpesification.get(supplier2.getSupplierId())
            .put(product3.getProductId(), product3);
      supplierIDsToTheirProductIDsAndTheirSpesification.get(supplier2.getSupplierId())
            .put(product4.getProductId(), product4);
      supplierIDsToTheirProductIDsAndTheirSpesification.put(supplier3.getSupplierId(), new HashMap<>());
      supplierIDsToTheirProductIDsAndTheirSpesification.get(supplier3.getSupplierId())
            .put(product5.getProductId(), product5);
      supplierIDsToTheirProductIDsAndTheirSpesification.get(supplier3.getSupplierId())
            .put(product6.getProductId(), product6);
      // Add products to productIDsToTheirSupplierIDs
      productIDsToTheirSupplierIDs.computeIfAbsent(product1.getProductId(), k -> new ArrayList<>())
            .add(supplier1.getSupplierId());
      productIDsToTheirSupplierIDs.computeIfAbsent(product2.getProductId(), k -> new ArrayList<>())
            .add(supplier1.getSupplierId());

      productIDsToTheirSupplierIDs.computeIfAbsent(product3.getProductId(), k -> new ArrayList<>())
            .add(supplier1.getSupplierId());
      productIDsToTheirSupplierIDs.computeIfAbsent(product3.getProductId(), k -> new ArrayList<>())
            .add(supplier2.getSupplierId());
      productIDsToTheirSupplierIDs.computeIfAbsent(product4.getProductId(), k -> new ArrayList<>())
            .add(supplier2.getSupplierId());
      productIDsToTheirSupplierIDs.computeIfAbsent(product5.getProductId(), k -> new ArrayList<>())
            .add(supplier3.getSupplierId());
      productIDsToTheirSupplierIDs.computeIfAbsent(product6.getProductId(), k -> new ArrayList<>())
            .add(supplier3.getSupplierId());
      // Add products to productCagalog
      productCagalog.add(new CatalogProductDTO(product1));
      productCagalog.add(new CatalogProductDTO(product2));
      productCagalog.add(new CatalogProductDTO(product3));
      productCagalog.add(new CatalogProductDTO(product4));
      productCagalog.add(new CatalogProductDTO(product5));
      productCagalog.add(new CatalogProductDTO(product6));

      // debug print
      // System.out.println("Initialized suppliers: " + suppliers);
      // System.out.println("Initialized supplier products: " +
      // supplierIDsToTheirProductIDsAndTheirSpesification);
      // System.out.println("Initialized supplier product map: " +
      // productIDsToTheirSupplierIDs);
      // System.out.println("Initialized product catalog: " + productCagalog);
      // System.out.println("Next supplier ID: " + nextSupplierID);
      // System.out.println("Next product ID: " + nextProductID);
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
      Map<Integer, SupplierProduct> products = supplierIDsToTheirProductIDsAndTheirSpesification.computeIfAbsent(
            supplier.getSupplierId(),
            k -> new HashMap<>());
      for (SupplierProductDTO product : supplierDTO.getProducts()) {
         SupplierProduct actualProduct = new SupplierProduct(product);
         // -VVV- this is used to find all the products for a supplier
         products.put(actualProduct.getProductId(), actualProduct);
         // -VVV- this is used to find all the suppliers for a product
         productIDsToTheirSupplierIDs.computeIfAbsent(actualProduct.getProductId(), k -> new ArrayList<>())
               .add(supplier.getSupplierId());
         // -VVV- this is used to find all the products available in the system
         productCagalog.add(new CatalogProductDTO(actualProduct));
      }
      // debug print
      System.out.println("Supplier created: " + supplier);
      System.out.println("Supplier products: " + products);
      System.out.println("Supplier product map: " + productIDsToTheirSupplierIDs);
      System.out.println("Product catalog: " + productCagalog);
      return supplier;
   }

   public boolean removeSupplier(int supplierID) {
      Supplier supplier = suppliers.remove(supplierID);
      if (supplier == null) {
         return false;
      }
      supplierIDsToTheirProductIDsAndTheirSpesification.remove(supplierID);
      for (Integer productID : supplier.getProducts()) {
         List<Integer> supplierIDs = productIDsToTheirSupplierIDs.get(productID);
         if (supplierIDs != null) {
            supplierIDs.removeIf(id -> id == supplierID);
            if (supplierIDs.isEmpty()) {
               productIDsToTheirSupplierIDs.remove(productID);
               productCagalog.removeIf(p -> p.productId() == productID);
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
      return supplierDTOs.stream()
            .sorted(Comparator.comparing(SupplierDTO::getId))
            .toList();
   }

   public void addProductToSupplierAndMemory(int supplierID, SupplierProductDTO product) {
      if (product == null) {
         throw new IllegalArgumentException("Product cannot be null");
      }
      Supplier supplier = suppliers.get(supplierID);
      if (supplier == null) {
         throw new IllegalArgumentException("Supplier not found");
      }
      if (supplierIDsToTheirProductIDsAndTheirSpesification.containsKey(supplierID)
            && supplierIDsToTheirProductIDsAndTheirSpesification.get(supplierID).containsKey(product.getProductId())) {
         throw new IllegalArgumentException("Product already exists for this supplier");
      }
      // if the product already exists in the catalog by name, set the product ID to
      // the existing one
      int productId = productCagalog.stream()
            .filter(p -> p.name().equalsIgnoreCase(product.getName()) && p.manufacturerName()
                  .equalsIgnoreCase(product.getManufacturerName()))
            .findFirst()
            .map(CatalogProductDTO::productId)
            .orElse(-1);
      if (productId != -1) {
         product.setProductId(productId);
      } else {
         product.setProductId(nextProductID++);
      }
      SupplierProduct supplierProduct = new SupplierProduct(product);
      supplier.addProduct(supplierProduct.getProductId());
      supplierIDsToTheirProductIDsAndTheirSpesification.computeIfAbsent(supplierID, k -> new HashMap<>())
            .put(supplierProduct.getProductId(), supplierProduct);
      productIDsToTheirSupplierIDs.computeIfAbsent(supplierProduct.getProductId(), k -> new ArrayList<>())
            .add(supplierID);
      productCagalog.add(new CatalogProductDTO(supplierProduct));
      // debug print
      System.out.println("Product added to supplier: " + supplier);
      System.out.println(
            "Supplier products and their prices: " + supplierIDsToTheirProductIDsAndTheirSpesification.get(supplierID));
      System.out.println("Product IDs to their supplier IDs: " + productIDsToTheirSupplierIDs);
      System.out.println("Product catalog: " + productCagalog);
   }

   public void removeProductFromSupplierAndMemory(int supplierID, int product) {
      Supplier supplier = suppliers.get(supplierID);
      if (supplier == null) {
         throw new IllegalArgumentException("Supplier not found");
      }
      if (!supplierIDsToTheirProductIDsAndTheirSpesification.containsKey(supplierID)
            || !supplierIDsToTheirProductIDsAndTheirSpesification.get(supplierID).containsKey(product)) {
         throw new IllegalArgumentException("Product not found for this supplier");
      }
      supplier.removeProduct(product);
      supplierIDsToTheirProductIDsAndTheirSpesification.get(supplierID).remove(product);
      List<Integer> suppliersForProduct = productIDsToTheirSupplierIDs.get(product);
      if (suppliersForProduct != null) {
         suppliersForProduct.removeIf(id -> id == supplierID);
         if (suppliersForProduct.isEmpty()) {
            productIDsToTheirSupplierIDs.remove(product);
            productCagalog.removeIf(p -> p.productId() == product);
         }
      }
   }

   public void updateProductInSupplierAndMemory(int supplierID, SupplierProductDTO productDTO) {
      if (productDTO == null) {
         throw new IllegalArgumentException("Product cannot be null");
      }
      Supplier supplier = suppliers.get(supplierID);
      if (supplier == null) {
         throw new IllegalArgumentException("Supplier not found");
      }
      if (!supplierIDsToTheirProductIDsAndTheirSpesification.containsKey(supplierID)
            || !supplierIDsToTheirProductIDsAndTheirSpesification.get(supplierID)
                  .containsKey(productDTO.getProductId())) {
         throw new IllegalArgumentException("Product not found for this supplier");
      }
      // Update the product in the catalog
      SupplierProduct existingProduct = supplierIDsToTheirProductIDsAndTheirSpesification
            .get(supplierID)
            .get(productDTO.getProductId());
      if (existingProduct == null) {
         throw new IllegalArgumentException("Product not found in supplier's product list");
      }
      supplierIDsToTheirProductIDsAndTheirSpesification.get(supplierID)
            .put(existingProduct.getProductId(), new SupplierProduct(productDTO));
      // Update the product in the catalog
      List<BeanPatch<SupplierProductDTO, SupplierProduct, ?>> rules = List.of(
            BeanPatch.of(SupplierProductDTO::getName, SupplierProduct::getName, SupplierProduct::setName),
            BeanPatch.of(SupplierProductDTO::getPrice, SupplierProduct::getPrice, SupplierProduct::setPrice),
            BeanPatch.of(SupplierProductDTO::getWeight, SupplierProduct::getWeight, SupplierProduct::setWeight),
            BeanPatch.of(SupplierProductDTO::getExpiresInDays, SupplierProduct::getExpiresInDays,
                  SupplierProduct::setExpiresInDays),
            BeanPatch.of(SupplierProductDTO::getManufacturerName, SupplierProduct::getManufacturerName,
                  SupplierProduct::setManufacturerName));

      // ─── single loop, no reflection ───
      rules.forEach(rule -> rule.apply(productDTO, existingProduct));
   }

   public List<SupplierProductDTO> getSupplierProducts(int supplierID) {
      Map<Integer, SupplierProduct> products = supplierIDsToTheirProductIDsAndTheirSpesification.get(supplierID);
      if (products == null) {
         return Collections.emptyList();
      }
      return products.values().stream()
            .map(SupplierProductDTO::new)
            .sorted(Comparator.comparing(SupplierProductDTO::getProductId))
            .toList();
   }

   public boolean checkSupplierExists(int supplierID) {
      return suppliers.containsKey(supplierID);
   }

   public List<CatalogProductDTO> getProductCatalog() {
      return productCagalog.stream()
            .sorted(Comparator.comparing(CatalogProductDTO::productId))
            .toList();
   }

   public AgreementDTO createAgreement(AgreementDTO agreementDTO) {
      if (agreementDTO == null) {
         throw new InvalidParameterException("AgreementDTO cannot be null");
      }
      Supplier supplier = suppliers.get(agreementDTO.getSupplierId());
      if (supplier == null) {
         throw new IllegalArgumentException("Supplier not found for ID: " + agreementDTO.getSupplierId());
      }
      agreementDTO.setSupplierName(supplier.getName());

      for (int i = 0; i < agreementDTO.getBillOfQuantitiesItems().size(); i++) {
         int productId = agreementDTO.getBillOfQuantitiesItems().get(i).getProductId();
         BillofQuantitiesItemDTO item = agreementDTO.getBillOfQuantitiesItems().get(i);
         if (!supplierIDsToTheirProductIDsAndTheirSpesification.containsKey(agreementDTO.getSupplierId())
               || !supplierIDsToTheirProductIDsAndTheirSpesification.get(agreementDTO.getSupplierId())
                     .containsKey(productId)) {
            throw new IllegalArgumentException("Product ID " + productId + " is not valid for supplier ID "
                  + agreementDTO.getSupplierId());
         }
         // update the item with the product name and ID
         SupplierProduct product = supplierIDsToTheirProductIDsAndTheirSpesification
               .get(agreementDTO.getSupplierId())
               .get(productId);
         if (product == null) {
            throw new IllegalArgumentException("Product ID " + productId + " not found in supplier's product list");
         }
         item.setProductName(product.getName());
         item.setProductId(product.getProductId());
         item.setLineInBillID(i + 1); // Set the line in bill ID to the index + 1
      }
      AgreementDTO createdAgreement = agreementFacade.createAgreement(agreementDTO);
      if (createdAgreement == null) {
         throw new IllegalArgumentException("Failed to create agreement");
      }
      // Add the agreement to the supplier
      suppliers.get(agreementDTO.getSupplierId())
            .addAgreement(createdAgreement.getAgreementId());
      return createdAgreement;
   }

   public boolean removeAgreement(int agreementID, int supplierID) {
      if (!agreementFacade.removeAgreement(agreementID, supplierID)) {
         throw new IllegalArgumentException("Failed to remove agreement");
      }
      Supplier supplier = suppliers.get(supplierID);
      if (supplier == null) {
         throw new IllegalArgumentException("Supplier not found");
      }
      supplier.removeAgreement(agreementID);
      return true;
   }

   public List<AgreementDTO> getAgreementsBySupplierId(int supplierId) {
      List<AgreementDTO> agreements = agreementFacade.getAgreementsBySupplierId(supplierId);
      if (agreements == null || agreements.isEmpty()) {
         throw new IllegalArgumentException("No agreements found for supplier ID: " + supplierId);
      }
      return agreements.stream()
            .sorted(Comparator.comparing(AgreementDTO::getAgreementId))
            .toList();
   }

   public AgreementDTO getAgreement(int agreementID) {
      AgreementDTO agreement = agreementFacade.getAgreementById(agreementID);
      if (agreement == null) {
         throw new IllegalArgumentException("Agreement not found for ID: " + agreementID);
      }
      agreement.setAgreementId(agreementID);

      return agreement;
   }

   public void updateAgreement(int agreementId, AgreementDTO updatedAgreement) {
      if (updatedAgreement == null) {
         throw new InvalidParameterException("UpdatedAgreement cannot be null");
      }
      Agreement actual = agreementFacade.getActualAgreement(agreementId);
      // update the dto with the actual agreement data
      updatedAgreement.setSupplierId(actual.getSupplierId());
      updatedAgreement.setSupplierName(actual.getSupplierName());
      updatedAgreement.setAgreementId(agreementId);
      // we need to compare the actual agreement's bill of quantities items with the
      // one in the updated
      // agreement and update the product names and IDs and line IDs
      List<BillofQuantitiesItemDTO> updatedItems = new ArrayList<>();
      for (int i = 0; i < updatedAgreement.getBillOfQuantitiesItems().size(); i++) {
         BillofQuantitiesItemDTO item = updatedAgreement.getBillOfQuantitiesItems().get(i);
         int productId = item.getProductId();
         if (!supplierIDsToTheirProductIDsAndTheirSpesification.containsKey(updatedAgreement.getSupplierId())
               || !supplierIDsToTheirProductIDsAndTheirSpesification.get(updatedAgreement.getSupplierId())
                     .containsKey(productId)) {
            throw new IllegalArgumentException("Product ID " + productId + " is not valid for supplier ID "
                  + updatedAgreement.getSupplierId());
         }
         SupplierProduct product = supplierIDsToTheirProductIDsAndTheirSpesification
               .get(updatedAgreement.getSupplierId())
               .get(productId);
         if (product == null) {
            throw new IllegalArgumentException("Product ID " + productId + " not found in supplier's product list");
         }
         item.setProductName(product.getName());
         item.setLineInBillID(i + 1); // Set the line in bill ID to the index + 1
         updatedItems.add(item);
      }
      updatedAgreement.setBillOfQuantitiesItems(updatedItems);
      if (!agreementFacade.updateAgreement(updatedAgreement)) {
         throw new IllegalArgumentException("Failed to update agreement");
      }
   }

}
