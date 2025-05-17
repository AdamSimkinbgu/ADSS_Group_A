package PresentationLayer.Controllers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import DomainLayer.Classes.Agreement;
import DomainLayer.Classes.Order;
import DomainLayer.Classes.Supplier;
import DomainLayer.Enums.WeekofDay;
import PresentationLayer.AbstractController;
import PresentationLayer.View;
import ServiceLayer.AgreementService;
import ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;
import ServiceLayer.OrderService;
import ServiceLayer.SupplierService;

public class SupplierAggregateController extends AbstractController {
   private final SupplierService supplierService;
   private final AgreementService agreementService;
   private final OrderService orderService;

   private final Map<String, Runnable> supplierOpts = new HashMap<>();
   private final Map<String, Runnable> agreementOpts = new HashMap<>();
   private final Map<String, Runnable> productOpts = new HashMap<>();
   private final Map<String, Runnable> orderOpts = new HashMap<>();

   public SupplierAggregateController(
         View view,
         SupplierService supplierService,
         AgreementService agreementService,
         OrderService orderService) {
      super(view, supplierService);
      this.supplierService = supplierService;
      this.agreementService = agreementService;
      this.orderService = orderService;
      this.implemented = true;

      // Top‐level menu
      controllerMenuOptions.put("1", this::handleSupplierMenu);
      controllerMenuOptions.put("2", this::handleAgreementMenu);
      controllerMenuOptions.put("3", this::handleProductMenu);
      controllerMenuOptions.put("4", this::handleOrderMenu);
      controllerMenuOptions.put("5", () -> System.out.println("Returning to main menu..."));
      controllerMenuOptions.put("?", () -> System.out.println("Invalid choice. Please try again."));

      // Supplier submenu
      supplierOpts.put("1", this::createSupplier);
      supplierOpts.put("2", this::updateSupplier);
      supplierOpts.put("3", this::deleteSupplier);
      supplierOpts.put("4", this::viewSupplier);
      supplierOpts.put("5", this::viewAllSuppliers);
      supplierOpts.put("6", () -> {
         /* back */});
      supplierOpts.put("?", () -> System.out.println("Invalid choice."));

      // Agreement submenu
      agreementOpts.put("1", this::createAgreement);
      agreementOpts.put("2", this::updateAgreement);
      agreementOpts.put("3", this::deleteAgreement);
      agreementOpts.put("4", this::viewAgreement);
      agreementOpts.put("5", this::listAllAgreements);
      agreementOpts.put("6", () -> System.out.println("Returning to main menu..."));
      agreementOpts.put("?", () -> System.out.println("Invalid choice."));

      // Product submenu
      productOpts.put("1", this::addProduct);
      productOpts.put("2", this::updateProduct);
      productOpts.put("3", this::deleteProduct);
      productOpts.put("4", this::listProducts);
      productOpts.put("5", () -> System.out.println("Returning to main menu..."));
      productOpts.put("?", () -> System.out.println("Invalid choice."));

      // Order submenu
      orderOpts.put("1", this::createOrder);
      orderOpts.put("2", this::updateOrder);
      orderOpts.put("3", this::deleteOrder);
      orderOpts.put("4", this::viewOrder);
      orderOpts.put("5", this::viewAllOrders);
      orderOpts.put("6", () -> System.out.println("Returning to main menu..."));
      orderOpts.put("?", () -> System.out.println("Invalid choice."));
   }

   @Override
   public List<String> showMenu() {
      return List.of(
            "Please choose an option:",
            "Supplier Menu",
            "Agreement Menu",
            "Product Menu",
            "Order Menu",
            "Back to Main Menu");
   }

   // --- top‐level handlers ---
   private void handleSupplierMenu() {
      List<String> m = List.of(
            "Supplier Menu:",
            "Create Supplier",
            "Update Supplier",
            "Delete Supplier",
            "View Supplier",
            "View All Suppliers",
            "Back");
      view.showOptions(m.get(0), m.subList(0, m.size()));
      supplierOpts.getOrDefault(view.readLine(), supplierOpts.get("?")).run();
   }

