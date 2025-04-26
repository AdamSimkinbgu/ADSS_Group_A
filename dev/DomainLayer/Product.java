package DomainLayer;

import java.util.UUID;

public record Product(
      UUID productId,
      String name,
      String manufacturerName // or Manufacturer ref
) {
}