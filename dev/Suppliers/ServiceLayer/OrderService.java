// OrderService.java
package Suppliers.ServiceLayer;

import java.util.List;

import Suppliers.DTOs.OrderDTO;
import Suppliers.DomainLayer.OrderFacade;

import Suppliers.ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;
import Suppliers.DomainLayer.Classes.Order;

public class OrderService extends BaseService {
   private final OrderFacade orderFacade;

   public OrderService(OrderFacade orderFacade) {
      this.orderFacade = orderFacade;
   }

   public ServiceResponse<Order> createOrder(OrderDTO json) {
      return ServiceResponse.fail(List.of("Not implemented"));
   }

   private ServiceResponse<Order> updateOrder(String json) {
      return ServiceResponse.fail(List.of("Not implemented"));
   }

   private ServiceResponse<Boolean> removeOrder(String json) {
      return ServiceResponse.fail(List.of("Not implemented"));
   }

   private ServiceResponse<Order> getOrder(String json) {
      return ServiceResponse.fail(List.of("Not implemented"));
   }

   private ServiceResponse<List<Order>> viewAllOrders(String ignored) {
      return ServiceResponse.fail(List.of("Not implemented"));
   }
}
