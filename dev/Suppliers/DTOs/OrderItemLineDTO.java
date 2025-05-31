package Suppliers.DTOs;

import Suppliers.DomainLayer.Classes.OrderItemLine;

import java.math.BigDecimal;

public class OrderItemLineDTO {
    private int orderID;
    private int productId;
    private int supplierProductCatalogNumber;
    private int quantity;
    private BigDecimal unitPrice;
    private String productName;
    private int orderItemLineID;
    private BigDecimal discount;

    public OrderItemLineDTO() {
    }

    public OrderItemLineDTO(int orderID, int supplierProductCatalogNumber, int quantity, BigDecimal unitPrice,
            String productName) {
        this.orderID = orderID;
        this.supplierProductCatalogNumber = supplierProductCatalogNumber;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.productName = productName;
    }

    public OrderItemLineDTO(OrderItemLine orderItemLine) {
        this.orderID = orderItemLine.getOrderId();
        this.supplierProductCatalogNumber = orderItemLine.getSupplierProductCatalogNumber();
        this.quantity = orderItemLine.getQuantity();
        this.unitPrice = orderItemLine.getUnitPrice();
        this.productName = orderItemLine.getProductName();

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

    public int getSupplierProductCatalogNumber() {
        return supplierProductCatalogNumber;
    }

    public void setSupplierProductCatalogNumber(int supplierProductCatalogNumber) {
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
}
