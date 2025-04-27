package ServiceLayer;

import java.util.HashMap;
import java.util.function.Function;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;

public abstract class BaseService {
   protected HashMap<String, Function<String, String>> serviceFucntions = new HashMap<>();
   protected ObjectMapper objectMapper;

   public BaseService() {
      this.objectMapper = new ObjectMapper();
      serviceFucntions.put("?", this::commandDoesNotExist);
      objectMapper.registerModule(new JavaTimeModule());
      objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
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

   /**
    * Try to bind the JSON into `targetType`. If it fails, return an error
    * ServiceResponse<Boolean>. Otherwise return a “true” value so you can go
    * on and call the facade.
    */
   public <T> ServiceResponse<Boolean> validateBinding(String json, Class<T> targetType) {
      try {
         // this will invoke the same @JsonCreator constructor
         objectMapper.readValue(json, targetType);
         return new ServiceResponse<>(true, "");
      } catch (JsonProcessingException e) {
         // Jackson tells you exactly which field/type failed
         return new ServiceResponse<>(
               false,
               "Type conversion error: " + e.getOriginalMessage());
      }
   }

   protected String injectKeyValueOnJsonString(String json, String key, String value) {
      try {
         HashMap<String, String> map = objectMapper.readValue(json, HashMap.class);
         map.put(key, value);
         json = objectMapper.writeValueAsString(map);
         return json;
      } catch (JsonProcessingException e) {
         throw new RuntimeException("Failed to inject key-value pair", e);
      }
   }
}
