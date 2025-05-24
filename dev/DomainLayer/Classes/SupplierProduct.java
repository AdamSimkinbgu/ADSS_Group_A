package DomainLayer.Classes;

import DTOs.SupplierProductDTO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

public class SupplierProduct implements Serializable {
   private static int nextProductID = 100;
   // private int supplierId;
   private int internalProductIdForInternalUsageOnly;
   private String name;
   private String supplierCatalogNumber;
   private BigDecimal price;
   private BigDecimal Weight;
   private int expiresInDays;
   private String manufacturerName;

   public SupplierProduct(SupplierProductDTO supplierProduct) {
      // this.supplierId = supplierProduct.supplierId();
      this.internalProductIdForInternalUsageOnly = supplierProduct.getProductId();
      this.name = supplierProduct.getName();
      this.supplierCatalogNumber = supplierProduct.getSupplierCatalogNumber();
      this.price = supplierProduct.getPrice();
      this.Weight = supplierProduct.getWeight();
      this.expiresInDays = supplierProduct.getExpiresInDays();
      this.manufacturerName = supplierProduct.getManufacturerName();
   }

   public SupplierProduct(
         int productId,
         String supplierCatalogNumber,
         String name,
         BigDecimal price,
         BigDecimal weight,
         int expiresInDays,
         String manufacturerName) {
      this.supplierCatalogNumber = Objects.requireNonNull(supplierCatalogNumber, "supplierCatalogNumber");
      this.internalProductIdForInternalUsageOnly = productId;
      this.name = name;
      this.price = Objects.requireNonNull(price, "price");
      this.Weight = Objects.requireNonNull(weight, "weight");
      this.expiresInDays = expiresInDays;
      this.manufacturerName = manufacturerName;
   }

   // ────────────────────────────────────────────────────────
   // Getters & Setters
   // ────────────────────────────────────────────────────────

   public int getProductId() {
      return internalProductIdForInternalUsageOnly;
   }

   public void setProductId(int productId) {
      this.internalProductIdForInternalUsageOnly = productId;
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

   public BigDecimal getWeight() {
      return Weight;
   }

   public void setWeight(BigDecimal weight) {
      Weight = weight;
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
      return internalProductIdForInternalUsageOnly == that.internalProductIdForInternalUsageOnly &&
            supplierCatalogNumber.equals(that.supplierCatalogNumber);
   }

   @Override
   public int hashCode() {
      return Objects.hash(internalProductIdForInternalUsageOnly, supplierCatalogNumber);
   }

   @Override
   public String toString() {
      // pretty json format
      return "{\n" +
            "   \"productId\": " + internalProductIdForInternalUsageOnly + ",\n" +
            "   \"supplierCatalogNumber\": \"" + supplierCatalogNumber + "\",\n" +
            "   \"name\": \"" + name + "\",\n" +
            "   \"price\": " + price + ",\n" +
            "   \"weight\": " + Weight + ",\n" +
            "   \"expiresInDays\": " + expiresInDays + ",\n" +
            "   \"manufacturerName\": \"" + manufacturerName + "\"\n" +
            "}";
   }

   public int getExpiresInDays() {
      return expiresInDays;
   }
}
