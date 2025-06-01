package Suppliers.DomainLayer.Repositories;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Suppliers.DTOs.AgreementDTO;
import Suppliers.DTOs.CatalogProductDTO;
import Suppliers.DTOs.SupplierDTO;
import Suppliers.DTOs.SupplierProductDTO;
import Suppliers.DataLayer.DAOs.*;

import Suppliers.DataLayer.Interfaces.*;
import Suppliers.DomainLayer.Repositories.RepositoryIntefaces.SuppliersAgreementsRepositoryInterface;

public class SupplierRepositoryImpl implements SuppliersAgreementsRepositoryInterface {
   private static final Logger LOGGER = LoggerFactory.getLogger(SupplierRepositoryImpl.class);
   private final SupplierDAOInterface supplierDAO;
   private final AgreementDAOInterface agreementDAO;
   private final SupplierProductDAOInterface supplierProductDAO;
   private static SupplierRepositoryImpl instance;

   static SupplierRepositoryImpl getInstance() {
      if (instance == null) {
         instance = new SupplierRepositoryImpl();
         LOGGER.info("Created new instance of SupplierRepositoryImpl");
      } else {
         LOGGER.info("Using existing instance of SupplierRepositoryImpl");
      }
      return instance;
   }

   private SupplierRepositoryImpl() {
      this.supplierDAO = new JdbcSupplierDAO();
      this.agreementDAO = new JdbcAgreementDAO();
      this.supplierProductDAO = new JdbcSupplierProductDAO();
   }

   @Override
   public void createSupplier(SupplierDTO supplier) throws SQLException {
      if (supplier == null) {
         LOGGER.error("Attempted to create a null supplier");
         throw new IllegalArgumentException("Supplier cannot be null");
      }
      LOGGER.info("Creating supplier: {}", supplier);
      supplierDAO.createSupplier(supplier);
   }

   @Override
   public Optional<SupplierDTO> getSupplierById(int id) throws SQLException {
      if (id < 0) {
         LOGGER.error("Attempted to get supplier with negative ID: {}", id);
         throw new IllegalArgumentException("Supplier ID cannot be negative");
      }
      LOGGER.info("Retrieving supplier with ID: {}", id);
      Optional<SupplierDTO> supplier = supplierDAO.getSupplier(id);
      if (supplier.isPresent()) {
         LOGGER.info("Found supplier: {}", supplier.get());
      } else {
         LOGGER.warn("No supplier found with ID: {}", id);
      }
      return supplier;
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
      List<SupplierDTO> suppliers = supplierDAO.getAllSuppliers();
      if (suppliers.isEmpty()) {
         LOGGER.warn("No suppliers found");
      } else {
         LOGGER.info("Found {} suppliers", suppliers.size());
      }
      return suppliers;
   }

   @Override
   public void addAgreementToSupplier(AgreementDTO agreement, int supplierId) throws SQLException {
      if (agreement == null) {
         LOGGER.error("Attempted to add a null agreement to supplier with ID: {}", supplierId);
         throw new IllegalArgumentException("Agreement cannot be null");
      }
      if (supplierId < 0) {
         LOGGER.error("Attempted to add agreement to supplier with negative ID: {}", supplierId);
         throw new IllegalArgumentException("Supplier ID cannot be negative");
      }
      LOGGER.info("Adding agreement {} to supplier with ID {}", agreement, supplierId);
      agreementDAO.createAgreement(agreement);
      LOGGER.info("Agreement added successfully to supplier with ID {}", supplierId);
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
      agreementDAO.deleteAgreement(agreementId);
      LOGGER.info("Agreement with ID {} removed successfully from supplier with ID {}", agreementId, supplierId);
   }

