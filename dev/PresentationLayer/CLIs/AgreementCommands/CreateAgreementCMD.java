package PresentationLayer.CLIs.AgreementCommands;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import DTOs.AgreementDTO;
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
      fakeLoadAgreement();
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
            agreementDTO.setSupplierId(supplierID);
            agreementDTO.setSupplierName(supplierDTO.getName());
            agreementDTO.setHasFixedSupplyDays(supplierDTO.getSelfSupply());
            ServiceResponse<?> res = agreementService.createAgreement(agreementDTO);
            if (res.isSuccess()) {
               supplierService.updateSupplier(supplierDTO.addAgreement(agreementDTO), supplierID);
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

   public void fakeLoadAgreement() {
      /*
       * int supplierId, String supplierName,
       * LocalDate agreementStartDate, LocalDate agreementEndDate, boolean
       * hasFixedSupplyDays,
       * List<BillofQuantitiesItemDTO> billOfQuantitiesItems
       */
      AgreementDTO agreementDTO = new AgreementDTO(
            1,
            "Supplier Name",
            LocalDate.now().plusDays(1),
            LocalDate.now().plusDays(31),
            true,
            new ArrayList<>());
      ServiceResponse<?> res = agreementService.createAgreement(agreementDTO);
      SupplierDTO supplierDTO = supplierService.getSupplierByID(1).getValue();
      if (res.isSuccess()) {
         supplierService.updateSupplier(supplierDTO.addAgreement(agreementDTO), 1);

         view.showMessage("-- Agreement created successfully --\n" + agreementDTO);
      } else {
         view.showError("-- Failed to create agreement --");
         AtomicInteger counter = new AtomicInteger(1);
         res.getErrors().forEach(error -> view.showError(counter.getAndIncrement() + ". " + error));
      }
   }

}
