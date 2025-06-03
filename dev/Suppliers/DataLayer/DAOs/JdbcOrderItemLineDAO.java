package Suppliers.DataLayer.DAOs;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Suppliers.DTOs.OrderItemLineDTO;
import Suppliers.DataLayer.Interfaces.OrderItemLineDAOInterface;

public class JdbcOrderItemLineDAO implements OrderItemLineDAOInterface {
   private static final Logger LOGGER = LoggerFactory.getLogger(JdbcOrderItemLineDAO.class);

   @Override
   public OrderItemLineDTO addOrderItemLine(OrderItemLineDTO orderItemLine) {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'addOrderItemLine'");
   }

   @Override
   public OrderItemLineDTO getOrderItemLine(int id) {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'getOrderItemLine'");
   }

   @Override
   public List<OrderItemLineDTO> listOrderItemLines() {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'listOrderItemLines'");
   }

   @Override
   public void deleteOrderItemLine(int id) {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'deleteOrderItemLine'");
   }

}
