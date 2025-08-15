package DataAccessLayer.SuppliersDAL.Interfaces;

import java.util.List;

import DTOs.SuppliersModuleDTOs.OrderItemLineDTO;

public interface OrderItemLineDAOInterface {
   /**
    * Adds a new order item line to the database.
    *
    * @param orderItemLine The order item line to be added.
    * @return The ID of the newly created order item line.
    */
   OrderItemLineDTO addOrderItemLine(OrderItemLineDTO orderItemLine);

   /**
    * Retrieves an order item line by its ID.
    *
    * @param id The ID of the order item line to retrieve.
    * @return An OrderItemLineDTO object representing the order item line, or null
    *         if not found.
    */
   OrderItemLineDTO getOrderItemLine(int orderId, int lineId);

   /**
    * Lists all order item lines in the database.
    *
    * @return A list of OrderItemLineDTO objects representing all order item lines.
    */
   List<OrderItemLineDTO> listOrderItemLines(int orderId);

   /**
    * Deletes an order item line by its ID.
    *
    * @param id The ID of the order item line to delete.
    */
   boolean deleteOrderItemLine(int id);

   /**
    * Updates an existing order item line in the database.
    *
    * @param orderItemLine The order item line to be updated.
    */
   boolean updateOrderItemLine(OrderItemLineDTO orderItemLine);

}
