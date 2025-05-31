package Suppliers.PresentationLayer.Commands.SupplierCommands;

import java.util.concurrent.atomic.AtomicInteger;

import Suppliers.DTOs.SupplierDTO;
import Suppliers.PresentationLayer.CommandInterface;
import Suppliers.PresentationLayer.View;
import Suppliers.PresentationLayer.Forms.SupplierForm;
import Suppliers.ServiceLayer.SupplierService;
import Suppliers.ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;

public final class CreateSupplierCMD implements CommandInterface {
   private final SupplierService service;
   private final SupplierForm form;
   private final View view;

   public CreateSupplierCMD(View view, SupplierService supplierService) {
      this.view = view;
      this.service = supplierService;
      this.form = new SupplierForm(view);
   }

   @Override
   public void execute() {
      form.fillBuild().ifPresent(dto -> {
         ServiceResponse<?> res = service.createSupplier(dto);
         if (res.isSuccess()) {
            view.showMessage("-- Supplier created successfully --\n" + dto);
         } else {
            view.showError("-- Failed to create supplier --");
            AtomicInteger counter = new AtomicInteger(1);
            res.getErrors().forEach(error -> view.showError(counter.getAndIncrement() + ". " + error));
         }
      });
   }

   public void execute(SupplierDTO dto) {
      ServiceResponse<?> res = service.createSupplier(dto);
      if (res.isSuccess()) {
         view.showMessage("-- Supplier created successfully --\n" + dto);
      } else {
         view.showError("-- Failed to create supplier --");
         AtomicInteger counter = new AtomicInteger(1);
         res.getErrors().forEach(error -> view.showError(counter.getAndIncrement() + ". " + error));
      }
   }
}
