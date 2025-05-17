// SystemService.java
package ServiceLayer;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
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
   // private final OrderFacade orderFacade;
   private final AgreementFacade agreementFacade;

   public SystemService(SupplierFacade supplierFacade, OrderFacade orderFacade, AgreementFacade agreementFacade) {
      this.supplierFacade = supplierFacade;
      // this.orderFacade = orderFacade;
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
      List<String> allErrors = new ArrayList<>();

      try (InputStream in = getClass().getResourceAsStream("/data.json")) {
         if (in == null) {
            throw new IllegalStateException("data.json not on classpath");
         }

         JsonNode root = objectMapper.readTree(in);

         // ─── Suppliers ───────────────────────────────────────────────────
         int idx = 0;
         for (JsonNode s : root.withArray("suppliers")) {
            idx++;
            String supplierJson = s.toString();

            ServiceResponse<Void> valSup = validatePatchViaSetters(supplierJson, Supplier.class);
            if (valSup.getError() != null && !valSup.getError().isEmpty()) {
               allErrors.add("Supplier[" + idx + "]: " + valSup.getError());
               continue;
            }

            supplierFacade.addSupplier(supplierJson);
         }

         // ─── Agreements ─────────────────────────────────────────────────
         idx = 0;
         for (JsonNode a : root.withArray("agreements")) {
            idx++;
            String agreementJson = a.toString();

            ServiceResponse<Void> valAgr = validateJsonPayload(agreementJson, Agreement.class);
            if (valAgr.getError() != null && !valAgr.getError().isEmpty()) {
               allErrors.add("Agreement[" + idx + "]: " + valAgr.getError());
               continue;
            }

            agreementFacade.createAgreement(agreementJson);
         }

         // ─── Build summary response ──────────────────────────────────────
         if (!allErrors.isEmpty()) {
            return ServiceResponse.error(
                  "Load completed with errors: " + String.join("; ", allErrors));
         } else {
            return ServiceResponse.ok("Data loaded successfully");
         }

      } catch (Exception e) {
         return ServiceResponse.error("Load failed: " + e.getMessage());
      }
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

      return resp;
   }
}