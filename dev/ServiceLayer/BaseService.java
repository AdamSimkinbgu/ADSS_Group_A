package ServiceLayer;

import java.util.HashMap;
import java.util.function.Function;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;

public abstract class BaseService {
   protected HashMap<String, Function<String, String>> serviceFucntions = new HashMap<>();
   protected ObjectMapper objectMapper;

   public BaseService() {
      this.objectMapper = new ObjectMapper();
      serviceFucntions.put("?", this::commandDoesNotExist);
   }

   public String commandDoesNotExist(String data) {
      return "Command does not exist";
   }

   /**
    * Helper to serialize the ServiceResponse envelope.
    */
   protected <T> String serialize(ServiceResponse<T> resp) {
      try {
         return objectMapper.writeValueAsString(resp);
      } catch (JsonProcessingException e) {
         return "{\"value\":null,\"error\":\"Serialization error\"}";
      }
   }
}
