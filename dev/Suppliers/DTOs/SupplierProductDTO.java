package Suppliers.DTOs;

import java.math.BigDecimal;
import java.util.List;

import Suppliers.DomainLayer.Classes.SupplierProduct;

public class SupplierProductDTO {
      private int supplierId;
      private int productId;
      private String supplierCatalogNumber;
      private String name;
      private BigDecimal price;
      private BigDecimal weight;
      private int expiresInDays;
      private String manufacturerName;

      public SupplierProductDTO() {
      }

      public SupplierProductDTO(int supplierId, int productId, String supplierCatalogNumber, String name,
                  BigDecimal price,
                  BigDecimal weight, int expiresInDays, String manufacturerName) {
            this.supplierId = supplierId;
            this.productId = productId;
            this.supplierCatalogNumber = supplierCatalogNumber;
            this.name = name;
            this.price = price;
            this.weight = weight;
            this.expiresInDays = expiresInDays;
            this.manufacturerName = manufacturerName;
      }

      public SupplierProductDTO(SupplierProduct product) {
            this.supplierId = product.getSupplierId();
            this.productId = product.getProductId();
            this.supplierCatalogNumber = product.getSupplierCatalogNumber();
            this.name = product.getName();
            this.price = product.getPrice();
            this.weight = product.getWeight();
            this.expiresInDays = product.getExpiresInDays();
            this.manufacturerName = product.getManufacturerName();
      }

      public int getSupplierId() {
            return supplierId;
      }

      public void setSupplierId(int supplierId) {
            this.supplierId = supplierId;
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
                                    product.getSupplierId(),
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
                                    product.getSupplierId(),
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
            final int MAX_NAME = 12;
            final int MAX_MF = 12;

            String nm = (name != null && name.length() > MAX_NAME)
                        ? name.substring(0, MAX_NAME - 3) + "..."
                        : (name != null ? name : "[no name]");

            String mf = (manufacturerName != null && manufacturerName.length() > MAX_MF)
                        ? manufacturerName.substring(0, MAX_MF - 3) + "..."
                        : (manufacturerName != null ? manufacturerName : "[no mf]");

            String scn = (supplierCatalogNumber != null)
                        ? supplierCatalogNumber
                        : "[no cat#]";

            @SuppressWarnings("deprecation")
            String pr = (price != null)
                        ? price.setScale(2, BigDecimal.ROUND_HALF_UP).toString()
                        : "0.00";

            @SuppressWarnings("deprecation")
            String wt = (weight != null)
                        ? weight.setScale(2, BigDecimal.ROUND_HALF_UP).toString()
                        : "0.00";

            return String.format(
                        "SupplierProd [%4d]  ProdID: %4d  Cat#: %-8s  Name: %-12s%n" +
                                    "                MF: %-12s  Price: %8s  Wt: %6s  ExpiresIn: %3ddays",
                        supplierId,
                        productId,
                        scn,
                        nm,
                        mf,
                        pr,
                        wt,
                        expiresInDays);
      }
}
