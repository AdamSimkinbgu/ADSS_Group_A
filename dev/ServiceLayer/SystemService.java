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
import ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;

public class SystemService extends BaseService {
   private final SupplierFacade supplierFacade;
   private final OrderFacade orderFacade;
   private final AgreementFacade agreementFacade;

   public SystemService(SupplierFacade supplierFacade, OrderFacade orderFacade, AgreementFacade agreementFacade) {
      this.supplierFacade = supplierFacade;
      this.orderFacade = orderFacade;
      this.agreementFacade = agreementFacade;
   }

   private ServiceResponse<String> loadData(String ignored) {
      return ServiceResponse.fail(List.of("Not implemented"));
   }

   private ServiceResponse<String> noData(String json) {
      return ServiceResponse.fail(List.of("Not implemented"));
   }

   private ServiceResponse<String> getAllData(String ignored) {
      return ServiceResponse.fail(List.of("Not implemented"));
   }
}