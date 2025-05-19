package DTOs;

import java.math.BigDecimal;
import java.util.UUID;

public record SupplierProductDTO(
      UUID supplierId,
      UUID productId,
      String supplierCatalogNumber,
      String name,
      BigDecimal price,
      String manufacturerName) {
}