   private void handleAgreementMenu() {
      List<String> m = List.of(
            "Agreement Menu:",
            "Create Agreement",
            "Update Agreement",
            "Delete Agreement",
            "View Agreement",
            "List All Agreements",
            "Back");
      view.showOptions(m.get(0), m.subList(0, m.size()));
      agreementOpts.getOrDefault(view.readLine(), agreementOpts.get("?")).run();
   }

   private void handleProductMenu() {
      List<String> m = List.of(
            "Product Menu:",
            "Add Product",
            "Update Product",
            "Delete Product",
            "List Products",
            "Back");
      view.showOptions(m.get(0), m.subList(0, m.size()));
      productOpts.getOrDefault(view.readLine(), productOpts.get("?")).run();
   }

   private void handleOrderMenu() {
      List<String> m = List.of(
            "Order Menu:",
            "Create Order",
            "Update Order",
            "Delete Order",
            "View Order",
            "View all Orders",
            "Back");
      view.showOptions(m.get(0), m.subList(0, m.size()));
      orderOpts.getOrDefault(view.readLine(), orderOpts.get("?")).run();
   }

   // --- Supplier actions ---
   public void createSupplier() {
      view.showMessage("Creating a new supplier… Please enter the following details:");
      ObjectNode payload = initCreatePayload();
      if (payload.isEmpty())
         return;
      payload.set("address", requestAddress());

      payload.set("paymentDetails", requestPaymentDetails());

      ArrayNode products = mapper.createArrayNode();

      payload.set("contacts", requestContacts());
      payload.set("products", products);
      // agreements is just [] because we don't have any yet
      payload.set("agreements", mapper.createArrayNode());

      String supplierJson = payload.toString();

      view.dispatchResponse(
            handleModuleCommand("addSupplier", supplierJson),
            Supplier.class);
   }

   public void updateSupplier() {
      view.showMessage("Updating an existing supplier…");

      String supplierId = view.readLine("Please enter the supplier ID to update:");
      String lookupJson = String.format("{\"supplierId\":\"%s\"}", supplierId);
      if (!doesSupplierExists(lookupJson)) {
         view.showError("Supplier doesn't exist, update cancelled");
         return;
      }

      ObjectNode payload = mapper.createObjectNode();
      payload.put("supplierId", supplierId);

      while (true) {
         String field = view.readLine(
               "Enter field to update (blank to finish):\n" +
                     "  name, taxNumber, address, paymentDetails, contacts")
               .trim().toLowerCase();

         if (field.isEmpty())
            break;

         switch (field) {
            case "name" -> payload.put("name", view.readLine("New name:"));
            case "taxnumber" -> payload.put("taxNumber", view.readLine("New tax number:"));
            case "address" -> payload.set("address", requestAddress());
            case "paymentdetails" -> payload.set("paymentDetails", requestPaymentDetails());
            case "contacts" -> payload.set("contacts", requestContacts());
            default -> view.showError("Unknown field: " + field);
         }
      }

      if (payload.size() == 1) {
         view.showMessage("No changes entered. Update cancelled.");
         return;
      }

      String responseJson = serialize(supplierService.execute("updateSupplier", payload.toString()));
      view.dispatchResponse(responseJson, Supplier.class);
   }

   public void deleteSupplier() {
      System.out.println("Deleting an existing supplier...");
      view.showMessage("Please enter the supplier ID to delete:");
      String supplierId = view.readLine();
      String deleteJson = String.format("{\"supplierId\":\"%s\"}", supplierId);
      String response = serialize(supplierService.execute("removeSupplier", deleteJson));
      view.dispatchResponse(response, ServiceResponse.class);
   }

   public void viewSupplier() {
      System.out.println("Viewing an existing supplier...");
      view.showMessage("Please enter the supplier ID to view:");
      String supplierId = view.readLine();
      String viewJson = String.format("{\"supplierId\":\"%s\"}", supplierId);
      String response = serialize(supplierService.execute("getSupplierDetails", viewJson));
      view.dispatchResponse(response, Supplier.class);
   }

