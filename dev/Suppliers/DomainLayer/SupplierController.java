package Suppliers.DomainLayer;

import java.security.InvalidParameterException;
import java.sql.SQLException;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Suppliers.DomainLayer.Repositories.SuppliersAgreementsRepositoryImpl;
import Suppliers.DTOs.AgreementDTO;
import Suppliers.DTOs.CatalogProductDTO;
import Suppliers.DTOs.SupplierDTO;
import Suppliers.DTOs.SupplierProductDTO;
import Suppliers.DTOs.Enums.InitializeState;

public class SupplierController {
   private final SuppliersAgreementsRepositoryImpl suppliersAgreementsRepo;
   private static final Logger LOGGER = LoggerFactory.getLogger(SupplierController.class);
   // private final SuppliersAgreementsRepositoryImpl agreementRepository;
   // Map of supplier IDs to their products and prices

   public SupplierController(InitializeState initState) {
      LOGGER.info("Initializing SupplierFacade with state: {}", initState);
      this.suppliersAgreementsRepo = SuppliersAgreementsRepositoryImpl.getInstance();
      try {
         suppliersAgreementsRepo.initialize(initState);
         LOGGER.info("Database initialized in state: {}", initState);
      } catch (SQLException e) {
         LOGGER.error("Error initializing database: {}", e.getMessage());
         throw new RuntimeException("Failed to initialize database", e);
      }
   }

   public SupplierDTO createSupplier(SupplierDTO supplierDTO) {
      if (supplierDTO == null) {
         LOGGER.error("SupplierDTO cannot be null");
         throw new InvalidParameterException("SupplierDTO cannot be null");
      }
      try {
         return suppliersAgreementsRepo.createSupplier(supplierDTO);
      } catch (SQLException e) {
         LOGGER.error("Error creating supplier in the database: {}", e.getMessage());
         throw new RuntimeException("Failed to create supplier in the database", e);
      }
   }

   public void removeSupplier(int supplierID) {
      try {
         suppliersAgreementsRepo.deleteSupplier(supplierID);
      } catch (SQLException e) {
         LOGGER.error("Error removing supplier from the database: {}", e.getMessage());
         throw new RuntimeException("Failed to remove supplier from the database", e);
      }
   }

   public void updateSupplier(SupplierDTO supplierDTO, int supplierID) {
      if (supplierDTO == null) {
         throw new InvalidParameterException("SupplierDTO cannot be null");
      }

      try {
         Optional<SupplierDTO> supplier = suppliersAgreementsRepo.getSupplierById(supplierID);
         if (supplier.isEmpty()) {
            throw new IllegalArgumentException("Supplier not found for ID: " + supplierID);
         }
         suppliersAgreementsRepo.updateSupplier(supplierDTO);
      } catch (SQLException e) {
         LOGGER.error("Error updating supplier in the database: {}", e.getMessage());
         throw new RuntimeException("Failed to update supplier in the database", e);
      }
   }

   public SupplierDTO getSupplierDTO(int supplierID) {
      try {
         Optional<SupplierDTO> supplier = suppliersAgreementsRepo.getSupplierById(supplierID);
         if (supplier.isEmpty()) {
            throw new IllegalArgumentException("Supplier not found for ID: " + supplierID);
         }
         return supplier.get();
      } catch (SQLException e) {
         LOGGER.error("Error retrieving supplier from the database: {}", e.getMessage());
         throw new RuntimeException("Failed to retrieve supplier from the database", e);
      }
   }

