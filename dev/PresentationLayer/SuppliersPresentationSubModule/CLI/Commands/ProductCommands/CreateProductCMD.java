package PresentationLayer.SuppliersPresentationSubModule.CLI.Commands.ProductCommands;

import PresentationLayer.SuppliersPresentationSubModule.CLI.CommandInterface;
import PresentationLayer.SuppliersPresentationSubModule.CLI.View;
import PresentationLayer.SuppliersPresentationSubModule.CLI.Forms.ProductForm;
import ServiceLayer.SuppliersServiceSubModule.SupplierService;
import ServiceLayer.SuppliersServiceSubModule.Interfaces_and_Abstracts.ServiceResponse;

public class CreateProductCMD implements CommandInterface {

   private final SupplierService service;
   private final ProductForm form;
   private final View view;

   public CreateProductCMD(View view, SupplierService supplierService) {
      this.view = view;
      this.service = supplierService;
      this.form = new ProductForm(view);

   }

   @Override
   public void execute() {
      int supplierID;
      try {
         supplierID = Integer.parseInt(view.readLine("Enter supplier ID: "));
      } catch (NumberFormatException e) {
         view.showError("Invalid supplier ID format. Please enter a valid integer.");
         return;
      }
      form.fillBuild().ifPresent(dto -> {
         // ask for supplier id and check if it exists
         dto.setSupplierId(supplierID);
         ServiceResponse<?> res = service.addProductToSupplier(dto, supplierID);
         if (res.isSuccess()) {
            view.showMessage("-- Product created successfully --\n" + dto);
         } else {
            view.showError("-- Failed to create product --");
            res.getErrors().forEach(error -> view.showError(error));
         }
      });
   }

   public void execute(int supplierID) {
      ServiceResponse<?> checkResponse = service.checkSupplierExists(supplierID);
      if (!checkResponse.isSuccess()) {
         view.showError("Supplier with ID " + supplierID + " does not exist.");
         return;
      }
      form.fillBuild().ifPresent(dto -> {
         // ask for supplier id and check if it exists
         ServiceResponse<?> res = service.addProductToSupplier(dto, supplierID);
         if (res.isSuccess()) {
            view.showMessage("-- Product created successfully --\n" + dto);
         } else {
            view.showError("-- Failed to create product --");
            res.getErrors().forEach(error -> view.showError(error));
         }
      });
   }

}
