package DomainLayer;

import java.util.*;

import DomainLayer.Classes.Supplier;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

public class SupplierFacade {
   // In-memory map of suppliers by their UUID
   private final Map<UUID, Supplier> suppliers = new HashMap<>();
   private ObjectMapper mapper = new ObjectMapper();

   public void addSupplier(String json) {
      try {
         Supplier sup = mapper.readValue(json, Supplier.class);
         suppliers.put(sup.getSupplierId(), sup);
      } catch (MismatchedInputException e) {
         // this will show you exactly which JSON field was unexpected or
         // couldn’t map to the constructor
         System.err.println("JSON parse error: " + e.getOriginalMessage());
         System.err.println(" at: " + e.getPathReference());
         e.printStackTrace();
         throw new RuntimeException("Supplier JSON parse failed", e);
      } catch (Exception e) {
         // e.printStackTrace();
         throw new RuntimeException("Supplier JSON parse failed", e);
      }
   }

   /**
    * Removes a supplier by its id.
    *
    * @param json the UUID of the supplier to remove
    * @return true if the supplier existed and was removed, false otherwise
    */
   public boolean removeSupplier(String json) {
      try {
         Map<String, String> map = mapper.readValue(json, Map.class);
         UUID id = UUID.fromString(map.get("supplierId"));
         if (suppliers.containsKey(id)) {
            suppliers.remove(id);
            return true;
         }
      } catch (Exception e) {
         // e.printStackTrace();
         throw new RuntimeException("Supplier JSON parse failed", e);
      }
      return false;
   }

   /**
    * Updates an existing supplier's information.
    * The supplier's id must already exist.
    *
    * @param s the Supplier with updated information (must have id set)
    * @return true if the supplier existed and was updated, false otherwise
    */
   public boolean updateSupplier(Supplier s) {
      UUID id = s.getSupplierId();
      if (id == null || !suppliers.containsKey(id)) {
         return false;
      }
      suppliers.put(id, s);
      return true;
   }

   /**
    * Retrieves a supplier by its id.
    *
    * @param jsonOfID the UUID of the supplier
    * @return an Optional containing the Supplier if found, or empty if not found
    */
   public Supplier getSupplier(String jsonOfID) {
      try {
         Map<String, String> map = mapper.readValue(jsonOfID, Map.class);
         UUID id = UUID.fromString(map.get("supplierId"));
         return suppliers.get(id);
      } catch (IllegalArgumentException e) {
         throw new RuntimeException("Invalid Supplier ID or UUID format: " + jsonOfID, e);
      } catch (Exception e) {
         // e.printStackTrace();
         throw new RuntimeException("Supplier JSON parse failed", e);
      }
   }

   /**
    * Lists all suppliers.
    *
    * @return a list of all Supplier entities
    */
   public List<Supplier> listSuppliers() {
      return new ArrayList<>(suppliers.values());
   }

   public boolean supplierExists(String json) {
      try {
         Map<String, String> map = mapper.readValue(json, Map.class);
         UUID id = UUID.fromString(map.get("supplierId"));
         return suppliers.containsKey(id);
      } catch (IllegalArgumentException e) {
         throw new RuntimeException("Invalid Supplier ID or UUID format: " + json, e);
      } catch (Exception e) {
         // e.printStackTrace();
         throw new RuntimeException("Supplier JSON parse failed", e);
      }

   }
}
