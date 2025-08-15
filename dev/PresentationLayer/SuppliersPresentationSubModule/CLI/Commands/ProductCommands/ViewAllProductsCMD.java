package PresentationLayer.SuppliersPresentationSubModule.CLI.Commands.ProductCommands;

import java.util.List;

import DTOs.SuppliersModuleDTOs.CatalogProductDTO;
import PresentationLayer.SuppliersPresentationSubModule.CLI.CommandInterface;
import PresentationLayer.SuppliersPresentationSubModule.CLI.View;
import ServiceLayer.SuppliersServiceSubModule.SupplierService;
import ServiceLayer.SuppliersServiceSubModule.Interfaces_and_Abstracts.ServiceResponse;

public class ViewAllProductsCMD implements CommandInterface {

   private final View view;
   private final SupplierService service;

   public ViewAllProductsCMD(View view, SupplierService supplierService) {
      this.view = view;
      this.service = supplierService;
   }

   @Override
   public void execute() {

      ServiceResponse<List<CatalogProductDTO>> res = service.getAllProducts();
      if (res.isSuccess()) {
         List<CatalogProductDTO> products = res.getValue();
         if (products.isEmpty()) {
            view.showMessage("-- No products found --");
         } else {
            view.showMessage("-- Products List --");
            for (CatalogProductDTO product : products) {
               view.showMessage(product.toString());
            }
         }
      } else {
         view.showError("-- Failed to retrieve products --");
         res.getErrors().forEach(error -> view.showError(error));
      }
   }

}
