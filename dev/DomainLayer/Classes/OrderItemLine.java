package DomainLayer.Classes;

import java.io.Serializable;
import java.math.BigDecimal;

public class OrderItemLine implements Serializable {
    private int orderID; // by having this field, we can change the order the item belongs to
    private int orderItemLineID;
    private String productId;
    private int quantity;
    private BigDecimal unitPrice;
    private int supplierProductCatalogNumber;
    private String productName;
    private String productManufacturerName;

    public OrderItemLine(
            int orderID,
            int orderItemLineID,
            String productId,
            int quantity,
            BigDecimal unitPrice,
            int supplierProductCatalogNumber,
            String productName,
            String productManufacturerName) {
        this.orderID = orderID;
        this.orderItemLineID = orderItemLineID;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.supplierProductCatalogNumber = supplierProductCatalogNumber;
        this.productName = productName;
        this.productManufacturerName = productManufacturerName;
    }

    public int getOrderItemLineID() {
        return orderItemLineID;
    }

    public void setOrderItemLineID(int orderItemLineID) {
        this.orderItemLineID = orderItemLineID;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = String.valueOf(productId);
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = Integer.parseInt(quantity);
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public int getOrderID() {
        return orderID;
    }

    public int getSupplierProductCatalogNumber() {
        return supplierProductCatalogNumber;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductManufacturerName() {
        return productManufacturerName;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    @Override
    public String toString() {
        return "{\n" +
                "   \"orderID\": " + orderID + ",\n" +
                "   \"orderItemLineID\": " + orderItemLineID + ",\n" +
                "   \"productId\": \"" + productId + "\",\n" +
                "   \"quantity\": " + quantity + ",\n" +
                "   \"unitPrice\": " + unitPrice + ",\n" +
                "   \"supplierProductCatalogNumber\": " + supplierProductCatalogNumber + ",\n" +
                "   \"productName\": \"" + productName + "\",\n" +
                "   \"productManufacturerName\": \"" + productManufacturerName + "\"\n" +
                "}";
    }
}
