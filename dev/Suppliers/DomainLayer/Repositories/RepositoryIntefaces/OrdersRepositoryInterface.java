package Suppliers.DomainLayer.Repositories.RepositoryIntefaces;

import java.util.List;

import Suppliers.DTOs.OrderDTO;
import Suppliers.DTOs.PeriodicOrderDTO;
import Suppliers.DTOs.Enums.OrderStatus;

public interface OrdersRepositoryInterface {
   /**
    * Creates a new order in the repository.
    *
    * @param order The order to be created.
    * @return The created order.
    */
   OrderDTO createRegularOrder(OrderDTO order);

   /**
    * Updates an existing order in the repository.
    * 
    * @param order The order to be updated.
    *
    * @return true if the order was updated successfully, false otherwise.
    */
   boolean updateRegularOrder(OrderDTO order);

   /**
    * Deletes an order from the repository.
    *
    * @param orderId The ID of the order to be deleted.
    */
   boolean deleteRegularOrder(int orderId);

   /**
    * Retrieves an order by its ID.
    *
    * @param orderId The ID of the order to be retrieved.
    * @return The order with the specified ID, or null if not found.
    */
   OrderDTO getRegularOrderById(int orderId);

   /**
    * Retrieves all orders in the repository.
    *
    * @return A list of all orders.
    */
   List<OrderDTO> getAllRegularOrders();

   /**
    * Retrieves all orders for a specific supplier.
    *
    * @param supplierId The ID of the supplier whose orders are to be retrieved.
    * @return A list of orders for the specified supplier.
    */

   PeriodicOrderDTO createPeriodicOrder(PeriodicOrderDTO periodicOrderDTO);

   /**
    * Updates an existing periodic order in the repository.
    * 
    * @param periodicOrderDTO The periodic order to be updated.
    */
   void updatePeriodicOrder(PeriodicOrderDTO periodicOrderDTO);

   /**
    * Deletes a periodic order from the repository.
    *
    * @param periodicOrderId The ID of the periodic order to be deleted.
    */
   void deletePeriodicOrder(int periodicOrderId);

   /**
    * Retrieves a periodic order by its ID.
    *
    * @param periodicOrderId The ID of the periodic order to be retrieved.
    * @return The periodic order with the specified ID, or null if not found.
    */
   PeriodicOrderDTO getPeriodicOrderById(int periodicOrderId);

   /**
    * Retrieves all periodic orders in the repository.
    *
    * @return A list of all periodic orders.
    */
   List<PeriodicOrderDTO> getAllPeriodicOrders();

   /**
    * Retrieves all periodic orders for a specific supplier.
    *
    * @param supplierId The ID of the supplier whose periodic orders are to be
    *                   retrieved.
    * @return A list of periodic orders for the specified supplier.
    */
   List<PeriodicOrderDTO> getPeriodicOrdersBySupplierId(int supplierId);

   List<OrderDTO> getOrdersByStatus(OrderStatus delivered);

}
