package DataAccessLayer.SuppliersDAL.Interfaces;

import java.util.List;

import DTOs.SuppliersModuleDTOs.ContactInfoDTO;

public interface ContactInfoDAOInterface {
   /**
    * Creates a new contact information entry in the database.
    *
    * @param contactInfo The contact information to be created.
    * @return The ID of the newly created contact information.
    */
   ContactInfoDTO createContactInfo(ContactInfoDTO contactInfo);

   /**
    * Updates an existing contact information entry in the database.
    *
    * @param contactInfo The contact information to be updated.
    * @return True if the update was successful, false otherwise.
    */
   boolean updateContactInfo(ContactInfoDTO contactInfo);

   /**
    * Deletes a contact information entry from the database.
    *
    * @param id The ID of the contact information to be deleted.
    * @return True if the deletion was successful, false otherwise.
    */
   boolean deleteContactInfo(int supplierId, String name);

   /**
    * Retrieves a list of all contact information entries for a specific supplier.
    *
    * @param supplierId The ID of the supplier whose contact information is to be
    *                   retrieved.
    * @return A list of ContactInfoDTO objects representing the contact information
    *         for the supplier.
    */
   List<ContactInfoDTO> getContactInfosBySupplierId(int supplierId);
}
