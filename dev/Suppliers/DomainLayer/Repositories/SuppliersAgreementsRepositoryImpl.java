package Suppliers.DomainLayer.Repositories;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Suppliers.DTOs.AddressDTO;
import Suppliers.DTOs.AgreementDTO;
import Suppliers.DTOs.BillofQuantitiesItemDTO;
import Suppliers.DTOs.CatalogProductDTO;
import Suppliers.DTOs.ContactInfoDTO;
import Suppliers.DTOs.PaymentDetailsDTO;
import Suppliers.DTOs.SupplierDTO;
import Suppliers.DTOs.SupplierProductDTO;
import Suppliers.DTOs.Enums.InitializeState;
import Suppliers.DTOs.Enums.PaymentMethod;
import Suppliers.DTOs.Enums.PaymentTerm;
import Suppliers.DataLayer.DAOs.*;

import Suppliers.DataLayer.Interfaces.*;
import Suppliers.DataLayer.util.Database;
import Suppliers.DomainLayer.Classes.Agreement;
import Suppliers.DomainLayer.Classes.BillofQuantitiesItem;
import Suppliers.DomainLayer.Classes.Supplier;
import Suppliers.DomainLayer.Classes.SupplierProduct;
import Suppliers.DomainLayer.Repositories.RepositoryIntefaces.SuppliersAgreementsRepositoryInterface;

public class SuppliersAgreementsRepositoryImpl implements SuppliersAgreementsRepositoryInterface {
   private static final Logger LOGGER = LoggerFactory.getLogger(SuppliersAgreementsRepositoryImpl.class);

   private final SupplierDAOInterface supplierDAO;
   private final ContactInfoDAOInterface contactInfoDAO;
   private final AgreementDAOInterface agreementDAO;
   private final SupplierProductDAOInterface supplierProductDAO;
   private static SuppliersAgreementsRepositoryImpl instance;

   public static SuppliersAgreementsRepositoryImpl getInstance() {
      if (instance == null) {
         instance = new SuppliersAgreementsRepositoryImpl();
         LOGGER.info("Created new instance of SupplierRepositoryImpl");
      } else {
         LOGGER.info("Using existing instance of SupplierRepositoryImpl");
      }
      return instance;
   }

   private SuppliersAgreementsRepositoryImpl() {
      this.supplierDAO = new JdbcSupplierDAO();
      this.contactInfoDAO = new JdbcContactInfoDAO();
      this.agreementDAO = new JdbcAgreementDAO();
      this.supplierProductDAO = new JdbcSupplierProductDAO();
   }

   public void initialize(InitializeState state) throws SQLException {
      LOGGER.info("Initiating SuppliersAgreementsRepositoryImpl with state: {}", state);
      switch (state) {
         case CURRENT_STATE -> {
            LOGGER.info("Loading current state from database");
            loadCurrentStateFromDatabase();
         }
         case DEFAULT_STATE -> {
            LOGGER.info("Loading default state from database");
            loadDefaultStateFromDatabase();
         }
         case NO_DATA_STATE -> {
            LOGGER.info("Loading empty state");
            loadEmptyState();
         }
         default -> {
            LOGGER.error("Unknown initialization state: {}", state);
            throw new IllegalArgumentException("Unknown initialization state: " + state);
         }
      }
   }

   private void loadEmptyState() {
      LOGGER.info("Clearing data base for empty state...");
      Database.deleteAllData();
      // suppliers.clear();
      // supplierIdToAgreements.clear();
      // supplierIDsToTheirProductIDsAndTheirSpesification.clear();
      // productIDsToTheirSupplierIDs.clear();
      // productCagalog.clear();
      LOGGER.info("Empty state loaded into memory cache");
   }

