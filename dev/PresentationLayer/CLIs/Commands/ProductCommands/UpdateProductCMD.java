package PresentationLayer.CLIs.Commands.ProductCommands;

import java.util.List;

import DTOs.SupplierProductDTO;
import PresentationLayer.View;
import PresentationLayer.CLIs.CommandInterface;
import PresentationLayer.CLIs.Forms.ProductForm;
import ServiceLayer.SupplierService;
import ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;

public class UpdateProductCMD implements CommandInterface {

   private final View view;
   private final SupplierService service;
   private final ProductForm productForm;

   public UpdateProductCMD(View view, SupplierService supplierService) {
      this.view = view;
      this.service = supplierService;
      this.productForm = new ProductForm(view);
   }

   @Override
   public void execute() {
      // Prompt for supplier ID
      int supplierID;
      try {
         supplierID = Integer.parseInt(view.readLine("Enter supplier ID: "));
         if (supplierID < 0) {
            view.showError("Supplier ID must be a positive integer.");
            return;
         }
         // if (!service.checkSupplierExists(supplierID).isSuccess()) {
         // view.showError("Supplier with ID " + supplierID + " does not exist.");
         // return;
         // }
      } catch (NumberFormatException e) {
         view.showError("Invalid supplier ID format. Please enter a valid integer.");
         return;
      }
      // Prompt for product ID
      String productIdString = view.readLine("Enter product ID to update: ");
      int productID;
      try {
         productID = Integer.parseInt(productIdString);
         // if (productID < 0) {
         // view.showError("Product ID must be a positive integer.");
         // return;
         // }
      } catch (NumberFormatException e) {
         view.showError("Invalid product ID format. Please enter a valid integer.");
         return;
      }
      // get the product by ID
      ServiceResponse<List<SupplierProductDTO>> productResponse = service.getSupplierProducts(supplierID);
      if (!productResponse.isSuccess()) {
         view.showError("Failed to retrieve products.");
         productResponse.getErrors().forEach(view::showError);
         return;
      }
      List<SupplierProductDTO> products = productResponse.getValue();
      SupplierProductDTO productToUpdate = products.stream()
            .filter(product -> product.getProductId() == productID)
            .findFirst()
            .orElse(null);
      if (productToUpdate == null) {
         view.showError("Product with ID " + productID + " not found for supplier ID " + supplierID);
         return;
      }
      // Prompt for new product details
      productForm.fillUpdate(productToUpdate).ifPresent(dto -> {
         // Update the product
         ServiceResponse<?> updateResponse = service.updateProduct(dto, supplierID);
         if (updateResponse.isSuccess()) {
            view.showMessage("-- Product updated successfully --\n" + dto);
         } else {
            view.showError("-- Failed to update product --");
            updateResponse.getErrors().forEach(view::showError);
         }
      });

   }
}
