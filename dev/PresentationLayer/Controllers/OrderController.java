package PresentationLayer.Controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import DomainLayer.Classes.Order;
import PresentationLayer.AbstractController;
import PresentationLayer.View;
import ServiceLayer.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Controller for Order-related commands.
 */
public class OrderController extends AbstractController {
   private final OrderService orderService;

   public OrderController(View view, OrderService orderService) {
      super(view, orderService);
      this.implemented = true;
      this.orderService = orderService;
      controllerMenuOptions.put("1", this::createOrder);
      controllerMenuOptions.put("2", this::updateOrder);
      controllerMenuOptions.put("3", this::deleteOrder);
      controllerMenuOptions.put("4", this::viewOrder);
      controllerMenuOptions.put("5", () -> System.out.println("Returning to the main menu..."));
      controllerMenuOptions.put("?", () -> System.out.println("Invalid choice. Please try again."));
   }

   @Override
   public List<String> showMenu() {
      return List.of(
            "Please choose an option:",
            "Create Order",
            "Update Order",
            "Delete Order",
            "View Order",
            "Back to Main Menu");
   }

   public void createOrder() {
      view.showMessage("Sorry, this method is not implemented yet.");
      return;
      // view.showMessage("Enter supplier ID:");
      // String supplierId = view.readLine().trim();
      // view.showMessage("Enter supplier name:");
      // String supplierName = view.readLine().trim();
      // view.showMessage("Enter supplier address:");
      // String supplierAddress = view.readLine().trim();
      // view.showMessage("Enter contact phone:");
      // String contactPhone = view.readLine().trim();

      // view.showMessage("How many items in this order?");
      // int itemCount = Integer.parseInt(view.readLine().trim());
      // List<Map<String, Object>> items = new ArrayList<>(itemCount);

      // for (int i = 1; i <= itemCount; i++) {
      // view.showMessage("=== Item " + i + " ===");
      // view.showMessage("Item ID:");
      // String itemId = view.readLine().trim();
      // view.showMessage("Item name:");
      // String itemName = view.readLine().trim();
      // view.showMessage("Quantity:");
      // int quantity = Integer.parseInt(view.readLine().trim());
      // view.showMessage("List price:");
      // double price = Double.parseDouble(view.readLine().trim());
      // view.showMessage("Discount (as percent, e.g. 10 for 10%):");
      // double discount = Double.parseDouble(view.readLine().trim());
      // // חישוב מחיר סופי
      // double finalPrice = price - (price * discount / 100.0);

      // Map<String, Object> itemMap = new LinkedHashMap<>();
      // itemMap.put("itemId", itemId);
      // itemMap.put("itemName", itemName);
      // itemMap.put("quantity", quantity);
      // itemMap.put("price", price);
      // itemMap.put("discount", discount);
      // itemMap.put("finalPrice", finalPrice);
      // items.add(itemMap);
      // }

      // Map<String, Object> orderMap = new LinkedHashMap<>();
      // orderMap.put("supplierName", supplierName);
      // orderMap.put("supplierId", supplierId);
      // orderMap.put("supplierAddress", supplierAddress);
      // orderMap.put("orderDate", LocalDateTime.now().toString());
      // orderMap.put("contactPhone", contactPhone);
      // orderMap.put("orderStatus", null);
      // orderMap.put("orderItems", items);

      // String json;
      // try {
      // json = mapper.writeValueAsString(orderMap);
      // } catch (JsonProcessingException e) {
      // view.showError("Failed to serialize order JSON: " + e.getMessage());
      // return;
      // }
      // String response = handleModuleCommand("addOrder", json);
      // view.dispatchResponse(response, Order.class);
   }

   public void updateOrder() {
      view.showMessage("Sorry, this method is not implemented yet.");
      // view.showMessage("Updating an existing order...");
      // view.showMessage("Enter Order ID to update:");
      // String id = view.readLine();
      // view.showMessage("Which field to update?");
      // String field = view.readLine();
      // view.showMessage("Enter new value:");
      // String value = view.readLine();
      // String updateJson = String.format(
      // "{\"orderId\":\"%s\", \"%s\":\"%s\"}",
      // id, field, value);
      // String response = handleModuleCommand("updateOrder", updateJson);
      // view.dispatchResponse(response, Order.class);
   }

   public void deleteOrder() {
      view.showMessage("Sorry, this method is not implemented yet.");
      // view.showMessage("Deleting an existing order...");
      // view.showMessage("Enter Order ID to delete:");
      // String id = view.readLine();
      // String response = handleModuleCommand("removeOrder", id);
      // view.dispatchResponse(response, Boolean.class);
   }

   public void viewOrder() {
      view.showMessage("Sorry, this method is not implemented yet.");
      // view.showMessage("Viewing an existing order...");
      // view.showMessage("Enter Order ID to view:");
      // String id = view.readLine();
      // view.dispatchResponse(
      // handleModuleCommand("getOrder", id),
      // Order.class);
   }
}