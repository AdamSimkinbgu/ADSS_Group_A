package PresentationLayer.Commands.AgreementCommands;

import DTOs.AgreementDTO;
import PresentationLayer.CommandInterface;
import PresentationLayer.View;
import PresentationLayer.Forms.AgreementForm;
import ServiceLayer.AgreementService;
import ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;
import ServiceLayer.Interfaces_and_Abstracts.Validators.AgreementValidator;

public class UpdateAgreementCMD implements CommandInterface {

   private final View view;
   private final AgreementService agreementService;
   private final AgreementValidator agreementValidator;
   private final AgreementForm agreementForm;

   public UpdateAgreementCMD(View view, AgreementService agreementService) {
      this.view = view;
      this.agreementService = agreementService;
      this.agreementValidator = new AgreementValidator();
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
         try {
            ServiceResponse<?> validationResponse = agreementValidator.validateUpdateDTO(updatedAgreement);
            if (validationResponse.isSuccess()) {
               ServiceResponse<?> res = agreementService.updateAgreement(agreementId, updatedAgreement);
               if (res.isSuccess()) {
                  view.showMessage("Agreement updated successfully");
               } else {
                  view.showError("Failed to update agreement: " + String.join(", ", res.getErrors()));
               }
            } else {
               view.showError("Validation failed: " + String.join(", ", validationResponse.getErrors()));
            }
         } catch (Exception e) {
            view.showError("Error updating agreement: " + e.getMessage());
         }
      });

   }

}
