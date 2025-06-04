// OrderService.java
package Suppliers.ServiceLayer;

import java.util.List;

import Suppliers.DTOs.OrderDTO;
import Suppliers.DTOs.PeriodicOrderDTO;
import Suppliers.DomainLayer.OrderFacade;

import Suppliers.ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;
import Suppliers.DomainLayer.Classes.Order;
import Suppliers.ServiceLayer.Interfaces_and_Abstracts.Validators.OrderValidator;
import Suppliers.ServiceLayer.Interfaces_and_Abstracts.Validators.PeriodicOrderValidator;

public class OrderService extends BaseService {
   private final OrderFacade orderFacade;
   OrderValidator orderValidator = new OrderValidator();
   PeriodicOrderValidator periodicOrderValidator = new PeriodicOrderValidator();

   public OrderService(OrderFacade orderFacade) {
      this.orderFacade = orderFacade;
   }

   public ServiceResponse<?> createOrder(OrderDTO dto) {
      // ServiceResponse <List <String>> response =
      // orderFacade.validateCreateDTO(dto);

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
      return ServiceResponse.fail(List.of("Not implemented"));
   }

   public ServiceResponse<List<OrderDTO>> getAllOrders() {
      return ServiceResponse.fail(List.of("Not implemented"));
   }

   public ServiceResponse<?> createPeriodicOrder(PeriodicOrderDTO periodicOrderDTO) {
      return ServiceResponse.fail(List.of("Not implemented"));
   }

   public ServiceResponse<PeriodicOrderDTO> getPeriodicOrderById(int periodicOrderId) {
      return ServiceResponse.fail(List.of("Not implemented"));
   }

   public ServiceResponse<?> updatePeriodicOrder(PeriodicOrderDTO updatedDto) {
      return ServiceResponse.fail(List.of("Not implemented"));
   }

   public ServiceResponse<?> removePeriodicOrder(int periodicOrderId) {
      return ServiceResponse.fail(List.of("Not implemented"));
   }

   public ServiceResponse<List<PeriodicOrderDTO>> getAllPeriodicOrders() {
      return ServiceResponse.fail(List.of("Not implemented"));
   }
}
