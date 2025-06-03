// OrderService.java
package Suppliers.ServiceLayer;

import java.util.List;

import Suppliers.DTOs.OrderDTO;
import Suppliers.DTOs.PeriodicOrderDTO;
import Suppliers.DomainLayer.OrderFacade;

import Suppliers.ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;
import Suppliers.DomainLayer.Classes.Order;

public class OrderService extends BaseService {
   private final OrderFacade orderFacade;

   public OrderService(OrderFacade orderFacade) {
      this.orderFacade = orderFacade;
   }

   public ServiceResponse<?> createOrder(OrderDTO dto) {
      ServiceResponse <List <String>> response =


      return ServiceResponse.fail(List.of("Not implemented"));
   }

   public ServiceResponse<Order> updateOrder(String json) {
      return ServiceResponse.fail(List.of("Not implemented"));
   }

   public ServiceResponse<Boolean> removeOrder(String json) {
      return ServiceResponse.fail(List.of("Not implemented"));
   }

   public ServiceResponse<Order> getOrder(String json) {
      return ServiceResponse.fail(List.of("Not implemented"));
   }

   public ServiceResponse<List<Order>> viewAllOrders(String ignored) {
      return ServiceResponse.fail(List.of("Not implemented"));
   }

    public ServiceResponse<OrderDTO> getOrderById(int orderId) {
    }

   public ServiceResponse<List<OrderDTO>> getAllOrders() {
   }

   public ServiceResponse<?> createPeriodicOrder(PeriodicOrderDTO periodicOrderDTO) {
   }

   public ServiceResponse<PeriodicOrderDTO> getPeriodicOrderById(int periodicOrderId) {
   }

   public ServiceResponse<?> updatePeriodicOrder(PeriodicOrderDTO updatedDto) {
   }

   public ServiceResponse<?> removePeriodicOrder(int periodicOrderId) {
   }

   public ServiceResponse<List<PeriodicOrderDTO>> getAllPeriodicOrders() {
   }
}
