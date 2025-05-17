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

   public void addSupplier(String json) {
      // TODO: Implement this method
   }

   public boolean removeSupplier(String json) {
      return false; // TODO: Implement this method
   }

   public boolean updateSupplier(Supplier updated) {
      return false; // TODO: Implement this method
   }

   public Supplier getSupplier(String jsonOfID) {
      return null; // TODO: Implement this method
   }

   public boolean supplierExists(String json) {
      return false; // TODO: Implement this method
   }

   public List<Supplier> getSuppliersWithFullDetail() {
      return new ArrayList<>(suppliers.values());
   }

   public boolean addProductToSupplier(String json) {
      return false; // TODO: Implement this method

   }

   public boolean updateProductOnSupplier(String json) {
      return false; // TODO: Implement this method
   }

   public boolean removeProductFromSupplier(String json) {
      return false; // TODO: Implement this method
   }

   public List<SupplierProduct> listProductsForSupplier(String json) {
      return null; // TODO: Implement this method
   }

   public boolean addAgreementToSupplier(String supUpdate) {
      return false; // TODO: Implement this method
   }
}
