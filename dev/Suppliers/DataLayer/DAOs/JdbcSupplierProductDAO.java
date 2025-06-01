package Suppliers.DataLayer.DAOs;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Suppliers.DTOs.CatalogProductDTO;
import Suppliers.DTOs.SupplierProductDTO;
import Suppliers.DataLayer.Interfaces.SupplierProductDAOInterface;
import Suppliers.DataLayer.util.Database;

public class JdbcSupplierProductDAO implements SupplierProductDAOInterface {
   private Logger LOGGER = LoggerFactory.getLogger(JdbcSupplierProductDAO.class);

   @Override
   public SupplierProductDTO createSupplierProduct(SupplierProductDTO supplierProduct) throws SQLException {
      if (supplierProduct == null || supplierProduct.getSupplierId() < 0) {
         LOGGER.error("Invalid supplier product data: {}", supplierProduct);
         throw new IllegalArgumentException("Invalid supplier product data");
      }
      String sql = "INSERT INTO supplier_products (supplier_id, supplier_catalog_number, manufacturer_name, "
            + "name, price, weight, days_to_expiry) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?)";
      try (PreparedStatement pstmt = Database.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
         pstmt.setInt(1, supplierProduct.getSupplierId());
         pstmt.setString(2, supplierProduct.getSupplierCatalogNumber());
         pstmt.setString(3, supplierProduct.getManufacturerName());
         pstmt.setString(4, supplierProduct.getName());
         pstmt.setBigDecimal(5, supplierProduct.getPrice());
         pstmt.setBigDecimal(6, supplierProduct.getWeight());
         pstmt.setInt(7, supplierProduct.getExpiresInDays());

         int affectedRows = pstmt.executeUpdate();
         if (affectedRows == 0) {
            LOGGER.error("Creating supplier product failed, no rows affected.");
            throw new SQLException("Creating supplier product failed, no rows affected.");
         }

         try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
               supplierProduct.setProductId(generatedKeys.getInt(1));
               LOGGER.info("Created supplier product with ID: {}", supplierProduct.getProductId());
            } else {
               LOGGER.error("Creating supplier product failed, no ID obtained.");
               throw new SQLException("Creating supplier product failed, no ID obtained.");
            }
         }
      }
      return supplierProduct;
   }

   @Override
   public Optional<SupplierProductDTO> getSupplierProductById(int supplierId, int productId) throws SQLException {
      if (supplierId < 0 || productId < 0) {
         LOGGER.error("Invalid supplier or product ID: supplierId={}, productId={}", supplierId, productId);
         throw new IllegalArgumentException("Invalid supplier or product ID");
      }
      String sql = "SELECT * FROM supplier_products WHERE supplier_id = ? AND product_id = ?";
      try (PreparedStatement pstmt = Database.getConnection().prepareStatement(sql)) {
         pstmt.setInt(1, supplierId);
         pstmt.setInt(2, productId);
         try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
               SupplierProductDTO supplierProduct = new SupplierProductDTO(
                     rs.getInt("supplier_id"),
                     rs.getInt("product_id"),
                     rs.getString("supplier_catalog_number"),
                     rs.getString("name"),
                     rs.getBigDecimal("price"),
                     rs.getBigDecimal("weight"),
                     rs.getInt("days_to_expiry"),
                     rs.getString("manufacturer_name"));
               return Optional.of(supplierProduct);
            } else {
               LOGGER.info("No supplier product found for supplierId={} and productId={}", supplierId, productId);
               return Optional.empty();
            }
         } catch (SQLException e) {
            LOGGER.error("Error retrieving supplier product: {}", e.getMessage());
            throw e;
         }
      } catch (SQLException e) {
         LOGGER.error("Error preparing statement for supplier product retrieval: {}", e.getMessage());
         throw e;
      }
   }

   @Override
   public void updateSupplierProduct(SupplierProductDTO supplierProduct) throws SQLException {
      if (supplierProduct == null || supplierProduct.getSupplierId() < 0 || supplierProduct.getProductId() < 0) {
         LOGGER.error("Invalid supplier product data: {}", supplierProduct);
         throw new IllegalArgumentException("Invalid supplier product data");
      }
      String sql = "UPDATE supplier_products SET supplier_catalog_number = ?, manufacturer_name = ?, "
            + "name = ?, price = ?, weight = ?, days_to_expiry = ? "
            + "WHERE supplier_id = ? AND product_id = ?";
      try (PreparedStatement pstmt = Database.getConnection().prepareStatement(sql)) {
         pstmt.setString(1, supplierProduct.getSupplierCatalogNumber());
         pstmt.setString(2, supplierProduct.getManufacturerName());
         pstmt.setString(3, supplierProduct.getName());
         pstmt.setBigDecimal(4, supplierProduct.getPrice());
         pstmt.setBigDecimal(5, supplierProduct.getWeight());
         pstmt.setInt(6, supplierProduct.getExpiresInDays());
         pstmt.setInt(7, supplierProduct.getSupplierId());
         pstmt.setInt(8, supplierProduct.getProductId());

         int affectedRows = pstmt.executeUpdate();
         if (affectedRows == 0) {
            LOGGER.error("Updating supplier product failed, no rows affected.");
            throw new SQLException("Updating supplier product failed, no rows affected.");
         }
         LOGGER.info("Updated supplier product with ID: {}", supplierProduct.getProductId());
      } catch (SQLException e) {
         LOGGER.error("Error updating supplier product: {}", e.getMessage());
         throw e;
      }
   }

   @Override
   public void deleteSupplierProduct(int supplierId, int productId) throws SQLException {
      if (supplierId < 0 || productId < 0) {
         LOGGER.error("Invalid supplier or product ID: supplierId={}, productId={}", supplierId, productId);
         throw new IllegalArgumentException("Invalid supplier or product ID");
      }
      String sql = "DELETE FROM supplier_products WHERE supplier_id = ? AND product_id = ?";
      try (PreparedStatement pstmt = Database.getConnection().prepareStatement(sql)) {
         pstmt.setInt(1, supplierId);
         pstmt.setInt(2, productId);
         int affectedRows = pstmt.executeUpdate();
         if (affectedRows == 0) {
            LOGGER.warn("No supplier product found for supplierId={} and productId={}", supplierId, productId);
         } else {
            LOGGER.info("Deleted supplier product with ID: {} for supplier: {}", productId, supplierId);
         }
      } catch (SQLException e) {
         LOGGER.error("Error deleting supplier product: {}", e.getMessage());
         throw e;
      }
   }

   @Override
   public List<SupplierProductDTO> getAllSupplierProducts() throws SQLException {
      String sql = "SELECT * FROM supplier_products";
      try (Statement stmt = Database.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
         List<SupplierProductDTO> supplierProducts = new java.util.ArrayList<>();
         while (rs.next()) {
            SupplierProductDTO supplierProduct = new SupplierProductDTO(
                  rs.getInt("supplier_id"),
                  rs.getInt("product_id"),
                  rs.getString("supplier_catalog_number"),
                  rs.getString("name"),
                  rs.getBigDecimal("price"),
                  rs.getBigDecimal("weight"),
                  rs.getInt("days_to_expiry"),
                  rs.getString("manufacturer_name"));
            supplierProducts.add(supplierProduct);
         }
         return supplierProducts;
      } catch (SQLException e) {
         LOGGER.error("Error retrieving all supplier products: {}", e.getMessage());
         throw e;
      }
   }

   @Override
   public List<SupplierProductDTO> getAllSupplierProductsForSupplier(int supplierId) throws SQLException {
      if (supplierId < 0) {
         LOGGER.error("Invalid supplier ID: {}", supplierId);
         throw new IllegalArgumentException("Invalid supplier ID");
      }
      String sql = "SELECT * FROM supplier_products WHERE supplier_id = ?";
      try (PreparedStatement pstmt = Database.getConnection().prepareStatement(sql)) {
         pstmt.setInt(1, supplierId);
         try (ResultSet rs = pstmt.executeQuery()) {
            List<SupplierProductDTO> supplierProducts = new java.util.ArrayList<>();
            while (rs.next()) {
               SupplierProductDTO supplierProduct = new SupplierProductDTO(
                     rs.getInt("supplier_id"),
                     rs.getInt("product_id"),
                     rs.getString("supplier_catalog_number"),
                     rs.getString("name"),
                     rs.getBigDecimal("price"),
                     rs.getBigDecimal("weight"),
                     rs.getInt("days_to_expiry"),
                     rs.getString("manufacturer_name"));
               supplierProducts.add(supplierProduct);
            }
            return supplierProducts;
         } catch (SQLException e) {
            LOGGER.error("Error retrieving supplier products for supplier ID {}: {}", supplierId, e.getMessage());
            throw e;
         }
      } catch (SQLException e) {
         LOGGER.error("Error preparing statement for retrieving supplier products for supplier ID {}: {}", supplierId,
               e.getMessage());
         throw e;
      }
   }

   @Override
   public List<CatalogProductDTO> getCatalogProducts() throws SQLException {
      // we need unique productID, their names and their manufacturer names
      String sql = "SELECT DISTINCT product_id, name, manufacturer_name FROM supplier_products";
      try (Statement stmt = Database.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
         List<CatalogProductDTO> catalogProducts = new java.util.ArrayList<>();
         while (rs.next()) {
            CatalogProductDTO catalogProduct = new CatalogProductDTO(
                  rs.getInt("product_id"),
                  rs.getString("name"),
                  rs.getString("manufacturer_name"));
            catalogProducts.add(catalogProduct);
         }
         LOGGER.info("Retrieved {} catalog products", catalogProducts.size());
         return catalogProducts;
      } catch (SQLException e) {
         LOGGER.error("Error retrieving catalog products: {}", e.getMessage());
         throw e;
      }
   }

   @Override
   public boolean supplierProductExists(int supplierId, int productId) throws SQLException {
      if (supplierId < 0 || productId < 0) {
         LOGGER.error("Invalid supplier or product ID: supplierId={}, productId={}", supplierId, productId);
         throw new IllegalArgumentException("Invalid supplier or product ID");
      }
      String sql = "SELECT 1 FROM supplier_products WHERE supplier_id = ? AND product_id = ?";
      try (PreparedStatement pstmt = Database.getConnection().prepareStatement(sql)) {
         pstmt.setInt(1, supplierId);
         pstmt.setInt(2, productId);
         try (ResultSet rs = pstmt.executeQuery()) {
            boolean exists = rs.next();
            LOGGER.info("Supplier product exists: {}", exists);
            return exists;
         }
      } catch (SQLException e) {
         LOGGER.error("Error checking if supplier product exists: {}", e.getMessage());
         throw e;
      }
   }

   @Override
   public List<Integer> getAllSupplierIdsForProductId(int productId) throws SQLException {
      if (productId < 0) {
         LOGGER.error("Invalid product ID: {}", productId);
         throw new IllegalArgumentException("Invalid product ID");
      }
      String sql = "SELECT DISTINCT supplier_id FROM supplier_products WHERE product_id = ?";
      try (PreparedStatement pstmt = Database.getConnection().prepareStatement(sql)) {
         pstmt.setInt(1, productId);
         try (ResultSet rs = pstmt.executeQuery()) {
            List<Integer> supplierIds = new java.util.ArrayList<>();
            while (rs.next()) {
               supplierIds.add(rs.getInt("supplier_id"));
            }
            LOGGER.info("Retrieved {} supplier IDs for product ID: {}", supplierIds.size(), productId);
            return supplierIds;
         } catch (SQLException e) {
            LOGGER.error("Error retrieving supplier IDs for product ID {}: {}", productId, e.getMessage());
            throw e;
         }
      } catch (SQLException e) {
         LOGGER.error("Error preparing statement for retrieving supplier IDs for product ID {}: {}", productId,
               e.getMessage());
         throw e;
      }
   }

   @Override
   public List<Integer> getAllProductIdsForSupplierId(int supplierId) throws SQLException {
      if (supplierId < 0) {
         LOGGER.error("Invalid supplier ID: {}", supplierId);
         throw new IllegalArgumentException("Invalid supplier ID");
      }
      String sql = "SELECT DISTINCT product_id FROM supplier_products WHERE supplier_id = ?";
      try (PreparedStatement pstmt = Database.getConnection().prepareStatement(sql)) {
         pstmt.setInt(1, supplierId);
         try (ResultSet rs = pstmt.executeQuery()) {
            List<Integer> productIds = new java.util.ArrayList<>();
            while (rs.next()) {
               productIds.add(rs.getInt("product_id"));
            }
            LOGGER.info("Retrieved {} product IDs for supplier ID: {}", productIds.size(), supplierId);
            return productIds;
         } catch (SQLException e) {
            LOGGER.error("Error retrieving product IDs for supplier ID {}: {}", supplierId, e.getMessage());
            throw e;
         }
      } catch (SQLException e) {
         LOGGER.error("Error preparing statement for retrieving product IDs for supplier ID {}: {}", supplierId,
               e.getMessage());
         throw e;
      }
   }

}