package Suppliers.DomainLayer.Repositories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Suppliers.DTOs.AgreementDTO;
import Suppliers.DTOs.BillofQuantitiesItemDTO;
import Suppliers.DTOs.CatalogProductDTO;
import Suppliers.DTOs.ContactInfoDTO;
import Suppliers.DTOs.SupplierDTO;
import Suppliers.DTOs.SupplierProductDTO;
import Suppliers.DTOs.Enums.InitializeState;
import Suppliers.DataLayer.DAOs.*;

import Suppliers.DataLayer.Interfaces.*;
import Suppliers.DataLayer.util.Database;
import Suppliers.DomainLayer.Classes.Agreement;
import Suppliers.DomainLayer.Classes.Supplier;
import Suppliers.DomainLayer.Classes.SupplierProduct;
import Suppliers.DomainLayer.Repositories.RepositoryIntefaces.SuppliersAgreementsRepositoryInterface;

public class SuppliersAgreementsRepositoryImpl implements SuppliersAgreementsRepositoryInterface {
   private static final Logger LOGGER = LoggerFactory.getLogger(SuppliersAgreementsRepositoryImpl.class);

   private final SupplierDAOInterface supplierDAO;
   private HashMap<Integer, Supplier> suppliersCache = new HashMap<>();
   private final ContactInfoDAOInterface contactInfoDAO;
   private final AgreementDAOInterface agreementDAO;
   private HashMap<Integer, List<Agreement>> agreementsCache = new HashMap<>();
   private final BillofQuantitiesItemDAOInterface billofQuantitiesItemDAO;
   private final SupplierProductDAOInterface supplierProductDAO;
   private HashMap<Integer, List<SupplierProduct>> supplierIdsToSpecifications = new HashMap<>();
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
      this.billofQuantitiesItemDAO = new JdbcBillofQuantitiesItemsDAO();
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
            Database.seedDefaultData();
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

   @Override
   public SupplierDTO createSupplier(SupplierDTO supplier) {
      if (supplier == null) {
         LOGGER.error("Attempted to create a null supplier");
         throw new IllegalArgumentException("Supplier cannot be null");
      }
      SupplierDTO created = supplierDAO.createSupplier(supplier);
      if (created == null) {
         LOGGER.error("Failed to create supplier: {}", supplier);
         throw new RuntimeException("Failed to create supplier");
      }
      List<ContactInfoDTO> createdContacts = new ArrayList<>();
      for (ContactInfoDTO contact : supplier.getContactsInfoDTOList()) {
         if (contact == null) {
            LOGGER.error("Attempted to create a null contact for supplier: {}", supplier.getName());
            throw new IllegalArgumentException("Contact cannot be null");
         }
         contact.setSupplierId(created.getId());
         contactInfoDAO.createContactInfo(contact);
         createdContacts.add(contact);
      }
      created.setContacts(createdContacts);
      suppliersCache.put(created.getId(), new Supplier(created));
      LOGGER.debug("Supplier created and cached: {}", created);
      return created;
   }

