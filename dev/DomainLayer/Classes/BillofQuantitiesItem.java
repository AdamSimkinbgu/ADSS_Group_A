package DomainLayer.Classes;

import java.io.Serializable;
import java.math.BigDecimal;

public class BillofQuantitiesItem implements Serializable {
   private int lineInBillID;
   private String productName;
   private int quantity;
   private BigDecimal discountPercent;
   private int productID;

   public BillofQuantitiesItem(
         int lineInBillId,
         String productName,
         int productID,
         int quantity,
         BigDecimal discountPrecent) {
      this.lineInBillID = lineInBillId;
      this.productName = productName;
      this.productID = productID;
      this.quantity = quantity;
      this.discountPercent = discountPrecent;

   }

   public int getLineInBillID() {
      return lineInBillID;
   }

   public void setLineInBillID(int lineInBillID) {
      this.lineInBillID = lineInBillID;
   }

   public String getProductName() {
      return productName;
   }

   public void setProductName(String productName) {
      this.productName = productName;
   }

   public int getProductID() {
      return productID;
   }

   public void setProductID(int productID) {
      this.productID = productID;
   }

   public int getQuantity() {
      return quantity;
   }

   public void setQuantity(int quantity) {
      this.quantity = quantity;
   }

   public void setDiscountPercent(BigDecimal unitPrice) {
      this.discountPercent = unitPrice;
   }

   public BigDecimal getDiscountPercent() {
      return discountPercent;
   }

   @Override
   public String toString() {
      return "{\n" +
            "   \"lineInBillID\": " + lineInBillID + ",\n" +
            "   \"description\": \"" + productName + "\",\n" +
            "   \"quantity\": " + quantity + ",\n" +
            "   \"discountPercent\": " + discountPercent + "\n" +
            "}";
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (!(o instanceof BillofQuantitiesItem))
         return false;

      BillofQuantitiesItem that = (BillofQuantitiesItem) o;

      // if (lineInBillID != that.lineInBillID)
      // return false;
      if (quantity != that.quantity)
         return false;
      if (productID != that.productID)
         return false;
      return discountPercent.equals(that.discountPercent);
   }

   @Override
   public int hashCode() {
      int result = 0;
      result = 31 * result + productID;
      result = 31 * result + quantity;
      result = 31 * result + discountPercent.hashCode();
      return result;
   }
}