// OrderService.java
package Suppliers.ServiceLayer;

import java.util.List;

import Suppliers.DTOs.OrderDTO;
import Suppliers.DTOs.PeriodicOrderDTO;
import Suppliers.DomainLayer.OrderFacade;

import Suppliers.ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;
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
      if (dto == null) {
         return ServiceResponse.fail(List.of("OrderDTO cannot be null"));
      }
      try {
         OrderDTO order = orderFacade.addOrder(dto);
         return ServiceResponse.ok(order);
      } catch (Exception e) {
         return ServiceResponse.fail(List.of("Failed to create order: " + e.getMessage()));
      }
   }

   public ServiceResponse<OrderDTO> updateOrder(String json) {
      return ServiceResponse.fail(List.of("Not implemented"));
   }

   public ServiceResponse<Boolean> removeOrder(String json) {
      return ServiceResponse.fail(List.of("Not implemented"));
   }

   public ServiceResponse<OrderDTO> getOrder(String json) {
      return ServiceResponse.fail(List.of("Not implemented"));
   }

   public ServiceResponse<List<OrderDTO>> viewAllOrders(String ignored) {
      return ServiceResponse.fail(List.of("Not implemented"));
   }

   public ServiceResponse<OrderDTO> getOrderById(int orderId) {
      return ServiceResponse.fail(List.of("Not implemented"));
   }

   public ServiceResponse<List<OrderDTO>> getAllOrders() {
      return ServiceResponse.fail(List.of("Not implemented"));
   }

   public ServiceResponse<?> createPeriodicOrder(PeriodicOrderDTO periodicOrderDTO) {
      if (periodicOrderDTO == null || periodicOrderDTO.getProductsInOrder() == null
            || periodicOrderDTO.getProductsInOrder().isEmpty()) {
         return ServiceResponse.fail(List.of("PeriodicOrderDTO and its products cannot be null or empty"));
      }
      try {
         PeriodicOrderDTO createdPeriodicOrder = orderFacade.createPeriodicOrder(
               periodicOrderDTO.getDeliveryDay(), periodicOrderDTO.getProductsInOrder());
         return ServiceResponse.ok(createdPeriodicOrder);
      } catch (Exception e) {
         return ServiceResponse.fail(List.of("Failed to create periodic order: " + e.getMessage()));
      }
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
