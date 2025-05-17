package DomainLayer.Classes;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BillofQuantitiesItem implements Serializable {

   private UUID id;
   private String description;
   private int quantity;
   private BigDecimal unitPrice;

   public BillofQuantitiesItem() {
   }

   public BillofQuantitiesItem(
         UUID id,
         String description,
         int quantity,
         BigDecimal unitPrice) {
      this.id = (id != null)
            ? id
            : UUID.nameUUIDFromBytes((description + ":" + quantity).getBytes());
      setDescription(description);
      setQuantity(quantity);
      setUnitPrice(unitPrice);
   }

   public void setDescription(String description) {
      if (description == null || description.isBlank()) {
         throw new IllegalArgumentException("description must not be blank");
      }
      this.description = description;
   }

   public void setQuantity(int quantity) {
      if (quantity < 0) {
         throw new IllegalArgumentException("quantity must be >= 0");
      }
      this.quantity = quantity;
   }

   public void setUnitPrice(BigDecimal unitPrice) {
      if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
         throw new IllegalArgumentException("unitPrice must be non-negative");
      }
      this.unitPrice = unitPrice;
   }

   public UUID getId() {
      return id;
   }

   public String getDescription() {
      return description;
   }

   public int getQuantity() {
      return quantity;
   }

   public BigDecimal getUnitPrice() {
      return unitPrice;
   }

   @Override
   public String toString() {
      return "BillOfQuantitiesItem{id=" + id +
            ", desc='" + description + '\'' +
            ", qty=" + quantity +
            ", unitPrice=" + unitPrice + '}';
   }
}