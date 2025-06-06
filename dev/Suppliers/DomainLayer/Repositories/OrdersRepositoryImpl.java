package Suppliers.DomainLayer.Repositories;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Suppliers.DTOs.OrderDTO;
import Suppliers.DTOs.OrderItemLineDTO;
import Suppliers.DTOs.PeriodicOrderDTO;
import Suppliers.DataLayer.DAOs.JdbcOrderDAO;
import Suppliers.DataLayer.DAOs.JdbcOrderItemLineDAO;
import Suppliers.DataLayer.Interfaces.OrderDAOInterface;
import Suppliers.DataLayer.Interfaces.OrderItemLineDAOInterface;
import Suppliers.DomainLayer.Repositories.RepositoryIntefaces.OrdersRepositoryInterface;

public class OrdersRepositoryImpl implements OrdersRepositoryInterface {
   private static final Logger LOGGER = LoggerFactory.getLogger(OrdersRepositoryImpl.class);
   private OrderDAOInterface ordersRepository;
   private OrderItemLineDAOInterface orderItemLineDAO;

   public OrdersRepositoryImpl() {
      this.ordersRepository = new JdbcOrderDAO();
      this.orderItemLineDAO = new JdbcOrderItemLineDAO();
   }

   @Override
   public OrderDTO createRegularOrder(OrderDTO order) {
      if (order == null) {
         throw new IllegalArgumentException("OrderDTO cannot be null");
      }
      OrderDTO createdOrder = ordersRepository.addOrder(order);
      List<OrderItemLineDTO> itemLines = new ArrayList<>();
      for (OrderItemLineDTO itemLine : order.getItems()) {
         itemLine.setOrderID(createdOrder.getOrderId());
         OrderItemLineDTO createdItemLine = orderItemLineDAO.addOrderItemLine(itemLine);
         if (createdItemLine == null) {
            LOGGER.error("Failed to create order item line for order ID: {}", createdOrder.getOrderId());
         }
         itemLines.add(createdItemLine);
      }
      createdOrder.setItems(itemLines);
      LOGGER.info("Regular order created with ID: and items: {}", createdOrder.getOrderId(), itemLines);
      return createdOrder;
   }

   @Override
   public void updateRegularOrder(OrderDTO order) {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'updateRegularOrder'");
   }

   @Override
   public void deleteRegularOrder(int orderId) {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'deleteRegularOrder'");
   }

   @Override
   public OrderDTO getRegularOrderById(int orderId) {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'getRegularOrderById'");
   }

   @Override
   public List<OrderDTO> getAllRegularOrders() {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'getAllRegularOrders'");
   }

   @Override
   public PeriodicOrderDTO createPeriodicOrder(PeriodicOrderDTO periodicOrderDTO) {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'createPeriodicOrder'");
   }

   @Override
   public void updatePeriodicOrder(PeriodicOrderDTO periodicOrderDTO) {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'updatePeriodicOrder'");
   }

   @Override
   public void deletePeriodicOrder(int periodicOrderId) {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'deletePeriodicOrder'");
   }

   @Override
   public PeriodicOrderDTO getPeriodicOrderById(int periodicOrderId) {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'getPeriodicOrderById'");
   }

   @Override
   public List<PeriodicOrderDTO> getAllPeriodicOrders() {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'getAllPeriodicOrders'");
   }

   @Override
   public List<PeriodicOrderDTO> getPeriodicOrdersBySupplierId(int supplierId) {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'getPeriodicOrdersBySupplierId'");
   }

   @Override
   public List<OrderDTO> getAllSentRegularOrders() {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'getAllSentRegularOrders'");
   }

   @Override
   public List<OrderDTO> getAllOnDeliveryRegularOrders() {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'getAllOnDeliveryRegularOrders'");
   }

   @Override
   public List<OrderDTO> getAllDeliveredRegularOrders() {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'getAllDeliveredRegularOrders'");
   }

   @Override
   public List<OrderDTO> getAllCompletedRegularOrders() {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'getAllCompletedRegularOrders'");
   }

   @Override
   public List<OrderDTO> getAllCanceledRegularOrders() {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'getAllCanceledRegularOrders'");
   }
}
