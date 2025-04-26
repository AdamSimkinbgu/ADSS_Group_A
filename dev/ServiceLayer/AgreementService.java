// AgreementService.java
package ServiceLayer;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.Function;

import DomainLayer.AgreementFacade;
import DomainLayer.Classes.Supplier;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ServiceLayer.Interfaces_and_Abstracts.IService;
import ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;
import DomainLayer.Classes.Agreement;

public class AgreementService extends  BaseService implements IService {
   private final HashMap<String, Function<String, String>> serviceFunctions = new HashMap<>();
   private final ObjectMapper objectMapper = new ObjectMapper();
   private final  AgreementFacade facade;

   public AgreementService(AgreementFacade agreementFacade) {
      facade = agreementFacade;
      serviceFunctions.put("addAgreement", this::addAgreement);
      serviceFunctions.put("updateAgreement", this::updateAgreement);
      serviceFunctions.put("removeAgreement", this::removeAgreement);
      serviceFunctions.put("getAgreement", this::getAgreement);
      serviceFunctions.put("?", this::commandDoesNotExist);
   }

   @Override
   public String execute(String serviceOption, String data) {
      Function<String, String> fn = serviceFunctions.getOrDefault(serviceOption, this::commandDoesNotExist);
      return fn.apply(data);
   }

   private String addAgreement(String json) {
      ServiceResponse<UUID> resp;
      try {
         UUID id = facade.createAgreement(json);
         resp = new ServiceResponse<>(id, "");
      } catch (Exception e) {
         resp = new ServiceResponse<>(null, e.getMessage());
      }
      return serialize(resp);
   }


   private String updateAgreement(String json) {
      ServiceResponse<Agreement> resp;
      try {
         // TODO: implement update logic
         resp = new ServiceResponse<>(null, "Not implemented yet");
      } catch (Exception e) {
         resp = new ServiceResponse<>(null, e.getMessage());
      }
      return serialize(resp);
   }


   private String removeAgreement(String json) {
      ServiceResponse<Agreement> resp;
      try {
         // TODO: implement update logic
         resp = new ServiceResponse<>(null, "Not implemented yet");
      } catch (Exception e) {
         resp = new ServiceResponse<>(null, e.getMessage());
      }
      return serialize(resp);
   }

   private String getAgreement(String json) {
      ServiceResponse<Agreement> resp;
      try {
         // TODO: implement update logic
         resp = new ServiceResponse<>(null, "Not implemented yet");
      } catch (Exception e) {
         resp = new ServiceResponse<>(null, e.getMessage());
      }
      return serialize(resp);
   }

//   private String commandDoesNotExist(String data) {
//      return "{\"value\":null,\"error\":\"Command does not exist\"}";
//   }
}