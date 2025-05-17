package DomainLayer.Classes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class SupplierProduct implements Serializable {
   private UUID supplierId;
   private UUID productId;
   private String name;
   private String supplierCatalogNumber;
   private BigDecimal price;
   private String manufacturerName;

   public SupplierProduct() {
   }

   public SupplierProduct(
         UUID supplierId,
         UUID productId,
         String supplierCatalogNumber,
         String name,
         BigDecimal price,
         String manufacturerName) {
      this.supplierId = Objects.requireNonNull(supplierId, "supplierId");
      this.supplierCatalogNumber = Objects.requireNonNull(supplierCatalogNumber, "supplierCatalogNumber");
      this.productId = UUID.nameUUIDFromBytes(
            (supplierId.toString() + ":" + supplierCatalogNumber).getBytes());
      this.name = name;
      this.price = Objects.requireNonNull(price, "price");
      this.manufacturerName = manufacturerName;
   }

   // ────────────────────────────────────────────────────────
   // Getters & Setters
   // ────────────────────────────────────────────────────────

   public UUID getSupplierId() {
      return supplierId;
   }

   public void setSupplierId(UUID supplierId) {
      this.supplierId = supplierId;
   }

   public UUID getProductId() {
      return productId;
   }

   public void setProductId(UUID productId) {
      this.productId = productId;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getSupplierCatalogNumber() {
      return supplierCatalogNumber;
   }

   public void setSupplierCatalogNumber(String supplierCatalogNumber) {
      this.supplierCatalogNumber = supplierCatalogNumber;
   }

   public BigDecimal getPrice() {
      return price;
   }

   public void setPrice(BigDecimal price) {
      this.price = price;
   }

   public String getManufacturerName() {
      return manufacturerName;
   }

   public void setManufacturerName(String manufacturerName) {
      this.manufacturerName = manufacturerName;
   }

   // ────────────────────────────────────────────────────────
   // equals, hashCode, toString
   // ────────────────────────────────────────────────────────

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (!(o instanceof SupplierProduct))
         return false;
      SupplierProduct that = (SupplierProduct) o;
      return supplierId.equals(that.supplierId) &&
            productId.equals(that.productId) &&
            supplierCatalogNumber.equals(that.supplierCatalogNumber);
   }

   @Override
   public int hashCode() {
      return Objects.hash(supplierId, productId, supplierCatalogNumber);
   }

   @Override
   public String toString() {
      return "SupplierProduct[" +
            "supplierId=" + supplierId +
            ", productId=" + productId +
            ", catalogNumber='" + supplierCatalogNumber + '\'' +
            ", price=" + price +
            ']';
   }
}
