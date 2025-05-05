// SystemService.java
package ServiceLayer;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import DomainLayer.*;
import DomainLayer.Classes.Agreement;
import DomainLayer.Classes.Supplier;
import ServiceLayer.Interfaces_and_Abstracts.IService;
import ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;

public class SystemService extends BaseService implements IService {
   private final SupplierFacade supplierFacade;
   private final OrderFacade orderFacade;
   private final AgreementFacade agreementFacade;

   public SystemService(SupplierFacade supplierFacade, OrderFacade orderFacade, AgreementFacade agreementFacade) {
      // Initialize the service functions
      this.supplierFacade = supplierFacade;
      this.orderFacade = orderFacade;
      this.agreementFacade = agreementFacade;
      serviceFunctions.put("loadData", this::loadData);
      serviceFunctions.put("noData", this::noData);
      serviceFunctions.put("getAllData", this::getAllData);
      serviceFunctions.put("?", this::commandDoesNotExist);
   }

   @Override
   public ServiceResponse<?> execute(String serviceOption, String data) {
      Function<String, ServiceResponse<?>> fn = serviceFunctions.getOrDefault(serviceOption, this::commandDoesNotExist);
      return fn.apply(data);
   }

   private ServiceResponse<String> loadData(String ignored) {
      ServiceResponse<String> resp;
      try (InputStream in = getClass().getResourceAsStream("/data.json")) {
         if (in == null)
            throw new IllegalStateException("data.json not on classpath");

         JsonNode root = objectMapper.readTree(in);

         // 1) load all suppliers and keep their IDs
         for (JsonNode s : root.withArray("suppliers")) {
            supplierFacade.addSupplier(s.toString());
         }

         List<UUID> supplierIds = supplierFacade.getSuppliersWithFullDetail()
               .stream().map(Supplier::getSupplierId).toList();

         int i = 0;
         // 2) load all agreements
         for (JsonNode a : root.withArray("agreements")) {
            // extract supplierId
            String sid = supplierIds.get(i++).toString();
            Supplier sup = supplierFacade.getSupplier(
                  "{\"supplierId\":\"" + sid + "\"}");
            if (sup == null) {
               throw new IllegalStateException("No supplier for ID: " + sid);
            }

            // inject the name and ID into the agreement
            ObjectNode copy = ((ObjectNode) a).deepCopy();
            copy.put("supplierName", sup.getName());
            copy.put("supplierId", sid);

            // create in the facade
            agreementFacade.createAgreement(copy.toString());
         }

         resp = ServiceResponse.ok("Data loaded successfully");

      } catch (Exception e) {
         resp = ServiceResponse.error("Load failed: " + e.getMessage());
      }
      return resp;
   }

   private ServiceResponse<String> noData(String json) {
      // simulated payload
      String dummy = "No data loaded";
      ServiceResponse<String> resp = ServiceResponse.ok(dummy);
      return resp;
   }

   private ServiceResponse<String> getAllData(String ignored) {
      ServiceResponse<String> resp;
      ObjectNode root;
      try {
         // 1) get lists from facades
         List<Supplier> allSuppliers = supplierFacade.getSuppliersWithFullDetail();
         List<Agreement> allAgreements = agreementFacade.getAgreementsWithFullDetail();
         // (and later on orders, etc.)

         // 2) build a single JSON object with arrays
         root = objectMapper.createObjectNode();
         root.set("suppliers", objectMapper.valueToTree(allSuppliers));
         root.set("agreements", objectMapper.valueToTree(allAgreements));
         // root.set("orders", mapper.valueToTree(allOrders));

         // 3) resp as ServiceResponse
         resp = ServiceResponse.ok(root.toPrettyString());
      } catch (Exception e) {
         resp = ServiceResponse.error("Get all data failed: " + e.getMessage());
      }

      // 4) return the serialized response
      return resp;
   }
}