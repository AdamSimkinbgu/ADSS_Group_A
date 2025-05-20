package DTOs;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import DomainLayer.Classes.SupplierProduct;

public record SupplierProductDTO(
            int productId,
            String supplierCatalogNumber,
            String name,
            BigDecimal price,
            String manufacturerName) {

      public static List<SupplierProductDTO> fromSupplierProductList(List<SupplierProduct> products) {
            return products.stream()
                        .map(product -> new SupplierProductDTO(
                                    product.getProductId(),
                                    product.getSupplierCatalogNumber(),
                                    product.getName(),
                                    product.getPrice(),
                                    product.getManufacturerName()))
                        .toList();
      }
}
