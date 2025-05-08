// OrderService.java
package ServiceLayer;

import java.util.List;
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
      serviceFunctions.put("viewAllOrders", this::viewAllOrders);
      serviceFunctions.put("?", this::commandDoesNotExist);
   }

   @Override
   public ServiceResponse<?> execute(String serviceOption, String data) {
      Function<String, ServiceResponse<?>> fn = serviceFunctions.getOrDefault(serviceOption, this::commandDoesNotExist);
      return fn.apply(data);
   }

   private ServiceResponse<Order> addOrder(String json) {
      // validate the shape before passing to domain
      ServiceResponse<Boolean> valid = validateBinding(json, Order.class);
      if (Boolean.FALSE.equals(valid.getValue())) {
         return ServiceResponse.error(valid.getError());
      }

      try {
         Order created = orderFacade.addOrder(json);
         return ServiceResponse.ok(created);
      } catch (Exception e) {
         return ServiceResponse.error(e.getMessage());
      }
   }

   private ServiceResponse<Order> updateOrder(String json) {
      ServiceResponse<Boolean> valid = validateBinding(json, Order.class);
      if (Boolean.FALSE.equals(valid.getValue())) {
         return ServiceResponse.error(valid.getError());
      }

      try {
         Order updated = orderFacade.updateOrder(objectMapper.readValue(json, Order.class));
         return ServiceResponse.ok(updated);
      } catch (Exception e) {
         return ServiceResponse.error(e.getMessage());
      }
   }

   private ServiceResponse<Boolean> removeOrder(String json) {
      // assume json is just {"orderId":"..."}
      ServiceResponse<Boolean> valid = validateBinding(json, java.util.Map.class);
      if (Boolean.FALSE.equals(valid.getValue())) {
         return ServiceResponse.error(valid.getError());
      }

      try {
         UUID ordId = objectMapper.readValue(objectMapper.readTree(json).get("orderId").asText(), UUID.class);
         orderFacade.deleteOrder(ordId);
         return ServiceResponse.ok(true);
      } catch (Exception e) {
         return ServiceResponse.error("Order was not deleted: " + e.getMessage());
      }
   }

   private ServiceResponse<Order> getOrder(String json) {
      // assume json is {"orderId":"..."} or plain ID string
      ServiceResponse<Boolean> valid = validateBinding(json, java.util.Map.class);
      if (Boolean.FALSE.equals(valid.getValue())) {
         return ServiceResponse.error(valid.getError());
      }
      try {
         String ordIdStr = objectMapper.readTree(json).get("orderId").asText();
         Order found = orderFacade.getOrder(UUID.fromString(ordIdStr));
         if (found != null) {
            return ServiceResponse.ok(found);
         } else {
            return ServiceResponse.error("Order not found");
         }
      } catch (Exception e) {
         return ServiceResponse.error(e.getMessage());
      }
   }

   private ServiceResponse<List<Order>> viewAllOrders(String ignored) {
      ServiceResponse<List<Order>> resp;
      try {
         List<Order> all = orderFacade.listOrders();
         resp = ServiceResponse.ok(all);
      } catch (Exception e) {
         resp = ServiceResponse.error("Failed to list orders: " + e.getMessage());
      }
      return resp;
   }
}
