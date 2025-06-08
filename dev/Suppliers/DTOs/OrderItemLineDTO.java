package Suppliers.DTOs;

import java.math.BigDecimal;

public class OrderItemLineDTO {
    private int orderID;
    private int productId;
    private String supplierProductCatalogNumber;
    private int quantity;
    private BigDecimal unitPrice;
    private String productName;
    private int orderItemLineID;
    private BigDecimal discount;

    public OrderItemLineDTO() {
    }

    public OrderItemLineDTO(int productId, int quantity) {
        this.orderID = -1;
        this.productId = productId;
        this.supplierProductCatalogNumber = "";
        this.quantity = quantity;
        this.unitPrice = BigDecimal.ZERO;
        this.productName = "";
        this.orderItemLineID = -1;
        this.discount = BigDecimal.ZERO;
    }

    public OrderItemLineDTO(OrderItemLineDTO item) {
        this.orderID = item.getOrderID();
        this.productId = item.getProductId();
        this.supplierProductCatalogNumber = item.getSupplierProductCatalogNumber();
        this.quantity = item.getQuantity();
        this.unitPrice = item.getUnitPrice();
        this.productName = item.getProductName();
        this.orderItemLineID = item.getOrderItemLineID();
        this.discount = item.getDiscount();
    }

    public OrderItemLineDTO(int orderID, int productId, String supplierProductCatalogNumber, int quantity,
            BigDecimal unitPrice, String productName, BigDecimal discount) {
        this.orderID = orderID;
        this.productId = productId;
        this.supplierProductCatalogNumber = supplierProductCatalogNumber;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.productName = productName;
        this.orderItemLineID = -1; // Default value, will be set by DAO
        this.discount = discount != null ? discount : BigDecimal.ZERO; // Default to zero if null
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getSupplierProductCatalogNumber() {
        return supplierProductCatalogNumber;
    }

    public void setSupplierProductCatalogNumber(String supplierProductCatalogNumber) {
        this.supplierProductCatalogNumber = supplierProductCatalogNumber;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getOrderItemLineID() {
        return orderItemLineID;
    }

    public void setOrderItemLineID(int orderItemLineID) {
        this.orderItemLineID = orderItemLineID;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getFinalPrice() {
        return unitPrice != null && discount != null
                ? unitPrice.multiply(BigDecimal.valueOf(quantity)).multiply(BigDecimal.ONE.subtract(discount))
                : BigDecimal.valueOf(Double.NaN);
    }

    private String truncate(String s, int maxLen) {
        if (s == null)
            return "";
        if (s.length() <= maxLen)
            return s;
        return s.substring(0, maxLen - 3) + "...";
    }

    @Override
    public String toString() {

        String shortName = truncate(productName, 15);
        String shortCat = truncate(supplierProductCatalogNumber, 15);

        String discStr = (discount == null || discount.compareTo(BigDecimal.ZERO) == 0)
                ? "0%"
                : discount.stripTrailingZeros().scale() <= 0
                        ? discount.intValue() + "%"
                        : discount.stripTrailingZeros().toPlainString() + "%";

        return String.format(
                "[Line %d] %-15s | Cat# %-15s | qty: %3d | price: %8.2f | disc: %4s" +
                        " | final: %8.2f",
                orderItemLineID,
                shortName,
                shortCat,
                quantity,
                unitPrice != null ? unitPrice.doubleValue() : 0.0,
                discStr,
                getFinalPrice() != null ? getFinalPrice().doubleValue() : 0.0);
    }

}
