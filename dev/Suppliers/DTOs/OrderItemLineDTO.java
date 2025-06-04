package Suppliers.DTOs;

import Suppliers.DomainLayer.Classes.OrderItemLine;

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

    public OrderItemLineDTO(int orderID, String supplierProductCatalogNumber, int quantity, BigDecimal unitPrice,
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
}
