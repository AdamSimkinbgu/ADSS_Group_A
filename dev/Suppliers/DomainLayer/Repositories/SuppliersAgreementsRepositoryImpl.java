package Suppliers.DomainLayer.Repositories;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
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

   public void initialize(InitializeState state) {
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
      LOGGER.info("Empty state loaded into DB");
   }

   private void loadDefaultStateFromDatabase() {
      Database.deleteAllData(); // clear the database first
      Supplier supplier1 = new Supplier("Supplier 1", "512345678",
            new AddressDTO("Street 1", "City 1", "Building 1"),
            new PaymentDetailsDTO("123456", PaymentMethod.CREDIT_CARD, PaymentTerm.N30),
            true, EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY), 0, new ArrayList<>());

      List<ContactInfoDTO> supplier2Contacts = new ArrayList<>();
      supplier2Contacts.add(new ContactInfoDTO("Danny", "danny@gmail.com", "054-235-7894"));
      Supplier supplier2 = new Supplier("Supplier 2", "587654321", new AddressDTO("Street 2", "City 2", "Building 22"),
            new PaymentDetailsDTO("412353", PaymentMethod.CASH, PaymentTerm.COD), false,
            EnumSet.noneOf(DayOfWeek.class), 2, supplier2Contacts);
      Supplier supplier3 = new Supplier("Supplier 3", "518273645",
            new AddressDTO("Street 3", "City 3", "Building 3"),
            new PaymentDetailsDTO("162534", PaymentMethod.CASH_ON_DELIVERY, PaymentTerm.N60),
            true, EnumSet.of(DayOfWeek.FRIDAY), 1, new ArrayList<>());

      for (Supplier supplier : List.of(supplier1, supplier2, supplier3)) {
         SupplierDTO newSupplier = createSupplier(new SupplierDTO(supplier));
         supplier.setSupplierId(newSupplier.getId());
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
         SupplierProductDTO newProduct = saveSupplierProductInMemoryDB(new SupplierProductDTO(product));
         product.setProductId(newProduct.getProductId());
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
            java.time.LocalDate.now().plusDays(10), java.time.LocalDate.now().plusDays(20),
            List.of(item1, item2, item3));
      Agreement agreement2 = new Agreement(-1, supplier2.getSupplierId(), supplier2.getName(),
            java.time.LocalDate.now().plusDays(5), java.time.LocalDate.now().plusDays(30),
            List.of(item3, item4));
      for (Agreement agreement : List.of(agreement1, agreement2)) {
         saveAgreementInMemoryDB(new AgreementDTO(agreement));
      }
      LOGGER.info("Default state loaded into DB");
   }

   private void loadCurrentStateFromDatabase() {

      LOGGER.info("Loading current suppliers from database");
      List<SupplierDTO> supplierDTOs = supplierDAO.getAllSuppliers();
      LOGGER.info("Loaded {} suppliers from database", supplierDTOs.size());

   }

   @Override
   public SupplierDTO createSupplier(SupplierDTO supplier) {
      if (supplier == null) {
         LOGGER.error("Attempted to create a null supplier");
         throw new IllegalArgumentException("Supplier cannot be null");
      }
      return supplierDAO.createSupplier(supplier);
   }

   @Override
   public Optional<SupplierDTO> getSupplierById(int id) {
      if (id < 0) {
         LOGGER.error("Attempted to get supplier with negative ID: {}", id);
         throw new IllegalArgumentException("Supplier ID cannot be negative");
      }
      LOGGER.info("Retrieving supplier with ID: {}", id);
      Optional<SupplierDTO> supplier = supplierDAO.getSupplier(id);
      if (supplier.isPresent()) {
         List<ContactInfoDTO> contacts = contactInfoDAO.getContactInfosBySupplierId(id);
         if (contacts.isEmpty()) {
            LOGGER.warn("No contact info found for supplier with ID: {}", id);
         } else {
            LOGGER.info("Found {} contact(s) for supplier with ID: {}", contacts.size(), id);
            supplier.get().setContacts(contacts);
         }
         LOGGER.info("Found supplier: {}", supplier.get());
         return supplier;
      } else {
         LOGGER.warn("No supplier found with ID: {}", id);
         return Optional.empty();
      }
   }

   @Override
   public boolean updateSupplier(SupplierDTO supplier) {
      if (supplier == null) {
         LOGGER.error("Attempted to update a null supplier");
         throw new IllegalArgumentException("Supplier cannot be null");
      }
      if (supplier.getId() < 0) {
         LOGGER.error("Attempted to update supplier with negative ID: {}", supplier.getId());
         throw new IllegalArgumentException("Supplier ID cannot be negative");
      }
      LOGGER.info("Updating supplier: {}", supplier);
      Optional<SupplierDTO> existingSup = supplierDAO.getSupplier(supplier.getId());
      if (!existingSup.equals(Optional.of(supplier))) {
         supplierDAO.updateSupplier(supplier);
         LOGGER.info("Supplier data has changed, proceeding with update");
      } else {
         LOGGER.info("No changes detected in supplier data, skipping update");
         return false;
      }
      List<ContactInfoDTO> supplierDTOcontacts = supplier.getContactsInfoDTOList();
      List<ContactInfoDTO> contactsInDB = contactInfoDAO.getContactInfosBySupplierId(supplier.getId());
      // supplier_id = -1 is used for creation
      // supplier_id = 0 is used for removal
      // supplier_id > 0 is used for update
      int contactsChanged = 0;
      for (ContactInfoDTO contact : supplierDTOcontacts) {
         if (contact.getSupplierId() < 0) {
            LOGGER.info("Creating new contact: {}", contact);
            contact.setSupplierId(supplier.getId());
            contactInfoDAO.createContactInfo(contact);
            contactsChanged++;
         } else if (contact.getSupplierId() == 0) {
            LOGGER.info("Removing contact: {}", contact);
            contactInfoDAO.deleteContactInfo(supplier.getId(), contact.getName());
            contactsChanged++;
         } else {
            Optional<ContactInfoDTO> existingContact = contactsInDB.stream()
                  .filter(c -> c.getName() == contact.getName()).findFirst();
            if (existingContact.isPresent()) {
               if (!existingContact.get().equals(contact)) {
                  LOGGER.info("Updating existing contact: {}", contact);
                  contactInfoDAO.updateContactInfo(contact);
                  contactsChanged++;
               } else {
                  LOGGER.info("No changes detected in contact: {}", contact);
               }
            } else {
               LOGGER.info("Creating new contact: {}", contact);
               contact.setSupplierId(supplier.getId());
               contactInfoDAO.createContactInfo(contact);
               contactsChanged++;
            }
         }
      }
      return contactsChanged > 0;
   }

   @Override
   public boolean deleteSupplier(int id) {
      if (id < 0) {
         LOGGER.error("Attempted to delete supplier with negative ID: {}", id);
         throw new IllegalArgumentException("Supplier ID cannot be negative");
      }
      LOGGER.info("Deleting supplier with ID: {}", id);
      if (!supplierDAO.supplierExists(id)) {
         LOGGER.warn("Supplier with ID {} does not exist", id);
      }
      if (supplierDAO.deleteSupplier(id)) {
         LOGGER.info("Supplier with ID {} deleted successfully from DB", id);
         return true;
      } else {
         LOGGER.error("Failed to delete supplier with ID {} from DB", id);
         return false;
      }
   }

   @Override
   public boolean supplierExists(int id) {
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
   public List<SupplierDTO> getAllSuppliers() {
      LOGGER.info("Retrieving all suppliers");
      List<SupplierDTO> suppliers = supplierDAO.getAllSuppliers();
      if (suppliers.isEmpty()) {
         LOGGER.warn("No suppliers found in DB");
      } else {
         LOGGER.info("Found {} suppliers in DB", suppliers.size());
      }
      return suppliers;
   }

   @Override
   public AgreementDTO addAgreementToSupplier(AgreementDTO agreement, int supplierId) {
      if (agreement == null) {
         LOGGER.error("Attempted to add a null agreement to supplier with ID: {}", supplierId);
         throw new IllegalArgumentException("Agreement cannot be null");
      }
      if (supplierId < 0) {
         LOGGER.error("Attempted to add agreement to supplier with negative ID: {}", supplierId);
         throw new IllegalArgumentException("Supplier ID cannot be negative");
      }
      LOGGER.info("Adding agreement {} to supplier with ID {}", agreement, supplierId);
      AgreementDTO createdAgreementDTO = saveAgreementInMemoryDB(agreement);
      return createdAgreementDTO;
   }

   public AgreementDTO saveAgreementInMemoryDB(AgreementDTO agreementToSave) {
      Optional<SupplierDTO> optionalSupplier = supplierDAO.getSupplier(agreementToSave.getSupplierId());
      if (optionalSupplier.isPresent()) {
         SupplierDTO supplier = optionalSupplier.get();
         agreementToSave.setSupplierName(supplier.getName());
         List<SupplierProductDTO> products = supplierProductDAO
               .getAllSupplierProductsForSupplier(agreementToSave.getSupplierId());
         if (!products.isEmpty()) {
            List<BillofQuantitiesItemDTO> billOfQuantitiesItems = new ArrayList<>();
            for (BillofQuantitiesItemDTO item : agreementToSave.getBillOfQuantitiesItems()) {
               Optional<SupplierProductDTO> product = products.stream()
                     .filter(p -> p.getProductId() == item.getProductId()).findFirst();
               if (product.isPresent()) {
                  if (billOfQuantitiesItems.stream()
                        .anyMatch(existingItem -> existingItem.getProductId() == item.getProductId()
                              && existingItem.getQuantity() == item.getQuantity()
                              && existingItem.getDiscountPercent().compareTo(item.getDiscountPercent()) == 0)) {
                     LOGGER.warn(
                           "Duplicate item found in bill of quantities for product ID {}. Skipping duplicate item.",
                           item.getProductId());
                     continue; // Skip duplicate items
                  }
                  item.setProductName(product.get().getName());
                  billOfQuantitiesItems.add(item);
               } else {
                  LOGGER.warn("Product with ID {} not found for supplier ID {}", item.getProductId(),
                        agreementToSave.getSupplierId(), " skipping to the next item");
               }
            }
            agreementToSave.setBillOfQuantitiesItems(billOfQuantitiesItems);
         }
         AgreementDTO savedAgreement = agreementDAO.createAgreement(agreementToSave);
         LOGGER.info("Agreement saved successfully: {}", savedAgreement);
         return savedAgreement;
      } else {
         LOGGER.error("Supplier with ID {} not found for agreement: {}", agreementToSave.getSupplierId(),
               agreementToSave);
         throw new IllegalArgumentException("Supplier not found for ID: " + agreementToSave.getSupplierId());
      }
   }

   @Override
   public Optional<AgreementDTO> getAgreementById(int agreementId) {
      if (agreementId < 0) {
         LOGGER.error("Attempted to get agreement with negative ID: {}", agreementId);
         throw new IllegalArgumentException("Agreement ID cannot be negative");
      }
      LOGGER.info("Retrieving agreement with ID: {}", agreementId);

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
   public boolean updateAgreement(AgreementDTO agreement) {
      if (agreement == null) {
         LOGGER.error("Attempted to update a null agreement");
         throw new IllegalArgumentException("Agreement cannot be null");
      }
      LOGGER.info("Updating agreement: {}", agreement);
      if (agreementDAO.updateAgreement(agreement)) {
         LOGGER.info("Agreement updated successfully: {}", agreement);
         return true;
      } else {
         LOGGER.error("Failed to update agreement: {}", agreement);
         return false;
      }
   }

   @Override
   public boolean removeAgreementFromSupplier(int agreementId, int supplierId) {
      if (agreementId < 0) {
         LOGGER.error("Attempted to remove agreement with negative ID: {}", agreementId);
         throw new IllegalArgumentException("Agreement ID cannot be negative");
      }
      if (supplierId < 0) {
         LOGGER.error("Attempted to remove agreement from supplier with negative ID: {}", supplierId);
         throw new IllegalArgumentException("Supplier ID cannot be negative");
      }
      LOGGER.info("Removing agreement with ID {} from supplier with ID {}", agreementId, supplierId);
      if (agreementDAO.deleteAgreement(agreementId)) {
         LOGGER.info("Agreement with ID {} removed successfully from supplier with ID {}", agreementId, supplierId);
         return true;
      } else {
         LOGGER.error("Failed to remove agreement with ID {} from supplier with ID {}", agreementId, supplierId);
         return false;
      }
   }

   @Override
   public List<AgreementDTO> getAllAgreementsForSupplier(int supplierId) {
      if (supplierId < 0) {
         LOGGER.error("Attempted to get agreements for supplier with negative ID: {}", supplierId);
         throw new IllegalArgumentException("Supplier ID cannot be negative");
      }
      LOGGER.info("Retrieving all agreements for supplier with ID: {}", supplierId);
      List<AgreementDTO> agreements = agreementDAO.getAllAgreementsForSupplier(supplierId);
      List<CatalogProductDTO> products = supplierProductDAO.getCatalogProducts();
      agreements.forEach(agreement -> {
         List<BillofQuantitiesItemDTO> items = agreement.getBillOfQuantitiesItems();
         items.forEach(item -> {
            Optional<CatalogProductDTO> product = products.stream()
                  .filter(p -> p.getProductId() == item.getProductId()).findFirst();
            if (product.isPresent()) {
               item.setProductName(product.get().getProductName());
            } else {
               LOGGER.warn("Product with ID {} not found for agreement ID {}", item.getProductId(),
                     agreement.getAgreementId(), " skipping to the next item");
            }
         });
         // the the supplier name from the supplierDTO
         Optional<SupplierDTO> supplier = supplierDAO.getSupplier(supplierId);
         if (supplier.isPresent()) {
            agreement.setSupplierName(supplier.get().getName());
         } else {
            LOGGER.warn("Supplier with ID {} not found for agreement ID {}", supplierId, agreement.getAgreementId(),
                  " skipping to the next agreement");
         }
      });

      LOGGER.info("Found {} agreements for supplier with ID: {}", agreements.size(), supplierId);
      return agreements;
   }

   @Override
   public List<AgreementDTO> getAllAgreements() {
      LOGGER.info("Retrieving all agreements");
      List<AgreementDTO> agreements = agreementDAO.getAllAgreements();
      if (agreements.isEmpty()) {
         LOGGER.warn("No agreements found in DB");
      } else {
         LOGGER.info("Found {} agreements in DB", agreements.size());
      }
      return agreements;
   }

   @Override
   public SupplierProductDTO createSupplierProduct(SupplierProductDTO supplierProduct) {
      if (supplierProduct == null) {
         LOGGER.error("Attempted to create a null supplier product");
         throw new IllegalArgumentException("Supplier product cannot be null");
      }
      if (supplierProduct.getSupplierId() < 0 || supplierProduct.getProductId() < 0) {
         LOGGER.error("Attempted to create supplier product with negative IDs: supplierId={}, productId={}",
               supplierProduct.getSupplierId(), supplierProduct.getProductId());
         throw new IllegalArgumentException("Supplier ID and Product ID cannot be negative");
      }
      SupplierProductDTO createdProduct = saveSupplierProductInMemoryDB(supplierProduct);
      return createdProduct;
   }

   public SupplierProductDTO saveSupplierProductInMemoryDB(SupplierProductDTO supplierProductToSave) {
      LOGGER.debug("Attempting to save the supplier product in memory and in DB: {}", supplierProductToSave);
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

   }

   @Override
   public Optional<SupplierProductDTO> getSupplierProductById(int supplierId, int productId) {
      if (supplierId < 0 || productId < 0) {
         LOGGER.error("Attempted to get supplier product with negative IDs: supplierId={}, productId={}", supplierId,
               productId);
         throw new IllegalArgumentException("Supplier ID and Product ID cannot be negative");
      }
      LOGGER.info("Retrieving supplier product with supplierId: {} and productId: {}", supplierId, productId);

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
   public boolean updateSupplierProduct(SupplierProductDTO supplierProduct) {
      if (supplierProduct == null) {
         LOGGER.error("Attempted to update a null supplier product");
         throw new IllegalArgumentException("Supplier product cannot be null");
      }
      if (supplierProduct.getSupplierId() < 0 || supplierProduct.getProductId() < 0) {
         LOGGER.error("Attempted to update supplier product with negative IDs: supplierId={}, productId={}",
               supplierProduct.getSupplierId(), supplierProduct.getProductId());
         throw new IllegalArgumentException("Supplier ID and Product ID cannot be negative");
      }
      SupplierProductDTO updatedProduct = saveSupplierProductInMemoryDB(supplierProduct);
      if (updatedProduct != null) {
         LOGGER.info("Supplier product updated successfully: {}", updatedProduct);
         return true;
      } else {
         LOGGER.error("Failed to update supplier product: {}", supplierProduct);
         return false;
      }
   }

   @Override
   public boolean deleteSupplierProduct(int supplierId, int productId) {
      if (supplierId < 0 || productId < 0) {
         LOGGER.error("Attempted to delete supplier product with negative IDs: supplierId={}, productId={}", supplierId,
               productId);
         throw new IllegalArgumentException("Supplier ID and Product ID cannot be negative");
      }
      LOGGER.info("Deleting supplier product with supplierId: {} and productId: {}", supplierId, productId);
      if (deleteSupplierProductFromMemoryDB(supplierId, productId)) {
         LOGGER.info("Supplier product with supplierId: {} and productId: {} deleted successfully", supplierId,
               productId);
         return true;
      } else {
         LOGGER.error("Failed to delete supplier product with supplierId: {} and productId: {}", supplierId, productId);
         return false;
      }
   }

   public boolean deleteSupplierProductFromMemoryDB(int supplierId, int productId) {
      LOGGER.info("Deleting supplier product with supplierId: {} and productId: {} from DB", supplierId,
            productId);

      if (supplierProductDAO.deleteSupplierProduct(supplierId, productId)) {
         LOGGER.info("Supplier product with supplierId: {} and productId: {} deleted from DB", supplierId,
               productId);
         return true;
      } else {
         LOGGER.warn("No supplier product found with supplierId: {} and productId: {}", supplierId, productId);
         return false;
      }
   }

   @Override
   public List<SupplierProductDTO> getAllSupplierProductsById(int supplierId) {
      if (supplierId < 0) {
         LOGGER.error("Attempted to get all supplier products for supplier with negative ID: {}", supplierId);
         throw new IllegalArgumentException("Supplier ID cannot be negative");
      }
      LOGGER.info("Retrieving all supplier products for supplier with ID: {}", supplierId);

      List<SupplierProductDTO> supplierProducts = supplierProductDAO.getAllSupplierProductsForSupplier(supplierId);
      if (supplierProducts.isEmpty()) {
         LOGGER.warn("No supplier products found for supplier with ID: {}", supplierId);
      } else {
         LOGGER.info("Found {} supplier products for supplier with ID: {}", supplierProducts.size(), supplierId);
      }
      return supplierProducts;
   }

   @Override
   public boolean supplierProductExists(int supplierId, int productId) {
      if (supplierId < 0 || productId < 0) {
         LOGGER.error("Attempted to check existence of supplier product with negative IDs: supplierId={}, productId={}",
               supplierId, productId);
         throw new IllegalArgumentException("Supplier ID and Product ID cannot be negative");
      }
      LOGGER.info("Checking if supplier product exists for supplierId: {} and productId: {}", supplierId, productId);

      boolean exists = supplierProductDAO.supplierProductExists(supplierId, productId);
      LOGGER.info("Supplier product with supplierId: {} and productId: {} exists: {}", supplierId, productId, exists);
      return exists;
   }

   @Override
   public List<SupplierProductDTO> getAllSupplierProducts() {
      LOGGER.info("Retrieving all supplier products");

      List<SupplierProductDTO> allSupplierProducts = supplierProductDAO.getAllSupplierProducts();
      if (allSupplierProducts.isEmpty()) {
         LOGGER.warn("No supplier products found in DB");
      } else {
         LOGGER.info("Found {} supplier products in DB", allSupplierProducts.size());
      }
      return allSupplierProducts;
   }

   @Override
   public List<Integer> getAllSuppliersForProductId(int productId) {
      if (productId < 0) {
         LOGGER.error("Attempted to get all suppliers for product with negative ID: {}", productId);
         throw new IllegalArgumentException("Product ID cannot be negative");
      }
      LOGGER.info("Retrieving all suppliers for product with ID: {}", productId);

      List<Integer> supplierIds = supplierProductDAO.getAllSupplierIdsForProductId(productId);
      if (supplierIds.isEmpty()) {
         LOGGER.warn("No suppliers found for product with ID: {}", productId);
      } else {
         LOGGER.info("Found {} suppliers for product with ID: {}", supplierIds.size(), productId);
      }
      return supplierIds;
   }

   @Override
   public List<Integer> getAllProductsForSupplierId(int supplierId) {
      if (supplierId < 0) {
         LOGGER.error("Attempted to get all products for supplier with negative ID: {}", supplierId);
         throw new IllegalArgumentException("Supplier ID cannot be negative");
      }
      LOGGER.info("Retrieving all products for supplier with ID: {}", supplierId);

      List<Integer> productIds = supplierProductDAO.getAllProductIdsForSupplierId(supplierId);
      if (productIds.isEmpty()) {
         LOGGER.warn("No products found for supplier with ID: {}", supplierId);
      } else {
         LOGGER.info("Found {} products for supplier with ID: {}", productIds.size(), supplierId);
      }
      return productIds;
   }

   @Override
   public List<CatalogProductDTO> getCatalogProducts() {
      LOGGER.info("Retrieving catalog products");

      List<CatalogProductDTO> catalogProducts = supplierProductDAO.getCatalogProducts();
      if (catalogProducts.isEmpty()) {
         LOGGER.warn("No catalog products found");
      } else {
         LOGGER.info("Found {} catalog products", catalogProducts.size());
      }
      return catalogProducts;
   }

   @Override
   public List<BillofQuantitiesItemDTO> getBillOfQuantitiesItemsForAgreement(int agreementId) {
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
   public boolean agreementExists(int agreementId) {
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