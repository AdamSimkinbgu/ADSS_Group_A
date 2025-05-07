// OrderService.java
package ServiceLayer;

import java.util.UUID;
import java.util.function.Function;

import DomainLayer.OrderFacade;
import com.fasterxml.jackson.databind.ObjectMapper;

import ServiceLayer.Interfaces_and_Abstracts.IService;
import ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;
import DomainLayer.Classes.Order;

public class OrderService extends BaseService implements IService {
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
   public ServiceResponse<?> execute(String serviceOption, String data) {
      Function<String, ServiceResponse<?>> fn = serviceFunctions.getOrDefault(serviceOption, this::commandDoesNotExist);
      return fn.apply(data);
   }

   private ServiceResponse<?> addOrder(String json) {
      ServiceResponse<Order> resp;
      try {
         return ServiceResponse.error("Sorry, this method is not implemented yet.");
      } catch (Exception e) {
         resp = ServiceResponse.error(e.getMessage());
      }
      return resp;
   }

   private ServiceResponse<?> updateOrder(String json) {
      ServiceResponse<Order> resp;
      try {
         Order order = objectMapper.readValue(json, Order.class);
         Order updatedOrder = orderFacade.updateOrder(order);
         resp = ServiceResponse.ok(updatedOrder);
      } catch (Exception e) {
         resp = ServiceResponse.error(e.getMessage());
      }
      return resp;
   }

   private ServiceResponse<?> removeOrder(String json) {
      ServiceResponse<Boolean> resp;
      try {
         UUID orderId = UUID.fromString(json.replace("\"", ""));
         orderFacade.deleteOrder(orderId);
         resp = ServiceResponse.ok(true);
      } catch (Exception e) {
         resp = ServiceResponse.error(e.getMessage());
      }
      return resp;
   }

   private ServiceResponse<?> getOrder(String json) {
      ServiceResponse<Order> resp;
      try {
         UUID orderId = UUID.fromString(json.replace("\"", ""));
         Order order = orderFacade.getOrder(orderId);
         resp = ServiceResponse.ok(order);
      } catch (Exception e) {
         resp = ServiceResponse.error(e.getMessage());
      }
      return resp;
   }

}