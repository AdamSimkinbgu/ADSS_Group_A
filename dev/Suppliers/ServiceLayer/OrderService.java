// OrderService.java
package Suppliers.ServiceLayer;

import java.util.ArrayList;
import java.util.List;

import Suppliers.DTOs.OrderDTO;
import Suppliers.DTOs.PeriodicOrderDTO;
import Suppliers.DataLayer.DAOs.DataAccessException;
import Suppliers.DomainLayer.OrderFacade;

import Suppliers.ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;
import Suppliers.ServiceLayer.Interfaces_and_Abstracts.Validators.OrderValidator;

public class OrderService extends BaseService {
   private final OrderFacade orderFacade;
   private final OrderValidator orderValidator = new OrderValidator();

   public OrderService(OrderFacade orderFacade) {
      this.orderFacade = orderFacade;
   }

   public ServiceResponse<?> createOrder(OrderDTO dto) {
      ServiceResponse<List<String>> validationResponse = orderValidator.validateCreateDTO(dto);
      if (validationResponse.isSuccess()) {
         try {
            OrderDTO createdOrder = orderFacade.addOrder(dto);
            return ServiceResponse.ok(createdOrder);
         } catch (DataAccessException e) {
            return ServiceResponse.fail(List.of("Error handling SQL exception: " + e.getMessage()));
         } catch (Exception e) {
            return ServiceResponse.fail(List.of("Failed to create order: " + e.getMessage()));
         }
      } else {
         return ServiceResponse.fail(validationResponse.getErrors());
      }
   }

   public ServiceResponse<OrderDTO> updateOrder(OrderDTO updatedDto) {
      ServiceResponse<List<String>> validationResponse = orderValidator.validateUpdateDTO(updatedDto);
      if (validationResponse.isSuccess()) {
         try {
            OrderDTO updatedOrder = orderFacade.updateOrder(updatedDto);
            return ServiceResponse.ok(updatedOrder);
         } catch (DataAccessException e) {
            return ServiceResponse.fail(List.of("Error handling SQL exception: " + e.getMessage()));
         } catch (Exception e) {
            return ServiceResponse.fail(List.of("Failed to update order: " + e.getMessage()));
         }
      } else {
         return ServiceResponse.fail(validationResponse.getErrors());
      }
   }

   public ServiceResponse<Boolean> removeOrder(int orderId) {
      ServiceResponse<List<String>> validationResponse = orderValidator.validateRemoveDTO(orderId);
      if (validationResponse.isSuccess()) {
         try {
            // boolean removed = orderFacade.deleteOrder(orderId);
            // if (removed) {
            // return ServiceResponse.ok(true);
            // } else {
            // return ServiceResponse.fail(List.of("Order with ID " + orderId + " not
            // found."));
            // }
            return ServiceResponse.fail(List.of("Not implemented"));
         } catch (DataAccessException e) {
            return ServiceResponse.fail(List.of("Error handling SQL exception: " + e.getMessage()));
         } catch (Exception e) {
            return ServiceResponse.fail(List.of("Failed to remove order: " + e.getMessage()));
         }
      } else {
         return ServiceResponse.fail(validationResponse.getErrors());
      }
   }

   public ServiceResponse<OrderDTO> getOrderById(int orderId) {
      ServiceResponse<List<String>> validationResponse = orderValidator.validateGetDTO(orderId);
      if (validationResponse.isSuccess()) {
         try {
            OrderDTO order = orderFacade.getOrder(orderId);
            if (order != null) {
               return ServiceResponse.ok(order);
            } else {
               return ServiceResponse.fail(List.of("Order with ID " + orderId + " not found."));
            }
         } catch (DataAccessException e) {
            return ServiceResponse.fail(List.of("Error handling SQL exception: " + e.getMessage()));
         } catch (Exception e) {
            return ServiceResponse.fail(List.of("Failed to fetch order: " + e.getMessage()));
         }
      } else {
         return ServiceResponse.fail(validationResponse.getErrors());
      }

   }

   public ServiceResponse<List<OrderDTO>> getAllOrders() {
      try {
         List<OrderDTO> orders = new ArrayList<>(); // Placeholder for actual implementation
         if (orders == null || orders.isEmpty()) {
            return ServiceResponse.fail(List.of());
         }
         return ServiceResponse.ok(orders);
      } catch (DataAccessException e) {
         return ServiceResponse.fail(List.of("Error handling SQL exception: " + e.getMessage()));
      } catch (Exception e) {
         return ServiceResponse.fail(List.of("Failed to fetch all orders: " + e.getMessage()));
      }
   }

