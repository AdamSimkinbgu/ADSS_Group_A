package Suppliers.DomainLayer;

import java.util.List;

import Suppliers.DTOs.PeriodicOrderDTO;

import Suppliers.DomainLayer.Repositories.OrdersRepositoryImpl;
import Suppliers.DomainLayer.Repositories.RepositoryIntefaces.OrdersRepositoryInterface;

public class PeriodicOrderHandler {
   private final OrdersRepositoryInterface ordersRepository = new OrdersRepositoryImpl();

   public PeriodicOrderDTO createPeriodicOrder(PeriodicOrderDTO periodicOrderDTO) {
      if (periodicOrderDTO == null) {
         throw new IllegalArgumentException("PeriodicOrderDTO cannot be null");
      }
      return ordersRepository.createPeriodicOrder(periodicOrderDTO);
   }

   public boolean deletePeriodicOrder(int periodicOrderId) {
      if (periodicOrderId <= 0) {
         throw new IllegalArgumentException("Invalid periodic order ID: " + periodicOrderId);
      }
      return ordersRepository.deletePeriodicOrder(periodicOrderId);
   }

   public List<PeriodicOrderDTO> getAllPeriodicOrders() {
      return ordersRepository.getAllPeriodicOrders();
   }

   public boolean updatePeriodicOrder(PeriodicOrderDTO updatedDto) {
      if (updatedDto == null || updatedDto.getPeriodicOrderID() <= 0) {
         throw new IllegalArgumentException("Invalid periodic order DTO");
      }
      return ordersRepository.updatePeriodicOrder(updatedDto);
   }

   public PeriodicOrderDTO getPeriodicOrder(int periodicOrderId) {
      if (periodicOrderId <= 0) {
         throw new IllegalArgumentException("Invalid periodic order ID: " + periodicOrderId);
      }
      return ordersRepository.getPeriodicOrderById(periodicOrderId);
   }
}
