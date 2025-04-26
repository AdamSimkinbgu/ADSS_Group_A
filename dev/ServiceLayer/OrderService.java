// OrderService.java
package ServiceLayer;

import java.util.HashMap;
import java.util.function.Function;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ServiceLayer.Interfaces_and_Abstracts.IService;
import ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;
import DomainLayer.Classes.Order;

public class OrderService implements IService {
   private final HashMap<String, Function<String, String>> serviceFunctions = new HashMap<>();
   private final ObjectMapper objectMapper = new ObjectMapper();

   public OrderService() {
      serviceFunctions.put("addOrder", this::addOrder);
      serviceFunctions.put("updateOrder", this::updateOrder);
      serviceFunctions.put("removeOrder", this::removeOrder);
      serviceFunctions.put("getOrder", this::getOrder);
      serviceFunctions.put("?", this::commandDoesNotExist);
   }

   @Override
   public String execute(String serviceOption, String data) {
      Function<String, String> fn = serviceFunctions.getOrDefault(serviceOption, this::commandDoesNotExist);
      return fn.apply(data);
   }

   private String addOrder(String json) {
      ServiceResponse<Order> resp = new ServiceResponse<>(null, "Not implemented yet");
      try {
         return objectMapper.writeValueAsString(resp);
      } catch (JsonProcessingException e) {
         return "{\"value\":null,\"error\":\"Serialization error\"}";
      }
   }

   private String updateOrder(String json) {
      ServiceResponse<Order> resp = new ServiceResponse<>(null, "Not implemented yet");
      try {
         return objectMapper.writeValueAsString(resp);
      } catch (JsonProcessingException e) {
         return "{\"value\":null,\"error\":\"Serialization error\"}";
      }
   }

   private String removeOrder(String json) {
      ServiceResponse<Boolean> resp = new ServiceResponse<>(false, "Not implemented yet");
      try {
         return objectMapper.writeValueAsString(resp);
      } catch (JsonProcessingException e) {
         return "{\"value\":null,\"error\":\"Serialization error\"}";
      }
   }

   private String getOrder(String json) {
      ServiceResponse<Order> resp = new ServiceResponse<>(null, "Not implemented yet");
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