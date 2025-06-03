package Suppliers.DataLayer.DAOs;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Suppliers.DTOs.OrderDTO;
import Suppliers.DataLayer.Interfaces.OrderDAOInterface;

public class JdbcOrderDAO implements OrderDAOInterface {
   private static final Logger LOGGER = LoggerFactory.getLogger(JdbcOrderDAO.class);

   @Override
   public OrderDTO addOrder(OrderDTO orderDTO) {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'addOrder'");
   }

   @Override
   public Optional<OrderDTO> getOrder(int orderID) {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'getOrder'");
   }

   @Override
   public List<OrderDTO> listOrders() {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'listOrders'");
   }

   @Override
   public void deleteOrder(int orderID) {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'deleteOrder'");
   }

   @Override
   public void updateOrder(OrderDTO updatedOrderDTO) {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'updateOrder'");
   }

}
