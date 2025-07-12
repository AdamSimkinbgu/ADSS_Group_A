package DataAccessLayer.SuppliersDAL.DAOs;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import DTOs.SuppliersModuleDTOs.PeriodicOrderDTO;
import DataAccessLayer.SuppliersDAL.Interfaces.PeriodicOrderDAOInterface;
import DataAccessLayer.SuppliersDAL.util.Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.sql.PreparedStatement;

public class JdbcPeriodicOrderDAO extends BaseDAO implements PeriodicOrderDAOInterface {
   private static final Logger LOGGER = LoggerFactory.getLogger(JdbcPeriodicOrderDAO.class);

   @Override
   public PeriodicOrderDTO createPeriodicOrder(PeriodicOrderDTO periodicOrder) {
      if (periodicOrder == null || periodicOrder.getDeliveryDay() == null) {
         LOGGER.error("Attempted to create a null periodic order.");
         throw new IllegalArgumentException("Periodic order cannot be null");
      }
      String sql = "INSERT INTO periodic_orders (delivery_day, is_active) VALUES (?, ?)";
      try (PreparedStatement preparedStatement = Database.getConnection().prepareStatement(sql,
            PreparedStatement.RETURN_GENERATED_KEYS)) {
         preparedStatement.setString(1, periodicOrder.getDeliveryDay().name());
         preparedStatement.setInt(2, 1);

         int rowsAffected = preparedStatement.executeUpdate();
         if (rowsAffected == 0) {
            LOGGER.error("Creating periodic order failed, no rows affected.");
            throw new RuntimeException("Creating periodic order failed, no rows affected.");
         }
         try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
               periodicOrder.setPeriodicOrderID(generatedKeys.getInt(1));
               LOGGER.debug("Periodic order created with ID: {}", periodicOrder.getPeriodicOrderID());
               return periodicOrder;
            } else {
               LOGGER.error("Creating periodic order failed, no ID obtained.");
               throw new RuntimeException("Creating periodic order failed, no ID obtained.");
            }
         }
      } catch (SQLException e) {
         LOGGER.error("Error handling SQL exception: {}", e.getMessage());
         handleSQLException(e);
      }
      return null;
   }

   @Override
   public boolean updatePeriodicOrder(PeriodicOrderDTO periodicOrder) {
      if (periodicOrder == null) {
         LOGGER.error("Attempted to update a null periodic order.");
         throw new IllegalArgumentException("Periodic order cannot be null");
      }
      String sql = "UPDATE periodic_orders SET delivery_day = ?, is_active = ? WHERE periodic_order_id = ?";
      try (PreparedStatement preparedStatement = Database.getConnection()
            .prepareStatement(sql)) {
         preparedStatement.setString(1, periodicOrder.getDeliveryDay().name());
         preparedStatement.setInt(2, 1);
         preparedStatement.setInt(3, periodicOrder.getPeriodicOrderID());

         LOGGER.debug("Updating periodic order: {}", periodicOrder);
         LOGGER.debug("PreparedStatement: {}", preparedStatement);

         int rowsAffected = preparedStatement.executeUpdate();
         if (rowsAffected > 0) {
            LOGGER.debug("Periodic order updated successfully.");
            return true;
         } else {
            LOGGER.debug("No periodic order found with ID: {}", periodicOrder.getPeriodicOrderID());
            return false;
         }
      } catch (SQLException e) {
         LOGGER.error("Error handling SQL exception: {}", e.getMessage());
         handleSQLException(e);
      }
      return false;
   }

   @Override
   public boolean deletePeriodicOrder(int id) {
      if (id <= 0) {
         LOGGER.error("Invalid periodic order ID: {}", id);
         throw new IllegalArgumentException("Periodic order ID must be greater than 0");
      }
      String sql = "DELETE FROM periodic_orders WHERE periodic_order_id = ?";
      try (PreparedStatement preparedStatement = Database.getConnection()
            .prepareStatement(sql)) {
         preparedStatement.setInt(1, id);

         LOGGER.debug("Deleting periodic order with ID: {}", id);
         LOGGER.debug("PreparedStatement: {}", preparedStatement);

         int rowsAffected = preparedStatement.executeUpdate();
         if (rowsAffected > 0) {
            LOGGER.debug("Periodic order deleted successfully.");
            return true;
         } else {
            LOGGER.debug("No periodic order found with ID: {}", id);
            return false;
         }
      } catch (SQLException e) {
         LOGGER.error("Error handling SQL exception: {}", e.getMessage());
         handleSQLException(e);
      }
      return false;
   }

   @Override
   public PeriodicOrderDTO getPeriodicOrder(int id) {
      if (id <= 0) {
         LOGGER.error("Invalid periodic order ID: {}", id);
         throw new IllegalArgumentException("Periodic order ID must be greater than 0");
      }
      String sql = "SELECT * FROM periodic_orders WHERE periodic_order_id = ?";
      try (PreparedStatement preparedStatement = Database.getConnection()
            .prepareStatement(sql)) {
         preparedStatement.setInt(1, id);

         LOGGER.debug("Retrieving periodic order with ID: {}", id);
         LOGGER.debug("PreparedStatement: {}", preparedStatement);

         ResultSet resultSet = preparedStatement.executeQuery();
         if (resultSet.next()) {
            PeriodicOrderDTO periodicOrder = new PeriodicOrderDTO();
            periodicOrder.setPeriodicOrderID(resultSet.getInt("periodic_order_id"));
            periodicOrder.setDeliveryDay(DayOfWeek.valueOf(resultSet.getString("delivery_day").toUpperCase()));
            periodicOrder.setActive(resultSet.getBoolean("is_active"));
            LOGGER.debug("Retrieved periodic order: {}", periodicOrder);
            return periodicOrder;
         } else {
            LOGGER.debug("No periodic order found with ID: {}", id);
            return null;
         }
      } catch (SQLException e) {
         LOGGER.error("Error handling SQL exception: {}", e.getMessage());
         handleSQLException(e);
      }
      return null;
   }

   @Override
   public List<PeriodicOrderDTO> listPeriodicOrders() {
      String sql = "SELECT * FROM periodic_orders";
      try (PreparedStatement preparedStatement = Database.getConnection().prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery()) {
         List<PeriodicOrderDTO> periodicOrders = new ArrayList<>();
         while (resultSet.next()) {
            PeriodicOrderDTO periodicOrder = new PeriodicOrderDTO();
            periodicOrder.setPeriodicOrderID(resultSet.getInt("periodic_order_id"));
            periodicOrder.setDeliveryDay(DayOfWeek.valueOf(resultSet.getString("delivery_day").toUpperCase()));
            periodicOrder.setActive(resultSet.getBoolean("is_active"));
            periodicOrders.add(periodicOrder);
         }
         LOGGER.debug("Listed {} periodic orders", periodicOrders.size());
         return periodicOrders;
      } catch (SQLException e) {
         LOGGER.error("Error handling SQL exception: {}", e.getMessage());
         handleSQLException(e);
      }
      return new ArrayList<>();
   }

}
