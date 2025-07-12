package DataAccessLayer.SuppliersDAL.DAOs;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import DTOs.SuppliersModuleDTOs.BillofQuantitiesItemDTO;
import DataAccessLayer.SuppliersDAL.Interfaces.BillofQuantitiesItemDAOInterface;
import DataAccessLayer.SuppliersDAL.util.Database;

public class JdbcBillofQuantitiesItemsDAO extends BaseDAO implements BillofQuantitiesItemDAOInterface {
   private static Logger LOGGER = LoggerFactory.getLogger(JdbcBillofQuantitiesItemsDAO.class);

   @Override
   public BillofQuantitiesItemDTO createBillofQuantitiesItem(BillofQuantitiesItemDTO item) {
      if (item == null || item.getAgreementId() < 0 || item.getProductId() < 0) {
         LOGGER.error("Invalid Bill of Quantities Item data: {}", item);
         throw new IllegalArgumentException("Invalid Bill of Quantities Item data");
      }
      String sql = "INSERT INTO boq_items (agreement_id, product_id, quantity, discount_percent) "
            + "VALUES (?, ?, ?, ?)";
      try (PreparedStatement preparedStatement = Database.getConnection().prepareStatement(sql,
            PreparedStatement.RETURN_GENERATED_KEYS)) {
         preparedStatement.setInt(1, item.getAgreementId());
         preparedStatement.setInt(2, item.getProductId());
         preparedStatement.setInt(3, item.getQuantity());
         preparedStatement.setBigDecimal(4, item.getDiscountPercent());
         int affectedRows = preparedStatement.executeUpdate();
         if (affectedRows == 0) {
            LOGGER.error("Creating Bill of Quantities Item failed, no rows affected.");
            throw new SQLException("Creating Bill of Quantities Item failed, no rows affected.");
         }
         try (var generatedKeys = preparedStatement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
               item.setLineInBillID(generatedKeys.getInt(1));
               LOGGER.debug("Created Bill of Quantities Item with ID: {}", item.getLineInBillID());
            } else {
               LOGGER.error("Creating Bill of Quantities Item failed, no ID obtained.");
               throw new SQLException("Creating Bill of Quantities Item failed, no ID obtained.");
            }
         }
      } catch (SQLException e) {
         LOGGER.error("Error handling SQL exception: {}", e.getMessage());
         handleSQLException(e);
      }
      return item;
   }

   @Override
   public boolean updateBillofQuantitiesItem(BillofQuantitiesItemDTO item) {
      if (item == null || item.getLineInBillID() < 0 || item.getAgreementId() < 0) {
         LOGGER.error("Invalid Bill of Quantities Item data for update: {}", item);
         throw new IllegalArgumentException("Invalid Bill of Quantities Item data for update");
      }
      String sql = "UPDATE boq_items SET quantity = ?, discount_percent = ? "
            + "WHERE agreement_id = ? AND line_in_bill = ?";
      try (PreparedStatement preparedStatement = Database.getConnection()
            .prepareStatement(sql)) {
         preparedStatement.setInt(1, item.getQuantity());
         preparedStatement.setBigDecimal(2, item.getDiscountPercent());
         preparedStatement.setInt(3, item.getAgreementId());
         preparedStatement.setInt(4, item.getLineInBillID());
         int affectedRows = preparedStatement.executeUpdate();
         if (affectedRows == 0) {
            LOGGER.error("Updating Bill of Quantities Item failed, no rows affected.");
            return false;
         }
         LOGGER.debug("Updated Bill of Quantities Item with ID: {}", item.getLineInBillID());
         return true;
      } catch (SQLException e) {
         LOGGER.error("Error handling SQL exception: {}", e.getMessage());
         handleSQLException(e);
      }
      return false;
   }

   @Override
   public boolean deleteBillofQuantitiesItem(int agreementId, int lineId) {
      if (agreementId < 0 || lineId < 0) {
         LOGGER.error("Invalid Bill of Quantities Item ID for deletion: agreementId={}, itemId={}", agreementId,
               lineId);
         throw new IllegalArgumentException("Invalid Bill of Quantities Item ID for deletion");
      }
      String sql = "DELETE FROM boq_items WHERE agreement_id = ? AND line_in_bill = ?";
      try (PreparedStatement preparedStatement = Database.getConnection()
            .prepareStatement(sql)) {
         preparedStatement.setInt(1, agreementId);
         preparedStatement.setInt(2, lineId);
         int affectedRows = preparedStatement.executeUpdate();
         if (affectedRows == 0) {
            LOGGER.error("Deleting Bill of Quantities Item failed, no rows affected.");
            return false;
         }
         LOGGER.debug("Deleted Bill of Quantities Item with ID: {}", lineId);
         return true;
      } catch (SQLException e) {
         LOGGER.error("Error handling SQL exception: {}", e.getMessage());
         handleSQLException(e);
      }
      return false;
   }

   @Override
   public List<BillofQuantitiesItemDTO> getBillofQuantitiesItemsById(int lineId) {
      if (lineId < 0) {
         LOGGER.error("Invalid product ID: {}", lineId);
         throw new IllegalArgumentException("Invalid product ID");
      }
      String sql = "SELECT * FROM boq_items WHERE line_in_bill = ?";
      try (PreparedStatement preparedStatement = Database.getConnection()
            .prepareStatement(sql)) {
         preparedStatement.setInt(1, lineId);
         try (ResultSet resultSet = preparedStatement.executeQuery()) {
            List<BillofQuantitiesItemDTO> items = new ArrayList<>();
            while (resultSet.next()) {
               BillofQuantitiesItemDTO item = new BillofQuantitiesItemDTO();
               item.setAgreementId(resultSet.getInt("agreement_id"));
               item.setLineInBillID(resultSet.getInt("line_in_bill"));
               item.setProductId(resultSet.getInt("product_id"));
               item.setQuantity(resultSet.getInt("quantity"));
               item.setDiscountPercent(resultSet.getBigDecimal("discount_percent"));
               items.add(item);
            }
            LOGGER.debug("Retrieved Bill of Quantities Items for Line ID: {}", lineId);
            return items;
         }
      } catch (SQLException e) {
         LOGGER.error("Error handling SQL exception: {}", e.getMessage());
         handleSQLException(e);
      }
      return new ArrayList<>();
   }

   @Override
   public List<BillofQuantitiesItemDTO> getAllBillofQantitiesItemsForAgreementId(int agreementId) {
      if (agreementId < 0) {
         LOGGER.error("Invalid Agreement ID: {}", agreementId);
         throw new IllegalArgumentException("Invalid Agreement ID");
      }
      String sql = "SELECT * FROM boq_items WHERE agreement_id = ?";
      try (PreparedStatement preparedStatement = Database.getConnection()
            .prepareStatement(sql)) {
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
            LOGGER.debug("Retrieved all Bill of Quantities Items for Agreement ID: {}", agreementId);
            return items;
         }
      } catch (SQLException e) {
         LOGGER.error("Error handling SQL exception: {}", e.getMessage());
         handleSQLException(e);
      }
      return new ArrayList<>();
   }

   public boolean deleteAllBillofQuantitiesItems(int agreementId) {
      if (agreementId < 0) {
         LOGGER.error("Invalid Agreement ID for deletion: {}", agreementId);
         throw new IllegalArgumentException("Invalid Agreement ID for deletion");
      }
      String sql = "DELETE FROM boq_items WHERE agreement_id = ?";
      try (PreparedStatement preparedStatement = Database.getConnection()
            .prepareStatement(sql)) {
         preparedStatement.setInt(1, agreementId);
         int affectedRows = preparedStatement.executeUpdate();
         if (affectedRows == 0) {
            LOGGER.debug("No Bill of Quantities Items found for Agreement ID: {}", agreementId);
            return false;
         } else {
            LOGGER.debug("Deleted all Bill of Quantities Items for Agreement ID: {}", agreementId);
            return true;
         }
      } catch (SQLException e) {
         LOGGER.error("Error handling SQL exception: {}", e.getMessage());
         handleSQLException(e);
      }
      return false;
   }
}