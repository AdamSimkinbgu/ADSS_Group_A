package Suppliers.DataLayer.Interfaces;

import java.util.List;

import Suppliers.DTOs.PeriodicOrderItemLineDTO;

public interface PeriodicOrderItemLineDAOInterface {
   /**
    * Adds a new periodic order item line to the database.
    *
    * @param periodicOrderItemLine The periodic order item line to be added.
    * @return The ID of the newly created periodic order item line.
    */
   PeriodicOrderItemLineDTO addPeriodicOrderItemLine(PeriodicOrderItemLineDTO periodicOrderItemLine);

   /**
    * Retrieves a periodic order item line by its ID.
    *
    * @param id The ID of the periodic order item line to retrieve.
    * @return A PeriodicOrderItemLineDTO object representing the periodic order
    *         item line, or null if not found.
    */
   PeriodicOrderItemLineDTO getPeriodicOrderItemLine(int id);

   /**
    * Lists all periodic order item lines in the database.
    *
    * @return A list of PeriodicOrderItemLineDTO objects representing all periodic
    *         order item lines.
    */
   List<PeriodicOrderItemLineDTO> listPeriodicOrderItemLinesByOrderId(int periodicOrderId);

   /**
    * Deletes a periodic order item line by its ID.
    *
    * @param id The ID of the periodic order item line to delete.
    */
   boolean deletePeriodicOrderItemLine(int id);

   /**
    * Updates an existing periodic order item line in the database.
    *
    * @param periodicOrderItemLine The periodic order item line to be updated.
    */
   boolean updatePeriodicOrderItemLine(PeriodicOrderItemLineDTO periodicOrderItemLine);
}
