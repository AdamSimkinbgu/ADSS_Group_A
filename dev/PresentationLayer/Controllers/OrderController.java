package PresentationLayer.Controllers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import DomainLayer.Classes.Order;
import PresentationLayer.AbstractController;
import PresentationLayer.View;
import ServiceLayer.OrderService;

/**
 * Controller for Order-related commands.
 */
public class OrderController extends AbstractController {

   public OrderController(View view, OrderService orderService) {
      super(view, orderService);
      this.service = orderService;
      this.implemented = false;
   }

   public List<String> showMenu() {
      return null;
   }

   public void createOrder() {
      view.showMessage("Sorry, this method is not implemented yet.");
   }

   public void updateOrder() {
      view.showMessage("Sorry, this method is not implemented yet.");

   }

   public void deleteOrder() {
      view.showMessage("Sorry, this method is not implemented yet.");

   }

   public void viewOrder() {
      view.showMessage("Sorry, this method is not implemented yet.");

   }
}