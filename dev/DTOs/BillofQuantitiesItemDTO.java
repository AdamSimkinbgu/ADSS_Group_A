package DTOs;

import java.math.BigDecimal;

public class BillofQuantitiesItemDTO {
   private int lineInBillID;
   private String itemName;
   private int quantity;
   private BigDecimal discountPercent;
   private int itemId;

   public BillofQuantitiesItemDTO(int lineInBillId, String itemName, int itemId, int quantity,
         BigDecimal discountPercent) {
      this.lineInBillID = lineInBillId;
      this.itemName = itemName;
      this.itemId = itemId;
      this.quantity = quantity;
      this.discountPercent = discountPercent;
   }

   public int getLineInBillID() {
      return lineInBillID;
   }

   public void setLineInBillID(int lineInBillID) {
      this.lineInBillID = lineInBillID;
   }

   public String getItemName() {
      return itemName;
   }

   public void setItemName(String itemName) {
      this.itemName = itemName;
   }

   public int getItemId() {
      return itemId;
   }

   public void setItemId(int itemId) {
      this.itemId = itemId;
   }

   public int getQuantity() {
      return quantity;
   }

   public void setQuantity(int quantity) {
      this.quantity = quantity;
   }

   public BigDecimal getDiscountPercent() {
      return discountPercent;
   }

   public void setDiscountPercent(BigDecimal discountPercent) {
      this.discountPercent = discountPercent;
   }

   @Override
   public String toString() {
      return "{\n" +
            "   \"lineInBillID\": " + lineInBillID + ",\n" +
            "   \"itemName\": \"" + itemName + "\",\n" +
            "   \"itemId\": " + itemId + ",\n" +
            "   \"quantity\": " + quantity + ",\n" +
            "   \"discountPercent\": " + discountPercent + "\n" +
            "}";
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (!(o instanceof BillofQuantitiesItemDTO))
         return false;

      BillofQuantitiesItemDTO that = (BillofQuantitiesItemDTO) o;

      if (lineInBillID != that.lineInBillID)
         return false;
      if (itemId != that.itemId)
         return false;
      if (!itemName.equals(that.itemName))
         return false;
      if (!(quantity == that.quantity))
         return false;
      return discountPercent.equals(that.discountPercent);
   }
}
