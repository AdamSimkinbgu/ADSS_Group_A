package Suppliers.DataLayer.DAOs;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Suppliers.DTOs.OrderDTO;
import Suppliers.DTOs.OrderItemLineDTO;
import Suppliers.DTOs.Enums.OrderStatus;
import Suppliers.DataLayer.Interfaces.OrderDAOInterface;
import Suppliers.DataLayer.Interfaces.OrderItemLineDAOInterface;
import Suppliers.DataLayer.util.Database;

public class JdbcOrderDAO extends BaseDAO implements OrderDAOInterface {
   private static final Logger LOGGER = LoggerFactory.getLogger(JdbcOrderDAO.class);

   @Override
   public OrderDTO addOrder(OrderDTO orderDTO) {
      if (orderDTO == null) {
         LOGGER.error("Attempted to add a null order.");
         throw new IllegalArgumentException("Order cannot be null");
      }
      String sql = "INSERT INTO orders (supplier_id, order_date, creation_date, status) VALUES (?, ?, ?, ?)";
      try (PreparedStatement preparedStatement = Database.getConnection().prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
         preparedStatement.setInt(1, orderDTO.getSupplierId());
         preparedStatement.setString(2, orderDTO.getOrderDate().toString());
         preparedStatement.setString(3, LocalDate.now().toString());
         preparedStatement.setString(4, orderDTO.getStatus().name());

         int affectedRows = preparedStatement.executeUpdate();
         if (affectedRows == 0) {
            LOGGER.error("Creating order failed, no rows affected.");
            throw new RuntimeException("Creating order failed, no rows affected.");
         }
         try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
               orderDTO.setOrderId(generatedKeys.getInt(1));
               LOGGER.info("Order created with ID: {}", orderDTO.getOrderId());
               return orderDTO;
            } else {
               LOGGER.error("Creating order failed, no ID obtained.");
               throw new RuntimeException("Creating order failed, no ID obtained.");
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
   public Optional<OrderDTO> getOrderByID(int orderID) {
      if (orderID <= 0) {
         LOGGER.error("Invalid order ID: {}", orderID);
         throw new IllegalArgumentException("Order ID must be greater than 0");
      }
      String sql = "SELECT * FROM orders WHERE order_id = ?";
      try (PreparedStatement preparedStatement = Database.getConnection().prepareStatement(sql)) {
         preparedStatement.setInt(1, orderID);
         ResultSet resultSet = preparedStatement.executeQuery();
         if (resultSet.next()) {
            OrderDTO orderDTO = new OrderDTO();
            orderDTO.setOrderId(resultSet.getInt("order_id"));
            orderDTO.setSupplierId(resultSet.getInt("supplier_id"));
            orderDTO.setOrderDate(resultSet.getDate("order_date").toLocalDate());
            orderDTO.setStatus(OrderStatus.valueOf(resultSet.getString("status")));
            orderDTO.setSupplierName(getSupplierName(orderDTO.getSupplierId()));
            LOGGER.info("Order retrieved: {}", orderDTO);
            return Optional.of(orderDTO);
         } else {
            LOGGER.warn("No order found with ID: {}", orderID);
            return Optional.empty();
         }
      } catch (SQLException e) {
         try {
            handleSQLException(e);
         } catch (Exception ex) {
            LOGGER.error("Error handling SQL exception: {}", ex.getMessage());
         }
      }
      return Optional.empty();
   }

   @Override
   public List<OrderDTO> listOrders() {
      String sql = "SELECT * FROM orders";
      try (PreparedStatement preparedStatement = Database.getConnection().prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery()) {
         List<OrderDTO> orders = new java.util.ArrayList<>();
         while (resultSet.next()) {
            OrderDTO orderDTO = new OrderDTO();
            orderDTO.setOrderId(resultSet.getInt("order_id"));
            orderDTO.setSupplierId(resultSet.getInt("supplier_id"));
            orderDTO.setOrderDate(resultSet.getDate("order_date").toLocalDate());
            orderDTO.setStatus(OrderStatus.valueOf(resultSet.getString("status")));
            orders.add(orderDTO);
         }
         LOGGER.info("Retrieved {} orders", orders.size());
         return orders;
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
   public boolean deleteOrder(int orderID) {
      if (orderID <= 0) {
         LOGGER.error("Invalid order ID: {}", orderID);
         throw new IllegalArgumentException("Order ID must be greater than 0");
      }
      String sql = "DELETE FROM orders WHERE order_id = ?";
      try (PreparedStatement preparedStatement = Database.getConnection().prepareStatement(sql)) {
         preparedStatement.setInt(1, orderID);
         int affectedRows = preparedStatement.executeUpdate();
         if (affectedRows == 0) {
            LOGGER.warn("No order found with ID: {}", orderID);
            return false;
         }
         LOGGER.info("Order with ID {} deleted successfully", orderID);
         return true;
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
   public boolean updateOrder(OrderDTO updatedOrderDTO) {
      if (updatedOrderDTO == null || updatedOrderDTO.getOrderId() <= 0) {
         LOGGER.error("Invalid order data for update: {}", updatedOrderDTO);
         throw new IllegalArgumentException("Order cannot be null and must have a valid ID");
      }
      String sql = "UPDATE orders SET supplier_id = ?, order_date = ?, status = ? WHERE order_id = ?";
      try (PreparedStatement preparedStatement = Database.getConnection().prepareStatement(sql)) {
         preparedStatement.setInt(1, updatedOrderDTO.getSupplierId());
         preparedStatement.setString(2, updatedOrderDTO.getOrderDate().toString());
         preparedStatement.setString(3, updatedOrderDTO.getStatus().name());
         preparedStatement.setInt(4, updatedOrderDTO.getOrderId());

         int affectedRows = preparedStatement.executeUpdate();
         if (affectedRows == 0) {
            LOGGER.warn("No order found with ID: {}", updatedOrderDTO.getOrderId());
            return false;
         }
         LOGGER.info("Order with ID {} updated successfully", updatedOrderDTO.getOrderId());
         return true;
      } catch (SQLException e) {
         try {
            handleSQLException(e);
         } catch (Exception ex) {
            LOGGER.error("Error handling SQL exception: {}", ex.getMessage());
         }
      }
      return false;
   }

   private String getSupplierName(int supplierId) {
      String sql = "SELECT name FROM suppliers WHERE supplier_id = ?";
      try (PreparedStatement preparedStatement = Database.getConnection().prepareStatement(sql)) {
         preparedStatement.setInt(1, supplierId);
         ResultSet resultSet = preparedStatement.executeQuery();
         if (resultSet.next()) {
            return resultSet.getString("name");
         } else {
            LOGGER.warn("No supplier found with ID: {}", supplierId);
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

}
