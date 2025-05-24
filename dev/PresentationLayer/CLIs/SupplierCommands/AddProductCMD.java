package PresentationLayer.CLIs.SupplierCommands;

import PresentationLayer.View;
import PresentationLayer.CLIs.CommandInterface;
import PresentationLayer.CLIs.Forms.ProductForm;
import ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;
import ServiceLayer.SupplierService;

public class AddProductCMD implements CommandInterface {
   private final SupplierService service;
   private final View view;
   private final ProductForm form;

   public AddProductCMD(View view, SupplierService supplierService) {
      this.view = view;
      this.service = supplierService;
      this.form = new ProductForm(view);
   }

   @Override
   public void execute() {
      view.showMessage("Adding a new product to the supplier...");
      String supplierIdString = view.readLine("Enter the supplier ID: ");
      if (supplierIdString == null || supplierIdString.isEmpty()) {
         view.showError("Supplier ID cannot be empty.");
         return;
      }
      int supplierId;
      try {
         supplierId = Integer.parseInt(supplierIdString);
      } catch (NumberFormatException e) {
         view.showError("Invalid supplier ID format. Please enter a valid integer.");
         return;
      }
      form.fillBuild().ifPresent(dto -> {
         ServiceResponse<?> res = service.addProductToSupplier(dto, supplierId);
         if (res.isSuccess()) {
            view.showMessage("-- Product added successfully --\n" + dto);
         } else {
            view.showError("-- Failed to add product --");
            res.getErrors().forEach(view::showError);
         }
      }); 
   }
   
}
