// SystemService.java
package ServiceLayer;

import java.util.List;

import DomainLayer.*;
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