   @Override
   public Optional<AgreementDTO> getAgreementById(int agreementId) throws SQLException {
      if (agreementId < 0) {
         LOGGER.error("Attempted to get agreement with negative ID: {}", agreementId);
         throw new IllegalArgumentException("Agreement ID cannot be negative");
      }
      LOGGER.info("Retrieving agreement with ID: {}", agreementId);
      Optional<AgreementDTO> agreement = agreementDAO.getAgreementById(agreementId);
      if (agreement.isPresent()) {
         LOGGER.info("Found agreement: {}", agreement.get());
      } else {
         LOGGER.warn("No agreement found with ID: {}", agreementId);
      }
      return agreement;
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
   public void deleteAgreement(int agreementId) throws SQLException {
      if (agreementId < 0) {
         LOGGER.error("Attempted to delete agreement with negative ID: {}", agreementId);
         throw new IllegalArgumentException("Agreement ID cannot be negative");
      }
      LOGGER.info("Deleting agreement with ID: {}", agreementId);
      agreementDAO.deleteAgreement(agreementId);
      LOGGER.info("Agreement with ID {} deleted successfully", agreementId);
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

   @Override
   public List<AgreementDTO> getAllAgreementsForSupplier(int supplierId) throws SQLException {
      if (supplierId < 0) {
         LOGGER.error("Attempted to get agreements for supplier with negative ID: {}", supplierId);
         throw new IllegalArgumentException("Supplier ID cannot be negative");
      }
      LOGGER.info("Retrieving all agreements for supplier with ID: {}", supplierId);
      List<AgreementDTO> agreements = agreementDAO.getAllAgreementsForSupplier(supplierId);
      if (agreements.isEmpty()) {
         LOGGER.warn("No agreements found for supplier with ID: {}", supplierId);
      } else {
         LOGGER.info("Found {} agreements for supplier with ID: {}", agreements.size(), supplierId);
      }
      return agreements;
   }

   @Override
   public List<AgreementDTO> getAllAgreements() throws SQLException {
      LOGGER.info("Retrieving all agreements");
      List<AgreementDTO> agreements = agreementDAO.getAllAgreements();
      if (agreements.isEmpty()) {
         LOGGER.warn("No agreements found");
      } else {
         LOGGER.info("Found {} agreements", agreements.size());
      }
      return agreements;
   }

   @Override
   public void createSupplierProduct(SupplierProductDTO supplierProduct) throws SQLException {
      if (supplierProduct == null) {
         LOGGER.error("Attempted to create a null supplier product");
         throw new IllegalArgumentException("Supplier product cannot be null");
      }
      LOGGER.info("Creating supplier product: {}", supplierProduct);
      supplierProductDAO.createSupplierProduct(supplierProduct);
      LOGGER.info("Supplier product created successfully: {}", supplierProduct);
   }

   @Override
   public Optional<SupplierProductDTO> getSupplierProductById(int supplierId, int productId) throws SQLException {
      if (supplierId < 0 || productId < 0) {
         LOGGER.error("Attempted to get supplier product with negative IDs: supplierId={}, productId={}", supplierId,
               productId);
         throw new IllegalArgumentException("Supplier ID and Product ID cannot be negative");
      }
      LOGGER.info("Retrieving supplier product with supplierId: {} and productId: {}", supplierId, productId);
      Optional<SupplierProductDTO> supplierProduct = supplierProductDAO.getSupplierProductById(supplierId, productId);
      if (supplierProduct.isPresent()) {
         LOGGER.info("Found supplier product: {}", supplierProduct.get());
      } else {
         LOGGER.warn("No supplier product found for supplierId: {} and productId: {}", supplierId, productId);
      }
      return supplierProduct;
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
      supplierProductDAO.updateSupplierProduct(supplierProduct);
      LOGGER.info("Supplier product updated successfully: {}", supplierProduct);
   }

   @Override
   public void deleteSupplierProduct(int supplierId, int productId) throws SQLException {
      if (supplierId < 0 || productId < 0) {
         LOGGER.error("Attempted to delete supplier product with negative IDs: supplierId={}, productId={}", supplierId,
               productId);
         throw new IllegalArgumentException("Supplier ID and Product ID cannot be negative");
      }
      LOGGER.info("Deleting supplier product with supplierId: {} and productId: {}", supplierId, productId);
      supplierProductDAO.deleteSupplierProduct(supplierId, productId);
      LOGGER.info("Supplier product with supplierId: {} and productId: {} deleted successfully", supplierId, productId);
   }

   @Override
   public List<SupplierProductDTO> getAllSupplierProductsById(int supplierId) throws SQLException {
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
   public boolean supplierProductExists(int supplierId, int productId) throws SQLException {
      if (supplierId < 0 || productId < 0) {
         LOGGER.error("Attempted to check existence of supplier product with negative IDs: supplierId={}, productId={}",
               supplierId, productId);
         throw new IllegalArgumentException("Supplier ID and Product ID cannot be negative");
      }
      LOGGER.info("Checking if supplier product exists for supplierId: {} and productId: {}", supplierId, productId);
      boolean exists = supplierProductDAO.supplierProductExists(supplierId, productId);
      LOGGER.info("Supplier product exists for supplierId: {} and productId: {}: {}", supplierId, productId, exists);
      return exists;
   }

   @Override
   public List<SupplierProductDTO> getAllSupplierProducts() throws SQLException {
      LOGGER.info("Retrieving all supplier products");
      List<SupplierProductDTO> supplierProducts = supplierProductDAO.getAllSupplierProducts();
      if (supplierProducts.isEmpty()) {
         LOGGER.warn("No supplier products found");
      } else {
         LOGGER.info("Found {} supplier products", supplierProducts.size());
      }
      return supplierProducts;
   }

   @Override
   public List<Integer> getAllSuppliersForProductId(int productId) throws SQLException {
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
   public List<Integer> getAllProductsForSupplierId(int supplierId) throws SQLException {
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
   public List<CatalogProductDTO> getCatalogProducts() throws SQLException {
      LOGGER.info("Retrieving catalog products");
      List<CatalogProductDTO> catalogProducts = supplierProductDAO.getCatalogProducts();
      if (catalogProducts.isEmpty()) {
         LOGGER.warn("No catalog products found");
      } else {
         LOGGER.info("Found {} catalog products", catalogProducts.size());
      }
      return catalogProducts;
   }
}

/*
 * package Suppliers.DataLayer.util;
 * 
 * import org.slf4j.Logger;
 * import org.slf4j.LoggerFactory;
 * 
 * import java.sql.*;
 * 
 * public final class Database {
 * private static final Logger log = LoggerFactory.getLogger(Database.class);
 * private static final String DB_URL = "jdbc:sqlite:supply.db";
 * private static Connection conn;
 * 
 * static {
 * try {
 * Class.forName("org.sqlite.JDBC");
 * conn = DriverManager.getConnection(DB_URL);
 * log.info("Connected to SQLite at {}", DB_URL);
 * 
 * try (Statement st = conn.createStatement()) {
 * // enforce FK rules in SQLite
 * st.executeUpdate("PRAGMA foreign_keys = ON;");
 * 
 * st.executeUpdate("""
 * CREATE TABLE IF NOT EXISTS suppliers(
 * supplier_id INTEGER PRIMARY KEY AUTOINCREMENT,
 * name TEXT NOT NULL,
 * tax_number TEXT NOT NULL,
 * self_supply INTEGER NOT NULL CHECK(self_supply IN (0,1)),
 * supply_days_mask TEXT NOT NULL
 * CHECK(length(supply_days_mask)=7
 * AND supply_days_mask GLOB '[01]*'),
 * street TEXT NOT NULL,
 * city TEXT NOT NULL,
 * building_number TEXT NOT NULL,
 * bank_account_number TEXT NOT NULL,
 * payment_method TEXT NOT NULL
 * CHECK(payment_method IN
 * ('CASH','CASH_ON_DELIVERY',
 * 'CREDIT_CARD','BANK_TRANSFER')),
 * payment_term TEXT NOT NULL
 * CHECK(payment_term IN ('N30','N60','N90','COD')),
 * created_at TEXT NOT NULL DEFAULT (CURRENT_TIMESTAMP),
 * UNIQUE(name, tax_number)
 * );
 * """);
 * 
 * st.executeUpdate("""
 * CREATE TABLE IF NOT EXISTS contacts(
 * supplier_id INTEGER NOT NULL,
 * phone TEXT NOT NULL,
 * name TEXT NOT NULL,
 * email TEXT NOT NULL,
 * PRIMARY KEY (supplier_id, phone),
 * UNIQUE (supplier_id, email),
 * FOREIGN KEY(supplier_id) REFERENCES suppliers(supplier_id)
 * ON DELETE CASCADE
 * );
 * """);
 * 
 * st.executeUpdate("""
 * CREATE TABLE IF NOT EXISTS supplier_products(
 * product_id INTEGER PRIMARY KEY AUTOINCREMENT,
 * supplier_id INTEGER NOT NULL,
 * supplier_catalog_number TEXT NOT NULL,
 * manufacturer_name TEXT NOT NULL,
 * name TEXT NOT NULL,
 * price REAL NOT NULL CHECK(price >= 0),
 * days_to_expiry INTEGER NOT NULL CHECK(days_to_expiry >= 0),
 * FOREIGN KEY(supplier_id) REFERENCES suppliers(supplier_id)
 * ON DELETE CASCADE
 * );
 * """);
 * st.executeUpdate("""
 * CREATE INDEX IF NOT EXISTS idx_supplier_products_supplier
 * ON supplier_products(supplier_id);
 * """);
 * 
 * st.executeUpdate("""
 * CREATE TABLE IF NOT EXISTS agreements(
 * agreement_id INTEGER PRIMARY KEY AUTOINCREMENT,
 * supplier_id INTEGER NOT NULL,
 * agreement_start_date TEXT NOT NULL,
 * agreement_end_date TEXT NOT NULL,
 * has_fixed_supply_days INTEGER NOT NULL
 * CHECK(has_fixed_supply_days IN (0,1)),
 * valid INTEGER NOT NULL DEFAULT 1
 * CHECK(valid IN (0,1)),
 * CHECK(agreement_end_date >= agreement_start_date),
 * FOREIGN KEY(supplier_id) REFERENCES suppliers(supplier_id)
 * ON DELETE CASCADE
 * );
 * """);
 * 
 * st.executeUpdate("""
 * CREATE TABLE IF NOT EXISTS boq_items(
 * agreement_id INTEGER NOT NULL,
 * line_in_bill INTEGER NOT NULL CHECK(line_in_bill > 0),
 * product_id INTEGER NOT NULL,
 * quantity INTEGER NOT NULL CHECK(quantity > 0),
 * discount_percent REAL NOT NULL
 * CHECK(discount_percent BETWEEN 0 AND 100),
 * PRIMARY KEY (agreement_id, line_in_bill),
 * FOREIGN KEY(agreement_id) REFERENCES agreements(agreement_id)
 * ON DELETE CASCADE,
 * FOREIGN KEY(product_id) REFERENCES supplier_products(product_id)
 * );
 * """);
 * 
 * log.info("Ensured database schema exists");
 * }
 * } catch (Exception e) {
 * log.error("Database initialization failed", e);
 * throw new ExceptionInInitializerError(e);
 * }
 * }
 * 
 * private Database() {}
 * 
 * public static Connection getConnection() throws SQLException {
 * return conn;
 * }
 * }
 */