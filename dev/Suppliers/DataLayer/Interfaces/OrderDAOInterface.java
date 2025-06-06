package Suppliers.DataLayer.Interfaces;

import java.util.List;
import java.util.Optional;

import Suppliers.DTOs.OrderDTO;

public interface OrderDAOInterface {
   /**
    * Adds a new order to the database.
    *
    * @param orderDTO The OrderDTO object containing the order details.
    * @return The ID of the newly created order.
    */
   OrderDTO addOrder(OrderDTO orderDTO);

   /**
    * Retrieves an order by its ID.
    *
    * @param orderID The ID of the order to retrieve.
    * @return An OrderDTO object representing the order, or null if not found.
    */
   Optional<OrderDTO> getOrder(int orderID);

   /**
    * Lists all orders in the database.
    *
    * @return A list of OrderDTO objects representing all orders.
    */
   List<OrderDTO> listOrders();

   /**
    * Deletes an order by its ID.
    *
    * @param orderID The ID of the order to delete.
    */
   boolean deleteOrder(int orderID);

   /**
    * Updates an existing order in the database.
    *
    * @param updatedOrderDTO The OrderDTO object containing the updated order
    *                        details.
    */
   boolean updateOrder(OrderDTO updatedOrderDTO);

}
