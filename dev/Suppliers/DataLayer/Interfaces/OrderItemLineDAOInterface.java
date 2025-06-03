package Suppliers.DataLayer.Interfaces;

import java.util.List;

import Suppliers.DTOs.OrderItemLineDTO;

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
   OrderItemLineDTO getOrderItemLine(int id);

   /**
    * Lists all order item lines in the database.
    *
    * @return A list of OrderItemLineDTO objects representing all order item lines.
    */
   List<OrderItemLineDTO> listOrderItemLines();

   /**
    * Deletes an order item line by its ID.
    *
    * @param id The ID of the order item line to delete.
    */
   void deleteOrderItemLine(int id);

}
