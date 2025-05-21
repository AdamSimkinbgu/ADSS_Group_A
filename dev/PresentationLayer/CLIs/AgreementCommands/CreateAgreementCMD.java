package PresentationLayer.CLIs.AgreementCommands;

import java.util.concurrent.atomic.AtomicInteger;

import DTOs.SupplierDTO;
import PresentationLayer.View;
import PresentationLayer.CLIs.CommandInterface;
import PresentationLayer.CLIs.Forms.AgreementForm;
import ServiceLayer.AgreementService;
import ServiceLayer.SupplierService;
import ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;

public class CreateAgreementCMD implements CommandInterface {
   private final View view;
   private final AgreementService agreementService;
   private final SupplierService supplierService;
   private final AgreementForm form;

   public CreateAgreementCMD(View view, AgreementService agreementService, SupplierService supplierService) {
      this.view = view;
      this.agreementService = agreementService;
      this.supplierService = supplierService;
      this.form = new AgreementForm(view);
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
      SupplierDTO supplierDTO;
      try {
         supplierDTO = supplierService.getSupplierByID(supplierID).getValue();
         if (supplierDTO == null) {
            view.showError("Supplier not found");
            return;
         }
      } catch (Exception e) {
         view.showError("Error fetching supplier: " + e.getMessage());
         return;
      }
      form.fillBuild().ifPresent(agreementDTO -> {
         try {
            agreementDTO.setSupplierId(supplierID); //
            agreementDTO.setSupplierName(supplierDTO.getName());
            agreementDTO.setHasFixedSupplyDays(supplierDTO.getSelfSupply());
            ServiceResponse<?> res = agreementService.createAgreement(agreementDTO);
            if (res.isSuccess()) {
               supplierService.updateSupplier(supplierDTO, supplierID);
               view.showMessage("-- Agreement created successfully --\n" + agreementDTO);
            } else {
               view.showError("-- Failed to create agreement --");
               AtomicInteger counter = new AtomicInteger(1);
               res.getErrors().forEach(error -> view.showError(counter.getAndIncrement() + ". " + error));
            }
         } catch (Exception e) {
            view.showError("Error creating agreement: " + e.getMessage());
         }
      });

   }

}
