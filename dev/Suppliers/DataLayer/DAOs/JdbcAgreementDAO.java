package Suppliers.DataLayer.DAOs;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Suppliers.DTOs.AgreementDTO;
import Suppliers.DTOs.BillofQuantitiesItemDTO;
import Suppliers.DataLayer.Interfaces.AgreementDAOInterface;
import Suppliers.DataLayer.util.Database;

public class JdbcAgreementDAO extends BaseDAO implements AgreementDAOInterface {
   private final Logger LOGGER = LoggerFactory.getLogger(JdbcAgreementDAO.class);
   private final static JdbcBillofQuantitiesItemsDAO billofQuantitiesItemsDAO = new JdbcBillofQuantitiesItemsDAO();

   @Override
   public AgreementDTO createAgreement(AgreementDTO agreement) {
      if (agreement == null || agreement.getSupplierId() < 0) {
         LOGGER.error("Invalid agreement data: {}", agreement);
         throw new IllegalArgumentException("Invalid agreement data");
      }
      String sql = "INSERT INTO agreements (supplier_id, agreement_start_date, "
            + "agreement_end_date, valid) VALUES (?, ?, ?, ?)";
      try (PreparedStatement pstmt = Database.getConnection().prepareStatement(sql,
            Statement.RETURN_GENERATED_KEYS)) {
         pstmt.setInt(1, agreement.getSupplierId());
         pstmt.setString(2, Date.valueOf(agreement.getAgreementStartDate()).toString());
         pstmt.setString(3, Date.valueOf(agreement.getAgreementEndDate()).toString());
         pstmt.setBoolean(4, agreement.isValid());

         int affectedRows = pstmt.executeUpdate();
         if (affectedRows == 0) {
            LOGGER.error("Creating agreement failed, no rows affected.");
            throw new SQLException("Creating agreement failed, no rows affected.");
         }

         try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
               int id = generatedKeys.getInt(1);
               agreement.setAgreementId(id);
            } else {
               LOGGER.error("Creating agreement failed, no ID obtained.");
               throw new SQLException("Creating agreement failed, no ID obtained.");
            }
         }

