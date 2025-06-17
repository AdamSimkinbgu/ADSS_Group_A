package Suppliers.DTOs;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

   public OrderPackageDTO(int orderId2, LocalDate deliveryDate2, SupplyDTO supplyDTO) {
      // TODO Auto-generated constructor stub
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
      DateTimeFormatter df = DateTimeFormatter.ISO_LOCAL_DATE;
      String deliv = (deliveryDate != null)
            ? deliveryDate.format(df)
            : "N/A";

      StringBuilder sb = new StringBuilder();
      sb.append(String.format(
            "OrderPackage [ID=%4d]  Delivery: %s%n",
            orderId, deliv));
      sb.append("  ───────────────────────────────────────\n");

      if (supplies == null || supplies.isEmpty()) {
         sb.append("  [No supplies in this package]\n");
      } else {
         sb.append("  Supplies:\n");
         for (SupplyDTO s : supplies) {
            String[] lines = s.toString().split("\\r?\\n");
            for (String line : lines) {
               sb.append("    ").append(line).append("\n");
            }
         }
         sb.append(String.format(
               "  (%d supply record%s)%n",
               supplies.size(),
               supplies.size() == 1 ? "" : "s"));
      }

      sb.append("================================================\n");
      return sb.toString();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (!(o instanceof OrderPackageDTO))
         return false;
      OrderPackageDTO that = (OrderPackageDTO) o;
      return (orderId == that.orderId) &&
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
