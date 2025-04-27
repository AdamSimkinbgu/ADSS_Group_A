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
      ObjectNode payload = mapper.createObjectNode();

      String name = view.readLine("Name:");
      payload.put("name", name);

      String tax = view.readLine("TaxNumber:");
      payload.put("taxNumber", tax);

      ObjectNode addr = mapper.createObjectNode();
      addr.put("street", view.readLine("Address – street:"));
      addr.put("city", view.readLine("Address – city:"));
      addr.put("buildingNumber", view.readLine("Address – buildingNumber:"));
      payload.set("address", addr);

      ObjectNode pay = mapper.createObjectNode();
      pay.put("bankAccountNumber", view.readLine("PaymentDetails – bankAccountNumber:"));
      pay.put("paymentMethod", view.readLine("PaymentDetails – paymentMethod (e.g. CASH):").toUpperCase());
      pay.put("paymentTerm", view.readLine("PaymentDetails – paymentTerm (e.g. N30):").toUpperCase());
      payload.set("paymentDetails", pay);

      ArrayNode contacts = mapper.createArrayNode();
      ArrayNode products = mapper.createArrayNode();
      ArrayNode agreements = mapper.createArrayNode();
      // if (view.readLine("Add contacts? (y/n):").equalsIgnoreCase("y")) {
      // while (true) {
      // String c = view.readLine(" Enter contact (blank to finish):");
      // if (c == null || c.isBlank())
      // break;
      // contacts.add(c);
      // }
      // }

      // even if user says “n”, contacts is just []
      payload.set("contacts", contacts);
      payload.set("products", products);
      payload.set("agreements", agreements);

      String supplierJson = payload.toString();

      view.dispatchResponse(
            handleModuleCommand("addSupplier", supplierJson),
            Supplier.class);
   }

   public void updateSupplier() {
      System.out.println("Updating an existing supplier...");
      view.showMessage("Please enter the supplier ID to update:");
      String supplierId = view.readLine();
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
}
