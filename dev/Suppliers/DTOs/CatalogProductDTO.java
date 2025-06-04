package Suppliers.DTOs;

import Suppliers.DomainLayer.Classes.SupplierProduct;

public record CatalogProductDTO(
            int productId,
            String name,
            String manufacturerName) {

      public CatalogProductDTO(SupplierProduct supplierProduct) {
            this(supplierProduct.getProductId(),
                        supplierProduct.getName(),
                        supplierProduct.getManufacturerName());
      }

      public CatalogProductDTO(SupplierProductDTO supplierProductDTO) {
            this(supplierProductDTO.getProductId(),
                        supplierProductDTO.getName(),
                        supplierProductDTO.getManufacturerName());
      }

      public CatalogProductDTO getCatalogProductDTO(SupplierProductDTO supplierProductDTO) {
            return new CatalogProductDTO(
                        supplierProductDTO.getProductId(),
                        supplierProductDTO.getName(),
                        supplierProductDTO.getManufacturerName());
      }

      public int getProductId() {
            return productId;
      }

      public String getProductName() {
            return name;
      }

      public String getManufacturerName() {
            return manufacturerName;
      }

      @Override
      public String toString() {
            return String.format(
                        "{\n" +
                                    "  \"productId\": %d,\n" +
                                    "  \"name\": \"%s\",\n" +
                                    "  \"manufacturerName\": \"%s\"\n" +
                                    "}",
                        productId,
                        name,
                        manufacturerName);
      }
}
