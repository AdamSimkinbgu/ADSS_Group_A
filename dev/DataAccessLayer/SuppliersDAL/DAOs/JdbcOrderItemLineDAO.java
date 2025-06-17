package DataAccessLayer.SuppliersDAL.DAOs;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import DTOs.SuppliersModuleDTOs.OrderItemLineDTO;
import DataAccessLayer.SuppliersDAL.Interfaces.OrderItemLineDAOInterface;
import DataAccessLayer.SuppliersDAL.util.Database;

public class JdbcOrderItemLineDAO extends BaseDAO implements OrderItemLineDAOInterface {
   private static final Logger LOGGER = LoggerFactory.getLogger(JdbcOrderItemLineDAO.class);

   @Override
   public OrderItemLineDTO addOrderItemLine(OrderItemLineDTO orderItemLine) {
      if (orderItemLine == null) {
         LOGGER.error("Attempted to add a null order item line.");
         throw new IllegalArgumentException("Order item line cannot be null");
      }
      int productId = orderItemLine.getProductId();
      if (productId <= 0) {
         LOGGER.error("Invalid product ID: {}", productId);
         throw new IllegalArgumentException("Product ID must be greater than 0");
      }
      try (PreparedStatement preparedStatement = Database.getConnection().prepareStatement(
            "INSERT INTO order_item_lines (order_id, product_id, quantity, unit_price, discount_pct) VALUES (?, ?, ?, ?, ?)",
            PreparedStatement.RETURN_GENERATED_KEYS)) {
         preparedStatement.setInt(1, orderItemLine.getOrderID());
         preparedStatement.setInt(2, orderItemLine.getProductId());
         preparedStatement.setInt(3, orderItemLine.getQuantity());
         preparedStatement.setBigDecimal(4, orderItemLine.getUnitPrice());
         preparedStatement.setBigDecimal(5,
               orderItemLine.getDiscount() != null ? orderItemLine.getDiscount() : BigDecimal.ZERO);

         LOGGER.debug("Adding order item line: {}", orderItemLine);
         int rowsEffected = preparedStatement.executeUpdate();
         if (rowsEffected == 0) {
            LOGGER.error("Creating order item line failed, no rows affected.");
            throw new RuntimeException("Creating order item line failed, no rows affected.");
         }
         try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
               orderItemLine.setOrderItemLineID(generatedKeys.getInt(1));
               LOGGER.debug("Order item line created with lineID: {}", orderItemLine.getOrderItemLineID());
               return orderItemLine;
            } else {
               LOGGER.error("Creating order item line failed, no ID obtained.");
               throw new RuntimeException("Creating order item line failed, no ID obtained.");
            }
         }
      } catch (SQLException e) {
         LOGGER.error("Error handling SQL exception: {}", e.getMessage());
         handleSQLException(e);
      }
      return null;
   }

   @Override
   public OrderItemLineDTO getOrderItemLine(int orderId, int lineId) {
      if (orderId <= 0 || lineId <= 0) {
         LOGGER.error("Invalid order ID or line ID: orderId={}, lineId={}", orderId, lineId);
         throw new IllegalArgumentException("Order ID and line ID must be greater than 0");
      }
      try (PreparedStatement preparedStatement = Database.getConnection().prepareStatement(
            "SELECT * FROM order_item_lines WHERE order_id = ? AND line_number = ?")) {
         preparedStatement.setInt(1, orderId);
         preparedStatement.setInt(2, lineId);
         try (ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
               OrderItemLineDTO orderItemLine = new OrderItemLineDTO();
               orderItemLine.setOrderItemLineID(resultSet.getInt("line_number"));
               orderItemLine.setOrderID(resultSet.getInt("order_id"));
               orderItemLine.setProductId(resultSet.getInt("product_id"));
               orderItemLine.setQuantity(resultSet.getInt("quantity"));
               orderItemLine.setUnitPrice(resultSet.getBigDecimal("unit_price"));
               orderItemLine.setDiscount(resultSet.getBigDecimal("discount_pct"));
               orderItemLine.setSupplierProductCatalogNumber(resultSet.getString("supplier_product_catalog_number"));
               orderItemLine.setProductName(resultSet.getString("product_name"));
               LOGGER.debug("Retrieved order item line: {}", orderItemLine);
               return orderItemLine;
            } else {
               LOGGER.warn("No order item line found with orderId: {} and lineId: {}", orderId, lineId);
               return null;
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
   public List<OrderItemLineDTO> listOrderItemLines(int orderId) {
      if (orderId <= 0) {
         LOGGER.error("Invalid order ID: {}", orderId);
         throw new IllegalArgumentException("Order ID must be greater than 0");
      }
      try (PreparedStatement preparedStatement = Database.getConnection().prepareStatement(
            "SELECT * FROM order_item_lines WHERE order_id = ?")) {
         preparedStatement.setInt(1, orderId);
         ResultSet resultSet = preparedStatement.executeQuery();
         {
            List<OrderItemLineDTO> orderItemLines = new ArrayList<>();
            while (resultSet.next()) {
               OrderItemLineDTO orderItemLine = new OrderItemLineDTO();
               orderItemLine.setOrderItemLineID(resultSet.getInt("line_number"));
               orderItemLine.setOrderID(resultSet.getInt("order_id"));
               orderItemLine.setProductId(resultSet.getInt("product_id"));
               orderItemLine.setQuantity(resultSet.getInt("quantity"));
               orderItemLine.setUnitPrice(resultSet.getBigDecimal("unit_price"));
               orderItemLine.setDiscount(BigDecimal.ONE.subtract(resultSet.getBigDecimal("discount_pct")));
               orderItemLines.add(orderItemLine);
            }
            LOGGER.debug("Listed {} order item lines", orderItemLines.size());
            return orderItemLines;
         }

      } catch (SQLException e) {
         LOGGER.error("Error handling SQL exception: {}", e.getMessage());
         handleSQLException(e);
      }
      return new ArrayList<>();
   }

   @Override
   public boolean deleteOrderItemLine(int id) {
      if (id <= 0) {
         LOGGER.error("Invalid order item line ID: {}", id);
         throw new IllegalArgumentException("Order item line ID must be greater than 0");
      }
      try (PreparedStatement preparedStatement = Database.getConnection().prepareStatement(
            "DELETE FROM order_item_lines WHERE line_number = ?")) {
         preparedStatement.setInt(1, id);
         int rowsAffected = preparedStatement.executeUpdate();
         if (rowsAffected == 0) {
            LOGGER.warn("No order item line found with ID: {}", id);
            return false;
         } else {
            LOGGER.debug("Deleted order item line with ID: {}", id);
            return true;
         }
      } catch (SQLException e) {
         LOGGER.error("Error handling SQL exception: {}", e.getMessage());
         handleSQLException(e);
      }
      return false;
   }

   @Override
   public boolean updateOrderItemLine(OrderItemLineDTO orderItemLine) {
      if (orderItemLine == null || orderItemLine.getOrderItemLineID() <= 0) {
         LOGGER.error("Invalid order item line: {}", orderItemLine);
         throw new IllegalArgumentException("Order item line cannot be null and must have a valid ID");
      }
      try (PreparedStatement preparedStatement = Database.getConnection().prepareStatement(
            "UPDATE order_item_lines SET product_id = ?, quantity = ?, unit_price = ? WHERE order_id = ? AND line_number = ?")) {
         preparedStatement.setInt(1, orderItemLine.getProductId());
         preparedStatement.setInt(2, orderItemLine.getQuantity());
         preparedStatement.setBigDecimal(3, orderItemLine.getUnitPrice());
         preparedStatement.setInt(4, orderItemLine.getOrderID());
         preparedStatement.setInt(5, orderItemLine.getOrderItemLineID());
         int rowsAffected = preparedStatement.executeUpdate();
         if (rowsAffected > 0) {
            LOGGER.info("Order item line updated successfully: {}", orderItemLine);
            return true;
         } else {
            LOGGER.warn("No order item line found with ID: {}", orderItemLine.getOrderItemLineID());
            return false;
         }
      } catch (SQLException e) {
         LOGGER.error("Error handling SQL exception: {}", e.getMessage());
         handleSQLException(e);
      }
      return false;
   }

   public List<OrderItemLineDTO> getAllOrderItemLinesForOrder(int orderId) {
      if (orderId <= 0) {
         LOGGER.error("Invalid order ID: {}", orderId);
         throw new IllegalArgumentException("Order ID must be greater than 0");
      }
      try (PreparedStatement preparedStatement = Database.getConnection().prepareStatement(
            "SELECT * FROM order_item_lines WHERE order_id = ?")) {
         preparedStatement.setInt(1, orderId);
         ResultSet resultSet = preparedStatement.executeQuery();
         List<OrderItemLineDTO> orderItemLines = new ArrayList<>();
         while (resultSet.next()) {
            OrderItemLineDTO orderItemLine = new OrderItemLineDTO();
            orderItemLine.setOrderItemLineID(resultSet.getInt("line_number"));
            orderItemLine.setOrderID(resultSet.getInt("order_id"));
            orderItemLine.setProductId(resultSet.getInt("product_id"));
            orderItemLine.setQuantity(resultSet.getInt("quantity"));
            orderItemLine.setUnitPrice(resultSet.getBigDecimal("unit_price"));
            orderItemLines.add(orderItemLine);
         }
         LOGGER.debug("Retrieved {} order item lines for order ID: {}", orderItemLines.size(), orderId);
         return orderItemLines;
      } catch (SQLException e) {
         LOGGER.error("Error handling SQL exception: {}", e.getMessage());
         handleSQLException(e);
      }
      return new ArrayList<>();

   }

}