   public void addAgreementToSupplier(AgreementDTO agreementDTO) {
      if (agreementDTO == null) {
         LOGGER.error("AgreementDTO cannot be null");
         throw new InvalidParameterException("AgreementDTO cannot be null");
      }
      try {
         SupplierDTO supplier = suppliersAgreementsRepo.getSupplierById(agreementDTO.getSupplierId())
               .orElseThrow(
                     () -> new IllegalArgumentException("Supplier not found for ID: " + agreementDTO.getSupplierId()));
         agreementDTO.setSupplierName(supplier.getName());
         agreementDTO.setSupplierId(supplier.getId());
         try {
            suppliersAgreementsRepo.addAgreementToSupplier(agreementDTO, supplier.getId());
         } catch (SQLException e) {
            LOGGER.error("Error creating agreement in the database: {}", e.getMessage());
            throw new RuntimeException("Failed to create agreement in the database", e);
         }
      } catch (SQLException e) {
         LOGGER.error("Error retrieving supplier for agreement: {}", e.getMessage());
         throw new RuntimeException("Failed to retrieve supplier for agreement", e);
      }
   }

   public List<SupplierDTO> getAllSuppliers() {
      try {
         List<SupplierDTO> suppliersList = suppliersAgreementsRepo.getAllSuppliers();
         if (suppliersList == null || suppliersList.isEmpty()) {
            LOGGER.warn("No suppliers found in the database");
            return Collections.emptyList();
         }
         return suppliersList.stream()
               .sorted(Comparator.comparing(SupplierDTO::getId))
               .toList();
      } catch (SQLException e) {
         LOGGER.error("Error retrieving suppliers from the database: {}", e.getMessage());
         throw new RuntimeException("Failed to retrieve suppliers from the database", e);
      }
   }

   public void addProductToSupplierAndMemory(int supplierID, SupplierProductDTO product) {
      // use suppliersAgreementsRepo to add the product to the database and memory
      if (product == null) {
         LOGGER.error("ProductDTO cannot be null");
         throw new InvalidParameterException("ProductDTO cannot be null");
      }
      try {
         suppliersAgreementsRepo.createSupplierProduct(product);
      } catch (SQLException e) {
         LOGGER.error("Error adding product to supplier in the database: {}", e.getMessage());
         throw new RuntimeException("Failed to add product to supplier in the database", e);
      }
   }

   public void removeProductFromSupplierAndMemory(int supplierID, int product) {
      if (product <= 0) {
         LOGGER.error("Product ID must be greater than 0");
         throw new InvalidParameterException("Product ID must be greater than 0");
      }
      try {
         suppliersAgreementsRepo.deleteSupplierProduct(supplierID, product);
      } catch (SQLException e) {
         LOGGER.error("Error removing product from supplier in the database: {}", e.getMessage());
         throw new RuntimeException("Failed to remove product from supplier in the database", e);
      }
   }

   public void updateProductInSupplierAndMemory(int supplierID, SupplierProductDTO productDTO) {
      if (productDTO == null) {
         LOGGER.error("ProductDTO cannot be null");
         throw new InvalidParameterException("ProductDTO cannot be null");
      }
      if (productDTO.getProductId() <= 0) {
         LOGGER.error("Product ID must be greater than 0");
         throw new InvalidParameterException("Product ID must be greater than 0");
      }
      try {
         suppliersAgreementsRepo.updateSupplierProduct(productDTO);
      } catch (SQLException e) {
         LOGGER.error("Error updating product in supplier in the database: {}", e.getMessage());
         throw new RuntimeException("Failed to update product in supplier in the database", e);
      }
   }

   public List<SupplierProductDTO> getSupplierProducts(int supplierID) {
      try {
         List<SupplierProductDTO> products = suppliersAgreementsRepo.getAllSupplierProductsById(supplierID);
         if (products == null || products.isEmpty()) {
            LOGGER.warn("No products found for supplier ID: {}", supplierID);
            return Collections.emptyList();
         }
         return products.stream()
               .sorted(Comparator.comparing(SupplierProductDTO::getProductId))
               .toList();
      } catch (SQLException e) {
         LOGGER.error("Error retrieving products for supplier ID {}: {}", supplierID, e.getMessage());
         throw new RuntimeException("Failed to retrieve products for supplier", e);
      }
   }

   public boolean checkSupplierExists(int supplierID) {
      try {
         return suppliersAgreementsRepo.supplierExists(supplierID);
      } catch (SQLException e) {
         LOGGER.error("Error checking if supplier exists: {}", e.getMessage());
         throw new RuntimeException("Failed to check if supplier exists", e);
      }
   }

