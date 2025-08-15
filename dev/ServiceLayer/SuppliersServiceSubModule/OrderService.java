// OrderService.java
package ServiceLayer.SuppliersServiceSubModule;

import java.util.ArrayList;
import java.util.List;

import DTOs.InventoryModuleDTOs.SupplyDTO;
import DTOs.SuppliersModuleDTOs.OrderDTO;
import DTOs.SuppliersModuleDTOs.OrderInfoDTO;
import DTOs.SuppliersModuleDTOs.OrderPackageDTO;
import DTOs.SuppliersModuleDTOs.OrderResultDTO;
import DTOs.SuppliersModuleDTOs.PeriodicOrderDTO;
import DTOs.SuppliersModuleDTOs.Enums.OrderStatus;
import DataAccessLayer.SuppliersDAL.DAOs.DataAccessException;
import DomainLayer.SuppliersDomainSubModule.OrderFacade;
import ServiceLayer.SuppliersServiceSubModule.Interfaces_and_Abstracts.ServiceResponse;
import ServiceLayer.SuppliersServiceSubModule.Interfaces_and_Abstracts.Validators.OrderValidator;

/**
 * Service class responsible for managing order operations in the suppliers
 * service module.
 * This class acts as an intermediary between the presentation layer and the
 * business logic layer,
 * handling order creation, updates, retrieval, and status management
 * operations.
 * 
 * <p>
 * The OrderService provides comprehensive order management functionality
 * including:
 * </p>
 * <ul>
 * <li>Manual and automatic order creation</li>
 * <li>Periodic order management and execution</li>
 * <li>Order status tracking and advancement</li>
 * <li>Order delivery to inventory integration</li>
 * <li>Order validation and error handling</li>
 * </ul>
 * 
 * <p>
 * All operations return ServiceResponse objects that encapsulate
 * success/failure status
 * and provide consistent error handling across the service layer.
 * </p>
 * 
 * <p>
 * This service integrates with:
 * </p>
 * <ul>
 * <li>OrderFacade - for business logic operations</li>
 * <li>OrderValidator - for input validation</li>
 * <li>IntegrationService - for inventory system integration</li>
 * </ul>
 * 
 * @see OrderFacade
 * @see OrderValidator
 * @see ServiceResponse
 * @see BaseService
 * 
 * @author ADSS Group A
 * @version 1.0
 * @since 1.0
 */
public class OrderService extends BaseService {
   private final OrderFacade orderFacade;
   private final OrderValidator orderValidator = new OrderValidator();

   public OrderService(OrderFacade orderFacade) {
      this.orderFacade = orderFacade;
   }

   /**
    * Manually adds a new order to the system.
    * 
    * This method creates a new order based on the provided OrderDTO without
    * performing validation (validation code is currently commented out).
    * 
    * @param dto the OrderDTO containing the order details to be created
    * @return ServiceResponse containing the created OrderDTO if successful,
    *         or error messages if the operation fails
    * @throws DataAccessException if there's an SQL-related error during order
    *                             creation
    * @throws Exception           if any other error occurs during the order
    *                             creation process
    */
   public ServiceResponse<?> addOrderManually(OrderDTO dto) {
      // ServiceResponse<List<String>> validationResponse =
      // orderValidator.validateCreateDTO(dto);
      // if (validationResponse.isSuccess()) {
      try {
         OrderDTO createdOrder = orderFacade.addOrderManually(dto);
         return ServiceResponse.ok(createdOrder);
      } catch (DataAccessException e) {
         return ServiceResponse.fail(List.of("Error handling SQL exception: " + e.getMessage()));
      } catch (Exception e) {
         return ServiceResponse.fail(List.of("Failed to create order: " + e.getMessage()));
      }
      // } else {
      // return ServiceResponse.fail(validationResponse.getErrors());
      // }
   }

