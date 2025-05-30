package DomainLayer.Classes;

import java.io.Serializable;
import java.math.BigDecimal;

public class OrderItemLine implements Serializable {
    private int orderId;
    private int productId;
    private int orderItemLineID;
    private int quantity;
    private BigDecimal unitPrice;
    private int supplierProductCatalogNumber;
    private String productName;
    private BigDecimal discount ;


    public OrderItemLine(
            int orderID,
            int orderItemLineID,
            int productId,
            int quantity,
            BigDecimal unitPrice,
            int supplierProductCatalogNumber,
            String productName) {
        this.orderId = orderID;
        this.orderItemLineID = orderItemLineID;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.supplierProductCatalogNumber = supplierProductCatalogNumber;
        this.productName = productName;
        //this.productManufacturerName = productManufacturerName;
    }

    public int getOrderItemLineID() {
        return orderItemLineID;
    }

    public void setOrderItemLineID(int orderItemLineID) {
        this.orderItemLineID = orderItemLineID;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) { this.productId = productId; }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = Integer.parseInt(quantity);
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public int getSupplierProductCatalogNumber() {
        return supplierProductCatalogNumber;
    }

    public String getProductName() {
        return productName;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public void setSupplierProductCatalogNumber(int supplierProductCatalogNumber) {
        this.supplierProductCatalogNumber = supplierProductCatalogNumber;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public BigDecimal getFinalPrice() {
        return unitPrice.multiply(discount);    // meby ned to change
    }


    @Override
    public String toString() {
        return "{\n" +
                "   \"orderItemLineID\": " + orderItemLineID + ",\n" +
                "   \"productId\": \"" + productId + "\",\n" +
                "   \"quantity\": " + quantity + ",\n" +
                "   \"unitPrice\": " + unitPrice + ",\n" +
                "   \"supplierProductCatalogNumber\": " + supplierProductCatalogNumber + ",\n" +
                "   \"productName\": \"" + productName + "\",\n" +
               // "   \"productManufacturerName\": \"" + productManufacturerName + "\"\n" +
                "}";
    }

}
