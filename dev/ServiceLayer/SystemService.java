// SystemService.java
package ServiceLayer;

import java.io.InputStream;
import java.util.HashMap;
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

   private String loadData(String unused) {
      ServiceResponse<String> resp;
      try (InputStream in = getClass()
            .getResourceAsStream("/data.json")) {

         if (in == null) {
            throw new IllegalStateException("data.json not on classpath");
         }

         // parse the whole file
         JsonNode root = objectMapper.readTree(in);

         // 1) load suppliers first
         for (JsonNode supNode : root.withArray("suppliers")) {
            String supJson = supNode.toString();
            supplierFacade.addSupplier(supJson);
         }

         // 2) then load agreements
         for (JsonNode agrNode : root.withArray("agreements")) {
            String agrJson = agrNode.toString();
            // inject supplierName if needed:
            String sid = agrNode.get("supplierId").asText();
            Supplier sup = supplierFacade.getSupplier(
                  "{\"supplierId\":\"" + sid + "\"}");
            ObjectNode tree = (ObjectNode) agrNode;
            tree.put("supplierName", sup.getName());
            agreementFacade.createAgreement(tree.toString());
         }

         resp = new ServiceResponse<>("Data loaded", "");
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