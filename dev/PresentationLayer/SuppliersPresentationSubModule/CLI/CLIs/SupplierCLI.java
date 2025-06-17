package PresentationLayer.SuppliersPresentationSubModule.CLI.CLIs;

import java.util.HashMap;
import java.util.Map;

import PresentationLayer.SuppliersPresentationSubModule.CLI.CommandInterface;
import PresentationLayer.SuppliersPresentationSubModule.CLI.View;

public class SupplierCLI {
   private final View view;
   private final Map<String, CommandInterface> supplierCommands;

   public SupplierCLI(View view, Map<String, CommandInterface> commands) {
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
         view.showMessage("Type 'back' or '0' to go back.");

         String choice = view.readLine("Choose an option: ").toLowerCase();
         if (choice.equals("return") || choice.equals("0")) {
            view.showMessage(" === Returning to the Suppliers main menu ===");
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
