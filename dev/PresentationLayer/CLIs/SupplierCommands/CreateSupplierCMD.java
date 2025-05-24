package PresentationLayer.CLIs.SupplierCommands;

import java.util.concurrent.atomic.AtomicInteger;

import PresentationLayer.View;
import PresentationLayer.CLIs.CommandInterface;
import PresentationLayer.CLIs.Forms.SupplierForm;
import ServiceLayer.SupplierService;
import ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;

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
            // potentially we can add the ability to add an agreement here
            // this would hurt the single responsibility principle
            // but it would be a nice feature
         } else {
            view.showError("-- Failed to create supplier --");
            AtomicInteger counter = new AtomicInteger(1);
            res.getErrors().forEach(error -> view.showError(counter.getAndIncrement() + ". " + error));
         }
      });
   }
}
