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
import DomainLayer.Classes.Supplier;
import DomainLayer.Enums.WeekofDay;
import PresentationLayer.AbstractController;
import PresentationLayer.View;
import ServiceLayer.SupplierService;
import ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;

public class SupplierAndAgreementController extends AbstractController {
   protected Map<String, Runnable> supplierOptions = new HashMap<>();
   protected Map<String, Runnable> agreementOptions = new HashMap<>();
   protected Map<String, Runnable> productOptions = new HashMap<>();

   public SupplierAndAgreementController(View view, SupplierService supplierService) {
      super(view, supplierService);
      this.implemented = true;
      controllerMenuOptions.put("1", () -> {
         List<String> menu = showSupplierMenu();
         view.showOptions(menu.get(0), menu.subList(0, menu.size()));
         handleSupplierMenuChoice();
      });
      controllerMenuOptions.put("2", () -> {
         List<String> menu = showAgreementMenu();
         view.showOptions(menu.get(0), menu.subList(0, menu.size()));
         handleAgreementMenuChoice();
      });
      controllerMenuOptions.put("3", () -> {
         List<String> menu = showProductMenu();
         view.showOptions(menu.get(0), menu.subList(0, menu.size()));
         handleProductMenuChoice();
      });
      controllerMenuOptions.put("4", () -> System.out.println("Returning to the main menu..."));
      supplierOptions.put("1", this::createSupplier);
      supplierOptions.put("2", this::updateSupplier);
      supplierOptions.put("3", this::deleteSupplier);
      supplierOptions.put("4", this::viewSupplier);
      supplierOptions.put("5", this::viewAllSuppliers);
      supplierOptions.put("6", () -> System.out.println("Returning to the main menu..."));
      supplierOptions.put("?", () -> System.out.println("Invalid choice. Please try again."));
      productOptions.put("1", this::addProduct);
      productOptions.put("2", this::updateProduct);
      productOptions.put("3", this::deleteProduct);
      productOptions.put("4", this::listProducts);
      productOptions.put("5", () -> System.out.println("Returning to the main menu..."));
      productOptions.put("?", () -> System.out.println("Invalid choice. Please try again."));
      agreementOptions.put("1", this::createAgreement);
      agreementOptions.put("2", this::updateAgreement);
      agreementOptions.put("3", this::deleteAgreement);
      agreementOptions.put("4", this::viewAgreement);
      agreementOptions.put("5", this::listAllAgreements);
      agreementOptions.put("6", () -> System.out.println("Returning to the main menu..."));
      agreementOptions.put("?", () -> System.out.println("Invalid choice. Please try again."));
   }

   public List<String> showMenu() {
      return List.of(
            "Please choose an option:",
            "Supplier Menu",
            "Agreement Menu",
            "Product Menu",
            "Back to Main Menu");
   }

   public List<String> showSupplierMenu() {
      return List.of(
            "Please choose an option:",
            "Create Supplier",
            "Update Supplier",
            "Delete Supplier",
            "View Supplier",
            "View All Suppliers",
            "Back to Main Menu");
   }

   public List<String> showAgreementMenu() {
      return List.of(
            "Please choose an option:",
            "Create Agreement",
            "Update Agreement",
            "Delete Agreement",
            "View Agreement",
            "List All Agreements",
            "Back to Main Menu");
   }

   private void handleSupplierMenuChoice() {
      String choice = view.readLine();
      Runnable action = supplierOptions.get(choice);
      if (action != null) {
         action.run();
      } else {
         supplierOptions.get("?").run();
      }
   }

   private void handleAgreementMenuChoice() {
      String choice = view.readLine();
      Runnable action = agreementOptions.get(choice);
      if (action != null) {
         action.run();
      } else {
         agreementOptions.get("?").run();
      }
   }

   private void showAndHandleProductMenu() {
      List<String> menu = showProductMenu();
      view.showOptions(menu.get(0), menu.subList(1, menu.size()));
      handleProductMenuChoice();
   }

   public List<String> showProductMenu() {
      return List.of(
            "Please choose an option:",
            "Add Product",
            "Update Product",
            "Delete Product",
            "List Products",
            "Back to Main Menu");
   }

   private void handleProductMenuChoice() {
      String choice = view.readLine();
      Runnable action = productOptions.getOrDefault(choice, productOptions.get("?"));
      action.run();
   }

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

      String responseJson = handleModuleCommand("updateSupplier", payload.toString());
      view.dispatchResponse(responseJson, Supplier.class);
   }

   public void deleteSupplier() {
      System.out.println("Deleting an existing supplier...");
      view.showMessage("Please enter the supplier ID to delete:");
      String supplierId = view.readLine();
      String deleteJson = String.format("{\"supplierId\":\"%s\"}", supplierId);
      String response = handleModuleCommand("removeSupplier", deleteJson);
      view.dispatchResponse(response, ServiceResponse.class);
   }

   public void viewSupplier() {
      System.out.println("Viewing an existing supplier...");
      view.showMessage("Please enter the supplier ID to view:");
      String supplierId = view.readLine();
      String viewJson = String.format("{\"supplierId\":\"%s\"}", supplierId);
      String response = handleModuleCommand("getSupplierDetails", viewJson);
      view.dispatchResponse(response, Supplier.class);
   }

   public void viewAllSuppliers() {
      System.out.println("Viewing all suppliers...");
      String response = handleModuleCommand("getAllSuppliers", "{}");
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

   public void createAgreement() {
      view.showMessage("Creating a new agreement... Please enter the following details:");

      ObjectNode payload = mapper.createObjectNode();
      String supplierId = view.readLine("Supplier ID:");
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

   private void addProduct() {
      view.showMessage("Adding a new product...");
      String sid = view.readLine("Supplier ID:");
      if (sid == null || sid.isEmpty()) {
         view.showError("Supplier ID cannot be empty.");
         return;
      }
      if (!doesSupplierExists("{\"supplierId\":\"" + sid + "\"}")) {
         view.showError("Supplier doesn't exist, product creation cancelled.");
         return;
      }

      ObjectNode p = mapper.createObjectNode();
      p.put("supplierId", sid);

      String name = view.readLine("Name:");
      p.put("name", name);
      String supplierCatalogNumber = view.readLine("Supplier catalog number:");
      p.put("supplierCatalogNumber", supplierCatalogNumber);
      String manufacturerName = view.readLine("Manufacturer name:");
      p.put("manufacturerName", manufacturerName);
      BigDecimal price = new BigDecimal(view.readLine("Price:"));
      // p.put(String, BigDecimal) creates a JSON numeric node
      p.put("price", price);

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

}