   private void loadDefaultStateFromDatabase() {
      Database.deleteAllData(); // clear the database first
      Supplier supplier1 = new Supplier("Supplier 1", "512345678",
            new AddressDTO("Street 1", "City 1", "Building 1"),
            new PaymentDetailsDTO("123456", PaymentMethod.CREDIT_CARD, PaymentTerm.N30),
            true, EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY), 0, new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());
      List<ContactInfoDTO> supplier2Contacts = new ArrayList<>();
      supplier2Contacts.add(new ContactInfoDTO("Danny", "Danny@gmail.com", "054-2357-894"));

      Supplier supplier2 = new Supplier("Supplier 2", "587654321", new AddressDTO("Street 2", "City 2", "Building 22"),
            new PaymentDetailsDTO("412353", PaymentMethod.CASH, PaymentTerm.COD), false,
            EnumSet.noneOf(DayOfWeek.class), 2, supplier2Contacts, new ArrayList<>(), new ArrayList<>());
      Supplier supplier3 = new Supplier("Supplier 3", "518273645",
            new AddressDTO("Street 3", "City 3", "Building 3"),
            new PaymentDetailsDTO("162534", PaymentMethod.CASH_ON_DELIVERY, PaymentTerm.N60),
            true, EnumSet.of(DayOfWeek.FRIDAY), 1, new ArrayList<>(),
            new ArrayList<>(), new ArrayList<>());

      for (Supplier supplier : List.of(supplier1, supplier2, supplier3)) {
         try {
            SupplierDTO newSupplier = saveSupplierInMemoryCache(new SupplierDTO(supplier));
            supplier.setSupplierId(newSupplier.getId());
         } catch (SQLException e) {
            LOGGER.error("Failed to save supplier in memory cache: {}", e.getMessage());
            throw new RuntimeException("Error while saving supplier in memory cache", e);
         }
      }
      SupplierProduct product1 = new SupplierProduct(supplier1.getSupplierId(), -1, "123456",
            "Milk 3%", new BigDecimal("10.00"), new BigDecimal("1.0"), 30, "Yotvata");
      SupplierProduct product2 = new SupplierProduct(supplier1.getSupplierId(), -1, "654321",
            "Cornflacks Cariot", new BigDecimal("20.00"), new BigDecimal("2.0"), 60, "Telma");
      SupplierProduct product3 = new SupplierProduct(supplier2.getSupplierId(), -1, "789012",
            "Cottage Cheese", new BigDecimal("15.00"), new BigDecimal("1.5"), 45, "Tnuva");
      SupplierProduct product4 = new SupplierProduct(supplier2.getSupplierId(), -1, "210987",
            "Pastrami Sandwich", new BigDecimal("25.00"), new BigDecimal("3.0"), 90, "DeliMeat");
      SupplierProduct product5 = new SupplierProduct(supplier3.getSupplierId(), -1, "345678",
            "Milk 3%", new BigDecimal("30.00"), new BigDecimal("2.5"), 15, "Tnuva");
      SupplierProduct product6 = new SupplierProduct(supplier3.getSupplierId(), -1, "876543",
            "Bamba", new BigDecimal("40.00"), new BigDecimal("4.0"), 120, "Ossem");
      for (SupplierProduct product : List.of(product1, product2, product3, product4, product5, product6)) {
         try {
            SupplierProductDTO newProduct = saveSupplierProductInMemoryCache(new SupplierProductDTO(product));
            product.setProductId(newProduct.getProductId());
         } catch (SQLException e) {
            LOGGER.error("Failed to save supplier product in memory cache: {}", e.getMessage());
            throw new RuntimeException("Error while saving supplier product in memory cache", e);
         }
      }

