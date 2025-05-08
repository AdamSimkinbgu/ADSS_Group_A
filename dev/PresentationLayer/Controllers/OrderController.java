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
      this.implemented = true;
      this.service = orderService;
      controllerMenuOptions.put("1", this::createOrder);
      controllerMenuOptions.put("2", this::updateOrder);
      controllerMenuOptions.put("3", this::deleteOrder);
      controllerMenuOptions.put("4", this::viewOrder);
      controllerMenuOptions.put("5", () -> System.out.println("Returning to the main menu..."));
      controllerMenuOptions.put("?", () -> System.out.println("Invalid choice. Please try again."));
   }

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
      view.showMessage("Creating a new order…");
      ObjectNode payload = initCreateOrderPayload();
      if (payload == null)
         return;
      String resp = handleModuleCommand("addOrder", payload.toString());
      view.dispatchResponse(resp, Order.class);
   }

   private ObjectNode initCreateOrderPayload() {
      ObjectNode payload = mapper.createObjectNode();

      // 1) supplierId
      String supplierId = view.readLine("Supplier ID for this order:");
      String lookupJson = String.format("{\"supplierId\":\"%s\"}", supplierId);
      try {
         String existsJson = serialize(service.execute("checkSupplierExists", lookupJson));
         if (!mapper.readTree(existsJson).get("value").asBoolean()) {
            view.showError("No such supplier – order cancelled.");
            return null;
         }
      } catch (Exception e) {
         view.showError("Error checking supplier: " + e.getMessage());
         return null;
      }
      payload.put("supplierId", supplierId);

      // 2) today’s date
      payload.put("orderDate", LocalDate.now().toString());

      // 3) line items
      ArrayNode items = mapper.createArrayNode();
      view.showMessage("Enter order line items (at least one).");
      while (true) {
         ObjectNode line = mapper.createObjectNode();
         String productId = view.readLine("  Product ID:");
         line.put("productId", productId);

         String qtyStr = view.readLine("  Quantity:");
         try {
            int qty = Integer.parseInt(qtyStr);
            line.put("quantity", qty);
         } catch (NumberFormatException ex) {
            view.showError("  Invalid quantity, try again.");
            continue;
         }

         String priceStr = view.readLine("  Unit price:");
         try {
            BigDecimal bd = new BigDecimal(priceStr);
            line.put("unitPrice", bd);
         } catch (NumberFormatException ex) {
            view.showError("  Invalid price, try again.");
            continue;
         }

         items.add(line);

         if (!requestBoolean("Add another line item? (y/n)")) {
            break;
         }
      }
      payload.set("items", items);

      // 4) any other fields?
      // payload.put("notes", view.readLine("Order notes (optional):"));

      return payload;
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