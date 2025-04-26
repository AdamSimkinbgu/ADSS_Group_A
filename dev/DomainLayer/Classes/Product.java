package DomainLayer.Classes;

import java.util.UUID;

public record Product(
      UUID productId,
      String name,
      String manufacturerName // or Manufacturer ref
) {
}