   @Override
   public Optional<SupplierDTO> getSupplierById(int id) {
      if (id < 0) {
         LOGGER.error("Attempted to get supplier with negative ID: {}", id);
         throw new IllegalArgumentException("Supplier ID cannot be negative");
      }
      LOGGER.info("Retrieving supplier with ID: {}", id);
      if (suppliersCache.containsKey(id)) {
         LOGGER.info("Supplier found in cache: {}", suppliersCache.get(id));
         return Optional.of(new SupplierDTO(suppliersCache.get(id)));
      }
      Optional<SupplierDTO> supplier = supplierDAO.getSupplier(id);
      if (supplier.isPresent()) {
         List<ContactInfoDTO> contacts = contactInfoDAO.getContactInfosBySupplierId(id);
         if (contacts.isEmpty()) {
            LOGGER.warn("No contact info found for supplier with ID: {}", id);
         } else {
            LOGGER.info("Found {} contact(s) for supplier with ID: {}", contacts.size(), id);
            supplier.get().setContacts(contacts);
         }
         suppliersCache.put(id, new Supplier(supplier.get()));
         LOGGER.info("Supplier retrieved and cached: {}", supplier.get());
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
            LOGGER.info("Creating new contact: {}", contact.getName());
            contact.setSupplierId(supplier.getId());
            contactInfoDAO.createContactInfo(contact);
            contactsChanged++;
         } else if (contact.getSupplierId() == 0) {
            LOGGER.info("Removing contact: {}", contact.getName());
            contactInfoDAO.deleteContactInfo(supplier.getId(), contact.getName());
            contactsChanged++;
         } else {
            Optional<ContactInfoDTO> existingContact = contactsInDB.stream()
                  .filter(c -> c.getName() == contact.getName()).findFirst();
            if (existingContact.isPresent()) {
               if (!existingContact.get().equals(contact)) {
                  LOGGER.info("Updating existing contact: {}", contact.getName());
                  contactInfoDAO.updateContactInfo(contact);
                  contactsChanged++;
               } else {
                  LOGGER.info("No changes detected in contact: {}", contact.getName());
               }
            }
         }
      }
      if (contactsChanged > 0) {
         LOGGER.info("Updated {} contact(s) for supplier with ID: {}", contactsChanged, supplier.getId());
      } else {
         LOGGER.info("No changes detected in contacts for supplier with ID: {}", supplier.getId());
      }
      suppliersCache.put(supplier.getId(), new Supplier(supplier));
      LOGGER.debug("Supplier updated and cached: {}", supplier);
      return true;
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
         return false;
      } else {
         SupplierDTO supplier = supplierDAO.getSupplier(id).orElseThrow(
               () -> new RuntimeException("Supplier not found for ID: " + id));
         for (ContactInfoDTO contact : supplier.getContactsInfoDTOList()) {
            if (contact.getSupplierId() < 0) {
               LOGGER.warn("Contact ID cannot be negative in contact info: {}, skipping", contact);
               continue; // Skip items with negative supplier IDs
            }
            // Remove from cache
            List<ContactInfoDTO> contacts = contactInfoDAO.getContactInfosBySupplierId(id);
            if (contacts != null) {
               contacts.removeIf(c -> c.getName().equals(contact.getName()));
               if (contacts.isEmpty()) {
                  contactInfoDAO.deleteContactInfo(id, contact.getName());
               }
            }
         }
         for (AgreementDTO agreement : agreementDAO.getAllAgreementsForSupplier(id)) {
            if (!billofQuantitiesItemDAO.deleteAllBillofQuantitiesItems(agreement.getAgreementId())) {
               LOGGER.error("Failed to delete Bill of Quantities items for agreement with ID: {}",
                     agreement.getAgreementId());
               throw new RuntimeException("Failed to delete Bill of Quantities items");
            }
            if (!agreementDAO.deleteAgreement(agreement.getAgreementId())) {
               LOGGER.error("Failed to delete agreement with ID: {}", agreement.getAgreementId());
               throw new RuntimeException("Failed to delete agreement");
            }
         }
         for (SupplierProductDTO product : supplierProductDAO.getAllSupplierProductsForSupplier(id)) {
            if (!supplierProductDAO.deleteSupplierProduct(id, product.getProductId())) {
               LOGGER.error("Failed to delete supplier product with ID: {} for supplier with ID: {}",
                     product.getProductId(), id);
               throw new RuntimeException("Failed to delete supplier product");
            }
         }
         // Remove from cache
         suppliersCache.remove(id);
         supplierIdsToSpecifications.remove(id);
         agreementsCache.remove(id);
      }
      if (!supplierDAO.deleteSupplier(id)) {
         LOGGER.error("Failed to delete supplier with ID: {}", id);
         throw new RuntimeException("Failed to delete supplier");
      }
      LOGGER.info("Supplier with ID {} deleted successfully from DB", id);
      return true;
   }

   @Override
   public boolean supplierExists(int id) {
      if (id < 0) {
         LOGGER.error("Attempted to check existence of supplier with negative ID: {}", id);
         throw new IllegalArgumentException("Supplier ID cannot be negative");
      }
      LOGGER.info("Checking if supplier with ID {} exists", id);
      boolean exists = suppliersCache.containsKey(id) || supplierDAO.supplierExists(id);
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
         for (SupplierDTO supplier : suppliers) {
            List<ContactInfoDTO> contacts = contactInfoDAO.getContactInfosBySupplierId(supplier.getId());
            if (!contacts.isEmpty()) {
               LOGGER.info("Found {} contact(s) for supplier with ID: {}", contacts.size(), supplier.getId());
               supplier.setContacts(contacts);
            } else {
               LOGGER.warn("No contact info found for supplier with ID: {}", supplier.getId());
            }
            suppliersCache.put(supplier.getId(), new Supplier(supplier));
         }
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
      LOGGER.info("Adding agreement for supplier with ID: {}", supplierId);
      AgreementDTO createdAgreementDTO = saveAgreementInMemoryDB(agreement);
      return createdAgreementDTO;
   }

   private AgreementDTO saveAgreementInMemoryDB(AgreementDTO agreementToSave) {
      Optional<SupplierDTO> optionalSupplier = getSupplierById(agreementToSave.getSupplierId());
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
         if (savedAgreement == null) {
            LOGGER.error("Failed to save agreement: {}", agreementToSave);
            throw new RuntimeException("Failed to save agreement");
         }
         List<BillofQuantitiesItemDTO> billOfQuantitiesItems = new ArrayList<>();
         for (BillofQuantitiesItemDTO item : savedAgreement.getBillOfQuantitiesItems()) {
            if (item.getProductId() < 0) {
               LOGGER.warn("Product ID cannot be negative in Bill of Quantities item: {}, skipping", item);
            }
            item.setAgreementId(savedAgreement.getAgreementId());
            BillofQuantitiesItemDTO createdBOQitem = billofQuantitiesItemDAO.createBillofQuantitiesItem(item);
            if (createdBOQitem == null) {
               LOGGER.error("Failed to create Bill of Quantities item: {}", item);
               throw new RuntimeException("Failed to create Bill of Quantities item");
            }
            billOfQuantitiesItems.add(createdBOQitem);
         }
         savedAgreement.setBillOfQuantitiesItems(billOfQuantitiesItems);
         agreementsCache.computeIfAbsent(supplier.getId(), k -> new ArrayList<>()).add(new Agreement(savedAgreement));
         LOGGER.info("Agreement saved and cached successfully: {}", savedAgreement.getAgreementId());
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
      if (agreementsCache.values().stream()
            .flatMap(List::stream)
            .anyMatch(a -> a.getAgreementId() == agreementId)) {
         LOGGER.info("Agreement found in cache with ID: {}", agreementId);
         return agreementsCache.values().stream()
               .flatMap(List::stream)
               .filter(a -> a.getAgreementId() == agreementId)
               .findFirst()
               .map(AgreementDTO::new);
      }
      Optional<AgreementDTO> agreement = agreementDAO.getAgreementById(agreementId);
      if (agreement.isPresent()) {
         LOGGER.info("Found agreement: {}", agreement.get());
         List<BillofQuantitiesItemDTO> boqItems = billofQuantitiesItemDAO
               .getAllBillofQantitiesItemsForAgreementId(agreementId);
         if (boqItems.isEmpty()) {
            LOGGER.warn("No Bill of Quantities items found for agreement with ID: {}", agreementId);
         } else {
            LOGGER.info("Found {} Bill of Quantities items for agreement with ID: {}", boqItems.size(), agreementId);
            agreement.get().setBillOfQuantitiesItems(boqItems);
            String supplierName = getSupplierById(agreement.get().getSupplierId())
                  .map(SupplierDTO::getName).orElse("Unknown Supplier");
            agreement.get().setSupplierName(supplierName);
         }
         agreementsCache.computeIfAbsent(agreement.get().getSupplierId(), k -> new ArrayList<>())
               .add(new Agreement(agreement.get()));
         LOGGER.info("Agreement retrieved and cached: {}", agreement.get().getAgreementId());
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
      LOGGER.info("Updating agreement: {}", agreement.getAgreementId());
      if (agreementDAO.updateAgreement(agreement)) {
         List<BillofQuantitiesItemDTO> updatedItems = new ArrayList<>();
         for (BillofQuantitiesItemDTO item : agreement.getBillOfQuantitiesItems()) {
            if (item.getProductId() < 0) {
               LOGGER.warn("Product ID cannot be negative in Bill of Quantities item: {}, skipping", item);
               continue; // Skip items with negative product IDs
            }
            if (billofQuantitiesItemDAO.updateBillofQuantitiesItem(item)) {
               updatedItems.add(item);
               LOGGER.debug("Updated Bill of Quantities item: {}", item);
            } else {
               LOGGER.error("Failed to update Bill of Quantities item: {}", item);
               throw new RuntimeException("Failed to update Bill of Quantities item");
            }
         }
         agreement.setBillOfQuantitiesItems(updatedItems);
         agreementsCache.computeIfAbsent(agreement.getSupplierId(), k -> new ArrayList<>())
               .removeIf(a -> a.getAgreementId() == agreement.getAgreementId());
         agreementsCache.get(agreement.getSupplierId()).add(new Agreement(agreement));
         LOGGER.info("Agreement updated and cached successfully: {}", agreement.getAgreementId());
         return true;
      } else {
         LOGGER.error("Failed to update agreement: {}", agreement.getAgreementId());
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
         // Remove from cache
         if (agreementsCache.containsKey(supplierId)) {
            List<Agreement> agreements = agreementsCache.get(supplierId);
            agreements.removeIf(a -> a.getAgreementId() == agreementId);
            if (agreements.isEmpty()) {
               agreementsCache.remove(supplierId);
            }
         }
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
      if (agreements.isEmpty()) {
         LOGGER.warn("No agreements found for supplier with ID: {}", supplierId);
      } else {
         for (AgreementDTO agreement : agreements) {
            List<BillofQuantitiesItemDTO> boqItems = billofQuantitiesItemDAO
                  .getAllBillofQantitiesItemsForAgreementId(agreement.getAgreementId());
            if (!boqItems.isEmpty()) {
               LOGGER.info("Found {} Bill of Quantities items for agreement with ID: {}", boqItems.size(),
                     agreement.getAgreementId());
            } else {
               LOGGER.warn("No Bill of Quantities items found for agreement with ID: {}", agreement.getAgreementId());
            }
            agreement.setBillOfQuantitiesItems(boqItems);
         }
      }
      for (AgreementDTO agreement : agreements) {
         agreementsCache.computeIfAbsent(supplierId, k -> new ArrayList<>());
         agreementsCache.get(supplierId).add(new Agreement(agreement));
      }
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
      if (supplierProduct.getSupplierId() < 0) {
         LOGGER.error("Attempted to create supplier product with negative supplier ID: {}",
               supplierProduct.getSupplierId());
         throw new IllegalArgumentException("Supplier ID cannot be negative");
      }
      if (supplierProduct.getProductId() >= 0) {
         LOGGER.error("Attempted to create supplier product with existing product ID: {}",
               supplierProduct.getProductId());
         throw new IllegalArgumentException("Product ID must be negative for new products");
      }
      SupplierProductDTO createdProduct = saveSupplierProductInMemoryDB(supplierProduct);
      return createdProduct;
   }

   private SupplierProductDTO saveSupplierProductInMemoryDB(SupplierProductDTO supplierProductToSave) {
      LOGGER.debug("Attempting to save the supplier product in memory and in DB: {}", supplierProductToSave.getName() +
            " with supplierId: " + supplierProductToSave.getSupplierId());
      if (supplierProductToSave.getProductId() < 0) {
         LOGGER.info("Creating new supplier product.");
         SupplierProductDTO newProduct = supplierProductDAO.createSupplierProduct(supplierProductToSave);
         if (newProduct == null) {
            LOGGER.error("Failed to create supplier product: {}", supplierProductToSave);
            throw new RuntimeException("Failed to create supplier product");
         }
         supplierIdsToSpecifications.computeIfAbsent(newProduct.getSupplierId(), k -> new ArrayList<>())
               .add(new SupplierProduct(newProduct));
         LOGGER.debug("Supplier product created and cached successfully: {}", newProduct);
         return newProduct;
      } else {
         LOGGER.info("Updating existing supplier product with IDs: {}, {}", supplierProductToSave.getSupplierId(),
               supplierProductToSave.getProductId());
         supplierProductDAO.updateSupplierProduct(supplierProductToSave);
         // Update the cache
         List<SupplierProduct> products = supplierIdsToSpecifications
               .computeIfAbsent(supplierProductToSave.getSupplierId(), k -> new ArrayList<>());
         products.removeIf(p -> p.getProductId() == supplierProductToSave.getProductId());
         products.add(new SupplierProduct(supplierProductToSave));
         // dont need to update catalogProductsCache here, as info in it is not changed
         LOGGER.debug("Supplier product updated successfully: {}", supplierProductToSave);
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
      if (supplierIdsToSpecifications.containsKey(supplierId)) {
         List<SupplierProduct> products = supplierIdsToSpecifications.get(supplierId);
         Optional<SupplierProduct> product = products.stream()
               .filter(p -> p.getProductId() == productId).findFirst();
         if (product.isPresent()) {
            LOGGER.info("Found supplier product in cache: {}", product.get());
            return Optional.of(new SupplierProductDTO(product.get()));
         } else {
            Optional<SupplierProductDTO> productInDB = supplierProductDAO.getSupplierProductById(supplierId, productId);
            if (productInDB.isPresent()) {
               supplierIdsToSpecifications.get(supplierId).add(new SupplierProduct(productInDB.get()));
               LOGGER.info("Found and cached supplier product: {}", productInDB.get());
               return productInDB;
            } else {
               LOGGER.warn("No supplier product found with supplierId: {} and productId: {}", supplierId, productId);
               return Optional.empty();
            }
         }
      } else {
         Optional<SupplierProductDTO> productInDB = supplierProductDAO.getSupplierProductById(supplierId, productId);
         if (productInDB.isPresent()) {
            supplierIdsToSpecifications.putIfAbsent(supplierId, new ArrayList<>());
            supplierIdsToSpecifications.get(supplierId).add(new SupplierProduct(productInDB.get()));
            LOGGER.info("Found and cached supplier product: {}", productInDB.get());
            return productInDB;
         } else {
            LOGGER.warn("No supplier product found with supplierId: {} and productId: {}", supplierId, productId);
            return Optional.empty();
         }
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
         LOGGER.info("Supplier product updated successfully: {}", updatedProduct.getProductId());
         return true;
      } else {
         LOGGER.error("Failed to update supplier product: {}", supplierProduct.getProductId());
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
         // Remove from cache
         if (supplierIdsToSpecifications.containsKey(supplierId)) {
            List<SupplierProduct> products = supplierIdsToSpecifications.get(supplierId);
            products.removeIf(p -> p.getProductId() == productId);
            if (products.isEmpty()) {
               supplierIdsToSpecifications.remove(supplierId);
            }
         }
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
      List<BillofQuantitiesItemDTO> boqItems = billofQuantitiesItemDAO
            .getAllBillofQantitiesItemsForAgreementId(agreementId);
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

   public List<BillofQuantitiesItemDTO> getBillOfQuantitiesItemsByProductId(int productId) {
      if (productId < 0) {
         LOGGER.error("Attempted to get Bill of Quantities items for product with negative ID: {}", productId);
         throw new IllegalArgumentException("Product ID cannot be negative");
      }
      LOGGER.info("Retrieving Bill of Quantities items for product with ID: {}", productId);
      List<BillofQuantitiesItemDTO> boqItems = billofQuantitiesItemDAO.getBillofQuantitiesItemsById(productId);
      if (boqItems.isEmpty()) {
         LOGGER.warn("No Bill of Quantities items found for product with ID: {}", productId);
      } else {
         LOGGER.info("Found {} Bill of Quantities items for product with ID: {}", boqItems.size(), productId);
      }
      return boqItems;
   }

   private void loadCurrentStateFromDatabase() {
      LOGGER.info("Loading current suppliers from database");
      // actually doing nothing here, just logging for the effect :)
      LOGGER.info("Loaded suppliers data from DB!");
   }

   private void loadEmptyState() {
      LOGGER.info("Clearing data base for empty state...");
      Database.deleteAllData();
      LOGGER.info("Empty state loaded into DB!");
   }

   public Optional<CatalogProductDTO> getCatalogProductById(int pid) {
      if (pid < 0) {
         LOGGER.error("Attempted to get catalog product with negative ID: {}", pid);
         throw new IllegalArgumentException("Product ID cannot be negative");
      }
      LOGGER.info("Retrieving catalog product with ID: {}", pid);
      Optional<CatalogProductDTO> catalogProduct = supplierProductDAO.getCatalogProducts().stream()
            .filter(product -> product.getProductId() == pid)
            .findFirst();
      if (catalogProduct.isEmpty()) {
         LOGGER.warn("No catalog product found with ID: {}", pid);
         return Optional.empty();
      }
      LOGGER.debug("Catalog product details: {}", catalogProduct.get());
      return catalogProduct;
   }
}