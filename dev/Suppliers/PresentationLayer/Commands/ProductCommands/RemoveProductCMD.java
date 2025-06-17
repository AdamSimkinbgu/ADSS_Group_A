package Suppliers.PresentationLayer.Commands.ProductCommands;

import Suppliers.PresentationLayer.CommandInterface;
import Suppliers.PresentationLayer.View;
import Suppliers.ServiceLayer.SupplierService;
import Suppliers.ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;

public class RemoveProductCMD implements CommandInterface {

   private final View view;
   private final SupplierService service;

   public RemoveProductCMD(View view, SupplierService supplierService) {
      this.view = view;
      this.service = supplierService;
   }

   @Override
   public void execute() {
      int supplierID;
      try {
         supplierID = Integer.parseInt(view.readLine("Enter supplier ID: "));
         // ServiceResponse<?> checkResponse = service.checkSupplierExists(supplierID);
         // if (!checkResponse.isSuccess()) {
         // view.showError("Supplier with ID " + supplierID + " does not exist.");
         // return;
         // }
      } catch (NumberFormatException e) {
         view.showError("Invalid supplier ID format. Please enter a valid integer.");
         return;
      }

      String productIdString = view.readLine("Enter product id to remove: ");
      int productID;
      try {
         productID = Integer.parseInt(productIdString);
      } catch (NumberFormatException e) {
         view.showError("Invalid product ID format. Please enter a valid integer.");
         return;
      }
      ServiceResponse<?> res = service.removeProduct(productID, supplierID);
      if (res.isSuccess()) {
         view.showMessage("-- Product removed successfully --\n" + productID);
      } else {
         view.showError("-- Failed to remove product --");
         res.getErrors().forEach(error -> view.showError(error));
      }
   }

}
