package DTOs.SuppliersModuleDTOs;

public class PeriodicOrderItemLineDTO {
   private int periodicOrderItemLineId;
   private int periodicOrderId;
   private int productId;
   private int quantity;

   public PeriodicOrderItemLineDTO() {
   }

   public PeriodicOrderItemLineDTO(PeriodicOrderItemLineDTO periodicOrderItemLine) {
      this.periodicOrderItemLineId = periodicOrderItemLine.getPeriodicOrderItemLineId();
      this.periodicOrderId = periodicOrderItemLine.getPeriodicOrderId();
      this.productId = periodicOrderItemLine.getProductId();
      this.quantity = periodicOrderItemLine.getQuantity();
   }

   public PeriodicOrderItemLineDTO(int lineId, int periodicOrderId, int productId, int quantity) {
      this.periodicOrderItemLineId = lineId;
      this.periodicOrderId = periodicOrderId;
      this.productId = productId;
      this.quantity = quantity;
   }

   public int getPeriodicOrderItemLineId() {
      return periodicOrderItemLineId;
   }

   public void setPeriodicOrderItemLineId(int id) {
      this.periodicOrderItemLineId = id;
   }

   public int getPeriodicOrderId() {
      return periodicOrderId;
   }

   public void setPeriodicOrderId(int periodicOrderId) {
      this.periodicOrderId = periodicOrderId;
   }

   public int getProductId() {
      return productId;
   }

   public void setProductId(int productId) {
      this.productId = productId;
   }

   public int getQuantity() {
      return quantity;
   }

   public void setQuantity(int quantity) {
      this.quantity = quantity;
   }

   @Override
   public String toString() {
      return String.format(
            "PeriodicItemLine [%4d]  OrderID: %4d  ProdID: %4d  Qty: %3d",
            periodicOrderItemLineId,
            periodicOrderId,
            productId,
            quantity);
   }
}