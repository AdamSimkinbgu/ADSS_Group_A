package DomainLayer.Classes;

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
   private UUID supplierId;
   private UUID productId;
   private String name;
   private String supplierCatalogNumber;
   private BigDecimal price;
   private String manufacturerName;

   public SupplierProduct() {
   }

   /** Jackson constructor */
   @JsonCreator
   public SupplierProduct(
         @JsonProperty("supplierId") UUID supplierId,
         @JsonProperty(value = "productId", required = false) UUID productId,
         @JsonProperty("supplierCatalogNumber") String supplierCatalogNumber,
         @JsonProperty("name") String name,
         @JsonProperty("price") BigDecimal price,
         @JsonProperty("manufacturerName") String manufacturerName) {
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

   @JsonProperty("supplierId")
   public void setSupplierId(UUID supplierId) {
      this.supplierId = supplierId;
   }

   public UUID getProductId() {
      return productId;
   }

   @JsonProperty("productId")
   public void setProductId(UUID productId) {
      this.productId = productId;
   }

   public String getName() {
      return name;
   }

   @JsonProperty("name")
   public void setName(String name) {
      this.name = name;
   }

   public String getSupplierCatalogNumber() {
      return supplierCatalogNumber;
   }

   @JsonProperty("supplierCatalogNumber")
   public void setSupplierCatalogNumber(String supplierCatalogNumber) {
      this.supplierCatalogNumber = supplierCatalogNumber;
   }

   public BigDecimal getPrice() {
      return price;
   }

   @JsonProperty("price")
   public void setPrice(BigDecimal price) {
      this.price = price;
   }

   public String getManufacturerName() {
      return manufacturerName;
   }

   @JsonProperty("manufacturerName")
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
