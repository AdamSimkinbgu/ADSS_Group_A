package DomainLayer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents how a given Supplier offers a given Product.
 * • supplierId – which Supplier
 * • productId – which Product
 * • supplierCatalogNumber – the supplier’s own SKU/code
 * • price – base unit price
 */
public class SupplierProduct implements Serializable {
   private static final long serialVersionUID = 1L;

   private UUID supplierId;
   private UUID productId;
   private String supplierCatalogNumber;
   private BigDecimal price;

   /** Jackson constructor */
   @JsonCreator
   public SupplierProduct(
         @JsonProperty("supplierId") UUID supplierId,
         @JsonProperty("productId") UUID productId,
         @JsonProperty("supplierCatalogNumber") String supplierCatalogNumber,
         @JsonProperty("price") BigDecimal price) {
      this.supplierId = Objects.requireNonNull(supplierId, "supplierId");
      this.productId = Objects.requireNonNull(productId, "productId");
      this.supplierCatalogNumber = Objects.requireNonNull(supplierCatalogNumber, "supplierCatalogNumber");
      this.price = Objects.requireNonNull(price, "price");
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