   /**
    * Creates a new order based on the provided order information.
    * 
    * This method validates the input data using the order validator, and if
    * validation
    * passes, delegates the order creation to the order facade. The method handles
    * various exceptions that may occur during the order creation process.
    * 
    * @param infoDTO the order information data transfer object containing all
    *                necessary details for creating an order
    * @return ServiceResponse containing either:
    *         - On success: OrderResultDTO with the created order details
    *         - On validation failure: List of validation error messages
    *         - On data access failure: List containing SQL exception error message
    *         - On general failure: List containing generic creation error message
    */
   public ServiceResponse<?> createOrder(OrderInfoDTO infoDTO) {
      ServiceResponse<List<String>> validationResponse = orderValidator.validateCreateDTO(infoDTO);
      if (validationResponse.isSuccess()) {
         try {
            OrderResultDTO createdOrder = orderFacade.createOrder(infoDTO);
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

   /**
    * Creates an order based on product shortage information.
    * 
    * This method validates the provided order information and, if valid, creates
    * a new order through the order facade to address product shortages in
    * inventory.
    * 
    * @param infoDTO the order information data transfer object containing details
    *                needed to create the order for shortage fulfillment
    * @return ServiceResponse containing the created OrderResultDTO if successful,
    *         or a list of error messages if validation fails or an exception
    *         occurs
    * @throws DataAccessException if there's an error accessing the database
    * @throws Exception           if any other error occurs during order creation
    */
   public ServiceResponse<?> createOrderByShortage(OrderInfoDTO infoDTO) {
      ServiceResponse<List<String>> validationResponse = orderValidator.validateCreateDTO(infoDTO);
      if (validationResponse.isSuccess()) {
         try {
            OrderResultDTO createdOrder = orderFacade.createOrderByShortage(infoDTO);
            return ServiceResponse.ok(createdOrder);
         } catch (DataAccessException e) {
            return ServiceResponse.fail(List.of("Error handling SQL exception: " + e.getMessage()));
         } catch (Exception e) {
            return ServiceResponse.fail(List.of("Failed to create order by shortage: " + e.getMessage()));
         }
      } else {
         return ServiceResponse.fail(validationResponse.getErrors());
      }
   }

   /**
    * Executes all periodic orders scheduled for a specific day of the week.
    * 
    * This method retrieves and processes all periodic orders that are configured
    * to run on the specified day. It returns the results of the order execution
    * including success/failure status and any relevant details.
    * 
    * @param day the day of the week for which to execute periodic orders (e.g.,
    *            "Monday", "Tuesday")
    * @return ServiceResponse containing a list of OrderResultDTO objects
    *         representing
    *         the execution results of each periodic order, or an error response if
    *         no orders are found or an exception occurs
    * @throws DataAccessException if there is an error accessing the database
    * @throws Exception           if any other error occurs during order execution
    */
   public ServiceResponse<List<OrderResultDTO>> executePeriodicOrdersForDay(String day) {
      try {
         // List<OrderResultDTO> results = orderFacade.executePeriodicOrdersForDay(day);
         // if (results == null || results.isEmpty()) {
         // return ServiceResponse.fail(List.of("No periodic orders found for the
         // specified day."));
         // }
         // return ServiceResponse.ok(results);
         return ServiceResponse.fail(List.of("Method not implemented yet."));
      } catch (DataAccessException e) {
         return ServiceResponse.fail(List.of("Error handling SQL exception: " + e.getMessage()));
      } catch (Exception e) {
         return ServiceResponse.fail(List.of("Failed to execute periodic orders: " + e.getMessage()));
      }
   }

   /**
    * Updates an existing order with the provided order information.
    * 
    * @param updatedDto the order information DTO containing the updated order
    *                   details
    * @return ServiceResponse containing the updated OrderDTO if successful,
    *         or a list of error messages if validation fails or an exception
    *         occurs
    * @throws DataAccessException if there is an error accessing the database
    * @throws Exception           if any other error occurs during the update
    *                             process
    */
   public ServiceResponse<OrderDTO> updateOrder(OrderInfoDTO updatedDto) {
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

   /**
    * Removes an order from the system by its unique identifier.
    * 
    * This method validates the order ID before attempting removal and handles
    * various error scenarios including database access issues and general
    * exceptions.
    * 
    * @param orderId the unique identifier of the order to be removed
    * @return ServiceResponse<Boolean> containing:
    *         - Success response with true if the order was successfully removed
    *         - Failure response with error messages if:
    *         - Validation fails for the provided order ID
    *         - Order with the specified ID is not found
    *         - Database access error occurs
    *         - Any other unexpected error occurs during the removal process
    */
   public ServiceResponse<Boolean> removeOrder(int orderId) {
      ServiceResponse<List<String>> validationResponse = orderValidator.validateRemoveDTO(orderId);
      if (validationResponse.isSuccess()) {
         try {
            boolean removed = orderFacade.deleteOrder(orderId);
            if (removed) {
               return ServiceResponse.ok(true);
            } else {
               return ServiceResponse.fail(List.of("Order with ID " + orderId + " not found."));
            }
         } catch (DataAccessException e) {
            return ServiceResponse.fail(List.of("Error handling SQL exception: " + e.getMessage()));
         } catch (Exception e) {
            return ServiceResponse.fail(List.of("Failed to remove order: " + e.getMessage()));
         }
      } else {
         return ServiceResponse.fail(validationResponse.getErrors());
      }
   }

   /**
    * Retrieves an order by its unique identifier.
    * 
    * This method validates the provided order ID, then attempts to fetch the
    * corresponding
    * order from the data layer through the order facade. It handles various error
    * scenarios
    * including validation failures, data access issues, and general exceptions.
    * 
    * @param orderId the unique identifier of the order to retrieve
    * @return ServiceResponse containing the OrderDTO if successful, or error
    *         messages if the
    *         operation fails due to validation errors, order not found, data
    *         access issues,
    *         or other exceptions
    * @throws none - all exceptions are caught and returned as failed
    *              ServiceResponse
    */
   public ServiceResponse<OrderDTO> getOrderById(int orderId) {
      ServiceResponse<List<String>> validationResponse = orderValidator.validateGetDTO(orderId);
      if (validationResponse.isSuccess()) {
         try {
            OrderDTO order = orderFacade.getOrderById(orderId);
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

   /**
    * Retrieves all orders from the system.
    * 
    * This method fetches all available orders through the order facade and returns
    * them
    * as a service response. If no orders are found, it returns a failure response
    * with
    * an appropriate message.
    * 
    * @return ServiceResponse<List<OrderDTO>> containing:
    *         - Success: List of OrderDTO objects representing all orders in the
    *         system
    *         - Failure: Error message if no orders found, database access fails,
    *         or any other exception occurs
    * 
    * @throws DataAccessException if there's an error accessing the database
    * @throws Exception           for any other unexpected errors during order
    *                             retrieval
    */
   public ServiceResponse<List<OrderDTO>> getAllOrders() {
      try {
         List<OrderDTO> orders = orderFacade.getAllOrders();
         if (orders == null || orders.isEmpty()) {
            return ServiceResponse.fail(List.of("No orders found."));
         }
         return ServiceResponse.ok(orders);
      } catch (DataAccessException e) {
         return ServiceResponse.fail(List.of("Error handling SQL exception: " + e.getMessage()));
      } catch (Exception e) {
         return ServiceResponse.fail(List.of("Failed to fetch all orders: " + e.getMessage()));
      }
   }

   /**
    * Creates a new periodic order based on the provided PeriodicOrderDTO.
    * 
    * This method validates the input DTO using the order validator, and if
    * validation
    * passes, delegates the creation to the order facade. The method handles both
    * data access exceptions and general exceptions that may occur during the
    * creation process.
    * 
    * @param periodicOrderDTO the periodic order data transfer object containing
    *                         the details of the periodic order to be created
    * @return ServiceResponse containing either the created PeriodicOrderDTO on
    *         success,
    *         or a list of error messages on failure. Returns validation errors if
    *         the input DTO is invalid, SQL-related errors for database issues,
    *         or general error messages for other exceptions.
    */
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

   /**
    * Retrieves a periodic order by its unique identifier.
    * 
    * This method validates the provided periodic order ID, fetches the
    * corresponding
    * periodic order from the data layer, and returns it wrapped in a
    * ServiceResponse.
    * 
    * @param periodicOrderId the unique identifier of the periodic order to
    *                        retrieve
    * @return ServiceResponse containing the PeriodicOrderDTO if found and valid,
    *         or error messages if validation fails, order not found, or exceptions
    *         occur
    * @throws DataAccessException if there's an issue accessing the data layer
    * @throws Exception           if any other unexpected error occurs during the
    *                             operation
    */
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

   /**
    * Updates an existing periodic order with the provided information.
    * 
    * @param updatedDto the PeriodicOrderDTO containing the updated periodic order
    *                   data
    * @return ServiceResponse containing the updated PeriodicOrderDTO if
    *         successful,
    *         or a ServiceResponse with error messages if validation fails or an
    *         exception occurs
    * @throws DataAccessException if there is an error accessing the database
    * @throws Exception           if any other error occurs during the update
    *                             process
    */
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

   /**
    * Removes a periodic order from the system.
    * 
    * This method validates the periodic order ID, attempts to delete the periodic
    * order
    * through the order facade, and returns appropriate success or failure
    * responses.
    * 
    * @param periodicOrderId the unique identifier of the periodic order to be
    *                        removed
    * @return ServiceResponse<?> containing:
    *         - Success response with true if the periodic order was successfully
    *         removed
    *         - Failure response with error messages if:
    *         - Validation fails for the provided ID
    *         - Periodic order with the given ID is not found
    *         - Database access error occurs
    *         - Any other unexpected error occurs during the removal process
    * @throws DataAccessException if there's an error accessing the database
    */
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

   /**
    * Retrieves all periodic orders from the system.
    * 
    * @return ServiceResponse containing a list of PeriodicOrderDTO objects if
    *         successful,
    *         or an empty list with failure status if no orders exist or an error
    *         occurs.
    *         In case of database errors, returns a ServiceResponse with error
    *         message details.
    */
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

   /**
    * Retrieves all periodic orders that are scheduled for today.
    * 
    * This method fetches periodic orders from the system that have a delivery
    * or processing date set for the current day. It handles potential database
    * access errors and returns appropriate success or failure responses.
    * 
    * @return ServiceResponse containing a list of OrderDTO objects representing
    *         today's periodic orders if successful, or an empty list with error
    *         messages if the operation fails or no orders are found
    * 
    * @throws DataAccessException if there's an error accessing the database
    * @throws Exception           for any other unexpected errors during the
    *                             operation
    */
   public ServiceResponse<List<OrderDTO>> getAllPeriodicOrdersForToday() {
      try {
         // List<OrderDTO> orders = orderFacade.getAllPeriodicOrdersForToday();
         List<OrderDTO> orders = new ArrayList<>(); // Placeholder for actual implementation
         if (orders == null || orders.isEmpty()) {
            return ServiceResponse.fail(List.of("No periodic orders found for today."));
         }
         return ServiceResponse.ok(orders);
      } catch (DataAccessException e) {
         return ServiceResponse.fail(List.of("Error handling SQL exception: " + e.getMessage()));
      } catch (Exception e) {
         return ServiceResponse.fail(List.of("Failed to fetch periodic orders for today: " + e.getMessage()));
      }
   }

   /**
    * Completes an order by marking it as collected in the system.
    * 
    * @param orderId the unique identifier of the order to be completed
    * @return ServiceResponse<?> containing a success message if the order was
    *         successfully
    *         marked as completed, or a failure response with error details if the
    *         operation failed
    */
   public ServiceResponse<?> completeOrder(int orderId) {
      if (orderFacade.markOrderAsCollected(orderId)) {
         return ServiceResponse.ok("Order with ID " + orderId + " has been marked as completed.");
      } else {
         return ServiceResponse.fail(List.of("Failed to mark order with ID " + orderId + " as completed."));
      }
   }

   /**
    * Delivers an order to the inventory system after validating that the order
    * exists and is in DELIVERED status.
    * 
    * This method performs the following operations:
    * 1. Validates the order ID using the order validator
    * 2. Retrieves the order from the database using the order facade
    * 3. Verifies that the order exists and has DELIVERED status
    * 4. Converts the order to supply DTOs and creates an order package
    * 5. Calls the integration service to deliver the order to inventory
    * 
    * @param orderId The unique identifier of the order to be delivered to
    *                inventory
    * @return ServiceResponse<?> containing success message if delivery is
    *         successful,
    *         or error messages if validation fails, order is not found, order
    *         status
    *         is invalid, or any exception occurs during the delivery process
    * 
    * @throws DataAccessException if there's an error accessing the database
    * @throws Exception           if any other error occurs during order processing
    *                             or delivery
    */
   public ServiceResponse<?> deliverOrderToInventory(int orderId) {
      ServiceResponse<List<String>> validationResponse = orderValidator.validateGetDTO(orderId);
      if (validationResponse.isSuccess()) {
         OrderDTO order = null;
         try {
            order = orderFacade.getOrderById(orderId);
            if (order == null) {
               return ServiceResponse.fail(List.of("Order with ID " + orderId + " not found."));
            }
            if (!order.getStatus().equals(OrderStatus.DELIVERED)) {
               return ServiceResponse.fail(List.of("Order with ID " + orderId + " is not in DELIVERED status."));
            }
            try {
               List<SupplyDTO> items = orderFacade.getSupplyDTOFromOrder(order);
               ServiceResponse<?> orderToInventory = IntegrationService.getIntegrationServiceInstance()
                     .deliverOrder(new OrderPackageDTO(order.getOrderId(), order.getDeliveryDate(), items));
               if (orderToInventory.isSuccess()) {
                  return ServiceResponse.ok("Order with ID " + orderId + " has been delivered to inventory.");
               } else {
                  return ServiceResponse.fail(orderToInventory.getErrors());
               }
            } catch (DataAccessException e) {
               return ServiceResponse.fail(List.of("Error handling SQL exception: " + e.getMessage()));
            } catch (Exception e) {
               return ServiceResponse.fail(List.of("Failed to deliver order to inventory: " + e.getMessage()));
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

   /**
    * Retrieves all orders that are currently in delivered status.
    * 
    * @return ServiceResponse containing a list of OrderDTO objects representing
    *         orders
    *         in delivered status, or an error response if no orders are found or
    *         if
    *         an exception occurs during the operation
    * @throws DataAccessException if there is a database access error
    * @throws Exception           if any other unexpected error occurs during
    *                             processing
    */
   public ServiceResponse<List<OrderDTO>> getOrdersInDeliveredStatus() {
      try {
         List<OrderDTO> orders = orderFacade.getOrdersInDeliveredStatus();
         if (orders == null || orders.isEmpty()) {
            return ServiceResponse.fail(List.of("No orders in delivered status found."));
         }
         return ServiceResponse.ok(orders);
      } catch (DataAccessException e) {
         return ServiceResponse.fail(List.of("Error handling SQL exception: " + e.getMessage()));
      } catch (Exception e) {
         return ServiceResponse.fail(List.of("Failed to fetch orders in delivered status: " + e.getMessage()));
      }
   }

   /**
    * Advances the status of an order to the specified status.
    * 
    * This method validates the order ID, retrieves the order, and checks if the
    * status
    * advancement is valid. Orders can only advance to higher status levels and
    * cannot
    * be advanced to CANCELLED or COMPLETED status through this method.
    * 
    * @param orderId the unique identifier of the order to advance
    * @param status  the target status to advance the order to
    * @return ServiceResponse containing success message if advancement is
    *         successful,
    *         or error messages if validation fails, order not found, invalid
    *         status
    *         transition attempted, or database errors occur
    * 
    * @throws DataAccessException if there's an error accessing the database
    * @throws Exception           for any other unexpected errors during the
    *                             operation
    */
   public ServiceResponse<?> advanceOrderStatus(int orderId, OrderStatus status) {
      ServiceResponse<List<String>> validationResponse = orderValidator.validateGetDTO(orderId);
      if (validationResponse.isSuccess()) {
         try {
            OrderDTO order = orderFacade.getOrderById(orderId);
            if (order == null) {
               return ServiceResponse.fail(List.of("Order with ID " + orderId + " not found."));
            }
            if (order.getStatus().ordinal() >= status.ordinal() || status.equals(OrderStatus.CANCELLED)
                  || status.equals(OrderStatus.COMPLETED)) {
               return ServiceResponse
                     .fail(List.of("Cannot advance order status to " + status + " from " + order.getStatus()));
            }
            orderFacade.advanceOrderStatus(orderId, status);
            return ServiceResponse.ok("Order status advanced to " + status);
         } catch (DataAccessException e) {
            return ServiceResponse.fail(List.of("Error handling SQL exception: " + e.getMessage()));
         } catch (Exception e) {
            return ServiceResponse.fail(List.of("Failed to advance order status: " + e.getMessage()));
         }
      } else {
         return ServiceResponse.fail(validationResponse.getErrors());
      }
   }

   /**
    * Executes all periodic orders that are scheduled for the current week.
    * This method retrieves and processes periodic orders that are due to be
    * executed
    * within the current week timeframe.
    *
    * @return ServiceResponse<?> containing:
    *         - Success response with count message if orders were executed
    *         - Failure response if no orders were found to execute
    * @throws RuntimeException if a DataAccessException occurs during database
    *                          operations
    * @throws RuntimeException if any other exception occurs during execution
    */
   public ServiceResponse<?> executePeriodicOrdersForThisWeek() {
      try {
         int executedCount = orderFacade.executePeriodicOrdersForThisWeek();
         if (executedCount > 0) {
            return ServiceResponse.ok("Executed " + executedCount + " periodic orders for this week.");
         } else {
            return ServiceResponse.fail(List.of("No periodic orders to execute for this week."));
         }
      } catch (DataAccessException e) {
         throw new RuntimeException("Error handling SQL exception: " + e.getMessage(), e);
      } catch (Exception e) {
         throw new RuntimeException("Failed to execute periodic orders for this week: " + e.getMessage(), e);
      }

   }
}
