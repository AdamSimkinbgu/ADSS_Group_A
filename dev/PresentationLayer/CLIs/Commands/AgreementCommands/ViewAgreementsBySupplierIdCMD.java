package PresentationLayer.CLIs.Commands.AgreementCommands;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import DTOs.AgreementDTO;
import PresentationLayer.View;
import PresentationLayer.CLIs.CommandInterface;
import ServiceLayer.AgreementService;
import ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;

public class ViewAgreementsBySupplierIdCMD implements CommandInterface {
   private final AgreementService service;
   private final View view;

   public ViewAgreementsBySupplierIdCMD(View view, AgreementService agreementService) {
      this.view = view;
      this.service = agreementService;
   }

   @Override
   public void execute() {
      String supplierIdString = view.readLine("Enter the supplier ID:");
      int supplierId;
      if (supplierIdString == null || supplierIdString.isBlank()) {
         view.showError("Supplier ID must not be blank");
         return;
      }
      try {
         supplierId = Integer.parseInt(supplierIdString);
      } catch (NumberFormatException e) {
         view.showError("Supplier ID must be a number");
         return;
      }
      ServiceResponse<List<AgreementDTO>> res = service.getAgreementsBySupplierId(supplierId);
      if (res.isSuccess()) {
         view.showMessage("-- Agreements for supplier ID " + supplierId + " --");
         res.getValue().forEach(agreement -> view.showMessage(agreement.toString()));
      } else {
         view.showError("-- Failed to retrieve agreements --");
         AtomicInteger counter = new AtomicInteger(1);
         res.getErrors().forEach(error -> view.showError(counter.getAndIncrement() + ". " + error));
      }

   }

}
