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
   }

   @Override
   public ServiceResponse<?> execute(String serviceOption, String data) {
      return ServiceResponse.error("Not implemented");
   }

   private ServiceResponse<Order> addOrder(String json) {
      return ServiceResponse.error("Not implemented");
   }

   private ServiceResponse<Order> updateOrder(String json) {
      return ServiceResponse.error("Not implemented");
   }

   private ServiceResponse<Boolean> removeOrder(String json) {
      return ServiceResponse.error("Not implemented");
   }

   private ServiceResponse<Order> getOrder(String json) {
      return ServiceResponse.error("Not implemented");
   }

   private ServiceResponse<List<Order>> viewAllOrders(String ignored) {
      return ServiceResponse.error("Not implemented");
   }
}
