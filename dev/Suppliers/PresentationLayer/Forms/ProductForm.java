package Suppliers.PresentationLayer.Forms;

import java.math.BigDecimal;

import Suppliers.DTOs.SupplierProductDTO;
import Suppliers.PresentationLayer.InteractiveForm;
import Suppliers.PresentationLayer.View;

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
            view.showMessage("Current details: \n" + dto);
            switch (askNonEmpty(
                        "What do you want to change? (supplierCatalogNumber, price, weight, expiresInDays)")
                        .toLowerCase()) {
                  case "suppliercatalognumber" -> dto = new SupplierProductDTO(
                              dto.getSupplierId(),
                              dto.getProductId(),
                              askNonEmpty("New Supplier Catalog Number: "),
                              dto.getName(),
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
                  case "expiresindays" -> dto = new SupplierProductDTO(
                              dto.getSupplierId(),
                              dto.getProductId(),
                              dto.getSupplierCatalogNumber(),
                              dto.getName(),
                              dto.getPrice(),
                              dto.getWeight(),
                              askInt("New Expires in (days): "),
                              dto.getManufacturerName());
            }
            return dto;
      }
}