package DomainLayer.Classes;

import DTOs.SupplierProductDTO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

public class SupplierProduct implements Serializable {
   private static int nextProductID = 1;
   // private int supplierId;
   private int productId;
   private String name;
   private String supplierCatalogNumber;
   private BigDecimal price;
   private String manufacturerName;

   public SupplierProduct(SupplierProductDTO supplierProduct) {
      // this.supplierId = supplierProduct.supplierId();
      this.productId = nextProductID++;
      this.name = supplierProduct.name();
      this.supplierCatalogNumber = supplierProduct.supplierCatalogNumber();
      this.price = supplierProduct.price();
      this.manufacturerName = supplierProduct.manufacturerName();
   }

   public SupplierProduct(
         int productId,
         String supplierCatalogNumber,
         String name,
         BigDecimal price,
         String manufacturerName) {
      this.supplierCatalogNumber = Objects.requireNonNull(supplierCatalogNumber, "supplierCatalogNumber");
      this.productId = nextProductID++;
      this.name = name;
      this.price = Objects.requireNonNull(price, "price");
      this.manufacturerName = manufacturerName;
   }

   // ────────────────────────────────────────────────────────
   // Getters & Setters
   // ────────────────────────────────────────────────────────

   public int getProductId() {
      return productId;
   }

   public void setProductId(int productId) {
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
      return productId == that.productId &&
            supplierCatalogNumber.equals(that.supplierCatalogNumber);
   }

   @Override
   public int hashCode() {
      return Objects.hash(productId, supplierCatalogNumber);
   }

   @Override
   public String toString() {
      // pretty json format
      return "{\n" +
            "   \"productId\": " + productId + ",\n" +
            "   \"supplierCatalogNumber\": \"" + supplierCatalogNumber + "\",\n" +
            "   \"name\": \"" + name + "\",\n" +
            "   \"price\": " + price + ",\n" +
            "   \"manufacturerName\": \"" + manufacturerName + "\"\n" +
            "}";
   }
}
