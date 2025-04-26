// SystemService.java
package ServiceLayer;

import java.util.HashMap;
import java.util.function.Function;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ServiceLayer.Interfaces_and_Abstracts.IService;
import ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;

public class SystemService implements IService {
   private final HashMap<String, Function<String, String>> serviceFunctions = new HashMap<>();
   private final ObjectMapper objectMapper = new ObjectMapper();

   public SystemService() {
      serviceFunctions.put("fakeLoadData", this::fakeLoadData);
      serviceFunctions.put("?", this::commandDoesNotExist);
   }

   @Override
   public String execute(String serviceOption, String data) {
      Function<String, String> fn = serviceFunctions.getOrDefault(serviceOption, this::commandDoesNotExist);
      return fn.apply(data);
   }

   private String fakeLoadData(String json) {
      // simulated payload
      String dummy = "System initialized with sample data";
      ServiceResponse<String> resp = new ServiceResponse<>(dummy, "");
      try {
         return objectMapper.writeValueAsString(resp);
      } catch (JsonProcessingException e) {
         return "{\"value\":null,\"error\":\"Serialization error\"}";
      }
   }

   private String commandDoesNotExist(String data) {
      return "{\"value\":null,\"error\":\"Command does not exist\"}";
   }
}