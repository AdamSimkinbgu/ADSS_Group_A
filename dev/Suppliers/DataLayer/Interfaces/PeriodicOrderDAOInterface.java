package Suppliers.DataLayer.Interfaces;

import java.util.List;

import Suppliers.DTOs.PeriodicOrderDTO;

public interface PeriodicOrderDAOInterface {
   /**
    * Creates a new periodic order in the database.
    *
    * @param periodicOrder The periodic order to be created.
    * @return The ID of the newly created periodic order.
    */
   PeriodicOrderDTO createPeriodicOrder(PeriodicOrderDTO periodicOrder);

   /**
    * Updates an existing periodic order in the database.
    *
    * @param periodicOrder The periodic order to be updated.
    * @return True if the update was successful, false otherwise.
    */
   boolean updatePeriodicOrder(PeriodicOrderDTO periodicOrder);

   /**
    * Deletes a periodic order from the database.
    *
    * @param id The ID of the periodic order to be deleted.
    * @return True if the deletion was successful, false otherwise.
    */
   boolean deletePeriodicOrder(int id);

   /**
    * Retrieves a periodic order by its ID.
    *
    * @param id The ID of the periodic order to retrieve.
    * @return A PeriodicOrderDTO object representing the periodic order, or null if
    *         not found.
    */
   PeriodicOrderDTO getPeriodicOrder(int id);

   /**
    * Lists all periodic orders in the database.
    *
    * @return A list of PeriodicOrderDTO objects representing all periodic orders.
    */
   List<PeriodicOrderDTO> listPeriodicOrders();
}
