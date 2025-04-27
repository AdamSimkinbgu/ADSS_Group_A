// SystemService.java
package ServiceLayer;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import DomainLayer.*;
import DomainLayer.Classes.Supplier;
import ServiceLayer.Interfaces_and_Abstracts.IService;
import ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;

public class SystemService extends BaseService implements IService {
   private final HashMap<String, Function<String, String>> serviceFunctions = new HashMap<>();
   private final ObjectMapper objectMapper = new ObjectMapper();
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
   public String execute(String serviceOption, String data) {
      Function<String, String> fn = serviceFunctions.getOrDefault(serviceOption, this::commandDoesNotExist);
      return fn.apply(data);
   }

   private String loadData(String ignored) {
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

            // inject the name
            ObjectNode copy = ((ObjectNode) a).deepCopy();
            copy.put("supplierName", sup.getName());

            // create in the facade
            agreementFacade.createAgreement(copy.toString());
         }

         resp = new ServiceResponse<>("Data loaded successfully", "");

      } catch (Exception e) {
         resp = new ServiceResponse<>(null, "Load failed: " + e.getMessage());
      }
      return serialize(resp);
   }

   private String noData(String json) {
      // simulated payload
      String dummy = "No data loaded";
      ServiceResponse<String> resp = new ServiceResponse<>(dummy, "");
      return serialize(resp);
   }

   private String getAllData(String json) {
      ServiceResponse<String> resp;
      try {
         // Get all data from the system
         String allData = supplierFacade.getSuppliersWithFullDetail() + "\n"
               + agreementFacade.getAgreementsWithFullDetail() + "\n";
         // + orderFacade.getOrdersWithFullDetail() + "\n";
         resp = new ServiceResponse<>(allData, "");
      } catch (Exception e) {
         resp = new ServiceResponse<>(null, "Get all data failed: " + e.getMessage());
      }
      return serialize(resp);
   }
}