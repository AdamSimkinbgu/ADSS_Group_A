package PresentationLayer.CLIs.Commands.CrossSystemCommands;

import PresentationLayer.View;
import PresentationLayer.CLIs.CommandInterface;

import java.util.Map;

public class CreateSupplierWithOptionalsCMD implements CommandInterface {

   private final View view;
   private final Map<String, CommandInterface> agreegatedCommandsMap;

   public CreateSupplierWithOptionalsCMD(View view, Map<String, CommandInterface> commands) {
      this.view = view;
      this.agreegatedCommandsMap = commands;
   }

   @Override
   public void execute() {
      view.showMessage("Creating a supplier with optional fields.");
      view.showMessage("You can choose to fill in optional fields or leave them blank.");

      // Execute the command to create a supplier with optionals
      CommandInterface createSupplierCommand = agreegatedCommandsMap.get("CreateSupplierCMD");
      if (createSupplierCommand != null) {
         createSupplierCommand.execute();
      } else {
         view.showError("CreateSupplierCMD not found in the command map.");
      }
      view.showMessage("Would you like to add Products to the supplier? (yes/no)");
      if (view.readLine().equalsIgnoreCase("yes")) {
         CommandInterface addProductsCommand = agreegatedCommandsMap.get("AddProductsToSupplierCMD");
         if (addProductsCommand != null) {
            addProductsCommand.execute();
            view.showMessage("Products added to the supplier successfully.");
            view.showMessage("Would you like to add Agreements to the supplier? (yes/no)");
            if (view.readLine().equalsIgnoreCase("yes")) {
               CommandInterface addAgreementsCommand = agreegatedCommandsMap.get("AddAgreementsToSupplierCMD");
               if (addAgreementsCommand != null) {
                  addAgreementsCommand.execute();
                  view.showMessage("Agreements added to the supplier successfully.");
               } else {
                  view.showError("AddAgreementsToSupplierCMD not found in the command map.");
               }
            } else {
               view.showMessage("No agreements added to the supplier.");
            }
         } else {
            view.showError("AddProductsToSupplierCMD not found in the command map.");
         }
      } else {
         view.showMessage("No products added to the supplier.");
      }
   }

}
