package ServiceLayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import DomainLayer.Classes.Supplier;
import DomainLayer.SupplierFacade;
import ServiceLayer.Interfaces_and_Abstracts.IService;
import ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;

/**
 * Service layer for Supplier operations, wrapping all responses in a
 * ServiceResponse<T> envelope.
 */
public class SupplierService extends BaseService implements IService {
   private final SupplierFacade facade;
   private final Map<String, Function<String, String>> serviceFunctions = new HashMap<>();

   public SupplierService(SupplierFacade facade) {
      this.facade = facade;
      serviceFunctions.put("addSupplier", this::addSupplier);
      serviceFunctions.put("updateSupplier", this::updateSupplier);
      serviceFunctions.put("removeSupplier", this::removeSupplier);
      serviceFunctions.put("getSupplierDetails", this::getSupplierDetails);
      serviceFunctions.put("getAllSuppliers", this::getAllSuppliers);
      serviceFunctions.put("?", this::commandDoesNotExist);
   }

   @Override
   public String execute(String serviceOption, String data) {
      Function<String, String> fn = serviceFunctions.getOrDefault(serviceOption, this::commandDoesNotExist);
      return fn.apply(data);
   }

   // example function
   private String addSupplier(String creationJson) {
      // make sure json is good
      ServiceResponse<Boolean> pre = validateBinding(creationJson, Supplier.class);
      // if not, return jackson error message
      if (pre.getValue() == null || !pre.getValue()) {
         return serialize(pre);
      }

      // use facade to try and add supplier
      ServiceResponse<Boolean> resp;
      try {
         facade.addSupplier(creationJson);
         resp = new ServiceResponse<>(true, "");
      } catch (Exception e) {
         resp = new ServiceResponse<>(false, e.getMessage());
      }
      return serialize(resp);
   }

   private String updateSupplier(String updateJson) {
      ServiceResponse<Supplier> resp;
      try {
         // TODO: implement update logic
         resp = new ServiceResponse<>(null, "Not implemented yet");
      } catch (Exception e) {
         resp = new ServiceResponse<>(null, e.getMessage());
      }
      return serialize(resp);
   }

   private String removeSupplier(String id) {
      ServiceResponse<Boolean> resp;
      try {
         boolean deleted = facade.removeSupplier(id);
         if (deleted) {
            resp = new ServiceResponse<>(true, "");
         } else {
            resp = new ServiceResponse<>(false, "No supplier with ID: " + id);
         }
      } catch (Exception e) {
         resp = new ServiceResponse<>(false, e.getMessage());
      }
      return serialize(resp);
   }

   private String getSupplierDetails(String id) {
      ServiceResponse<Supplier> resp;
      try {
         Supplier supplier = facade.getSupplier(id);
         if (supplier != null) {
            resp = new ServiceResponse<>(supplier, "");
         } else {
            resp = new ServiceResponse<>(null, "No supplier with ID: " + id);
         }
      } catch (Exception e) {
         resp = new ServiceResponse<>(null, e.getMessage());
      }
      return serialize(resp);
   }

   private String getAllSuppliers(String id) {
      ServiceResponse<Map<String, String>> resp;
      try {
         List<Supplier> suppliers = facade.listSuppliers();
         Map<String, String> suppliersMap = new HashMap<>();
         for (Supplier supplier : suppliers) {
            suppliersMap.put(supplier.getSupplierId().toString(), supplier.getName());
         }
         resp = new ServiceResponse<>(suppliersMap, "");
      } catch (Exception e) {
         resp = new ServiceResponse<>(null, e.getMessage());
      }
      return serialize(resp);
   }
}
