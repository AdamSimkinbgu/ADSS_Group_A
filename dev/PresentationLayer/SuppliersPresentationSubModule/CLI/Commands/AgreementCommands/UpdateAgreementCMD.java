package PresentationLayer.SuppliersPresentationSubModule.CLI.Commands.AgreementCommands;

import DTOs.SuppliersModuleDTOs.AgreementDTO;
import ServiceLayer.SuppliersServiceSubModule.AgreementService;
import ServiceLayer.SuppliersServiceSubModule.Interfaces_and_Abstracts.ServiceResponse;
import PresentationLayer.SuppliersPresentationSubModule.CLI.CommandInterface;
import PresentationLayer.SuppliersPresentationSubModule.CLI.View;
import PresentationLayer.SuppliersPresentationSubModule.CLI.Forms.AgreementForm;

public class UpdateAgreementCMD implements CommandInterface {

   private final View view;
   private final AgreementService agreementService;
   private final AgreementForm agreementForm;

   public UpdateAgreementCMD(View view, AgreementService agreementService) {
      this.view = view;
      this.agreementService = agreementService;
      this.agreementForm = new AgreementForm(view);
   }

   @Override
   public void execute() {
      String agreementIdString = view.readLine("Enter the agreement ID to update:");
      if (agreementIdString == null || agreementIdString.isBlank()) {
         view.showError("Agreement ID must not be blank");
         return;
      }
      int agreementId;
      try {
         agreementId = Integer.parseInt(agreementIdString);
      } catch (NumberFormatException e) {
         view.showError("Agreement ID must be a number");
         return;
      }
      ServiceResponse<AgreementDTO> agreementResponse = agreementService.getAgreement(agreementId);
      if (!agreementResponse.isSuccess()) {
         view.showError("Failed to retrieve agreement: " + String.join(", ", agreementResponse.getErrors()));
         return;
      }
      AgreementDTO existingAgreement = agreementResponse.getValue();
      if (existingAgreement == null) {
         view.showError("Presentation error: Agreement not found");
         return;
      }
      agreementForm.fillUpdate(existingAgreement).ifPresent(updatedAgreement -> {
         ServiceResponse<?> updateResponse = agreementService.updateAgreement(agreementId, updatedAgreement);
         if (updateResponse.isSuccess()) {
            view.showMessage("-- Agreement updated successfully --\n" + updatedAgreement);
         } else {
            view.showError("-- Failed to update agreement --");
            updateResponse.getErrors().forEach(view::showError);
         }
      });
   }
}
