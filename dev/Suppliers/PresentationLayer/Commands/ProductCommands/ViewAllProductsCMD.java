package Suppliers.PresentationLayer.Commands.ProductCommands;

import java.util.List;

import Suppliers.DTOs.CatalogProductDTO;
import Suppliers.PresentationLayer.CommandInterface;
import Suppliers.PresentationLayer.View;
import Suppliers.ServiceLayer.SupplierService;
import Suppliers.ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;

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
