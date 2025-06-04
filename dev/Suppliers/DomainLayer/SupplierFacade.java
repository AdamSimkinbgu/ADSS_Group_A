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

public class SupplierFacade {
   private final SuppliersAgreementsRepositoryImpl suppliersAgreementsRepo;
   private static final Logger LOGGER = LoggerFactory.getLogger(SupplierFacade.class);
   // private final SuppliersAgreementsRepositoryImpl agreementRepository;
   // Map of supplier IDs to their products and prices

   public SupplierFacade(InitializeState initState) {
      LOGGER.info("Initializing SupplierFacade with state: {}", initState);
      this.suppliersAgreementsRepo = SuppliersAgreementsRepositoryImpl.getInstance();
      suppliersAgreementsRepo.initialize(initState);
      LOGGER.info("Database initialized in state: {}", initState);

   }

   public SupplierDTO createSupplier(SupplierDTO supplierDTO) {
      if (supplierDTO == null) {
         LOGGER.error("SupplierDTO cannot be null");
         throw new InvalidParameterException("SupplierDTO cannot be null");
      }
      return suppliersAgreementsRepo.createSupplier(supplierDTO);

   }

   public boolean removeSupplier(int supplierID) {

      if (suppliersAgreementsRepo.deleteSupplier(supplierID)) {
         LOGGER.info("Supplier with ID {} removed successfully", supplierID);
         return true;
      } else {
         LOGGER.warn("No supplier found with ID: {}", supplierID);
         return false;
      }

   }

   public boolean updateSupplier(SupplierDTO supplierDTO, int supplierID) {
      if (supplierDTO == null) {
         throw new InvalidParameterException("SupplierDTO cannot be null");
      }
      Optional<SupplierDTO> supplier = suppliersAgreementsRepo.getSupplierById(supplierID);
      if (supplier.isEmpty()) {
         throw new IllegalArgumentException("Supplier not found for ID: " + supplierID);
      }
      // check if there is nothing to update
      if (supplier.get().equals(supplierDTO)) {
         LOGGER.info("No changes detected for supplier ID: {}", supplierID);
         return false; // No changes to update
      }
      if (suppliersAgreementsRepo.updateSupplier(supplierDTO)) {
         LOGGER.info("Supplier with ID {} updated successfully", supplierID);
         return true;
      } else {
         LOGGER.warn("No changes made to supplier with ID: {}", supplierID);
         return false;
      }
   }

   public SupplierDTO getSupplierDTO(int supplierID) {
      Optional<SupplierDTO> supplier = suppliersAgreementsRepo.getSupplierById(supplierID);
      if (supplier.isEmpty()) {
         throw new IllegalArgumentException("Supplier not found for ID: " + supplierID);
      }
      return supplier.get();

   }

   public void addAgreementToSupplier(AgreementDTO agreementDTO) {
      if (agreementDTO == null) {
         LOGGER.error("AgreementDTO cannot be null");
         throw new InvalidParameterException("AgreementDTO cannot be null");
      }

      SupplierDTO supplier = suppliersAgreementsRepo.getSupplierById(agreementDTO.getSupplierId())
            .orElseThrow(
                  () -> new IllegalArgumentException("Supplier not found for ID: " + agreementDTO.getSupplierId()));
      agreementDTO.setSupplierName(supplier.getName());
      agreementDTO.setSupplierId(supplier.getId());
      suppliersAgreementsRepo.addAgreementToSupplier(agreementDTO, supplier.getId());
   }

   public List<SupplierDTO> getAllSuppliers() {

      List<SupplierDTO> suppliersList = suppliersAgreementsRepo.getAllSuppliers();
      if (suppliersList == null || suppliersList.isEmpty()) {
         LOGGER.warn("No suppliers found in the database");
         return Collections.emptyList();
      }
      return suppliersList.stream()
            .sorted(Comparator.comparing(SupplierDTO::getId))
            .toList();

   }