   public ServiceResponse<?> createPeriodicOrder(PeriodicOrderDTO periodicOrderDTO) {
      ServiceResponse<List<String>> validationResponse = orderValidator.validateCreateDTO(periodicOrderDTO);
      if (validationResponse.isSuccess()) {
         try {
            PeriodicOrderDTO createdPeriodicOrder = orderFacade.createPeriodicOrder(periodicOrderDTO);
            return ServiceResponse.ok(createdPeriodicOrder);
         } catch (DataAccessException e) {
            return ServiceResponse.fail(List.of("Error handling SQL exception: " + e.getMessage()));
         } catch (Exception e) {
            return ServiceResponse.fail(List.of("Failed to create periodic order: " + e.getMessage()));
         }
      } else {
         return ServiceResponse.fail(validationResponse.getErrors());
      }
   }

   public ServiceResponse<PeriodicOrderDTO> getPeriodicOrderById(int periodicOrderId) {
      ServiceResponse<List<String>> validationResponse = orderValidator.validateGetDTO(periodicOrderId);
      if (validationResponse.isSuccess()) {
         try {
            PeriodicOrderDTO periodicOrder = orderFacade.getPeriodicOrder(periodicOrderId);
            if (periodicOrder != null) {
               return ServiceResponse.ok(periodicOrder);
            } else {
               return ServiceResponse.fail(List.of("Periodic order with ID " + periodicOrderId + " not found."));
            }
         } catch (DataAccessException e) {
            return ServiceResponse.fail(List.of("Error handling SQL exception: " + e.getMessage()));
         } catch (Exception e) {
            return ServiceResponse.fail(List.of("Failed to fetch periodic order: " + e.getMessage()));
         }
      } else {
         return ServiceResponse.fail(validationResponse.getErrors());
      }
   }

   public ServiceResponse<?> updatePeriodicOrder(PeriodicOrderDTO updatedDto) {
      ServiceResponse<List<String>> validationResponse = orderValidator.validateUpdateDTO(updatedDto);
      if (validationResponse.isSuccess()) {
         try {
            PeriodicOrderDTO updatedPeriodicOrder = orderFacade.updatePeriodicOrder(updatedDto);
            return ServiceResponse.ok(updatedPeriodicOrder);
         } catch (DataAccessException e) {
            return ServiceResponse.fail(List.of("Error handling SQL exception: " + e.getMessage()));
         } catch (Exception e) {
            return ServiceResponse.fail(List.of("Failed to update periodic order: " + e.getMessage()));
         }
      } else {
         return ServiceResponse.fail(validationResponse.getErrors());
      }
   }

   public ServiceResponse<?> removePeriodicOrder(int periodicOrderId) {
      ServiceResponse<List<String>> validationResponse = orderValidator.validateRemoveDTO(periodicOrderId);
      if (validationResponse.isSuccess()) {
         try {
            boolean removed = orderFacade.deletePeriodicOrder(periodicOrderId);
            if (removed) {
               return ServiceResponse.ok(true);
            } else {
               return ServiceResponse.fail(List.of("Periodic order with ID " +
                     periodicOrderId + " not found."));
            }
         } catch (DataAccessException e) {
            return ServiceResponse.fail(List.of("Error handling SQL exception: " + e.getMessage()));
         } catch (Exception e) {
            return ServiceResponse.fail(List.of("Failed to remove periodic order: " + e.getMessage()));
         }
      } else {
         return ServiceResponse.fail(validationResponse.getErrors());
      }
   }

   public ServiceResponse<List<PeriodicOrderDTO>> getAllPeriodicOrders() {
      try {
         List<PeriodicOrderDTO> periodicOrders = orderFacade.getAllPeriodicOrders();
         if (periodicOrders == null || periodicOrders.isEmpty()) {
            return ServiceResponse.fail(List.of());
         }
         return ServiceResponse.ok(periodicOrders);
      } catch (DataAccessException e) {
         return ServiceResponse.fail(List.of("Error handling SQL exception: " + e.getMessage()));
      } catch (Exception e) {
         return ServiceResponse.fail(List.of("Failed to fetch all periodic orders: " + e.getMessage()));
      }
   }

   public ServiceResponse<List<OrderDTO>> getAllPeriodicOrdersForToday() {
      try {
         // List<OrderDTO> orders = orderFacade.getAllPeriodicOrdersForToday();
         List<OrderDTO> orders = new ArrayList<>(); // Placeholder for actual implementation
         if (orders == null || orders.isEmpty()) {
            return ServiceResponse.fail(List.of());
         }
         return ServiceResponse.ok(orders);
      } catch (DataAccessException e) {
         return ServiceResponse.fail(List.of("Error handling SQL exception: " + e.getMessage()));
      } catch (Exception e) {
         return ServiceResponse.fail(List.of("Failed to fetch periodic orders for today: " + e.getMessage()));
      }
   }
}