   public void viewAllSuppliers() {
      System.out.println("Viewing all suppliers...");
      String response = serialize(supplierService.execute("getAllSuppliers", "{}"));
      view.dispatchResponse(response, Supplier.class);
   }

   private ObjectNode initCreatePayload() {
      ObjectNode payload = mapper.createObjectNode();
      boolean res = true;
      while (res) {
         String name = view.readLine("Name:");
         String tax = view.readLine("TaxNumber:");
         String supplierId = name + ":" + tax;
         String supJson = String.format("{\"supplierId\":\"%s\"}", supplierId);
         String exeRes = serialize(service.execute("checkSupplierExists", supJson));
         try {
            if (mapper.readTree(exeRes).get("value").asBoolean()) {
               res = requestBoolean(
                     "Supplier with this name and tax number already exists. Do you want to try again? (y/n):");
               if (res == false) {
                  return payload; // empty payload
               }
               continue;
            }
         } catch (Exception e) {
            view.showError(e.getMessage());
         }
         payload.put("name", name);
         payload.put("taxNumber", tax);
         res = false;
      }
      return payload;
   }

   private boolean doesSupplierExists(String supJson) {
      ServiceResponse<?> exeRes = service.execute("checkSupplierExists", supJson);
      if ((boolean) exeRes.getValue() == false) {
         view.showError("Supplier doesn't exist, update cancelled");
         return false;
      }
      return true;
   }

   // --- Agreement actions ---
   public void createAgreement() {
      view.showMessage("Creating a new agreement... Please enter the following details:");

      ObjectNode payload = mapper.createObjectNode();
      String supplierId = view.readLine("Supplier ID:");
      if (service.execute("checkSupplierExists", "{\"supplierId\":\"" + supplierId + "\"}").getValue() == null) {
         view.showError("Supplier doesn't exist, agreement creation cancelled.");
         return;
      }
      payload.put("supplierId", supplierId);
      Boolean selfSupply = requestBoolean("Is this a self-supply agreement (true/false):");
      payload.put("selfSupply", selfSupply);

      ArrayNode daysArray = payload.putArray("supplyDays");
      daysArray.addAll(askForSupplyDays().stream()
            .map(WeekofDay::name)
            .collect(mapper::createArrayNode, ArrayNode::add, ArrayNode::addAll));
      payload.put("agreementStartDate",
            askForFutureOrTodayDate("Enter agreement start date").toString());
      payload.put("agreementEndDate",
            askForFutureOrTodayDate("Enter agreement end date").toString());
      Boolean hasFixedSupplyDays = requestBoolean("Does this agreement have fixed supply days (true/false):");
      payload.put("hasFixedSupplyDays", hasFixedSupplyDays);
      String response = handleModuleCommand("addAgreement", payload.toString());
      view.dispatchResponse(response, Agreement.class);
   }

   public void updateAgreement() {
      view.showMessage("Updating an existing agreement…");

      String agreementId = view.readLine("Please enter the agreement ID to update:");
      String lookup = String.format("{\"agreementId\":\"%s\"}", agreementId);
      if (!doesAgreementExist(lookup)) {
         view.showError("Agreement not found, update cancelled.");
         return;
      }

      ObjectNode payload = mapper.createObjectNode();
      payload.put("agreementId", agreementId);

      while (true) {
         String field = view.readLine(
               "Enter field to update (blank to finish):\n" +
                     "  selfSupply, supplyDays, agreementStartDate, agreementEndDate, hasFixedSupplyDays")
               .trim().toLowerCase();
         if (field.isEmpty())
            break;

         switch (field) {
            case "selfsupply" -> payload.put("selfSupply",
                  requestBoolean("Self Supply? (true/false):"));
            case "supplydays" -> {
               ArrayNode days = mapper.createArrayNode();
               askForSupplyDays().forEach(d -> days.add(d.name()));
               payload.set("supplyDays", days);
            }
            case "agreementstartdate" -> payload.put("agreementStartDate",
                  askForFutureOrTodayDate("Enter new start date").toString());
            case "agreementenddate" -> payload.put("agreementEndDate",
                  askForFutureOrTodayDate("Enter new end date").toString());
            case "hasfixedsupplydays" -> payload.put("hasFixedSupplyDays",
                  requestBoolean("Has fixed supply days? (true/false):"));
            default -> view.showError("Unknown field: " + field);
         }
      }

      if (payload.size() == 1) {
         view.showMessage("No changes entered. Update cancelled.");
         return;
      }

      // 3) Dispatch and render result
      String respJson = handleModuleCommand("updateAgreement", payload.toString());
      view.dispatchResponse(respJson, Agreement.class);
   }

