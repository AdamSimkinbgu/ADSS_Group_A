package PresentationLayer.CLIs.AgreementCommands;

import PresentationLayer.View;
import PresentationLayer.CLIs.CommandInterface;
import ServiceLayer.AgreementService;
import ServiceLayer.SupplierService;
import ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;

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

      ServiceResponse<?> res = agreementService.getAgreementsBySupplierId(agreementID);
      if (res.isSuccess()) {
         ServiceResponse<?> removeRes = agreementService.removeAgreement(agreementID);
         if (removeRes.isSuccess()) {
            ServiceResponse<?> updateRes = supplierService.updateSupplier(
                  supplierService.getSupplierByID(agreementID).getValue().removeAgreement(agreementID), agreementID);
            if (updateRes.isSuccess()) {
               view.showMessage("-- Agreement removed successfully --");
            } else {
               view.showError("-- Failed to update supplier after removing agreement --");
               updateRes.getErrors().forEach(view::showError);
            }
         } else {
            view.showError("-- Failed to remove agreement --");
            removeRes.getErrors().forEach(view::showError);
         }
      } else {
         view.showError("-- Agreement not found --");
         res.getErrors().forEach(view::showError);
      }
   }
}
