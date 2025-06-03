package Suppliers.DomainLayer.Repositories;

import java.util.List;

import Suppliers.DTOs.OrderDTO;
import Suppliers.DTOs.PeriodicOrderDTO;
import Suppliers.DomainLayer.Classes.Order;
import Suppliers.DomainLayer.Repositories.RepositoryIntefaces.OrdersRepositoryInterface;

public class OrdersRepositoryImpl implements OrdersRepositoryInterface {

   @Override
   public OrderDTO createRegularOrder(Order order) {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'createRegularOrder'");
   }

   @Override
   public void updateRegularOrder(Order order) {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'updateRegularOrder'");
   }

   @Override
   public void deleteRegularOrder(int orderId) {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'deleteRegularOrder'");
   }

   @Override
   public Order getRegularOrderById(int orderId) {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'getRegularOrderById'");
   }

   @Override
   public List<Order> getAllRegularOrders() {
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