   private boolean doesAgreementExist(String lookupJson) {
      ServiceResponse<?> r = service.execute("checkAgreementExists", lookupJson);
      if ((boolean) r.getValue() == false) {
         view.showError("Agreement doesn't exist, update cancelled");
         return false;
      }
      return Boolean.TRUE.equals(r.getValue());
   }

   public void deleteAgreement() {
      view.showMessage("Deleting an existing agreement...");
      view.showMessage("Enter Agreement ID to delete:");
      String id = view.readLine();
      String response = handleModuleCommand("removeAgreement", id);
      view.dispatchResponse(response, Boolean.class);
   }

   public void viewAgreement() {
      view.showMessage("Viewing an existing agreement...");
      view.showMessage("Enter Agreement ID to view:");
      String id = view.readLine();
      view.dispatchResponse(
            handleModuleCommand("getAgreement", id),
            Agreement.class);
   }

   public void listAllAgreements() {
      view.showMessage("Listing all agreements...");
      view.dispatchResponse(
            handleModuleCommand("getAllAgreements", ""),
            Agreement[].class);
   }

   // --- Product actions ---
   private void addProduct() {
      view.showMessage("Adding a product…");
      ObjectNode p = mapper.createObjectNode();
      String sid = view.readLine("Supplier ID:");
      p.put("supplierId", sid);
      p.put("supplierCatalogNumber", view.readLine("Catalog #:"));
      p.put("manufacturerName", view.readLine("Manufacturer:"));
      p.put("name", view.readLine("Product name:"));
      try {
         p.put("price", new BigDecimal(view.readLine("Price:")));
      } catch (Exception e) {
         view.showError("Bad price");
         return;
      }
      String resp = handleModuleCommand("addProduct", p.toString());
      view.dispatchResponse(resp, Boolean.class);
   }

   private void updateProduct() {
      view.showMessage("Updating a product...");
      String sid = view.readLine("Supplier ID:");
      ObjectNode p = mapper.createObjectNode();
      p.put("supplierId", sid);
      p.put("productId", view.readLine("Product ID to update:"));
      if (requestBoolean("Change name? (y/n)")) {
         p.put("name", view.readLine("New name:"));
      }
      if (requestBoolean("Change price? (y/n)")) {
         p.put("price", view.readLine("New price:"));
      }
      String resp = handleModuleCommand("updateProduct", p.toString());
      view.dispatchResponse(resp, Boolean.class);
   }

   private void deleteProduct() {
      view.showMessage("Deleting a product...");
      String sid = view.readLine("Supplier ID:");
      ObjectNode p = mapper.createObjectNode();
      p.put("supplierId", sid);
      p.put("productId", view.readLine("Product ID to remove:"));
      String resp = handleModuleCommand("removeProduct", p.toString());
      view.dispatchResponse(resp, Boolean.class);
   }

   private void listProducts() {
      view.showMessage("Listing products for a supplier...");
      String sid = view.readLine("Supplier ID:");
      ObjectNode p = mapper.createObjectNode();
      p.put("supplierId", sid);
      String resp = handleModuleCommand("listProducts", p.toString());
      view.dispatchResponse(resp, List.class /* or SupplierProduct[].class */);
   }

