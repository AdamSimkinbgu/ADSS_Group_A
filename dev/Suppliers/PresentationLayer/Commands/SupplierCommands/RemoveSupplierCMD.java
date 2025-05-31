package Suppliers.PresentationLayer.Commands.SupplierCommands;

import Suppliers.PresentationLayer.CommandInterface;
import Suppliers.PresentationLayer.View;
import Suppliers.ServiceLayer.SupplierService;
import Suppliers.ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;

public class RemoveSupplierCMD implements CommandInterface {
   private final SupplierService service;
   private final View view;

   public RemoveSupplierCMD(View view, SupplierService supplierService) {
      this.view = view;
      this.service = supplierService;
   }

   @Override
   public void execute() {
      String stringID = view.readLine("Enter the ID of the supplier to remove:");
      int intID;
      if (stringID == null || stringID.isBlank()) {
         view.showError("ID must not be blank");
         return;
      }
      try {
         intID = Integer.parseInt(stringID);
      } catch (NumberFormatException e) {
         view.showError("ID must be a number");
         return;
      }
      ServiceResponse<?> res = service.removeSupplier(intID);
      if (res.isSuccess()) {
         view.showMessage("-- Supplier removed successfully --");
      } else {
         view.showError("-- Failed to remove supplier --");
         res.getErrors().forEach(view::showError);
      }
   }

}
