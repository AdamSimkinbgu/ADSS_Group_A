package DomainLayer;

import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;
import java.util.*;

import DomainLayer.Classes.Supplier;
import DomainLayer.Classes.SupplierProduct;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
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

   public boolean addProductToSupplier(String json) {
      try {
         // payload must include supplierId + product fields
         var node = mapper.readTree(json);
         UUID sid = UUID.fromString(node.get("supplierId").asText());
         Supplier s = suppliers.get(sid);
         if (s == null)
            return false;

         SupplierProduct p = mapper.treeToValue(node, SupplierProduct.class);
         s.getProducts().add(p);
         supplierProducts.put(p.getProductId(), p);
         return true;
      } catch (MismatchedInputException e) {
         throw new RuntimeException("Product JSON parse failed: " + e.getOriginalMessage(), e);
      } catch (Exception e) {
         throw new RuntimeException("Add product failed", e);
      }
   }

   public boolean updateProductOnSupplier(String json) {
      try {
         var node = mapper.readTree(json);
         UUID sid = UUID.fromString(node.get("supplierId").asText());
         String pid = node.get("productId").asText();
         Supplier s = suppliers.get(sid);
         if (s == null)
            return false;

         SupplierProduct updated = mapper.treeToValue(node, SupplierProduct.class);
         var prods = s.getProducts();
         for (int i = 0; i < prods.size(); i++) {
            if (prods.get(i).getProductId().equals(pid)) {
               prods.set(i, updated);
               return true;
            }
         }
         return false;
      } catch (Exception e) {
         throw new RuntimeException("Update product failed", e);
      }
   }

   public boolean removeProductFromSupplier(String json) {
      try {
         var map = mapper.readValue(json, Map.class);
         UUID sid = UUID.fromString((String) map.get("supplierId"));
         String pid = (String) map.get("productId");
         Supplier s = suppliers.get(sid);
         if (s == null)
            return false;
         return s.getProducts().removeIf(p -> p.getProductId().equals(pid));
      } catch (Exception e) {
         throw new RuntimeException("Remove product failed", e);
      }
   }

   public List<SupplierProduct> listProductsForSupplier(String json) {
      try {
         var map = mapper.readValue(json, Map.class);
         UUID sid = UUID.fromString((String) map.get("supplierId"));
         Supplier s = suppliers.get(sid);
         if (s == null)
            return List.of();
         return new ArrayList<>(s.getProducts());
      } catch (Exception e) {
         throw new RuntimeException("List products failed", e);
      }
   }

   public boolean addAgreementToSupplier(String supUpdate) {
      try {
         var node = mapper.readTree(supUpdate);
         UUID sid = UUID.fromString(node.get("supplierId").asText());
         Supplier s = suppliers.get(sid);
         if (s == null)
            throw new RuntimeException("Supplier not found: " + sid);
         String agreementId = node.get("agreementId").asText();
         s.getAgreements().add(UUID.fromString(agreementId));
         return true;
      } catch (Exception e) {
         throw new RuntimeException("Add agreement failed", e);
      }
   }
}
