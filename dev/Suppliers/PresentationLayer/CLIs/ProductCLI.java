package Suppliers.PresentationLayer.CLIs;

import java.util.HashMap;
import java.util.Map;

import Suppliers.PresentationLayer.CommandInterface;
import Suppliers.PresentationLayer.View;

public class ProductCLI {
   private final View view;
   private final Map<String, CommandInterface> productCommands;

   public ProductCLI(View view, Map<String, CommandInterface> commands) {
      this.view = view;
      this.productCommands = new HashMap<>();
      productCommands.put("1", commands.get("CreateProductCMD"));
      productCommands.put("2", commands.get("UpdateProductCMD"));
      productCommands.put("3", commands.get("RemoveProductCMD"));
      productCommands.put("4", commands.get("ViewAllProductsCMD"));
   }

   public void start() {
      while (true) {
         view.showMessage("Product Management Menu");
         view.showMessage("1. Create Product");
         view.showMessage("2. Update Product");
         view.showMessage("3. Remove Product");
         view.showMessage("4. List Products");
         view.showMessage("Type 'back' or '0' to go back.");

         String choice = view.readLine("Choose an option: ").toLowerCase();
         if (choice.equals("back") || choice.equals("0")) {
            view.showMessage(" === Returning to the Suppliers main menu ===");
            break;
         }
         CommandInterface command = productCommands.getOrDefault(choice, null);
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
