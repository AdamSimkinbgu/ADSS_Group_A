package Suppliers.DomainLayer;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Suppliers.DTOs.PeriodicOrderDTO;
import Suppliers.DTOs.PeriodicOrderItemLineDTO;
import Suppliers.DataLayer.DAOs.JdbcPeriodicOrderDAO;
import Suppliers.DataLayer.DAOs.JdbcPeriodicOrderItemLineDAO;
import Suppliers.DataLayer.Interfaces.PeriodicOrderDAOInterface;
import Suppliers.DataLayer.Interfaces.PeriodicOrderItemLineDAOInterface;

public class PeriodicOrderHandler {
   private final PeriodicOrderDAOInterface periodicOrderDAO = new JdbcPeriodicOrderDAO();
   private final PeriodicOrderItemLineDAOInterface periodicOrderItemLineDAO = new JdbcPeriodicOrderItemLineDAO();

   public PeriodicOrderDTO createPeriodicOrder(DayOfWeek fixedDay, Map<Integer, Integer> filteredProducts) {
      if (fixedDay == null || filteredProducts == null || filteredProducts.isEmpty()) {
         throw new IllegalArgumentException("Fixed day and products cannot be null or empty");
      }
      PeriodicOrderDTO periodicOrderDTO = new PeriodicOrderDTO(-1, fixedDay,
            new HashMap<>(filteredProducts), true);
      PeriodicOrderDTO createdPeriodicOrder = periodicOrderDAO.createPeriodicOrder(periodicOrderDTO);
      if (createdPeriodicOrder == null) {
         throw new RuntimeException("Failed to create periodic order");
      }
      HashMap<Integer, Integer> productsThatProccessedWell = new HashMap<>();
      for (Map.Entry<Integer, Integer> entry : filteredProducts.entrySet()) {
         int productId = entry.getKey();
         int quantity = entry.getValue();
         PeriodicOrderItemLineDTO newLine = periodicOrderItemLineDAO.addPeriodicOrderItemLine(
               new PeriodicOrderItemLineDTO(-1, createdPeriodicOrder.getPeriodicOrderID(), productId, quantity));
         if (newLine == null) {
            throw new RuntimeException("Failed to create periodic order item line for product ID: " + productId);
         }
         productsThatProccessedWell.put(productId, quantity);
      }
      createdPeriodicOrder.setProductsInOrder(productsThatProccessedWell);
      return createdPeriodicOrder;
   }
}
