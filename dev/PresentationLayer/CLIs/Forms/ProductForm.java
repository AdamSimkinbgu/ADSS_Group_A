package PresentationLayer.CLIs.Forms;

import java.math.BigDecimal;

import DTOs.SupplierProductDTO;
import PresentationLayer.View;
import PresentationLayer.CLIs.InteractiveForm;

public class ProductForm extends InteractiveForm<SupplierProductDTO> {
      public ProductForm(View view) {
            super(view);
      }

      @Override
      protected SupplierProductDTO build() throws Cancelled {
            view.showMessage("Enter product details:");
            String supplierCatalogNumber = askNonEmpty("Supplier Catalog Number: ");
            String name = askNonEmpty("Product Name: ");
            BigDecimal price = askBigDecimal("Price: ");
            BigDecimal weight = askBigDecimal("Weight: ");
            int expiresInDays = askInt("Expires in (days): ");
            String manufacturerName = askNonEmpty("Manufacturer Name: ");
            return new SupplierProductDTO(
                        -1, // Assuming supplierId is auto-generated or set later
                        -1, // Assuming productId is auto-generated
                        supplierCatalogNumber,
                        name,
                        price,
                        weight,
                        expiresInDays,
                        manufacturerName);
      }

      @Override
      protected SupplierProductDTO update(SupplierProductDTO dto) throws Cancelled {
            view.showMessage("Updating product... (enter 'cancel' to cancel)");
            switch (askNonEmpty(
                        "What do you want to change? (supplierCatalogNumber, name, price, weight, expiresInDays, manufacturerName)")) {
                  case "supplierCatalogNumber" -> dto = new SupplierProductDTO(
                              dto.getSupplierId(),
                              dto.getProductId(),
                              askNonEmpty("New Supplier Catalog Number: "),
                              dto.getName(),
                              dto.getPrice(),
                              dto.getWeight(),
                              dto.getExpiresInDays(),
                              dto.getManufacturerName());
                  case "name" -> dto = new SupplierProductDTO(
                              dto.getSupplierId(),
                              dto.getProductId(),
                              dto.getSupplierCatalogNumber(),
                              askNonEmpty("New Product Name: "),
                              dto.getPrice(),
                              dto.getWeight(),
                              dto.getExpiresInDays(),
                              dto.getManufacturerName());
                  case "price" -> dto = new SupplierProductDTO(
                              dto.getSupplierId(),
                              dto.getProductId(),
                              dto.getSupplierCatalogNumber(),
                              dto.getName(),
                              askBigDecimal("New Price: "),
                              dto.getWeight(),
                              dto.getExpiresInDays(),
                              dto.getManufacturerName());
                  case "weight" -> dto = new SupplierProductDTO(
                              dto.getSupplierId(),
                              dto.getProductId(),
                              dto.getSupplierCatalogNumber(),
                              dto.getName(),
                              dto.getPrice(),
                              askBigDecimal("New Weight: "),
                              dto.getExpiresInDays(),
                              dto.getManufacturerName());
                  case "expiresInDays" -> dto = new SupplierProductDTO(
                              dto.getSupplierId(),
                              dto.getProductId(),
                              dto.getSupplierCatalogNumber(),
                              dto.getName(),
                              dto.getPrice(),
                              dto.getWeight(),
                              askInt("New Expires in (days): "),
                              dto.getManufacturerName());
                  case "manufacturerName" -> dto = new SupplierProductDTO(
                              dto.getSupplierId(),
                              dto.getProductId(),
                              dto.getSupplierCatalogNumber(),
                              dto.getName(),
                              dto.getPrice(),
                              dto.getWeight(),
                              dto.getExpiresInDays(),
                              askNonEmpty("New Manufacturer Name: "));
            }
            return dto;
      }
}