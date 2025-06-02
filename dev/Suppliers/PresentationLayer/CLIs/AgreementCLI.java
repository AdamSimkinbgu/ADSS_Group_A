package Suppliers.PresentationLayer.CLIs;

import java.util.HashMap;
import java.util.Map;

import Suppliers.PresentationLayer.CommandInterface;
import Suppliers.PresentationLayer.View;

public class AgreementCLI {
   private final View view;
   private final Map<String, CommandInterface> agreementCommands;

   public AgreementCLI(View view, Map<String, CommandInterface> commands) {
      this.view = view;
      this.agreementCommands = new HashMap<>();
      agreementCommands.put("1", commands.get("CreateAgreementCMD"));
      agreementCommands.put("2", commands.get("UpdateAgreementCMD"));
      agreementCommands.put("3", commands.get("RemoveAgreementCMD"));
      // commands.put("4", commands.get("GetAgreementCMD"));
      // commands.put("5", commands.get("GetAllAgreementsCMD"));
      agreementCommands.put("6", commands.get("ViewAllAgreementsForSupplierCMD"));
   }

   public void start() {
      while (true) {
         view.showMessage("Agreement Management Menu");
         view.showMessage("1. Create Agreement");
         view.showMessage("2. Update Agreement");
         view.showMessage("3. Remove Agreement");
         // view.showMessage("4. Get Agreement");
         // view.showMessage("5. Get All Agreements");
         view.showMessage("6. Get Agreements By Supplier");
         view.showMessage("Type 'return' to go back.");

         String choice = view.readLine("Choose an option: ").toLowerCase();
         if (choice.equals("return")) {
            break;
         }
         CommandInterface command = agreementCommands.getOrDefault(choice, null);
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
