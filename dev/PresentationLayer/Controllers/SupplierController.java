package PresentationLayer.Controllers;

import java.lang.reflect.Array;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import DomainLayer.Classes.Supplier;
import PresentationLayer.AbstractController;
import PresentationLayer.View;
import ServiceLayer.SupplierService;
import ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;

public class SupplierController extends AbstractController {

   public SupplierController(View view, SupplierService supplierService) {
      super(view, supplierService);
      this.implemented = true;
      controllerMenuOptions.put("1", this::createSupplier);
      controllerMenuOptions.put("2", this::updateSupplier);
      controllerMenuOptions.put("3", this::deleteSupplier);
      controllerMenuOptions.put("4", this::viewSupplier);
      controllerMenuOptions.put("5", this::viewAllSuppliers);
      controllerMenuOptions.put("6", () -> {
         System.out.println("Returning to the main menu...");
      });
      controllerMenuOptions.put("?", () -> {
         System.out.println("Invalid choice. Please try again.");
      });
   }

   public List<String> showMenu() {
      return List.of(
            "Please choose an option:",
            "Create Supplier",
            "Update Supplier",
            "Delete Supplier",
            "View Supplier",
            "View All Suppliers",
            "Back to Main Menu");
   }

   public void createSupplier() {
      view.showMessage("Creating a new supplier… Please enter the following details:");
      ObjectNode payload = initCreatePayload();
      if (payload == null)
         return;
      payload.set("address", requestAddress());

      payload.set("paymentDetails", requestPaymentDetails());

      ArrayNode products = mapper.createArrayNode();

      // even if user says “n”, contacts is just []
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
      System.out.println("Updating an existing supplier...");
      String supplierId = view.readLine("Please enter the supplier ID to update:");
      if (!doesSupplierExists(supplierId)) {
         view.showError("Supplier id does not exist.");
         return;
      }
      view.showMessage(
            "What do you want to update? (Name, TaxNumber, Address, BankNumber, BranchNumber, AccountNumber, PaymentMethod, PaymentTerm)");
      String fieldToUpdate = view.readLine();
      view.showMessage("Please enter the new value:");
      String newValue = view.readLine();
      String updateJson = String.format("{\"supplierId\":\"%s\", \"%s\":\"%s\"}", supplierId, fieldToUpdate, newValue);
      String response = handleModuleCommand("updateSupplier", updateJson);
      view.dispatchResponse(response, Supplier.class);
   }

   public void deleteSupplier() {
      System.out.println("Deleting an existing supplier...");
      view.showMessage("Please enter the supplier ID to delete:");
      String supplierId = view.readLine();
      String deleteJson = String.format("{\"supplierId\":\"%s\"}", supplierId);
      String response = handleModuleCommand("removeSupplier", deleteJson);
      view.dispatchResponse(response, ServiceResponse.class);
      // if (response != null) {
      // ServiceResponse<Supplier> serviceResponse = mapper.readValue(response,
      // ServiceResponse.class);
      // if (serviceResponse.getError() == null) {
      // view.showMessage("Supplier deleted successfully.");
      // } else {
      // view.showError("Error deleting supplier: " + serviceResponse.getError());
      // }
      // } else {
      // view.showError("Failed to delete supplier.");
      // }
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
         if (service.execute("checkSupplierExists", supJson).equals("false")) {
            res = requestBoolean(
                  "Supplier with this name and tax number already exists. Do you want to try again? (true/false):");
            if (res == false) {
               return null;
            }
            continue;
         }
         payload.put("name", name);
         payload.put("taxNumber", tax);
      }
      return payload;
   }

   private boolean doesSupplierExists(String supName, String supTax) {
      try {
         String viewJson = String.format("{\"supplierId\":\"%s\"}", supName + ":" + supTax);
         String response = handleModuleCommand("getSupplierDetails", viewJson);
         Supplier supplier = mapper.readValue(response, Supplier.class);
         return supplier != null;
      } catch (Exception e) {
         view.showError("Error: " + e.getMessage());
         return false;
      }
   }

   private boolean doesSupplierExists(String supplierId) {
      try {
         String viewJson = String.format("{\"supplierId\":\"%s\"}", supplierId);
         String response = handleModuleCommand("getSupplierDetails", viewJson);
         Supplier supplier = mapper.readValue(response, Supplier.class);
         return supplier != null;
      } catch (Exception e) {
         view.showError("Error: " + e.getMessage());
         return false;
      }
   }

   private ArrayNode requestContacts() {
      ArrayNode contacts = mapper.createArrayNode();
      String wantsToContinue = view.readLine("Do you want to add a contact? (y/n):").toLowerCase();
      while (wantsToContinue.equals("y")) {
         ObjectNode contact = mapper.createObjectNode();
         String name = null;
         while (name == null || name.isEmpty())
            view.readLine("Please enter contact name (Can not be empty):");
         String email = null;
         while (email == null || email.isEmpty())
            view.readLine("Please enter contact email (Can not be empty):");
         String phone = null;
         while (phone == null || phone.isEmpty())
            view.readLine("Please enter contact phone (Can not be empty):");
         contacts.add(contact);
         wantsToContinue = view.readLine("Do you want to add another contact? (y/n):").toLowerCase();
      }
      return contacts;
   }

   private ObjectNode requestAddress() {
      ObjectNode addr = mapper.createObjectNode();
      String street = null;
      while (street == null || street.isEmpty())
         view.readLine("Please enter an address – street (Can not be empty):");
      String city = null;
      while (city == null || city.isEmpty())
         view.readLine("Please enter an address – city (Can not be empty):");
      String buildingNumber = null;
      while (buildingNumber == null || buildingNumber.isEmpty())
         view.readLine("Please enter an address – buildingNumber (Can not be empty):");

      addr.put("street", street);
      addr.put("city", city);
      addr.put("buildingNumber", buildingNumber);
      return addr;
   }

   private ObjectNode requestPaymentDetails() {
      ObjectNode pay = mapper.createObjectNode();
      String bankAccountNumber = null;
      while (bankAccountNumber == null || bankAccountNumber.isEmpty())
         view.readLine("Please enter payment details – bankAccountNumber (Can not be empty):");
      String paymentMethod = null;
      while (paymentMethod == null || paymentMethod.isEmpty())
         view.readLine(
               "Please enter payment details – paymentMethod (Can not be empty, Must be valid or creation will be rejected later [e.g. CASH]):");
      String paymentTerm = null;
      while (paymentTerm == null || paymentTerm.isEmpty())
         view.readLine(
               "Please enter payment details – paymentTerm (Can not be empty and must be valid or creation will be rejected later[e.g. N30]):");

      pay.put("bankAccountNumber", bankAccountNumber);
      pay.put("paymentMethod", paymentMethod);
      pay.put("paymentTerm", paymentTerm);
      return pay;
   }
}
