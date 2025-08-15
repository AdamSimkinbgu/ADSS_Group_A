package DomainLayer.SuppliersDomainSubModule;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DTOs.SuppliersModuleDTOs.PeriodicOrderDTO;
import DTOs.SuppliersModuleDTOs.PeriodicOrderItemLineDTO;
import DataAccessLayer.SuppliersDAL.DAOs.JdbcPeriodicOrderDAO;
import DataAccessLayer.SuppliersDAL.DAOs.JdbcPeriodicOrderItemLineDAO;
import DataAccessLayer.SuppliersDAL.Interfaces.PeriodicOrderDAOInterface;
import DataAccessLayer.SuppliersDAL.Interfaces.PeriodicOrderItemLineDAOInterface;
import DomainLayer.SuppliersDomainSubModule.Classes.PeriodicOrder;

public class PeriodicOrderHandler {
   private final PeriodicOrderDAOInterface periodicOrderDAO = new JdbcPeriodicOrderDAO();
   private final PeriodicOrderItemLineDAOInterface periodicOrderItemLineDAO = new JdbcPeriodicOrderItemLineDAO();
   private HashMap<DayOfWeek, List<PeriodicOrder>> periodicOrdersByDay = new HashMap<>();

   public PeriodicOrderDTO createPeriodicOrder(PeriodicOrderDTO periodicOrderDTO) {
      if (periodicOrderDTO == null) {
         throw new IllegalArgumentException("PeriodicOrderDTO cannot be null");
      }

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

   public List<PeriodicOrder> getAllActivePeriodicOrdersByDay(DayOfWeek day) {
      return null;
   }

   public boolean deletePeriodicOrder(int periodicOrderId) {
      if (periodicOrderId <= 0) {
         throw new IllegalArgumentException("Invalid periodic order ID: " + periodicOrderId);
      }
      boolean deleted = periodicOrderDAO.deletePeriodicOrder(periodicOrderId);
      if (deleted) {
         // Remove from the in-memory map
         for (List<PeriodicOrder> orders : periodicOrdersByDay.values()) {
            orders.removeIf(order -> order.getPeriodicOrderID() == periodicOrderId);
         }
      }
      return deleted;
   }

   public List<PeriodicOrderDTO> getAllPeriodicOrders() {
      List<PeriodicOrderDTO> periodicOrders = periodicOrderDAO.listPeriodicOrders();
      if (periodicOrders == null || periodicOrders.isEmpty()) {
         return new ArrayList<>();
      }
      for (PeriodicOrderDTO order : periodicOrders) {
         // Fetch item lines for each periodic order
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
      // Populate the in-memory map
      for (PeriodicOrderDTO order : periodicOrders) {
         DayOfWeek day = order.getDeliveryDay();
         periodicOrdersByDay.computeIfAbsent(day, k -> new ArrayList<>()).add(new PeriodicOrder(order));
      }
      return periodicOrders;
   }

   public PeriodicOrderDTO updatePeriodicOrder(PeriodicOrderDTO updatedDto) {
      if (updatedDto == null || updatedDto.getPeriodicOrderID() <= 0) {
         throw new IllegalArgumentException("Invalid periodic order DTO");
      }
      PeriodicOrderDTO existingOrder = periodicOrderDAO.getPeriodicOrder(updatedDto.getPeriodicOrderID());
      if (existingOrder == null) {
         throw new RuntimeException("Periodic order not found for ID: " + updatedDto.getPeriodicOrderID());
      }
      // Update the existing order
      existingOrder.setDeliveryDay(updatedDto.getDeliveryDay());
      existingOrder.setActive(updatedDto.isActive());
      if (!periodicOrderDAO.updatePeriodicOrder(existingOrder)) {
         throw new RuntimeException("Failed to update periodic order");
      }
      // Update item lines
      int counter = 1;
      for (Map.Entry<Integer, Integer> entry : updatedDto.getProductsInOrder().entrySet()) {
         int productId = entry.getKey();
         int quantity = entry.getValue();
         PeriodicOrderItemLineDTO itemLine = new PeriodicOrderItemLineDTO(counter++, existingOrder.getPeriodicOrderID(),
               productId, quantity);
         if (periodicOrderItemLineDAO.updatePeriodicOrderItemLine(itemLine)) {

            if (periodicOrderItemLineDAO.addPeriodicOrderItemLine(itemLine) == null) {
               throw new RuntimeException("Failed to update periodic order item line for product ID: " + productId);
            }
         }
      }
      // Update the in-memory map
      DayOfWeek fixedDay = updatedDto.getDeliveryDay();
      periodicOrdersByDay.computeIfAbsent(fixedDay, k -> new ArrayList<>());
      List<PeriodicOrder> ordersForDay = periodicOrdersByDay.get(fixedDay);
      ordersForDay.removeIf(order -> order.getPeriodicOrderID() == updatedDto.getPeriodicOrderID());
      ordersForDay.add(new PeriodicOrder(updatedDto));
      return existingOrder;
   }

   public PeriodicOrderDTO getPeriodicOrder(int periodicOrderId) {
      if (periodicOrderId <= 0) {
         throw new IllegalArgumentException("Invalid periodic order ID: " + periodicOrderId);
      }
      // flatten the periodic orders map to find the order
      for (List<PeriodicOrder> orders : periodicOrdersByDay.values()) {
         for (PeriodicOrder order : orders) {
            if (order.getPeriodicOrderID() == periodicOrderId) {
               return new PeriodicOrderDTO(order);
            }
         }
      }
      // else, fetch from the database
      PeriodicOrderDTO periodicOrder = periodicOrderDAO.getPeriodicOrder(periodicOrderId);
      if (periodicOrder == null) {
         throw new RuntimeException("Periodic order not found for ID: " + periodicOrderId);
      }
      // Fetch item lines for the periodic order
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
}
