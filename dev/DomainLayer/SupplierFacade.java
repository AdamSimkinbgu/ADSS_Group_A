package DomainLayer;

import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;
import java.util.*;

import DomainLayer.Classes.Supplier;
import DomainLayer.Classes.SupplierProduct;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class SupplierFacade {
   // In-memory map of suppliers by their UUID
   private final Map<UUID, Supplier> suppliers = new HashMap<>();
   private final Map<UUID, SupplierProduct> supplierProducts = new HashMap<>();
   private ObjectMapper mapper = new ObjectMapper();

   public void addSupplier(String json) {
      try {
         ObjectNode root = (ObjectNode) mapper.readTree(json);
         Supplier sup = mapper.treeToValue(root, Supplier.class);
         suppliers.put(sup.getSupplierId(), sup);
      } catch (Exception e) {
         throw new RuntimeException("JSON parse error: ", e);
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

   public boolean updateSupplier(Supplier updated) {
      UUID id = updated.getSupplierId();
      if (!suppliers.containsKey(id)) {
         return false;
      }
      suppliers.put(id, updated);
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

   public boolean supplierExists(String json) {
      try {
         Map<String, String> map = mapper.readValue(json, Map.class);
         UUID id;
         String candidate = map.get("supplierId");
         try {
            id = UUID.fromString(candidate);
         } catch (IllegalArgumentException ex) {
            id = UUID.nameUUIDFromBytes(
                  candidate.getBytes(StandardCharsets.UTF_8));
         } catch (Exception e) {
            throw new InvalidParameterException("Can not parse ID");
         }
         return suppliers.containsKey(id);
      } catch (IllegalArgumentException e) {
         throw new RuntimeException("Invalid Supplier ID or UUID format: " + json, e);
      } catch (Exception e) {
         // e.printStackTrace();
         throw new RuntimeException("Supplier JSON parse failed", e);
      }

   }

   public List<Supplier> getSuppliersWithFullDetail() {
      return new ArrayList<>(suppliers.values());
   }
}
