package ServiceLayer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import DomainLayer.Supplier;
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
      serviceFunctions.put("?", this::commandDoesNotExist);
   }

   @Override
   public String execute(String serviceOption, String data) {
      Function<String, String> fn = serviceFunctions.getOrDefault(serviceOption, this::commandDoesNotExist);
      return fn.apply(data);
   }

   private String addSupplier(String creationJson) {
      ServiceResponse<Boolean> resp;
      try {
         boolean res = facade.addSupplier(creationJson);
         resp = new ServiceResponse<>(res, "");
      } catch (Exception e) {
         resp = new ServiceResponse<>(null, e.getMessage());
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
         // TODO: implement retrieval logic
         resp = new ServiceResponse<>(null, "Not implemented yet");
      } catch (Exception e) {
         resp = new ServiceResponse<>(null, e.getMessage());
      }
      return serialize(resp);
   }
}