   public List<CatalogProductDTO> getProductCatalog() {
      try {
         return suppliersAgreementsRepo.getCatalogProducts();
      } catch (SQLException e) {
         LOGGER.error("Error retrieving product catalog: {}", e.getMessage());
         throw new RuntimeException("Failed to retrieve product catalog", e);
      }
   }

   public AgreementDTO createAgreement(AgreementDTO agreementDTO) {
      if (agreementDTO == null) {
         LOGGER.error("AgreementDTO cannot be null");
         throw new InvalidParameterException("AgreementDTO cannot be null");
      }
      try {
         return suppliersAgreementsRepo.addAgreementToSupplier(agreementDTO, agreementDTO.getSupplierId());
      } catch (SQLException e) {
         LOGGER.error("Error creating agreement in the database: {}", e.getMessage());
         throw new RuntimeException("Failed to create agreement in the database", e);
      }
   }

   public void removeAgreement(int agreementID, int supplierID) {
      if (agreementID <= 0 || supplierID <= 0) {
         LOGGER.error("Agreement ID and Supplier ID must be greater than 0");
         throw new InvalidParameterException("Agreement ID and Supplier ID must be greater than 0");
      }
      try {
         suppliersAgreementsRepo.removeAgreementFromSupplier(agreementID, supplierID);
      } catch (SQLException e) {
         LOGGER.error("Error removing agreement from supplier in the database: {}", e.getMessage());
         throw new RuntimeException("Failed to remove agreement from supplier in the database", e);
      }
   }

   public List<AgreementDTO> getAgreementsBySupplierId(int supplierId) {
      try {
         List<AgreementDTO> agreements = suppliersAgreementsRepo.getAllAgreementsForSupplier(supplierId);
         if (agreements == null || agreements.isEmpty()) {
            LOGGER.warn("No agreements found for supplier ID: {}", supplierId);
            return Collections.emptyList();
         }
         return agreements.stream()
               .sorted(Comparator.comparing(AgreementDTO::getAgreementId))
               .toList();
      } catch (SQLException e) {
         LOGGER.error("Error retrieving agreements for supplier ID {}: {}", supplierId, e.getMessage());
         throw new RuntimeException("Failed to retrieve agreements for supplier", e);
      }
   }

   public AgreementDTO getAgreement(int agreementID) {
      if (agreementID <= 0) {
         LOGGER.error("Agreement ID must be greater than 0");
         throw new InvalidParameterException("Agreement ID must be greater than 0");
      }
      try {
         Optional<AgreementDTO> agreement = suppliersAgreementsRepo.getAgreementById(agreementID);
         if (agreement == null) {
            LOGGER.warn("No agreement found for ID: {}", agreementID);
            return null;
         }
         return agreement.orElseThrow(() -> new IllegalArgumentException("Agreement not found for ID: " + agreementID));
      } catch (SQLException e) {
         LOGGER.error("Error retrieving agreement from the database: {}", e.getMessage());
         throw new RuntimeException("Failed to retrieve agreement from the database", e);
      }
   }

   public void updateAgreement(int agreementId, AgreementDTO updatedAgreement) {
      if (updatedAgreement == null) {
         throw new InvalidParameterException("UpdatedAgreement cannot be null");
      }
      if (agreementId <= 0) {
         LOGGER.error("Agreement ID must be greater than 0");
         throw new InvalidParameterException("Agreement ID must be greater than 0");
      }
      try {
         Optional<AgreementDTO> existingAgreement = suppliersAgreementsRepo.getAgreementById(agreementId);
         if (existingAgreement.isEmpty()) {
            throw new IllegalArgumentException("Agreement not found for ID: " + agreementId);
         }
         suppliersAgreementsRepo.updateAgreement(updatedAgreement);
      } catch (SQLException e) {
         LOGGER.error("Error updating agreement in the database: {}", e.getMessage());
         throw new RuntimeException("Failed to update agreement in the database", e);
      }
   }

}
