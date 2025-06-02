package Suppliers.PresentationLayer.Commands.AgreementCommands;

import Suppliers.PresentationLayer.CommandInterface;
import Suppliers.PresentationLayer.View;
import Suppliers.ServiceLayer.AgreementService;
import Suppliers.ServiceLayer.SupplierService;
import Suppliers.ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;

public class RemoveAgreementCMD implements CommandInterface {
   private final View view;
   private final AgreementService agreementService;
   private final SupplierService supplierService;

   public RemoveAgreementCMD(View view, AgreementService agreementService, SupplierService supplierService) {
      this.view = view;
      this.agreementService = agreementService;
      this.supplierService = supplierService;
   }

   @Override
   public void execute() {
      String supplierIDString = view.readLine("Enter the supplier ID:");
      int supplierID;
      if (supplierIDString == null || supplierIDString.isBlank()) {
         view.showError("Supplier ID must not be blank");
         return;
      }
      try {
         supplierID = Integer.parseInt(supplierIDString);
      } catch (NumberFormatException e) {
         view.showError("Supplier ID must be a number");
         return;
      }
      String agreementIDString = view.readLine("Enter the agreement ID:");
      int agreementID;
      if (agreementIDString == null || agreementIDString.isBlank()) {
         view.showError("Agreement ID must not be blank");
         return;
      }
      try {
         agreementID = Integer.parseInt(agreementIDString);
      } catch (NumberFormatException e) {
         view.showError("Agreement ID must be a number");
         return;
      }

      ServiceResponse<?> removeRes = agreementService.removeAgreement(agreementID, supplierID);
      if (removeRes.isSuccess()) {
         view.showMessage("-- Agreement removed successfully --");
      } else {
         view.showError("-- Failed to remove agreement --");
         removeRes.getErrors().forEach(view::showError);
      }

   }
}
