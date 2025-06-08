package Suppliers.DomainLayer.Repositories;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Suppliers.DTOs.OrderDTO;
import Suppliers.DTOs.OrderItemLineDTO;
import Suppliers.DTOs.PeriodicOrderDTO;
import Suppliers.DTOs.PeriodicOrderItemLineDTO;
import Suppliers.DataLayer.DAOs.JdbcOrderDAO;
import Suppliers.DataLayer.DAOs.JdbcOrderItemLineDAO;
import Suppliers.DataLayer.DAOs.JdbcPeriodicOrderDAO;
import Suppliers.DataLayer.DAOs.JdbcPeriodicOrderItemLineDAO;
import Suppliers.DataLayer.Interfaces.OrderDAOInterface;
import Suppliers.DataLayer.Interfaces.OrderItemLineDAOInterface;
import Suppliers.DataLayer.Interfaces.PeriodicOrderDAOInterface;
import Suppliers.DataLayer.Interfaces.PeriodicOrderItemLineDAOInterface;
import Suppliers.DomainLayer.Classes.PeriodicOrder;
import Suppliers.DomainLayer.Repositories.RepositoryIntefaces.OrdersRepositoryInterface;

public class OrdersRepositoryImpl implements OrdersRepositoryInterface {
   private static final Logger LOGGER = LoggerFactory.getLogger(OrdersRepositoryImpl.class);
   private OrderDAOInterface ordersRepository;
   private OrderItemLineDAOInterface orderItemLineDAO;
   private PeriodicOrderDAOInterface periodicOrderDAO;
   private PeriodicOrderItemLineDAOInterface periodicOrderItemLineDAO;
   private HashMap<DayOfWeek, List<PeriodicOrder>> periodicOrdersByDay = new HashMap<>();

