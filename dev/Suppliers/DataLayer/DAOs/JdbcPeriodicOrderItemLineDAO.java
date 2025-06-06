package Suppliers.DataLayer.DAOs;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Suppliers.DTOs.PeriodicOrderItemLineDTO;
import Suppliers.DataLayer.Interfaces.PeriodicOrderItemLineDAOInterface;
import Suppliers.DataLayer.util.Database;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcPeriodicOrderItemLineDAO extends BaseDAO implements PeriodicOrderItemLineDAOInterface {
   private static final Logger LOGGER = LoggerFactory.getLogger(JdbcPeriodicOrderItemLineDAO.class);

   @Override
   public PeriodicOrderItemLineDTO addPeriodicOrderItemLine(PeriodicOrderItemLineDTO periodicOrderItemLine) {
      if (periodicOrderItemLine == null) {
         LOGGER.error("Attempted to add a null periodic order item line.");
         throw new IllegalArgumentException("Periodic order item line cannot be null");
      }
      String sql = "INSERT INTO periodic_order_item_lines (periodic_order_id, product_id, quantity) VALUES (?, ?, ?) ON CONFLICT (periodic_order_id, product_id) DO UPDATE SET quantity = EXCLUDED.quantity, unit_price = EXCLUDED.unit_price RETURNING periodic_order_item_line_id";
      try (PreparedStatement preparedStatement = Database.getConnection().prepareStatement(sql,
            PreparedStatement.RETURN_GENERATED_KEYS)) {
         preparedStatement.setInt(1, periodicOrderItemLine.getPeriodicOrderId());
         preparedStatement.setInt(2, periodicOrderItemLine.getProductId());
         preparedStatement.setInt(3, periodicOrderItemLine.getQuantity());

         int affectedRows = preparedStatement.executeUpdate();
         if (affectedRows == 0) {
            LOGGER.error("Creating periodic order item line failed, no rows affected.");
            throw new RuntimeException("Creating periodic order item line failed, no rows affected.");
         }

         try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
               periodicOrderItemLine.setPeriodicOrderItemLineId(generatedKeys.getInt(1));
               LOGGER.info("Periodic order item line created with ID: {}",
                     periodicOrderItemLine.getPeriodicOrderItemLineId());
               return periodicOrderItemLine;
            } else {
               LOGGER.error("Creating periodic order item line failed, no ID obtained.");
               throw new RuntimeException("Creating periodic order item line failed, no ID obtained.");
            }
         }
      } catch (SQLException e) {
         try {
            handleSQLException(e);
         } catch (Exception ex) {
            LOGGER.error("Error handling SQL exception: {}", ex.getMessage());
         }
      }
      return null;
   }

   @Override
   public PeriodicOrderItemLineDTO getPeriodicOrderItemLine(int id) {
      if (id <= 0) {
         LOGGER.error("Invalid periodic order item line ID: {}", id);
         throw new IllegalArgumentException("Periodic order item line ID must be greater than 0");
      }
      String sql = "SELECT * FROM periodic_order_item_lines WHERE periodic_order_item_line_id = ?";
      try (PreparedStatement preparedStatement = Database.getConnection().prepareStatement(sql)) {
         preparedStatement.setInt(1, id);
         ResultSet resultSet = preparedStatement.executeQuery();
         if (resultSet.next()) {
            PeriodicOrderItemLineDTO periodicOrderItemLine = new PeriodicOrderItemLineDTO();
            periodicOrderItemLine.setPeriodicOrderItemLineId(resultSet.getInt("periodic_order_item_line_id"));
            periodicOrderItemLine.setPeriodicOrderId(resultSet.getInt("periodic_order_id"));
            periodicOrderItemLine.setProductId(resultSet.getInt("product_id"));
            periodicOrderItemLine.setQuantity(resultSet.getInt("quantity"));
            return periodicOrderItemLine;
         } else {
            LOGGER.warn("Periodic order item line with ID {} not found.", id);
            return null;
         }
      } catch (SQLException e) {
         try {
            handleSQLException(e);
         } catch (Exception ex) {
            LOGGER.error("Error handling SQL exception: {}", ex.getMessage());
         }
      }
      return null;
   }

   @Override
   public List<PeriodicOrderItemLineDTO> listPeriodicOrderItemLinesByOrderId(int periodicOrderId) {
      if (periodicOrderId <= 0) {
         LOGGER.error("Invalid periodic order ID: {}", periodicOrderId);
         throw new IllegalArgumentException("Periodic order ID must be greater than 0");
      }
      String sql = "SELECT * FROM periodic_order_item_lines WHERE periodic_order_id = ?";
      try (PreparedStatement preparedStatement = Database.getConnection().prepareStatement(sql)) {
         preparedStatement.setInt(1, periodicOrderId);
         ResultSet resultSet = preparedStatement.executeQuery();
         List<PeriodicOrderItemLineDTO> itemLines = new ArrayList<>();
         while (resultSet.next()) {
            PeriodicOrderItemLineDTO itemLine = new PeriodicOrderItemLineDTO();
            itemLine.setPeriodicOrderItemLineId(resultSet.getInt("periodic_order_item_line_id"));
            itemLine.setPeriodicOrderId(resultSet.getInt("periodic_order_id"));
            itemLine.setProductId(resultSet.getInt("product_id"));
            itemLine.setQuantity(resultSet.getInt("quantity"));
            itemLines.add(itemLine);
         }
         return itemLines;
      } catch (SQLException e) {
         try {
            handleSQLException(e);
         } catch (Exception ex) {
            LOGGER.error("Error handling SQL exception: {}", ex.getMessage());
         }
      }
      return new ArrayList<>();
   }

   @Override
   public boolean deletePeriodicOrderItemLine(int id) {
      if (id <= 0) {
         LOGGER.error("Invalid periodic order item line ID: {}", id);
         throw new IllegalArgumentException("Periodic order item line ID must be greater than 0");
      }
      String sql = "DELETE FROM periodic_order_item_lines WHERE periodic_order_item_line_id = ?";
      try (PreparedStatement preparedStatement = Database.getConnection().prepareStatement(sql)) {
         preparedStatement.setInt(1, id);
         int affectedRows = preparedStatement.executeUpdate();
         if (affectedRows == 0) {
            LOGGER.warn("No periodic order item line found with ID: {}", id);
            return false;
         } else {
            LOGGER.info("Periodic order item line with ID {} deleted successfully.", id);
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
   public boolean updatePeriodicOrderItemLine(PeriodicOrderItemLineDTO periodicOrderItemLine) {

      if (periodicOrderItemLine == null || periodicOrderItemLine.getPeriodicOrderItemLineId() <= 0) {
         LOGGER.error("Invalid periodic order item line: {}", periodicOrderItemLine);
         throw new IllegalArgumentException("Periodic order item line cannot be null and must have a valid ID");
      }
      String sql = "UPDATE periodic_order_item_lines SET quantity = ? WHERE periodic_order_id = ? AND line_number = ?";
      try (PreparedStatement preparedStatement = Database.getConnection().prepareStatement(sql)) {
         preparedStatement.setInt(1, periodicOrderItemLine.getQuantity());
         preparedStatement.setInt(2, periodicOrderItemLine.getPeriodicOrderId());
         preparedStatement.setInt(3, periodicOrderItemLine.getPeriodicOrderItemLineId());

         int rowsAffected = preparedStatement.executeUpdate();
         if (rowsAffected > 0) {
            LOGGER.info("Periodic order item line updated successfully.");
            return true;
         } else {
            LOGGER.warn("No periodic order item line found with ID: {}",
                  periodicOrderItemLine.getPeriodicOrderItemLineId());
            return false;
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

}
