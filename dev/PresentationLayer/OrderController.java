package PresentationLayer;

import java.util.List;
import DomainLayer.Order;
import ServiceLayer.OrderService;

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
      view.showMessage("Creating a new order...");
      List<String> params = view.readParameters(
            "Please enter order details: <field1>-<field2>-...");
      String json = fuseClassAttributesAndParametersToJson(Order.class, params);
      view.dispatchResponse(
            handleModuleCommand("addOrder", json),
            Order.class);
   }

   public void updateOrder() {
      view.showMessage("Updating an existing order...");
      view.showMessage("Enter Order ID to update:");
      String id = view.readLine();
      view.showMessage("Which field to update?");
      String field = view.readLine();
      view.showMessage("Enter new value:");
      String value = view.readLine();
      String updateJson = String.format(
            "{\"orderId\":\"%s\", \"%s\":\"%s\"}",
            id, field, value);
      view.dispatchResponse(
            handleModuleCommand("updateOrder", updateJson),
            Order.class);
   }

   public void deleteOrder() {
      view.showMessage("Deleting an existing order...");
      view.showMessage("Enter Order ID to delete:");
      String id = view.readLine();
      String response = handleModuleCommand("removeOrder", id);
      view.dispatchResponse(response, Boolean.class);
   }

   public void viewOrder() {
      view.showMessage("Viewing an existing order...");
      view.showMessage("Enter Order ID to view:");
      String id = view.readLine();
      view.dispatchResponse(
            handleModuleCommand("getOrder", id),
            Order.class);
   }
}