package DomainLayer.Classes;

import java.util.UUID;

public class BillofQuantitiesItem {
   private int minQuantity;
   private Double discountRate;
   private UUID id;

   public BillofQuantitiesItem(int minQuantity, Double discountRate, UUID id) {
      this.minQuantity = minQuantity;
      this.discountRate = discountRate;
      this.id = id;
   }

   public int getMinQuantity() {
      return minQuantity;
   }

   public Double getDiscountRate() {
      return discountRate;
   }

   public UUID getId() {
      return id;
   }
}