package DTOs;

import java.math.BigDecimal;
import java.util.List;

import DomainLayer.Classes.SupplierProduct;

public record SupplierProductDTO(
            int productId,
            String supplierCatalogNumber,
            String name,
            BigDecimal price,
            BigDecimal weight,
            String manufacturerName) {

      public static List<SupplierProductDTO> fromSupplierProductList(List<SupplierProduct> products) {
            return products.stream()
                        .map(product -> new SupplierProductDTO(
                                    product.getProductId(),
                                    product.getSupplierCatalogNumber(),
                                    product.getName(),
                                    product.getPrice(),
                                    product.getWeight(),
                                    product.getManufacturerName()))
                        .toList();
      }

      public static List<SupplierProduct> toSupplierProductList(List<SupplierProductDTO> products) {
            return products.stream()
                        .map(product -> new SupplierProduct(
                                    product.productId(),
                                    product.supplierCatalogNumber(),
                                    product.name(),
                                    product.price(),
                                    product.manufacturerName()))
                        .toList();

      }
}