   // --- Order actions ---
   private void createOrder() {
      view.showMessage("Creating an order…");
      ObjectNode payload = mapper.createObjectNode();
      String sid = view.readLine("Supplier ID:");
      payload.put("supplierId", sid);
      payload.put("orderDate", LocalDate.now().toString());
      ArrayNode items = payload.putArray("items");
      while (true) {
         ObjectNode line = mapper.createObjectNode();
         line.put("productId", view.readLine("Product ID:"));
         try {
            line.put("quantity", Integer.parseInt(view.readLine("Qty:")));
         } catch (Exception e) {
            view.showError("Bad qty");
            continue;
         }
         try {
            line.put("unitPrice", new BigDecimal(view.readLine("Unit price:")));
         } catch (Exception e) {
            view.showError("Bad price");
            continue;
         }
         items.add(line);
         if (!requestBoolean("Add more? (y/n)"))
            break;
      }
      String resp = serialize(
            orderService.execute("addOrder", payload.toString()));
      view.dispatchResponse(resp, Order.class);
   }

   public void updateOrder() {
      view.showMessage("Updating an existing order…");

      String orderId = view.readLine("Please enter the order ID to update:");
      String lookupJson = String.format("{\"orderId\":\"%s\"}", orderId);
      if (!doesOrderExist(lookupJson)) {
         view.showError("Order doesn't exist, update cancelled");
         return;
      }

      ObjectNode payload = mapper.createObjectNode();
      payload.put("orderId", orderId);

      while (true) {
         String field = view.readLine(
               "Enter field to update (blank to finish):\n" +
                     "  orderDate, items")
               .trim().toLowerCase();
         if (field.isEmpty())
            break;

         switch (field) {
            case "orderdate" -> {
               String raw = view.readLine("New order date (YYYY-MM-DD):");
               payload.put("orderDate", LocalDate.parse(raw).toString());
            }
            case "items" -> {
               ArrayNode items = mapper.createArrayNode();
               view.showMessage("Re-enter line items:");
               while (true) {
                  ObjectNode line = mapper.createObjectNode();
                  line.put("productId", view.readLine("  Product ID:"));
                  try {
                     int qty = Integer.parseInt(view.readLine("  Quantity:"));
                     BigDecimal price = new BigDecimal(view.readLine("  Unit price:"));
                     line.put("quantity", qty);
                     line.put("unitPrice", price);
                  } catch (Exception ex) {
                     view.showError("Bad number, try again.");
                     continue;
                  }
                  items.add(line);
                  if (!requestBoolean("Add another line item? (y/n)"))
                     break;
               }
               payload.set("items", items);
            }
            default -> view.showError("Unknown field: " + field);
         }
      }

      if (payload.size() == 1) {
         view.showMessage("No changes entered. Update cancelled.");
         return;
      }

      String responseJson = serialize(orderService.execute("updateOrder", payload.toString()));
      view.dispatchResponse(responseJson, Order.class);
   }

   public void deleteOrder() {
      view.showMessage("Deleting an existing order...");
      String orderId = view.readLine("Please enter the order ID to delete:");
      String deleteJson = String.format("{\"orderId\":\"%s\"}", orderId);
      String response = serialize(orderService.execute("removeOrder", deleteJson));
      view.dispatchResponse(response, ServiceResponse.class);
   }

   // 3) View one order
   public void viewOrder() {
      view.showMessage("Viewing an existing order...");
      String orderId = view.readLine("Please enter the order ID to view:");
      String viewJson = String.format("{\"orderId\":\"%s\"}", orderId);
      String response = serialize(orderService.execute("getOrder", viewJson));
      view.dispatchResponse(response, Order.class);
   }

   // 4) List all orders
   public void viewAllOrders() {
      view.showMessage("Listing all orders...");
      String response = serialize(orderService.execute("viewAllOrders", "{}"));
      view.dispatchResponse(response, Order[].class);
   }

   // helper to check existence
   private boolean doesOrderExist(String lookupJson) {
      ServiceResponse<?> r = orderService.execute("getOrder", lookupJson);
      if (r.getValue() == null) {
         view.showError("Order doesn't exist, update cancelled");
         return false;
      }
      return true;
   }