      BillofQuantitiesItem item1 = new BillofQuantitiesItem(-1, -1, "Milk 3%", product1.getProductId(), 100,
            new BigDecimal("5"));
      BillofQuantitiesItem item2 = new BillofQuantitiesItem(-1, -1, "Cornflacks Cariot", product2.getProductId(), 50,
            new BigDecimal("10"));
      BillofQuantitiesItem item3 = new BillofQuantitiesItem(-1, -1, "Cottage Cheese", product3.getProductId(), 75,
            new BigDecimal("7.5"));
      BillofQuantitiesItem item4 = new BillofQuantitiesItem(-1, -1, "Pastrami Sandwich", product4.getProductId(), 30,
            new BigDecimal("12.5"));
      Agreement agreement1 = new Agreement(-1, supplier1.getSupplierId(), supplier1.getName(),
            java.time.LocalDate.now().minusDays(10), java.time.LocalDate.now().plusDays(20),
            List.of(item1, item2, item3));
      Agreement agreement2 = new Agreement(-1, supplier2.getSupplierId(), supplier2.getName(),
            java.time.LocalDate.now().minusDays(5), java.time.LocalDate.now().plusDays(30),
            List.of(item3, item4));
      for (Agreement agreement : List.of(agreement1, agreement2)) {
         try {
            saveAgreementInMemoryCache(new AgreementDTO(agreement));

         } catch (SQLException e) {
            LOGGER.error("Failed to save agreement in memory cache: {}", e.getMessage());
            throw new RuntimeException("Error while saving agreement in memory cache", e);
         }
         // now load the info
      }
      LOGGER.info("Default state loaded into memory cache");
   }

   private void loadCurrentStateFromDatabase() {
      try {
         LOGGER.info("Loading current suppliers from database");
         // int supplierCounter = 0;
         List<SupplierDTO> supplierDTOs = supplierDAO.getAllSuppliers();
         // for (SupplierDTO supplierDTO : supplierDTOs) {
         // supplierCounter++;
         // LOGGER.debug("Loaded supplier: {}", supplierDTO);
         // }
         LOGGER.info("Loaded {} suppliers from database", supplierDTOs.size());
      } catch (SQLException e) {
         LOGGER.error("Failed to load current state from database: {}", e.getMessage());
         throw new RuntimeException("Error while loading current state from database", e);
      }
   }

   @Override
   public SupplierDTO createSupplier(SupplierDTO supplier) throws SQLException {
      if (supplier == null) {
         LOGGER.error("Attempted to create a null supplier");
         throw new IllegalArgumentException("Supplier cannot be null");
      }
      return supplierDAO.createSupplier(supplier);
   }

   private SupplierDTO saveSupplierInMemoryCache(SupplierDTO supplierToSave) throws SQLException {
      LOGGER.debug("Attempting to save the supplier in memory and in cache: {}", supplierToSave);
      try {
         if (supplierToSave.getId() < 0) {
            LOGGER.info("Creating new supplier.");
            SupplierDTO newSupplier = supplierDAO.createSupplier(supplierToSave);
            if (newSupplier.getContactsInfoDTOList() != null) {
               for (var contact : newSupplier.getContactsInfoDTOList()) {
                  contact.setSupplierId(newSupplier.getId());
                  saveContactInfo(contact);
               }
            }
            if (supplierToSave.getProducts() != null) {
               for (SupplierProductDTO product : supplierToSave.getProducts()) {
                  product.setSupplierId(newSupplier.getId());
                  saveSupplierProductInMemoryCache(product);
               }
            }
            if (supplierToSave.getAgreements() != null) {
               for (Integer agreement : supplierToSave.getAgreements()) {
                  AgreementDTO agreementDTO = agreementDAO.getAgreementById(agreement)
                        .orElseThrow(() -> new SQLException("Agreement not found with ID: " + agreement));
                  saveAgreementInMemoryCache(agreementDTO);
               }
            }
            LOGGER.info("Supplier created successfully: {}", newSupplier);
            return newSupplier;
         } else {
            LOGGER.info("Updating existing supplier with ID: {}", supplierToSave.getId());
            supplierDAO.updateSupplier(supplierToSave);
            // Update products and agreements if they exist
            if (supplierToSave.getProducts() != null) {
               for (SupplierProductDTO product : supplierToSave.getProducts()) {
                  product.setSupplierId(supplierToSave.getId());
                  saveSupplierProductInMemoryCache(product);
               }
            }
            if (supplierToSave.getAgreements() != null) {
               LOGGER.info("Supplier updated successfully: {}", supplierToSave);
               return supplierToSave;
            }
         }
         LOGGER.info("Supplier updated successfully: {}", supplierToSave);
         return supplierToSave;
      } catch (SQLException e) {
         LOGGER.error("Failed to create or update supplier in the database: {}", e.getMessage());
         throw new SQLException("Error while creating or updating supplier in the database", e);
      }
   }

   private void saveContactInfo(ContactInfoDTO contact) {
      LOGGER.debug("Attempting to save contact info in memory and in cache: {}", contact);
      try {
         ContactInfoDTO savedContact = contactInfoDAO.createContactInfo(contact);
         LOGGER.info("Contact info saved successfully: {}", savedContact);
      } catch (SQLException e) {
         LOGGER.error("Failed to save contact info in the database: {}", e.getMessage());
         throw new RuntimeException("Error while saving contact info in the database", e);
      }
   }

   @Override
   public Optional<SupplierDTO> getSupplierById(int id) throws SQLException {
      if (id < 0) {
         LOGGER.error("Attempted to get supplier with negative ID: {}", id);
         throw new IllegalArgumentException("Supplier ID cannot be negative");
      }
      LOGGER.info("Retrieving supplier with ID: {}", id);
      // sql get only
      Optional<SupplierDTO> supplier = supplierDAO.getSupplier(id);
      if (supplier.isPresent()) {
         LOGGER.info("Found supplier: {}", supplier.get());
         return supplier;
      } else {
         LOGGER.warn("No supplier found with ID: {}", id);
         return Optional.empty();
      }
   }

   @Override
   public void updateSupplier(SupplierDTO supplier) throws SQLException {
      if (supplier == null) {
         LOGGER.error("Attempted to update a null supplier");
         throw new IllegalArgumentException("Supplier cannot be null");
      }
      if (supplier.getId() < 0) {
         LOGGER.error("Attempted to update supplier with negative ID: {}", supplier.getId());
         throw new IllegalArgumentException("Supplier ID cannot be negative");
      }
      LOGGER.info("Updating supplier: {}", supplier);
      supplierDAO.updateSupplier(supplier);
      LOGGER.info("Supplier updated successfully: {}", supplier);
   }

   @Override
   public void deleteSupplier(int id) throws SQLException {
      if (id < 0) {
         LOGGER.error("Attempted to delete supplier with negative ID: {}", id);
         throw new IllegalArgumentException("Supplier ID cannot be negative");
      }
      LOGGER.info("Deleting supplier with ID: {}", id);
      if (!supplierDAO.supplierExists(id)) {
         LOGGER.warn("Supplier with ID {} does not exist", id);
         throw new SQLException("Supplier with ID " + id + " does not exist");
      }
      supplierDAO.deleteSupplier(id);
      LOGGER.info("Supplier with ID {} deleted successfully", id);
   }

   @Override
   public boolean supplierExists(int id) throws SQLException {
      if (id < 0) {
         LOGGER.error("Attempted to check existence of supplier with negative ID: {}", id);
         throw new IllegalArgumentException("Supplier ID cannot be negative");
      }
      LOGGER.info("Checking if supplier with ID {} exists", id);
      boolean exists = supplierDAO.supplierExists(id);
      LOGGER.info("Supplier with ID {} exists: {}", id, exists);
      return exists;
   }

   @Override
   public List<SupplierDTO> getAllSuppliers() throws SQLException {
      LOGGER.info("Retrieving all suppliers");
      // sql get only
      List<SupplierDTO> suppliers = supplierDAO.getAllSuppliers();
      if (suppliers.isEmpty()) {
         LOGGER.warn("No suppliers found in memory cache");
      } else {
         LOGGER.info("Found {} suppliers in memory cache", suppliers.size());
      }
      return suppliers;
   }

   @Override
   public AgreementDTO addAgreementToSupplier(AgreementDTO agreement, int supplierId) throws SQLException {
      if (agreement == null) {
         LOGGER.error("Attempted to add a null agreement to supplier with ID: {}", supplierId);
         throw new IllegalArgumentException("Agreement cannot be null");
      }
      if (supplierId < 0) {
         LOGGER.error("Attempted to add agreement to supplier with negative ID: {}", supplierId);
         throw new IllegalArgumentException("Supplier ID cannot be negative");
      }
      LOGGER.info("Adding agreement {} to supplier with ID {}", agreement, supplierId);
      AgreementDTO createdAgreementDTO = saveAgreementInMemoryCache(agreement);
      // supplierIdToAgreements
      // .computeIfAbsent(supplierId, k -> new java.util.ArrayList<>())
      // .add(new Agreement(createdAgreementDTO));
      // LOGGER.info("Agreement {} added to supplier with ID {}", createdAgreementDTO,
      // supplierId);
      return createdAgreementDTO;
   }

   public AgreementDTO saveAgreementInMemoryCache(AgreementDTO agreementToSave) throws SQLException {
      LOGGER.debug("Attempting to save the agreement in memory and in cache: {}", agreementToSave);
      try {
         // int supplierId = agreementToSave.getSupplierId();
         // if (!supplierIdToAgreements.containsKey(supplierId)) {
         // supplierIdToAgreements.put(supplierId, new java.util.ArrayList<>());
         // }
         AgreementDTO savedAgreement = agreementDAO.createAgreement(agreementToSave);
         // update the supplier to have the agreement ID
         // SupplierDTO supplier = supplierDAO.getSupplier(supplierId)
         // .orElseThrow(() -> new SQLException("Supplier not found with ID: " +
         // supplierId));
         // supplier.getAgreements().add(savedAgreement.getAgreementId());
         // saveSupplierInMemoryCache(supplier);
         // supplierIdToAgreements.get(supplierId).add(new Agreement(savedAgreement));
         LOGGER.info("Agreement saved successfully: {}", savedAgreement);
         return savedAgreement;
      } catch (SQLException e) {
         LOGGER.error("Failed to create or update agreement in the database: {}", e.getMessage());
         throw new SQLException("Error while creating or updating agreement in the database", e);
      }
   }

   @Override
   public Optional<AgreementDTO> getAgreementById(int agreementId) throws SQLException {
      if (agreementId < 0) {
         LOGGER.error("Attempted to get agreement with negative ID: {}", agreementId);
         throw new IllegalArgumentException("Agreement ID cannot be negative");
      }
      LOGGER.info("Retrieving agreement with ID: {}", agreementId);
      // only sql get
      Optional<AgreementDTO> agreement = agreementDAO.getAgreementById(agreementId);
      if (agreement.isPresent()) {
         LOGGER.info("Found agreement: {}", agreement.get());
         return agreement;
      } else {
         LOGGER.warn("No agreement found with ID: {}", agreementId);
         return Optional.empty();
      }
   }

   @Override
   public void updateAgreement(AgreementDTO agreement) throws SQLException {
      if (agreement == null) {
         LOGGER.error("Attempted to update a null agreement");
         throw new IllegalArgumentException("Agreement cannot be null");
      }
      LOGGER.info("Updating agreement: {}", agreement);
      agreementDAO.updateAgreement(agreement);
      LOGGER.info("Agreement updated successfully: {}", agreement);
   }

   @Override
   public void removeAgreementFromSupplier(int agreementId, int supplierId) throws SQLException {
      if (agreementId < 0) {
         LOGGER.error("Attempted to remove agreement with negative ID: {}", agreementId);
         throw new IllegalArgumentException("Agreement ID cannot be negative");
      }
      if (supplierId < 0) {
         LOGGER.error("Attempted to remove agreement from supplier with negative ID: {}", supplierId);
         throw new IllegalArgumentException("Supplier ID cannot be negative");
      }
      LOGGER.info("Removing agreement with ID {} from supplier with ID {}", agreementId, supplierId);
      agreementDAO.deleteAgreement(agreementId); // then delete the agreement itself
      LOGGER.info("Agreement with ID {} removed successfully from supplier with ID {}", agreementId, supplierId);
   }

   @Override
   public List<AgreementDTO> getAllAgreementsForSupplier(int supplierId) throws SQLException {
      if (supplierId < 0) {
         LOGGER.error("Attempted to get agreements for supplier with negative ID: {}", supplierId);
         throw new IllegalArgumentException("Supplier ID cannot be negative");
      }
      LOGGER.info("Retrieving all agreements for supplier with ID: {}", supplierId);
      List<AgreementDTO> agreements = agreementDAO.getAllAgreementsForSupplier(supplierId);
      LOGGER.info("Found {} agreements for supplier with ID: {}", agreements.size(), supplierId);
      return agreements;
   }

   @Override
   public List<AgreementDTO> getAllAgreements() throws SQLException {
      LOGGER.info("Retrieving all agreements");
      List<AgreementDTO> agreements = agreementDAO.getAllAgreements();
      if (agreements.isEmpty()) {
         LOGGER.warn("No agreements found in memory cache");
      } else {
         LOGGER.info("Found {} agreements in memory cache", agreements.size());
      }
      return agreements;
   }

   @Override
   public SupplierProductDTO createSupplierProduct(SupplierProductDTO supplierProduct) throws SQLException {
      if (supplierProduct == null) {
         LOGGER.error("Attempted to create a null supplier product");
         throw new IllegalArgumentException("Supplier product cannot be null");
      }
      if (supplierProduct.getSupplierId() < 0 || supplierProduct.getProductId() < 0) {
         LOGGER.error("Attempted to create supplier product with negative IDs: supplierId={}, productId={}",
               supplierProduct.getSupplierId(), supplierProduct.getProductId());
         throw new IllegalArgumentException("Supplier ID and Product ID cannot be negative");
      }
      LOGGER.info("Creating supplier product: {}", supplierProduct);
      SupplierProductDTO createdProduct = saveSupplierProductInMemoryCache(supplierProduct);
      LOGGER.info("Supplier product created successfully: {}", createdProduct);
      return createdProduct;
   }

   public SupplierProductDTO saveSupplierProductInMemoryCache(SupplierProductDTO supplierProductToSave)
         throws SQLException {
      LOGGER.debug("Attempting to save the supplier product in memory and in cache: {}", supplierProductToSave);
      try {
         if (supplierProductToSave.getProductId() < 0) {
            LOGGER.info("Creating new supplier product.");
            SupplierProductDTO newProduct = supplierProductDAO.createSupplierProduct(supplierProductToSave);
            LOGGER.info("Supplier product created successfully: {}", newProduct);
            return newProduct;
         } else {
            LOGGER.info("Updating existing supplier product with IDs: {}, {}", supplierProductToSave.getSupplierId(),
                  supplierProductToSave.getProductId());
            supplierProductDAO.updateSupplierProduct(supplierProductToSave);
            LOGGER.info("Supplier product updated successfully: {}", supplierProductToSave);
            return supplierProductToSave;
         }
      } catch (SQLException e) {
         LOGGER.error("Failed to create or update supplier product in the database: {}", e.getMessage());
         throw new SQLException("Error while creating or updating supplier product in the database", e);
      }
   }

   @Override
   public Optional<SupplierProductDTO> getSupplierProductById(int supplierId, int productId) throws SQLException {
      if (supplierId < 0 || productId < 0) {
         LOGGER.error("Attempted to get supplier product with negative IDs: supplierId={}, productId={}", supplierId,
               productId);
         throw new IllegalArgumentException("Supplier ID and Product ID cannot be negative");
      }
      LOGGER.info("Retrieving supplier product with supplierId: {} and productId: {}", supplierId, productId);
      // only sql get
      Optional<SupplierProductDTO> product = supplierProductDAO.getSupplierProductById(supplierId, productId);
      if (product.isPresent()) {
         LOGGER.info("Found supplier product: {}", product.get());
         return product;
      } else {
         LOGGER.warn("No supplier product found with supplierId: {} and productId: {}", supplierId, productId);
         return Optional.empty();
      }
   }

   @Override
   public void updateSupplierProduct(SupplierProductDTO supplierProduct) throws SQLException {
      if (supplierProduct == null) {
         LOGGER.error("Attempted to update a null supplier product");
         throw new IllegalArgumentException("Supplier product cannot be null");
      }
      if (supplierProduct.getSupplierId() < 0 || supplierProduct.getProductId() < 0) {
         LOGGER.error("Attempted to update supplier product with negative IDs: supplierId={}, productId={}",
               supplierProduct.getSupplierId(), supplierProduct.getProductId());
         throw new IllegalArgumentException("Supplier ID and Product ID cannot be negative");
      }
      LOGGER.info("Updating supplier product: {}", supplierProduct);
      SupplierProductDTO updatedProduct = saveSupplierProductInMemoryCache(supplierProduct);
      LOGGER.info("Supplier product updated successfully: {}", updatedProduct);
   }

   @Override
   public void deleteSupplierProduct(int supplierId, int productId) throws SQLException {
      if (supplierId < 0 || productId < 0) {
         LOGGER.error("Attempted to delete supplier product with negative IDs: supplierId={}, productId={}", supplierId,
               productId);
         throw new IllegalArgumentException("Supplier ID and Product ID cannot be negative");
      }
      LOGGER.info("Deleting supplier product with supplierId: {} and productId: {}", supplierId, productId);
      deleteSupplierProductFromMemoryCache(supplierId, productId);
      LOGGER.info("Supplier product with supplierId: {} and productId: {} deleted successfully", supplierId, productId);
   }

   public void deleteSupplierProductFromMemoryCache(int supplierId, int productId) {
      LOGGER.info("Deleting supplier product with supplierId: {} and productId: {} from memory cache", supplierId,
            productId);
      // only sql delete\
      try {
         supplierProductDAO.deleteSupplierProduct(supplierId, productId);
      } catch (SQLException e) {
         LOGGER.error("Failed to delete supplier product from database: {}", e.getMessage());
         throw new RuntimeException("Error while deleting supplier product from database", e);
      }
      LOGGER.info("Supplier product with supplierId: {} and productId: {} deleted from memory cache", supplierId,
            productId);
   }

   @Override
   public List<SupplierProductDTO> getAllSupplierProductsById(int supplierId) throws SQLException {
      if (supplierId < 0) {
         LOGGER.error("Attempted to get all supplier products for supplier with negative ID: {}", supplierId);
         throw new IllegalArgumentException("Supplier ID cannot be negative");
      }
      LOGGER.info("Retrieving all supplier products for supplier with ID: {}", supplierId);
      // only sql get
      List<SupplierProductDTO> supplierProducts = supplierProductDAO.getAllSupplierProductsForSupplier(supplierId);
      if (supplierProducts.isEmpty()) {
         LOGGER.warn("No supplier products found for supplier with ID: {}", supplierId);
      } else {
         LOGGER.info("Found {} supplier products for supplier with ID: {}", supplierProducts.size(), supplierId);
      }
      return supplierProducts;
   }

   @Override
   public boolean supplierProductExists(int supplierId, int productId) throws SQLException {
      if (supplierId < 0 || productId < 0) {
         LOGGER.error("Attempted to check existence of supplier product with negative IDs: supplierId={}, productId={}",
               supplierId, productId);
         throw new IllegalArgumentException("Supplier ID and Product ID cannot be negative");
      }
      LOGGER.info("Checking if supplier product exists for supplierId: {} and productId: {}", supplierId, productId);
      // only sql check
      boolean exists = supplierProductDAO.supplierProductExists(supplierId, productId);
      LOGGER.info("Supplier product with supplierId: {} and productId: {} exists: {}", supplierId, productId, exists);
      return exists;
   }

   @Override
   public List<SupplierProductDTO> getAllSupplierProducts() throws SQLException {
      LOGGER.info("Retrieving all supplier products");
      // only sql get
      List<SupplierProductDTO> allSupplierProducts = supplierProductDAO.getAllSupplierProducts();
      if (allSupplierProducts.isEmpty()) {
         LOGGER.warn("No supplier products found in memory cache");
      } else {
         LOGGER.info("Found {} supplier products in memory cache", allSupplierProducts.size());
      }
      return allSupplierProducts;
   }

   @Override
   public List<Integer> getAllSuppliersForProductId(int productId) throws SQLException {
      if (productId < 0) {
         LOGGER.error("Attempted to get all suppliers for product with negative ID: {}", productId);
         throw new IllegalArgumentException("Product ID cannot be negative");
      }
      LOGGER.info("Retrieving all suppliers for product with ID: {}", productId);
      // only sql get
      List<Integer> supplierIds = supplierProductDAO.getAllSupplierIdsForProductId(productId);
      if (supplierIds.isEmpty()) {
         LOGGER.warn("No suppliers found for product with ID: {}", productId);
      } else {
         LOGGER.info("Found {} suppliers for product with ID: {}", supplierIds.size(), productId);
      }
      return supplierIds;
   }

   @Override
   public List<Integer> getAllProductsForSupplierId(int supplierId) throws SQLException {
      if (supplierId < 0) {
         LOGGER.error("Attempted to get all products for supplier with negative ID: {}", supplierId);
         throw new IllegalArgumentException("Supplier ID cannot be negative");
      }
      LOGGER.info("Retrieving all products for supplier with ID: {}", supplierId);
      // only sql get
      List<Integer> productIds = supplierProductDAO.getAllProductIdsForSupplierId(supplierId);
      if (productIds.isEmpty()) {
         LOGGER.warn("No products found for supplier with ID: {}", supplierId);
      } else {
         LOGGER.info("Found {} products for supplier with ID: {}", productIds.size(), supplierId);
      }
      return productIds;
   }

   @Override
   public List<CatalogProductDTO> getCatalogProducts() throws SQLException {
      LOGGER.info("Retrieving catalog products");
      // only sql get
      List<CatalogProductDTO> catalogProducts = supplierProductDAO.getCatalogProducts();
      if (catalogProducts.isEmpty()) {
         LOGGER.warn("No catalog products found");
      } else {
         LOGGER.info("Found {} catalog products", catalogProducts.size());
      }
      return catalogProducts;
   }

   @Override
   public List<BillofQuantitiesItemDTO> getBillOfQuantitiesItemsForAgreement(int agreementId) throws SQLException {
      if (agreementId < 0) {
         LOGGER.error("Attempted to get Bill of Quantities items for agreement with negative ID: {}", agreementId);
         throw new IllegalArgumentException("Agreement ID cannot be negative");
      }
      LOGGER.info("Retrieving Bill of Quantities items for agreement with ID: {}", agreementId);
      List<BillofQuantitiesItemDTO> boqItems = agreementDAO.getBillOfQuantitiesItemsForAgreement(agreementId);
      if (boqItems.isEmpty()) {
         LOGGER.warn("No Bill of Quantities items found for agreement with ID: {}", agreementId);
      } else {
         LOGGER.info("Found {} Bill of Quantities items for agreement with ID: {}", boqItems.size(), agreementId);
      }
      return boqItems;
   }

   @Override
   public boolean agreementExists(int agreementId) throws SQLException {
      if (agreementId < 0) {
         LOGGER.error("Attempted to check existence of agreement with negative ID: {}", agreementId);
         throw new IllegalArgumentException("Agreement ID cannot be negative");
      }
      LOGGER.info("Checking if agreement with ID {} exists", agreementId);
      boolean exists = agreementDAO.agreementExists(agreementId);
      LOGGER.info("Agreement with ID {} exists: {}", agreementId, exists);
      return exists;
   }
}