   public OrdersRepositoryImpl() {
      this.ordersRepository = new JdbcOrderDAO();
      this.orderItemLineDAO = new JdbcOrderItemLineDAO();
      this.periodicOrderDAO = new JdbcPeriodicOrderDAO();
      this.periodicOrderItemLineDAO = new JdbcPeriodicOrderItemLineDAO();
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
   public void updateRegularOrder(OrderDTO order) {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'updateRegularOrder'");
   }

   @Override
   public void deleteRegularOrder(int orderId) {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'deleteRegularOrder'");
   }

   @Override
   public OrderDTO getRegularOrderById(int orderId) {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'getRegularOrderById'");
   }

   @Override
   public List<OrderDTO> getAllRegularOrders() {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'getAllRegularOrders'");
   }

   @Override
   public PeriodicOrderDTO createPeriodicOrder(PeriodicOrderDTO periodicOrderDTO) {
      PeriodicOrderDTO createdPeriodicOrder = periodicOrderDAO.createPeriodicOrder(periodicOrderDTO);
      if (createdPeriodicOrder == null) {
         throw new RuntimeException("Failed to create periodic order");
      }
      HashMap<Integer, Integer> productsThatProccessedWell = new HashMap<>();
      for (Map.Entry<Integer, Integer> entry : periodicOrderDTO.getProductsInOrder().entrySet()) {
         int productId = entry.getKey();
         int quantity = entry.getValue();
         PeriodicOrderItemLineDTO newLine = periodicOrderItemLineDAO.addPeriodicOrderItemLine(
               new PeriodicOrderItemLineDTO(-1, createdPeriodicOrder.getPeriodicOrderID(), productId, quantity));
         if (newLine == null) {
            throw new RuntimeException("Failed to create periodic order item line for product ID: " + productId);
         }
         productsThatProccessedWell.put(productId, quantity);
      }
      DayOfWeek fixedDay = periodicOrderDTO.getDeliveryDay();
      periodicOrdersByDay.computeIfAbsent(fixedDay, k -> new ArrayList<>());
      periodicOrdersByDay.get(fixedDay).add(new PeriodicOrder(periodicOrderDTO));
      createdPeriodicOrder.setProductsInOrder(productsThatProccessedWell);
      return createdPeriodicOrder;
   }

   @Override
   public boolean updatePeriodicOrder(PeriodicOrderDTO updatedDto) {
      PeriodicOrderDTO existingOrder = periodicOrderDAO.getPeriodicOrder(updatedDto.getPeriodicOrderID());
      if (existingOrder == null) {
         throw new RuntimeException("Periodic order not found for ID: " + updatedDto.getPeriodicOrderID());
      }
      existingOrder.setDeliveryDay(updatedDto.getDeliveryDay());
      existingOrder.setActive(updatedDto.isActive());
      if (!periodicOrderDAO.updatePeriodicOrder(existingOrder)) {
         throw new RuntimeException("Failed to update periodic order");
      }
      int counter = 1;
      for (Map.Entry<Integer, Integer> entry : updatedDto.getProductsInOrder().entrySet()) {
         int productId = entry.getKey();
         int quantity = entry.getValue();
         PeriodicOrderItemLineDTO itemLine = new PeriodicOrderItemLineDTO(counter++, existingOrder.getPeriodicOrderID(),
               productId, quantity);
         if (!periodicOrderItemLineDAO.updatePeriodicOrderItemLine(itemLine)) {
            throw new RuntimeException("Failed to update periodic order item line for product ID: " + productId);
         }
      }
      DayOfWeek fixedDay = updatedDto.getDeliveryDay();
      periodicOrdersByDay.computeIfAbsent(fixedDay, k -> new ArrayList<>());
      List<PeriodicOrder> ordersForDay = periodicOrdersByDay.get(fixedDay);
      ordersForDay.removeIf(order -> order.getPeriodicOrderID() == updatedDto.getPeriodicOrderID());
      ordersForDay.add(new PeriodicOrder(updatedDto));
      return true;
   }

   @Override
   public boolean deletePeriodicOrder(int periodicOrderId) {
      boolean deleted = periodicOrderDAO.deletePeriodicOrder(periodicOrderId);
      if (deleted) {
         for (List<PeriodicOrder> orders : periodicOrdersByDay.values()) {
            orders.removeIf(order -> order.getPeriodicOrderID() == periodicOrderId);
         }
      }
      return deleted;
   }

   @Override
   public PeriodicOrderDTO getPeriodicOrderById(int periodicOrderId) {
      for (List<PeriodicOrder> orders : periodicOrdersByDay.values()) {
         for (PeriodicOrder order : orders) {
            if (order.getPeriodicOrderID() == periodicOrderId) {
               return new PeriodicOrderDTO(order);
            }
         }
      }
      PeriodicOrderDTO periodicOrder = periodicOrderDAO.getPeriodicOrder(periodicOrderId);
      if (periodicOrder == null) {
         throw new RuntimeException("Periodic order not found for ID: " + periodicOrderId);
      }
      List<PeriodicOrderItemLineDTO> itemLines = periodicOrderItemLineDAO
            .listPeriodicOrderItemLinesByOrderId(periodicOrderId);
      if (itemLines != null) {
         HashMap<Integer, Integer> productsInOrder = new HashMap<>();
         for (PeriodicOrderItemLineDTO itemLine : itemLines) {
            productsInOrder.put(itemLine.getProductId(), itemLine.getQuantity());
         }
         periodicOrder.setProductsInOrder(productsInOrder);
      }
      return periodicOrder;
   }

   @Override
   public List<PeriodicOrderDTO> getAllPeriodicOrders() {
      List<PeriodicOrderDTO> periodicOrders = periodicOrderDAO.listPeriodicOrders();
      if (periodicOrders == null || periodicOrders.isEmpty()) {
         return new ArrayList<>();
      }
      for (PeriodicOrderDTO order : periodicOrders) {
         List<PeriodicOrderItemLineDTO> itemLines = periodicOrderItemLineDAO
               .listPeriodicOrderItemLinesByOrderId(order.getPeriodicOrderID());
         if (itemLines != null) {
            HashMap<Integer, Integer> productsInOrder = new HashMap<>();
            for (PeriodicOrderItemLineDTO itemLine : itemLines) {
               productsInOrder.put(itemLine.getProductId(), itemLine.getQuantity());
            }
            order.setProductsInOrder(productsInOrder);
         }
      }
      for (PeriodicOrderDTO order : periodicOrders) {
         DayOfWeek day = order.getDeliveryDay();
         periodicOrdersByDay.computeIfAbsent(day, k -> new ArrayList<>()).add(new PeriodicOrder(order));
      }
      return periodicOrders;
   }

   @Override
   public List<PeriodicOrderDTO> getPeriodicOrdersBySupplierId(int supplierId) {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'getPeriodicOrdersBySupplierId'");
   }

   @Override
   public List<OrderDTO> getAllSentRegularOrders() {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'getAllSentRegularOrders'");
   }

   @Override
   public List<OrderDTO> getAllOnDeliveryRegularOrders() {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'getAllOnDeliveryRegularOrders'");
   }

   @Override
   public List<OrderDTO> getAllDeliveredRegularOrders() {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'getAllDeliveredRegularOrders'");
   }

   @Override
   public List<OrderDTO> getAllCompletedRegularOrders() {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'getAllCompletedRegularOrders'");
   }

   @Override
   public List<OrderDTO> getAllCanceledRegularOrders() {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'getAllCanceledRegularOrders'");
   }
}
