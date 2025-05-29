package PresentationLayer.CLIs.Controllers;

import java.util.HashMap;
import java.util.Map;

import PresentationLayer.View;
import PresentationLayer.CLIs.CommandInterface;

public class SupplierController{
   private final View view;
   private final Map<String, CommandInterface> supplierCommands;

   public SupplierController(View view, Map<String, CommandInterface> commands) {
      this.view = view;
      this.supplierCommands = new HashMap<>();
      supplierCommands.put("1", commands.get("CreateSupplierCMD"));
      supplierCommands.put("2", commands.get("UpdateSupplierCMD"));
      supplierCommands.put("3", commands.get("RemoveSupplierCMD"));
      supplierCommands.put("4", commands.get("ViewAllSuppliersCMD"));
   }

   public void start() {
      while (true) {
         view.showMessage("Supplier Management Menu");
         view.showMessage("1. Create Supplier");
         view.showMessage("2. Update Supplier");
         view.showMessage("3. Remove Supplier");
         view.showMessage("4. List Suppliers");
         view.showMessage("Type 'return' to go back.");

         String choice = view.readLine("Choose an option: ").toLowerCase();
         if (choice.equals("return")) {
            break;
         }
         CommandInterface command = supplierCommands.getOrDefault(choice, null);
         if (command != null) {
            try {
               command.execute();
            } catch (Exception e) {
               view.showMessage("An error occurred: " + e.getMessage());
            }
         } else {
            view.showError("Invalid option");
         }

      }
   }
}
