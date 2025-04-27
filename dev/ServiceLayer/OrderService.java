// OrderService.java
package ServiceLayer;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.Function;

import DomainLayer.OrderFacade;
import com.fasterxml.jackson.databind.ObjectMapper;

import ServiceLayer.Interfaces_and_Abstracts.IService;
import ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;
import DomainLayer.Classes.Order;

public class OrderService extends BaseService implements IService {
   private final HashMap<String, Function<String, String>> serviceFunctions = new HashMap<>();
   private final ObjectMapper objectMapper = new ObjectMapper();
   private final OrderFacade orderFacade;

   public OrderService(OrderFacade orderFacade) {
      this.orderFacade = orderFacade;
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
      ServiceResponse<Order> resp;
      try {
         return new ServiceResponse<String>(null, "Sorry, this method is not implemented yet.").toString();
         // Order createdOrder = orderFacade.createOrder(json);
         // resp = new ServiceResponse<>(createdOrder, "");
      } catch (Exception e) {
         resp = new ServiceResponse<>(null, e.getMessage());
      }
      return serialize(resp);
   }

   private String updateOrder(String json) {
      ServiceResponse<Order> resp;
      try {
         Order order = objectMapper.readValue(json, Order.class);
         Order updatedOrder = orderFacade.updateOrder(order);
         resp = new ServiceResponse<>(updatedOrder, "");
      } catch (Exception e) {
         resp = new ServiceResponse<>(null, e.getMessage());
      }
      return serialize(resp);
   }

   private String removeOrder(String json) {
      ServiceResponse<Boolean> resp;
      try {
         UUID orderId = UUID.fromString(json.replace("\"", ""));
         orderFacade.deleteOrder(orderId);
         resp = new ServiceResponse<>(true, "");
      } catch (Exception e) {
         resp = new ServiceResponse<>(false, e.getMessage());
      }
      return serialize(resp);
   }

   private String getOrder(String json) {
      ServiceResponse<Order> resp;
      try {
         UUID orderId = UUID.fromString(json.replace("\"", ""));
         Order order = orderFacade.getOrder(orderId);
         resp = new ServiceResponse<>(order, "");
      } catch (Exception e) {
         resp = new ServiceResponse<>(null, e.getMessage());
      }
      return serialize(resp);
   }

}