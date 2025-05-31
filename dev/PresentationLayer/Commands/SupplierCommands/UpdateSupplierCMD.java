package PresentationLayer.Commands.SupplierCommands;

import java.util.concurrent.atomic.AtomicInteger;

import DTOs.SupplierDTO;
import PresentationLayer.CommandInterface;
import PresentationLayer.View;
import PresentationLayer.Forms.SupplierForm;
import ServiceLayer.SupplierService;
import ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;

public class UpdateSupplierCMD implements CommandInterface {
   private final SupplierService service;
   private final SupplierForm form;
   private final View view;

   public UpdateSupplierCMD(View view, SupplierService supplierService) {
      this.view = view;
      this.service = supplierService;
      this.form = new SupplierForm(view);
   }

   @Override
   public void execute() {
      String stringID = view.readLine("Enter the ID of the supplier to update:");
      int intID;
      if (stringID == null || stringID.isBlank()) {
         view.showError("ID must not be blank");
         return;
      }
      try {
         intID = Integer.parseInt(stringID);
      } catch (NumberFormatException e) {
         view.showError("ID must be a number");
         return;
      }

      SupplierDTO supplierDTO;
      try {
         ServiceResponse<SupplierDTO> res = service.getSupplierByID(intID);
         if (res.isSuccess()) {
            supplierDTO = res.getValue();
         } else {
            view.showError("Supplier with ID " + intID + " not found");
            return;
         }
         supplierDTO = res.getValue();
      } catch (Exception e) {
         view.showError("Failed to retrieve supplier: " + e.getMessage());
         return;
      }

      form.fillUpdate(supplierDTO).ifPresent(dto -> {
         ServiceResponse<?> res = service.updateSupplier(dto, intID);
         if (res.isSuccess()) {
            view.showMessage("-- Supplier updated successfully --\n" + dto);
         } else {
            view.showError("-- Failed to update supplier --");
            AtomicInteger counter = new AtomicInteger(1);
            res.getErrors().forEach(error -> view.showError(counter.getAndIncrement() + ". " + error));
         }
      });
   }

}
