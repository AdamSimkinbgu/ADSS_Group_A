package PresentationLayer;

import java.security.Provider.Service;
import java.util.List;

import DomainLayer.Supplier;
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
      controllerMenuOptions.put("5", () -> {
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
            "Back to Main Menu");
   }

   public void createSupplier() {
      System.out.println("Creating a new supplier...");
      List<String> input = view.readParameters(
            "Please enter the supplier details: Name-TaxNumber-Address-BankNumber-BranchNumber-AccountNumber-PaymentMethod-PaymentTerm");
      String supplierJson = fuseClassAttributesAndParametersToJson(Supplier.class, input);
      view.dispatchResponse(handleModuleCommand(
            "addSupplier",
            supplierJson), Supplier.class);
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
}