   public void addProductToSupplierAndMemory(int supplierID, SupplierProductDTO product) {
      // use suppliersAgreementsRepo to add the product to the database and memory
      if (product == null) {
         LOGGER.error("ProductDTO cannot be null");
         throw new InvalidParameterException("ProductDTO cannot be null");
      }
      if (supplierID <= 0) {
         LOGGER.error("Supplier ID must be greater than 0");
         throw new InvalidParameterException("Supplier ID must be greater than 0");
      }

      product.setSupplierId(supplierID);
      SupplierProductDTO createdSupplierProduct = suppliersAgreementsRepo.createSupplierProduct(product);
      LOGGER.info("Product added to supplier in memory: {}", createdSupplierProduct);

   }

   public void removeProductFromSupplierAndDB(int supplierID, int product) {
      if (product <= 0) {
         LOGGER.error("Product ID must be greater than 0");
         throw new InvalidParameterException("Product ID must be greater than 0");
      }
      suppliersAgreementsRepo.deleteSupplierProduct(supplierID, product);

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
      suppliersAgreementsRepo.updateSupplierProduct(productDTO);

   }

   public List<SupplierProductDTO> getSupplierProducts(int supplierID) {
      List<SupplierProductDTO> products = suppliersAgreementsRepo.getAllSupplierProductsById(supplierID);
      if (products == null || products.isEmpty()) {
         LOGGER.warn("No products found for supplier ID: {}", supplierID);
         return Collections.emptyList();
      }
      return products.stream()
            .sorted(Comparator.comparing(SupplierProductDTO::getProductId))
            .toList();

   }

   public boolean checkSupplierExists(int supplierID) {
      return suppliersAgreementsRepo.supplierExists(supplierID);

   }

   public List<CatalogProductDTO> getProductCatalog() {
      return suppliersAgreementsRepo.getCatalogProducts();

   }

   public AgreementDTO createAgreement(AgreementDTO agreementDTO) {
      if (agreementDTO == null) {
         LOGGER.error("AgreementDTO cannot be null");
         throw new InvalidParameterException("AgreementDTO cannot be null");
      }
      return suppliersAgreementsRepo.addAgreementToSupplier(agreementDTO, agreementDTO.getSupplierId());

   }

   public void removeAgreement(int agreementID, int supplierID) {
      if (agreementID <= 0 || supplierID <= 0) {
         LOGGER.error("Agreement ID and Supplier ID must be greater than 0");
         throw new InvalidParameterException("Agreement ID and Supplier ID must be greater than 0");
      }
      suppliersAgreementsRepo.removeAgreementFromSupplier(agreementID, supplierID);

   }

   public List<AgreementDTO> getAgreementsBySupplierId(int supplierId) {
      List<AgreementDTO> agreements = suppliersAgreementsRepo.getAllAgreementsForSupplier(supplierId);
      if (agreements == null || agreements.isEmpty()) {
         LOGGER.warn("No agreements found for supplier ID: {}", supplierId);
         return Collections.emptyList();
      }
      return agreements.stream()
            .sorted(Comparator.comparing(AgreementDTO::getAgreementId))
            .toList();

   }

   public AgreementDTO getAgreement(int agreementID) {
      if (agreementID <= 0) {
         LOGGER.error("Agreement ID must be greater than 0");
         throw new InvalidParameterException("Agreement ID must be greater than 0");
      }
      Optional<AgreementDTO> agreement = suppliersAgreementsRepo.getAgreementById(agreementID);
      if (agreement == null) {
         LOGGER.warn("No agreement found for ID: {}", agreementID);
         return null;
      }
      return agreement.orElseThrow(() -> new IllegalArgumentException("Agreement not found for ID: " + agreementID));

   }

   public void updateAgreement(int agreementId, AgreementDTO updatedAgreement) {
      if (updatedAgreement == null) {
         throw new InvalidParameterException("UpdatedAgreement cannot be null");
      }
      if (agreementId <= 0) {
         LOGGER.error("Agreement ID must be greater than 0");
         throw new InvalidParameterException("Agreement ID must be greater than 0");
      }
      Optional<AgreementDTO> existingAgreement = suppliersAgreementsRepo.getAgreementById(agreementId);
      if (existingAgreement.isEmpty()) {
         throw new IllegalArgumentException("Agreement not found for ID: " + agreementId);
      }
      suppliersAgreementsRepo.updateAgreement(updatedAgreement);

   }

}
