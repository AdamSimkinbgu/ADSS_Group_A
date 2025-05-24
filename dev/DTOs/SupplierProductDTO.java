package DTOs;

import java.math.BigDecimal;
import java.util.List;

import DomainLayer.Classes.SupplierProduct;

public class SupplierProductDTO {

      private int productId;
      private String supplierCatalogNumber;
      private String name;
      private BigDecimal price;
      private BigDecimal weight;
      private int expiresInDays;
      private String manufacturerName;

      public SupplierProductDTO(int productId, String supplierCatalogNumber, String name, BigDecimal price,
                  BigDecimal weight, int expiresInDays, String manufacturerName) {
            this.productId = productId;
            this.supplierCatalogNumber = supplierCatalogNumber;
            this.name = name;
            this.price = price;
            this.weight = weight;
            this.expiresInDays = expiresInDays;
            this.manufacturerName = manufacturerName;
      }

      public SupplierProductDTO(SupplierProduct product) {
            this.productId = product.getProductId();
            this.supplierCatalogNumber = product.getSupplierCatalogNumber();
            this.name = product.getName();
            this.price = product.getPrice();
            this.weight = product.getWeight();
            this.expiresInDays = product.getExpiresInDays();
            this.manufacturerName = product.getManufacturerName();
      }

      public int getProductId() {
            return productId;
      }

      public String getSupplierCatalogNumber() {
            return supplierCatalogNumber;
      }
      
      public String getName() {
            return name;
      }

      public BigDecimal getPrice() {
            return price;
      }

      public BigDecimal getWeight() {
            return weight;
      }

      public int getExpiresInDays() {
            return expiresInDays;
      }

      public String getManufacturerName() {
            return manufacturerName;
      }

      public void setProductId(int productId) {
            this.productId = productId;
      }

      public void setSupplierCatalogNumber(String supplierCatalogNumber) {
            this.supplierCatalogNumber = supplierCatalogNumber;
      }

      public void setName(String name) {
            this.name = name;
      }

      public void setPrice(BigDecimal price) {
            this.price = price;
      }

      public void setWeight(BigDecimal weight) {
            this.weight = weight;
      }

      public void setExpiresInDays(int expiresInDays) {
            this.expiresInDays = expiresInDays;
      }

      public void setManufacturerName(String manufacturerName) {
            this.manufacturerName = manufacturerName;
      }

      

      public static List<SupplierProductDTO> fromSupplierProductList(List<SupplierProduct> products) {
            return products.stream()
                        .map(product -> new SupplierProductDTO(
                                    product.getProductId(),
                                    product.getSupplierCatalogNumber(),
                                    product.getName(),
                                    product.getPrice(),
                                    product.getWeight(),
                                    product.getExpiresInDays(),
                                    product.getManufacturerName()))
                        .toList();
      }

      public static List<SupplierProduct> toSupplierProductList(List<SupplierProductDTO> products) {
            return products.stream()
                        .map(product -> new SupplierProduct(
                                    product.getProductId(),
                                    product.getSupplierCatalogNumber(),
                                    product.getName(),
                                    product.getPrice(),
                                    product.getWeight(),
                                    product.getExpiresInDays(),
                                    product.getManufacturerName()))
                        .toList();

      }

      @Override
      public String toString() {
            return String.format(
                        "{\n" +
                                    "  \"productId\": %d,\n" +
                                    "  \"supplierCatalogNumber\": \"%s\",\n" +
                                    "  \"name\": \"%s\",\n" +
                                    "  \"price\": %.2f,\n" +
                                    "  \"weight\": %.2f,\n" +
                                    "  \"expiresInDays\": %d,\n" +
                                    "  \"manufacturerName\": \"%s\"\n" +
                        "}",
                        productId,
                        supplierCatalogNumber,
                        name,
                        price,
                        weight,
                        expiresInDays,
                        manufacturerName);
      }
}
