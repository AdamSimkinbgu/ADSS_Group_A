package DomainLayer.Classes;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Represents a single line item in an order, including product details,
 * quantity, pricing, and any applicable discount.
 */
public class OrderItemLine implements Serializable {
    private int orderId;
    private int productId;
    private int orderItemLineID;
    private int quantity;
    private BigDecimal unitPrice;
    private int supplierProductCatalogNumber;
    private String productName;
    private BigDecimal discount;

    /**
     * Constructs a new OrderItemLine without discount.
     *
     * @param orderId                    the ID of the parent order
     * @param orderItemLineID            the line number within the order
     * @param productId                  the internal product identifier
     * @param quantity                   the quantity of the product ordered
     * @param unitPrice                  the price per unit
     * @param supplierProductCatalogNumber the catalog number from the supplier
     * @param productName                the name of the product
     */
    public OrderItemLine(int orderId,
                         int orderItemLineID,
                         int productId,
                         int quantity,
                         BigDecimal unitPrice,
                         int supplierProductCatalogNumber,
                         String productName) {
        this.orderId = orderId;
        this.orderItemLineID = orderItemLineID;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.supplierProductCatalogNumber = supplierProductCatalogNumber;
        this.productName = productName;
    }

    /**
     * Constructs a new OrderItemLine with discount applied.
     *
     * @param orderId                    the ID of the parent order
     * @param productId                  the internal product identifier
     * @param orderItemLineID            the line number within the order
     * @param quantity                   the quantity of the product ordered
     * @param unitPrice                  the price per unit
     * @param supplierProductCatalogNumber the catalog number from the supplier
     * @param productName                the name of the product
     * @param discount                   the discount multiplier (e.g., 0.90 for 10% off)
     */
    public OrderItemLine(int orderId,
                         int productId,
                         int orderItemLineID,
                         int quantity,
                         BigDecimal unitPrice,
                         int supplierProductCatalogNumber,
                         String productName,
                         BigDecimal discount) {
        this(orderId, orderItemLineID, productId, quantity, unitPrice, supplierProductCatalogNumber, productName);
        this.discount = discount;
    }

    /**
     * @return the parent order ID for this line item
     */
    public int getOrderId() {
        return orderId;
    }

    /**
     * @return the unique line ID for this item within its order
     */
    public int getOrderItemLineID() {
        return orderItemLineID;
    }

    /**
     * Sets the unique line ID for this item within its order.
     *
     * @param orderItemLineID the line ID to set
     */
    public void setOrderItemLineID(int orderItemLineID) {
        this.orderItemLineID = orderItemLineID;
    }

    /**
     * @return the product identifier
     */
    public int getProductId() {
        return productId;
    }

    /**
     * Sets the product identifier.
     *
     * @param productId the product ID to set
     */
    public void setProductId(int productId) {
        this.productId = productId;
    }

    /**
     * @return the quantity ordered
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity by parsing a String value.
     *
     * @param quantity the quantity as a String
     */
    public void setQuantity(String quantity) {
        this.quantity = Integer.parseInt(quantity);
    }

    /**
     * @return the price per unit
     */
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    /**
     * Sets the price per unit.
     *
     * @param unitPrice the unit price to set
     */
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    /**
     * @return the supplier's catalog number for this product
     */
    public int getSupplierProductCatalogNumber() {
        return supplierProductCatalogNumber;
    }

    /**
     * Sets the supplier's catalog number for this product.
     *
     * @param supplierProductCatalogNumber the catalog number to set
     */
    public void setSupplierProductCatalogNumber(int supplierProductCatalogNumber) {
        this.supplierProductCatalogNumber = supplierProductCatalogNumber;
    }

    /**
     * @return the name of the product
     */
    public String getProductName() {
        return productName;
    }

    /**
     * Sets the name of the product.
     *
     * @param productName the product name to set
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * @return the discount multiplier applied to this item
     */
    public BigDecimal getDiscount() {
        return discount;
    }

    /**
     * Sets the discount multiplier for this item.
     *
     * @param discount the discount to set
     */
    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    /**
     * Calculates and returns the final price for this line item.
     * <p>
     * Formula: unitPrice * quantity * discount
     *
     * @return the final price as BigDecimal
     */
    public BigDecimal getFinalPrice() {
        BigDecimal qty = new BigDecimal(quantity);
        BigDecimal disc = discount != null ? discount : BigDecimal.ONE;
        return unitPrice.multiply(qty).multiply(disc);
    }

    /**
     * Provides a  string representation of this line item.
     *
     * @return string representation
     */
    @Override
    public String toString() {
        return "{\n" +
                "   \"orderItemLineID\": " + orderItemLineID + ",\n" +
                "   \"productId\": \"" + productId + "\",\n" +
                "   \"quantity\": " + quantity + ",\n" +
                "   \"unitPrice\": " + unitPrice + ",\n" +
                "   \"supplierProductCatalogNumber\": " + supplierProductCatalogNumber + ",\n" +
                "   \"productName\": \"" + productName + "\",\n" +
                "   \"discount\": " + discount + "\n" +
                "}";
    }
}
