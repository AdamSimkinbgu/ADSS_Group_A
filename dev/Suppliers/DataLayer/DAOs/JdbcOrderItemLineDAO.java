package Suppliers.DataLayer.DAOs;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Suppliers.DTOs.OrderItemLineDTO;
import Suppliers.DataLayer.Interfaces.OrderItemLineDAOInterface;
import Suppliers.DataLayer.util.Database;

public class JdbcOrderItemLineDAO implements OrderItemLineDAOInterface {
   private static final Logger LOGGER = LoggerFactory.getLogger(JdbcOrderItemLineDAO.class);

   @Override
   public OrderItemLineDTO addOrderItemLine(OrderItemLineDTO orderItemLine) {
      if (orderItemLine == null) {
         LOGGER.error("Attempted to add a null order item line.");
         throw new IllegalArgumentException("Order item line cannot be null");
      }
      try (PreparedStatement preparedStatement = Database.getConnection().prepareStatement(
            "INSERT INTO order_item_lines (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)",
            PreparedStatement.RETURN_GENERATED_KEYS)) {
         preparedStatement.setInt(1, orderItemLine.getOrderID());
         preparedStatement.setInt(2, orderItemLine.getProductId());
         preparedStatement.setInt(3, orderItemLine.getQuantity());
         preparedStatement.setBigDecimal(4, orderItemLine.getUnitPrice());
         LOGGER.info("Adding order item line: {}", orderItemLine);
         LOGGER.debug("PreparedStatement: {}", preparedStatement);
         int rowsEffected = preparedStatement.executeUpdate();
         if (rowsEffected == 0) {
            LOGGER.error("Creating order item line failed, no rows affected.");
            throw new RuntimeException("Creating order item line failed, no rows affected.");
         }
         try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
               orderItemLine.setOrderItemLineID(generatedKeys.getInt(1));
               LOGGER.info("Order item line created with lineID: {}", orderItemLine.getOrderItemLineID());
               return orderItemLine;
            } else {
               LOGGER.error("Creating order item line failed, no ID obtained.");
               throw new RuntimeException("Creating order item line failed, no ID obtained.");
            }
         }
      } catch (Exception e) {
         LOGGER.error("Error adding order item line: {}", e.getMessage(), e);
         throw new RuntimeException("Error adding order item line", e);
      }
   }

   @Override
   public OrderItemLineDTO getOrderItemLine(int id) {
      if (id <= 0) {
         LOGGER.error("Invalid order item line ID: {}", id);
         throw new IllegalArgumentException("Order item line ID must be greater than 0");
      }
      try (PreparedStatement preparedStatement = Database.getConnection().prepareStatement(
            "SELECT * FROM order_item_lines WHERE order_item_line_id = ?")) {
         preparedStatement.setInt(1, id);
         try (ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
               OrderItemLineDTO orderItemLine = new OrderItemLineDTO();
               orderItemLine.setOrderItemLineID(resultSet.getInt("order_item_line_id"));
               orderItemLine.setOrderID(resultSet.getInt("order_id"));
               orderItemLine.setProductId(resultSet.getInt("product_id"));
               orderItemLine.setQuantity(resultSet.getInt("quantity"));
               orderItemLine.setUnitPrice(resultSet.getBigDecimal("price"));
               LOGGER.info("Retrieved order item line: {}", orderItemLine);
               return orderItemLine;
            } else {
               LOGGER.warn("No order item line found with ID: {}", id);
               return null;
            }
         }
      } catch (Exception e) {
         LOGGER.error("Error retrieving order item line: {}", e.getMessage(), e);
         throw new RuntimeException("Error retrieving order item line", e);
      }
   }

   @Override
   public List<OrderItemLineDTO> listOrderItemLines() {
      try (PreparedStatement preparedStatement = Database.getConnection().prepareStatement(
            "SELECT * FROM order_item_lines");
            ResultSet resultSet = preparedStatement.executeQuery()) {
         List<OrderItemLineDTO> orderItemLines = new java.util.ArrayList<>();
         while (resultSet.next()) {
            OrderItemLineDTO orderItemLine = new OrderItemLineDTO();
            orderItemLine.setOrderItemLineID(resultSet.getInt("order_item_line_id"));
            orderItemLine.setOrderID(resultSet.getInt("order_id"));
            orderItemLine.setProductId(resultSet.getInt("product_id"));
            orderItemLine.setQuantity(resultSet.getInt("quantity"));
            orderItemLine.setUnitPrice(resultSet.getBigDecimal("price"));
            orderItemLines.add(orderItemLine);
         }
         LOGGER.info("Listed {} order item lines", orderItemLines.size());
         return orderItemLines;
      } catch (Exception e) {
         LOGGER.error("Error listing order item lines: {}", e.getMessage(), e);
         throw new RuntimeException("Error listing order item lines", e);
      }
   }

   @Override
   public void deleteOrderItemLine(int id) {
      if (id <= 0) {
         LOGGER.error("Invalid order item line ID: {}", id);
         throw new IllegalArgumentException("Order item line ID must be greater than 0");
      }
      try (PreparedStatement preparedStatement = Database.getConnection().prepareStatement(
            "DELETE FROM order_item_lines WHERE order_item_line_id = ?")) {
         preparedStatement.setInt(1, id);
         int rowsAffected = preparedStatement.executeUpdate();
         if (rowsAffected == 0) {
            LOGGER.warn("No order item line found with ID: {}", id);
         } else {
            LOGGER.info("Deleted order item line with ID: {}", id);
         }
      } catch (Exception e) {
         LOGGER.error("Error deleting order item line: {}", e.getMessage(), e);
         throw new RuntimeException("Error deleting order item line", e);
      }
   }

}
