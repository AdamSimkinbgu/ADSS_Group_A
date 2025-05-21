package PresentationLayer.CLIs.SupplierCommands;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import DTOs.SupplierDTO;
import PresentationLayer.View;
import PresentationLayer.CLIs.CommandInterface;
import ServiceLayer.SupplierService;
import ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;

public class ViewAllSuppliersCMD implements CommandInterface {
   private SupplierService supplierService;
   private View view;

   public ViewAllSuppliersCMD(View view, SupplierService supplierService) {
      this.view = view;
      this.supplierService = supplierService;
   }

   @Override
   public void execute() throws Exception {
      ServiceResponse<List<SupplierDTO>> res = supplierService.getAllSuppliers();
      if (res.isSuccess()) {
         view.showMessage("-- Suppliers --");
         if (res.getValue().isEmpty()) {
            view.showMessage("No suppliers found.");
            return;
         }
         AtomicInteger counter = new AtomicInteger(1);
         res.getValue().forEach(supplier -> {
            view.showMessage(counter.getAndIncrement() + ". " + supplier);
         });
      }
   }

}
