package DomainLayer.Classes;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderItem implements Serializable {
    private UUID orderItemId;
    private String productId;
    private int quantity;
    private BigDecimal unitPrice;

    public OrderItem() {
    }

    @JsonCreator
    public OrderItem(
            @JsonProperty(value = "orderItemId", required = false) UUID orderItemId,
            @JsonProperty(value = "productId", required = true) String productId,
            @JsonProperty(value = "quantity", required = true) int quantity,
            @JsonProperty(value = "unitPrice", required = true) BigDecimal unitPrice) {
        this.orderItemId = (orderItemId != null)
                ? orderItemId
                : UUID.nameUUIDFromBytes((productId + ":" + quantity).getBytes());
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    @JsonProperty("orderItemId")
    public void setOrderItemId(UUID orderItemId) {
        this.orderItemId = orderItemId;
    }

    @JsonProperty("productId")
    public void setProductId(String productId) {
        this.productId = productId;
    }

    @JsonProperty("quantity")
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @JsonProperty("unitPrice")
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public UUID getOrderItemId() {
        return orderItemId;
    }

    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    @Override
    public String toString() {
        return "OrderItem{id=" + orderItemId +
                ", productId='" + productId + '\'' +
                ", qty=" + quantity +
                ", unitPrice=" + unitPrice + '}';
    }
}
