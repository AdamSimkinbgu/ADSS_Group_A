package DomainLayer.SuppliersDomainSubModule.Repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import DTOs.SuppliersModuleDTOs.OrderDTO;
import DTOs.SuppliersModuleDTOs.OrderItemLineDTO;
import DTOs.SuppliersModuleDTOs.PeriodicOrderDTO;
import DTOs.SuppliersModuleDTOs.Enums.OrderStatus;
import DataAccessLayer.SuppliersDAL.DAOs.JdbcOrderDAO;
import DataAccessLayer.SuppliersDAL.DAOs.JdbcOrderItemLineDAO;
import DataAccessLayer.SuppliersDAL.Interfaces.OrderDAOInterface;
import DataAccessLayer.SuppliersDAL.Interfaces.OrderItemLineDAOInterface;
import DomainLayer.SuppliersDomainSubModule.Repositories.RepositoryIntefaces.OrdersRepositoryInterface;

public class OrdersRepositoryImpl implements OrdersRepositoryInterface {
   private static final Logger LOGGER = LoggerFactory.getLogger(OrdersRepositoryImpl.class);
   private OrderDAOInterface ordersRepository;
   private OrderItemLineDAOInterface orderItemLineDAO;

   public OrdersRepositoryImpl() {
      this.ordersRepository = new JdbcOrderDAO();
      this.orderItemLineDAO = new JdbcOrderItemLineDAO();
   }

   @Override
   public OrderDTO createRegularOrder(OrderDTO order) {
      if (order == null) {
         throw new IllegalArgumentException("OrderDTO cannot be null");
      }
      OrderDTO createdOrder = ordersRepository.addOrder(order);
      List<OrderItemLineDTO> itemLines = new ArrayList<>();
      for (OrderItemLineDTO itemLine : order.getItems()) {
         itemLine.setOrderID(createdOrder.getOrderId());
         OrderItemLineDTO createdItemLine = orderItemLineDAO.addOrderItemLine(itemLine);
         if (createdItemLine == null) {
            LOGGER.error("Failed to create order item line for order ID: {}", createdOrder.getOrderId());
         }
         itemLines.add(createdItemLine);
      }
      createdOrder.setItems(itemLines);
      LOGGER.info("Regular order created with ID: and items: {}", createdOrder.getOrderId(), itemLines);
      return createdOrder;
   }

   @Override
   public boolean updateRegularOrder(OrderDTO order) {
      if (order == null || order.getOrderId() <= 0) {
         throw new IllegalArgumentException("OrderDTO cannot be null and must have a valid ID");
      }
      boolean updated = ordersRepository.updateOrder(order);
      if (updated) {
         LOGGER.info("Regular order with ID: {} updated successfully", order.getOrderId());
      } else {
         LOGGER.error("Failed to update regular order with ID: {}", order.getOrderId());
      }
      return updated;
   }

   @Override
   public boolean deleteRegularOrder(int orderId) {
      if (orderId <= 0) {
         throw new IllegalArgumentException("Order ID must be a positive integer");
      }
      boolean deleted = ordersRepository.deleteOrder(orderId);
      if (deleted) {
         LOGGER.info("Regular order with ID: {} deleted successfully", orderId);
      } else {
         LOGGER.error("Failed to delete regular order with ID: {}", orderId);
      }
      return deleted;
   }

   @Override
   public OrderDTO getRegularOrderById(int orderId) {
      if (orderId <= 0) {
         throw new IllegalArgumentException("Order ID must be a positive integer");
      }
      Optional<OrderDTO> orderopt = ordersRepository.getOrderByID(orderId);
      if (orderopt.isEmpty()) {
         LOGGER.warn("No regular order found with ID: {}", orderId);
         return null;
      }
      OrderDTO order = orderopt.get();
      if (order == null) {
         LOGGER.warn("No regular order found with ID: {}", orderId);
         return null;
      }
      List<OrderItemLineDTO> itemLines = orderItemLineDAO.listOrderItemLines(orderId);
      if (itemLines != null && !itemLines.isEmpty()) {
         order.setItems(itemLines);
      } else {
         LOGGER.warn("No item lines found for order ID: {}", orderId);
      }
      LOGGER.info("Retrieved regular order with ID: {}", orderId);
      return order;
   }

   @Override
   public List<OrderDTO> getAllRegularOrders() {
      List<OrderDTO> orders = ordersRepository.listOrders();
      if (orders == null || orders.isEmpty()) {
         LOGGER.warn("No regular orders found in the repository");
         return new ArrayList<>();
      }
      for (OrderDTO order : orders) {
         List<OrderItemLineDTO> itemLines = orderItemLineDAO.listOrderItemLines(order.getOrderId());
         if (itemLines != null && !itemLines.isEmpty()) {
            order.setItems(itemLines);
         } else {
            LOGGER.warn("No item lines found for order ID: {}", order.getOrderId());
         }
      }
      LOGGER.info("Retrieved {} regular orders from the repository", orders.size());
      return orders;
   }

   @Override
   public PeriodicOrderDTO createPeriodicOrder(PeriodicOrderDTO periodicOrderDTO) {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'createPeriodicOrder'");
   }

   @Override
   public void updatePeriodicOrder(PeriodicOrderDTO periodicOrderDTO) {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'updatePeriodicOrder'");
   }

   @Override
   public void deletePeriodicOrder(int periodicOrderId) {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'deletePeriodicOrder'");
   }

   @Override
   public PeriodicOrderDTO getPeriodicOrderById(int periodicOrderId) {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'getPeriodicOrderById'");
   }

   @Override
   public List<PeriodicOrderDTO> getAllPeriodicOrders() {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'getAllPeriodicOrders'");
   }

   @Override
   public List<PeriodicOrderDTO> getPeriodicOrdersBySupplierId(int supplierId) {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'getPeriodicOrdersBySupplierId'");
   }

   @Override
   public List<OrderDTO> getOrdersByStatus(OrderStatus delivered) {
      if (delivered == null) {
         throw new IllegalArgumentException("OrderStatus cannot be null");
      }
      List<OrderDTO> orders = ordersRepository.getOrdersByStatus(delivered);
      if (orders == null || orders.isEmpty()) {
         LOGGER.warn("No orders found with status: {}", delivered);
         return new ArrayList<>();
      }
      for (OrderDTO order : orders) {
         List<OrderItemLineDTO> itemLines = orderItemLineDAO.listOrderItemLines(order.getOrderId());
         if (itemLines != null && !itemLines.isEmpty()) {
            order.setItems(itemLines);
         } else {
            LOGGER.warn("No item lines found for order ID: {}", order.getOrderId());
         }
      }
      LOGGER.info("Retrieved {} orders with status: {}", orders.size(), delivered);
      return orders;
   }
}
