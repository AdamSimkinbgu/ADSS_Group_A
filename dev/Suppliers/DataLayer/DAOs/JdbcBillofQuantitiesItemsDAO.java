package Suppliers.DataLayer.DAOs;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Suppliers.DTOs.BillofQuantitiesItemDTO;
import Suppliers.DataLayer.Interfaces.BillofQuantitiesItemDAOInterface;
import Suppliers.DataLayer.util.Database;

public class JdbcBillofQuantitiesItemsDAO implements BillofQuantitiesItemDAOInterface {
   private static Logger LOGGER = LoggerFactory.getLogger(JdbcBillofQuantitiesItemsDAO.class);

   @Override
   public BillofQuantitiesItemDTO createBillofQuantitiesItem(BillofQuantitiesItemDTO item) throws SQLException {
      if (item == null || item.getAgreementId() < 0 || item.getProductId() < 0) {
         LOGGER.error("Invalid Bill of Quantities Item data: {}", item);
         throw new IllegalArgumentException("Invalid Bill of Quantities Item data");
      }
      String sql = "INSERT INTO boq_items (agreement_id, line_in_bill, product_id, quantity, discount_percent) "
            + "VALUES (?, ?, ?, ?, ?)";
      try (PreparedStatement preparedStatement = Database.getConnection().prepareStatement(sql,
            PreparedStatement.RETURN_GENERATED_KEYS)) {
         preparedStatement.setInt(1, item.getAgreementId());
         preparedStatement.setInt(2, item.getLineInBillID());
         preparedStatement.setInt(3, item.getProductId());
         preparedStatement.setInt(4, item.getQuantity());
         preparedStatement.setBigDecimal(5, item.getDiscountPercent());
         int affectedRows = preparedStatement.executeUpdate();
         if (affectedRows == 0) {
            LOGGER.error("Creating Bill of Quantities Item failed, no rows affected.");
            throw new SQLException("Creating Bill of Quantities Item failed, no rows affected.");
         }
         try (var generatedKeys = preparedStatement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
               item.setLineInBillID(generatedKeys.getInt(1));
               LOGGER.info("Created Bill of Quantities Item with ID: {}", item.getLineInBillID());
            } else {
               LOGGER.error("Creating Bill of Quantities Item failed, no ID obtained.");
               throw new SQLException("Creating Bill of Quantities Item failed, no ID obtained.");
            }
         }
      }
      return item;
   }

   @Override
   public void updateBillofQuantitiesItem(BillofQuantitiesItemDTO item) throws SQLException {
      if (item == null || item.getLineInBillID() < 0 || item.getAgreementId() < 0) {
         LOGGER.error("Invalid Bill of Quantities Item data for update: {}", item);
         throw new IllegalArgumentException("Invalid Bill of Quantities Item data for update");
      }
      String sql = "UPDATE boq_items SET quantity = ?, discount_percent = ? "
            + "WHERE agreement_id = ? AND line_in_bill = ?";
      try (PreparedStatement preparedStatement = Database.getConnection().prepareStatement(sql)) {
         preparedStatement.setInt(1, item.getQuantity());
         preparedStatement.setBigDecimal(2, item.getDiscountPercent());
         preparedStatement.setInt(3, item.getAgreementId());
         preparedStatement.setInt(4, item.getLineInBillID());
         int affectedRows = preparedStatement.executeUpdate();
         if (affectedRows == 0) {
            LOGGER.error("Updating Bill of Quantities Item failed, no rows affected.");
            throw new SQLException("Updating Bill of Quantities Item failed, no rows affected.");
         }
         LOGGER.info("Updated Bill of Quantities Item with ID: {}", item.getLineInBillID());
      }
   }

   @Override
   public void deleteBillofQuantitiesItem(int agreementId, int lineId) throws SQLException {
      if (agreementId < 0 || lineId < 0) {
         LOGGER.error("Invalid Bill of Quantities Item ID for deletion: agreementId={}, itemId={}", agreementId,
               lineId);
         throw new IllegalArgumentException("Invalid Bill of Quantities Item ID for deletion");
      }
      String sql = "DELETE FROM boq_items WHERE agreement_id = ? AND line_in_bill = ?";
      try (PreparedStatement preparedStatement = Database.getConnection().prepareStatement(sql)) {
         preparedStatement.setInt(1, agreementId);
         preparedStatement.setInt(2, lineId);
         int affectedRows = preparedStatement.executeUpdate();
         if (affectedRows == 0) {
            LOGGER.error("Deleting Bill of Quantities Item failed, no rows affected.");
            throw new SQLException("Deleting Bill of Quantities Item failed, no rows affected.");
         }
         LOGGER.info("Deleted Bill of Quantities Item with ID: {}", lineId);
      }
   }

   @Override
   public BillofQuantitiesItemDTO getBillofQuantitiesItemById(int agreementId, int lineId) throws SQLException {
      if (agreementId < 0 || lineId < 0) {
         LOGGER.error("Invalid Bill of Quantities Item ID: agreementId={}, itemId={}", agreementId, lineId);
         throw new IllegalArgumentException("Invalid Bill of Quantities Item ID");
      }
      String sql = "SELECT * FROM boq_items WHERE agreement_id = ? AND line_in_bill = ?";
      try (PreparedStatement preparedStatement = Database.getConnection().prepareStatement(sql)) {
         preparedStatement.setInt(1, agreementId);
         preparedStatement.setInt(2, lineId);
         try (var resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
               BillofQuantitiesItemDTO item = new BillofQuantitiesItemDTO();
               item.setAgreementId(resultSet.getInt("agreement_id"));
               item.setLineInBillID(resultSet.getInt("line_in_bill"));
               item.setProductId(resultSet.getInt("product_id"));
               item.setQuantity(resultSet.getInt("quantity"));
               item.setDiscountPercent(resultSet.getBigDecimal("discount_percent"));
               LOGGER.info("Retrieved Bill of Quantities Item with ID: {}", lineId);
               return item;
            } else {
               LOGGER.warn("No Bill of Quantities Item found with ID: {}", lineId);
               return null;
            }
         }
      }
   }

   @Override
   public List<BillofQuantitiesItemDTO> getAllBillofQuantitiesItems(int agreementId) throws SQLException {
      if (agreementId < 0) {
         LOGGER.error("Invalid Agreement ID: {}", agreementId);
         throw new IllegalArgumentException("Invalid Agreement ID");
      }
      String sql = "SELECT * FROM boq_items WHERE agreement_id = ?";
      try (PreparedStatement preparedStatement = Database.getConnection().prepareStatement(sql)) {
         preparedStatement.setInt(1, agreementId);
         try (var resultSet = preparedStatement.executeQuery()) {
            List<BillofQuantitiesItemDTO> items = new java.util.ArrayList<>();
            while (resultSet.next()) {
               BillofQuantitiesItemDTO item = new BillofQuantitiesItemDTO();
               item.setAgreementId(resultSet.getInt("agreement_id"));
               item.setLineInBillID(resultSet.getInt("line_in_bill"));
               item.setProductId(resultSet.getInt("product_id"));
               item.setQuantity(resultSet.getInt("quantity"));
               item.setDiscountPercent(resultSet.getBigDecimal("discount_percent"));
               items.add(item);
            }
            LOGGER.info("Retrieved all Bill of Quantities Items for Agreement ID: {}", agreementId);
            return items;
         }
      }
   }

   public void deleteAllBillofQuantitiesItems(int agreementId) throws SQLException {
      if (agreementId < 0) {
         LOGGER.error("Invalid Agreement ID for deletion: {}", agreementId);
         throw new IllegalArgumentException("Invalid Agreement ID for deletion");
      }
      String sql = "DELETE FROM boq_items WHERE agreement_id = ?";
      try (PreparedStatement preparedStatement = Database.getConnection().prepareStatement(sql)) {
         preparedStatement.setInt(1, agreementId);
         int affectedRows = preparedStatement.executeUpdate();
         if (affectedRows == 0) {
            LOGGER.warn("No Bill of Quantities Items found for Agreement ID: {}", agreementId);
         } else {
            LOGGER.info("Deleted all Bill of Quantities Items for Agreement ID: {}", agreementId);
         }
      }
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