         for (BillofQuantitiesItemDTO item : agreement.getBillOfQuantitiesItems()) {
            item.setAgreementId(agreement.getAgreementId());
            billofQuantitiesItemsDAO.createBillofQuantitiesItem(item);
         }
         LOGGER.debug("Inserted {} bill of quantities items for agreement ID: {}",
               agreement.getBillOfQuantitiesItems().size(), agreement.getAgreementId());

      } catch (SQLException e) {
         LOGGER.error("Error handling SQL exception: {}", e.getMessage());
         handleSQLException(e);
      }
      LOGGER.info("Created agreement with ID: {}", agreement.getAgreementId());
      return agreement;
   }

   @Override
   public Optional<AgreementDTO> getAgreementById(int id) {
      if (id < 0) {
         LOGGER.error("Invalid agreement ID: {}", id);
         throw new IllegalArgumentException("Invalid agreement ID");
      }
      String sql = "SELECT * FROM agreements WHERE agreement_id = ?";
      try (PreparedStatement pstmt = Database.getConnection().prepareStatement(sql)) {
         pstmt.setInt(1, id);
         try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
               AgreementDTO agreement = new AgreementDTO();
               agreement.setAgreementId(rs.getInt("agreement_id"));
               agreement.setSupplierId(rs.getInt("supplier_id"));
               agreement.setAgreementStartDate(LocalDate.parse(rs.getString("agreement_start_date")));
               agreement.setAgreementEndDate(LocalDate.parse(rs.getString("agreement_end_date")));
               agreement.setValid(rs.getBoolean("valid"));

               List<BillofQuantitiesItemDTO> items = billofQuantitiesItemsDAO
                     .getAllBillofQantitiesItemsForAgreementId(id);
               for (BillofQuantitiesItemDTO item : items) {
                  item.setAgreementId(id);
                  String productCatalogSql = "SELECT name FROM supplier_products WHERE product_id = ?";
                  try (PreparedStatement productPstmt = Database.getConnection()
                        .prepareStatement(productCatalogSql)) {
                     productPstmt.setInt(1, item.getProductId());
                     try (ResultSet productRs = productPstmt.executeQuery()) {
                        if (productRs.next()) {
                           item.setProductName(productRs.getString("name"));
                        } else {
                           LOGGER.warn("No product found for item with ID: {}", item.getProductId());
                        }
                     }
                  } catch (SQLException e) {
                     LOGGER.error("Error retrieving product details for item ID: {} - {}", item.getProductId(),
                           e.getMessage());
                  }
               }
               agreement.setBillOfQuantitiesItems(items);
               String supplierSql = "SELECT name FROM suppliers WHERE supplier_id = ?";
               try (PreparedStatement supplierPstmt = Database.getConnection()
                     .prepareStatement(supplierSql)) {
                  supplierPstmt.setInt(1, agreement.getSupplierId());
                  try (ResultSet supplierRs = supplierPstmt.executeQuery()) {
                     if (supplierRs.next()) {
                        agreement.setSupplierName(supplierRs.getString("name"));
                     } else {
                        LOGGER.warn("No supplier found with ID: {}", agreement.getSupplierId());
                     }
                  }
               } catch (SQLException e) {
                  LOGGER.error("Error retrieving supplier details for ID: {} - {}", agreement.getSupplierId(),
                        e.getMessage());
               }
               LOGGER.info("Retrieved agreement with ID: {}", id);
               return Optional.of(agreement);
            } else {
               LOGGER.warn("No agreement found with ID: {}", id);
               return Optional.empty();
            }
         }
      } catch (SQLException e) {
         LOGGER.error("Error handling SQL exception: {}", e.getMessage());
         handleSQLException(e);
      }
      return Optional.empty();
   }

   @Override
   public boolean updateAgreement(AgreementDTO agreement) {
      if (agreement == null || agreement.getAgreementId() < 0) {
         LOGGER.error("Invalid agreement data: {}", agreement);
         throw new IllegalArgumentException("Invalid agreement data");
      }
      String sql = "UPDATE agreements SET supplier_id = ?, agreement_start_date = ?, "
            + "agreement_end_date = ?, valid = ? WHERE agreement_id = ?";
      try (PreparedStatement pstmt = Database.getConnection().prepareStatement(sql)) {
         pstmt.setInt(1, agreement.getSupplierId());
         pstmt.setString(2, Date.valueOf(agreement.getAgreementStartDate()).toString());
         pstmt.setString(3, Date.valueOf(agreement.getAgreementEndDate()).toString());
         pstmt.setBoolean(4, agreement.isValid());
         pstmt.setInt(5, agreement.getAgreementId());

         int affectedRows = pstmt.executeUpdate();
         if (affectedRows == 0) {
            LOGGER.warn("No agreement found with ID: {}", agreement.getAgreementId());
            return false;
         } else {
            for (BillofQuantitiesItemDTO item : agreement.getBillOfQuantitiesItems()) {
               item.setAgreementId(agreement.getAgreementId());
               billofQuantitiesItemsDAO.updateBillofQuantitiesItem(item);
            }
            LOGGER.info("Updated agreement with ID: {}", agreement.getAgreementId());
            return true;
         }

      } catch (SQLException e) {
         LOGGER.error("Error handling SQL exception: {}", e.getMessage());
         handleSQLException(e);
      }
      return false;
   }

   @Override
   public boolean deleteAgreement(int id) {
      if (id < 0) {
         LOGGER.error("Invalid agreement ID: {}", id);
         throw new IllegalArgumentException("Invalid agreement ID");
      }
      String sql = "DELETE FROM agreements WHERE agreement_id = ?";
      try (PreparedStatement pstmt = Database.getConnection().prepareStatement(sql)) {
         pstmt.setInt(1, id);
         int affectedRows = pstmt.executeUpdate();
         if (affectedRows == 0) {
            LOGGER.warn("No agreement found with ID: {}", id);
            return false;
         } else {
            LOGGER.info("Deleted agreement with ID: {}", id);
            return true;
         }
      } catch (SQLException e) {
         try {
            handleSQLException(e);
         } catch (Exception ex) {
            LOGGER.error("Error handling SQL exception: {}", ex.getMessage());
         }
      }
      return false;
   }

   @Override
   public List<AgreementDTO> getAllAgreements() {
      String sql = "SELECT agreement_id FROM agreements";
      try (PreparedStatement pstmt = Database.getConnection().prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()) {
         List<AgreementDTO> agreements = new ArrayList<>();
         while (rs.next()) {
            int agreementId = rs.getInt("agreement_id");
            Optional<AgreementDTO> agreementOpt = getAgreementById(agreementId);
            agreementOpt.ifPresent(agreements::add);
         }
         LOGGER.info("Retrieved {} agreements", agreements.size());
         return agreements;
      } catch (SQLException e) {
         LOGGER.error("Error handling SQL exception: {}", e.getMessage());
         handleSQLException(e);
      }
      return new ArrayList<>();
   }

   @Override
   public List<AgreementDTO> getAllAgreementsForSupplier(int supplierId) {
      if (supplierId < 0) {
         LOGGER.error("Invalid supplier ID: {}", supplierId);
         throw new IllegalArgumentException("Invalid supplier ID");
      }
      String sql = "SELECT agreement_id FROM agreements WHERE supplier_id = ?";
      try (PreparedStatement pstmt = Database.getConnection().prepareStatement(sql)) {
         pstmt.setInt(1, supplierId);
         try (ResultSet rs = pstmt.executeQuery()) {
            List<AgreementDTO> agreements = new ArrayList<>();
            while (rs.next()) {
               int agreementId = rs.getInt("agreement_id");
               Optional<AgreementDTO> agreementOpt = getAgreementById(agreementId);
               agreementOpt.ifPresent(agreements::add);
            }
            LOGGER.info("Retrieved {} agreements for supplier ID: {}", agreements.size(), supplierId);
            return agreements;
         }
      } catch (SQLException e) {
         LOGGER.error("Error handling SQL exception: {}", e.getMessage());
         handleSQLException(e);
      }
      return new ArrayList<>();
   }

   @Override
   public boolean agreementExists(int agreementId) {
      if (agreementId < 0) {
         LOGGER.error("Invalid agreement ID: {}", agreementId);
         throw new IllegalArgumentException("Invalid agreement ID");
      }
      String sql = "SELECT 1 FROM agreements WHERE agreement_id = ?";
      try (PreparedStatement pstmt = Database.getConnection().prepareStatement(sql)) {
         pstmt.setInt(1, agreementId);
         try (ResultSet rs = pstmt.executeQuery()) {
            boolean exists = rs.next();
            LOGGER.info("Agreement with ID: {} exists: {}", agreementId, exists);
            return exists;
         }
      } catch (SQLException e) {
         LOGGER.error("Error handling SQL exception: {}", e.getMessage());
         handleSQLException(e);
      }
      return false;
   }

   @Override
   public List<BillofQuantitiesItemDTO> getBillOfQuantitiesItemsForAgreement(int agreementId) {
      if (agreementId < 0) {
         LOGGER.error("Invalid agreement ID: {}", agreementId);
         throw new IllegalArgumentException("Invalid agreement ID");
      }
      return billofQuantitiesItemsDAO.getAllBillofQantitiesItemsForAgreementId(agreementId);
   }

   @Override
   public List<BillofQuantitiesItemDTO> getBillOfQuantitiesItemsByProductId(int productId) {
      if (productId < 0) {
         LOGGER.error("Invalid product ID: {}", productId);
         throw new IllegalArgumentException("Invalid product ID");
      }
      String sql = "SELECT * FROM bill_of_quantities_items WHERE product_id = ?";
      try (PreparedStatement pstmt = Database.getConnection().prepareStatement(sql)) {
         pstmt.setInt(1, productId);
         try (ResultSet rs = pstmt.executeQuery()) {
            List<BillofQuantitiesItemDTO> items = new ArrayList<>();
            while (rs.next()) {
               BillofQuantitiesItemDTO item = new BillofQuantitiesItemDTO();
               item.setProductId(rs.getInt("product_id"));
               item.setLineInBillID(rs.getInt("line_in_bill"));
               item.setAgreementId(rs.getInt("agreement_id"));
               item.setProductId(rs.getInt("product_id"));
               item.setQuantity(rs.getInt("quantity"));
               items.add(item);
            }
            LOGGER.info("Retrieved {} bill of quantities items for product ID: {}", items.size(), productId);
            return items;
         }
      } catch (SQLException e) {
         LOGGER.error("Error handling SQL exception: {}", e.getMessage());
         handleSQLException(e);
      }
      return new ArrayList<>();
   }
}