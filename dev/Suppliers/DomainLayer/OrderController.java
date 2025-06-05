package Suppliers.DomainLayer;

import Suppliers.DTOs.OrderDTO;
import Suppliers.DomainLayer.Repositories.OrdersRepositoryImpl;
import Suppliers.DomainLayer.Repositories.RepositoryIntefaces.OrdersRepositoryInterface;

public class OrderController {
   private static final OrdersRepositoryInterface ordersRepository = new OrdersRepositoryImpl();

   public OrderDTO addOrder(OrderDTO orderDTO) {
      if (orderDTO == null) {
         throw new IllegalArgumentException("OrderDTO cannot be null");
      }
      OrderDTO createdOrder = ordersRepository.createRegularOrder(orderDTO);
      if (createdOrder == null) {
         throw new RuntimeException("Failed to create order");
      }
      return createdOrder;
   }

}
