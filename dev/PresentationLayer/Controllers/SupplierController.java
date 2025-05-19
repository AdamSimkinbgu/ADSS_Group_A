package PresentationLayer.Controllers;

import java.util.HashMap;
import java.util.Map;

import PresentationLayer.View;
import PresentationLayer.CLIs.CommandInterface;
import PresentationLayer.CLIs.Commands.CreateSupplierCMD;
import ServiceLayer.SupplierService;

public class SupplierController {
   private final View view;
   private final Map<String, CommandInterface> commands;

   public SupplierController(View view, SupplierService supplierService) {
      this.view = view;
      this.commands = new HashMap<>();
      commands.put("1", new CreateSupplierCMD(view, supplierService));
   }

   public void start() {
      while (true) {
         view.showMessage("Supplier Management Menu");
         view.showMessage("1. Create Supplier");
         view.showMessage("2. Update Supplier");
         view.showMessage("3. Delete Supplier");
         view.showMessage("4. List Suppliers");
         view.showMessage("Type 'return' to go back.");

         String choice = view.readLine("Choose an option: ").toLowerCase();
         if (choice.equals("return")) {
            break;
         }
         CommandInterface command = commands.getOrDefault(choice, null);
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
