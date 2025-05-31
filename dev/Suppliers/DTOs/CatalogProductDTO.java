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

      public CatalogProductDTO getCatalogProductDTO(SupplierProductDTO supplierProductDTO) {
            return new CatalogProductDTO(
                        supplierProductDTO.getProductId(),
                        supplierProductDTO.getName(),
                        supplierProductDTO.getManufacturerName());
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