   // --- shared helpers ---
   private LocalDate askForFutureOrTodayDate(String prompt) {
      DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;
      while (true) {
         view.showMessage(prompt + " (format YYYY-MM-DD, must be today or later):");
         String raw = view.readLine().trim();
         try {
            LocalDate entered = LocalDate.parse(raw, fmt);

            if (entered.isBefore(LocalDate.now())) {
               view.showMessage("The date cannot be in the past. Please enter today’s date or later.");
               continue;
            }

            return entered;

         } catch (DateTimeParseException ex) {
            view.showMessage("Invalid format. Please use YYYY-MM-DD, e.g. 2025-04-15.");
         }
      }
   }

   private EnumSet<WeekofDay> askForSupplyDays() {
      EnumSet<WeekofDay> supplyDays = EnumSet.noneOf(WeekofDay.class);
      String userInput = "";
      while (userInput == null || userInput.isEmpty()) {
         userInput = view
               .readLine(
                     "Enter delivery day (e.g. Monday/monday/MONDAY), then enter 'done' (To deselect, enter the day again):")
               .trim().toUpperCase();
         if (userInput.equals("DONE")) {
            view.showMessage("Selected days: " + supplyDays);
            break;
         }
         try {
            WeekofDay day = WeekofDay.valueOf(userInput);
            if (!supplyDays.add(day)) {
               supplyDays.remove(day);
               view.showMessage(day + " removed. Current days: " + supplyDays);
            } else {
               view.showMessage(day + " added. Current days: " + supplyDays);
            }
         } catch (IllegalArgumentException e) {
            view.showMessage("Invalid entry. Please enter a valid weekday or DONE.");
         }
      }

      return supplyDays;
   }

   private ArrayNode requestContacts() {
      ArrayNode contacts = mapper.createArrayNode();
      String wantsToContinue = view.readLine("Do you want to add a contact? (y/n):").toLowerCase();
      while (wantsToContinue.equals("y")) {
         ObjectNode contact = mapper.createObjectNode();
         String name = null;
         while (name == null || name.isEmpty())
            name = view.readLine("Please enter contact name (Can not be empty):");
         contact.put("name", name);
         String email = null;
         while (email == null || email.isEmpty())
            email = view.readLine("Please enter contact email (Can not be empty):");
         contact.put("email", email);
         String phone = null;
         while (phone == null || phone.isEmpty())
            phone = view.readLine("Please enter contact phone (Can not be empty):");
         contact.put("phone", phone);
         contacts.add(contact);
         wantsToContinue = view.readLine("Do you want to add another contact? (y/n):").toLowerCase();
      }
      return contacts;
   }

   private ObjectNode requestAddress() {
      ObjectNode addr = mapper.createObjectNode();
      String street = null;
      while (street == null || street.isEmpty())
         street = view.readLine("Please enter an address – street (Can not be empty):");
      String city = null;
      while (city == null || city.isEmpty())
         city = view.readLine("Please enter an address – city (Can not be empty):");
      String buildingNumber = null;
      while (buildingNumber == null || buildingNumber.isEmpty())
         buildingNumber = view.readLine("Please enter an address – buildingNumber (Can not be empty):");

      addr.put("street", street);
      addr.put("city", city);
      addr.put("buildingNumber", buildingNumber);
      return addr;
   }

   private ObjectNode requestPaymentDetails() {
      ObjectNode pay = mapper.createObjectNode();
      String bankAccountNumber = null;
      while (bankAccountNumber == null || bankAccountNumber.isEmpty())
         bankAccountNumber = view.readLine("Please enter payment details – bankAccountNumber (Can not be empty):");
      String paymentMethod = null;
      while (paymentMethod == null || paymentMethod.isEmpty())
         paymentMethod = view.readLine(
               "Please enter payment details – paymentMethod (Can not be empty, Must be valid or creation will be rejected later [e.g. CASH]):")
               .toUpperCase();
      String paymentTerm = null;
      while (paymentTerm == null || paymentTerm.isEmpty())
         paymentTerm = view.readLine(
               "Please enter payment details – paymentTerm (Can not be empty and must be valid or creation will be rejected later[e.g. N30]):")
               .toUpperCase();

      pay.put("bankAccountNumber", bankAccountNumber);
      pay.put("paymentMethod", paymentMethod);
      pay.put("paymentTerm", paymentTerm);
      return pay;
   }
}
