package Suppliers.DTOs;

import java.time.LocalDate;
import java.util.List;

import Inventory.DTO.SupplyDTO;

public class OrderPackageDTO {
   private int orderId;
   private LocalDate deliveryDate;
   private List<SupplyDTO> supplies; // having of products with their quantities, productId and their experation
                                     // dates

   public OrderPackageDTO(int orderId, LocalDate deliveryDate, List<SupplyDTO> supplies) {
      this.orderId = orderId;
      this.deliveryDate = deliveryDate;
      this.supplies = supplies;
   }

   public int getOrderId() {
      return orderId;
   }

   public void setOrderId(int orderId) {
      this.orderId = orderId;
   }

   public LocalDate getDeliveryDate() {
      return deliveryDate;
   }

   public void setDeliveryDate(LocalDate deliveryDate) {
      this.deliveryDate = deliveryDate;
   }

   public List<SupplyDTO> getSupplies() {
      return supplies;
   }

   public void setSupplies(List<SupplyDTO> supplies) {
      this.supplies = supplies;
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("{\n");
      sb.append("   \"orderId\": \"").append(orderId).append("\",\n");
      sb.append("   \"deliveryDate\": \"").append(deliveryDate).append("\",\n");
      sb.append("   \"supplies\": [\n");
      for (int i = 0; i < supplies.size(); i++) {
         sb.append("      ").append(supplies.get(i).toString());
         if (i < supplies.size() - 1) {
            sb.append(",\n");
         } else {
            sb.append("\n");
         }
      }
      sb.append("   ]\n");
      sb.append("}");
      return sb.toString();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (!(o instanceof OrderPackageDTO))
         return false;
      OrderPackageDTO that = (OrderPackageDTO) o;
      return (orderId == orderId) &&
            deliveryDate.equals(that.deliveryDate) &&
            supplies.equals(that.supplies);
   }

   @Override
   public int hashCode() {
      int result = orderId;
      result = 31 * result + deliveryDate.hashCode();
      result = 31 * result + supplies.hashCode();
      return result;
   }

}
