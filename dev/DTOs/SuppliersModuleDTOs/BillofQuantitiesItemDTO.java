package DTOs.SuppliersModuleDTOs;

import java.math.BigDecimal;

import DomainLayer.SuppliersDomainSubModule.Classes.BillofQuantitiesItem;

public class BillofQuantitiesItemDTO {
   private int agreementId;
   private int lineInBillID;
   private String productName;
   private int quantity;
   private BigDecimal discountPercent;
   private int productId;

   public BillofQuantitiesItemDTO() {
   }

   public BillofQuantitiesItemDTO(BillofQuantitiesItem item, int agreementId) {
      this.agreementId = agreementId;
      this.lineInBillID = item.getLineInBillID();
      this.productName = item.getProductName();
      this.productId = item.getProductID();
      this.quantity = item.getQuantity();
      this.discountPercent = item.getDiscountPercent();
   }

   public BillofQuantitiesItemDTO(int agreementId, int lineInBillId, String itemName, int itemId, int quantity,
         BigDecimal discountPercent) {
      this.agreementId = agreementId;
      this.lineInBillID = lineInBillId;
      this.productName = itemName;
      this.productId = itemId;
      this.quantity = quantity;
      this.discountPercent = discountPercent;
   }

   public BillofQuantitiesItemDTO(BillofQuantitiesItem item) {
      this.agreementId = item.getAgreementId();
      this.lineInBillID = item.getLineInBillID();
      this.productName = item.getProductName();
      this.productId = item.getProductID();
      this.quantity = item.getQuantity();
      this.discountPercent = item.getDiscountPercent();
   }

   public int getAgreementId() {
      return agreementId;
   }

   public void setAgreementId(int agreementId) {
      this.agreementId = agreementId;
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

   public void setProductName(String itemName) {
      this.productName = itemName;
   }

   public int getProductId() {
      return productId;
   }

   public void setProductId(int itemId) {
      this.productId = itemId;
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
      // Truncate productName if it’s very long
      final int MAX_PRODNAME = 18;
      String pn = (productName != null)
            ? (productName.length() <= MAX_PRODNAME
                  ? productName
                  : productName.substring(0, MAX_PRODNAME - 3) + "...")
            : "[no product]";

      @SuppressWarnings("deprecation")
      String disc = (discountPercent != null)
            ? discountPercent.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "%"
            : "0%";

      return String.format(
            "[Line %2d] %-18s | ProdID: %4d | Qty: %3d | Disc: %5s",
            lineInBillID,
            pn,
            productId,
            quantity,
            disc);
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
      if (productId != that.productId)
         return false;
      if (!productName.equals(that.productName))
         return false;
      if (!(quantity == that.quantity))
         return false;
      return discountPercent.equals(that.discountPercent);
   }

   @Override
   public int hashCode() {
      int result = 0;
      result = 31 * result + productId;
      result = 31 * result + quantity;
      result = 31 * result + discountPercent.hashCode();
      return result;
   }
}
