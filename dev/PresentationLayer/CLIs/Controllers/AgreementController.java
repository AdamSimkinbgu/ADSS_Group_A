package PresentationLayer.CLIs.Controllers;

import java.util.HashMap;
import java.util.Map;

import PresentationLayer.View;
import PresentationLayer.CLIs.CommandInterface;
import PresentationLayer.CLIs.AgreementCommands.CreateAgreementCMD;
import PresentationLayer.CLIs.AgreementCommands.RemoveAgreementCMD;
import PresentationLayer.CLIs.AgreementCommands.ViewAgreementsBySupplierIdCMD;
import ServiceLayer.AgreementService;
import ServiceLayer.SupplierService;

public class AgreementController {
   private final View view;
   private final Map<String, CommandInterface> commands;

   public AgreementController(View view, AgreementService agreementService, SupplierService supplierService) {
      this.view = view;
      this.commands = new HashMap<>();
      commands.put("1", new CreateAgreementCMD(view, agreementService, supplierService));
      // commands.put("2", new UpdateAgreementCMD(view, agreementService));
      commands.put("3", new RemoveAgreementCMD(view, agreementService, supplierService));
      // commands.put("4", new GetAgreementCMD(view, agreementService));
      // commands.put("5", new GetAllAgreementsCMD(view, agreementService));
      commands.put("6", new ViewAgreementsBySupplierIdCMD(view, agreementService));
   }

   public void start() {
      while (true) {
         view.showMessage("Agreement Management Menu");
         view.showMessage("1. Create Agreement");
         // view.showMessage("2. Update Agreement");
         view.showMessage("3. Remove Agreement");
         // view.showMessage("4. Get Agreement");
         // view.showMessage("5. Get All Agreements");
         view.showMessage("6. Get Agreements By Supplier");
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
