package DomainLayer.Classes;

import DomainLayer.Enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Order implements Serializable {
    private UUID orderId;
    private UUID supplierId;
    private LocalDate orderDate;
    private List<OrderItem> items;
    private OrderStatus status;

    public Order() {
        this.items = new ArrayList<>();
    }

    @JsonCreator
    public Order(
            @JsonProperty(value = "orderId", required = false) UUID orderId,
            @JsonProperty(value = "supplierId", required = true) UUID supplierId,
            @JsonProperty(value = "orderDate", required = true) LocalDate orderDate,
            @JsonProperty(value = "items", required = true) List<OrderItem> items,
            @JsonProperty(value = "status", required = false) OrderStatus status) {
        this.orderId = (orderId != null)
                ? orderId
                : UUID.randomUUID();
        this.supplierId = supplierId;
        this.orderDate = orderDate;
        this.items = items;
        this.status = OrderStatus.PENDING;
    }

    @JsonProperty("supplierId")
    public void setSupplierId(UUID supplierId) {
        this.supplierId = supplierId;
    }

    @JsonProperty("orderDate")
    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    @JsonProperty("items")
    public void setItems(List<OrderItem> items) {
        if (items == null) {
            this.items = new ArrayList<>();
        } else {
            this.items = new ArrayList<>(items);
        }
    }

    @JsonProperty("status")
    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public UUID getSupplierId() {
        return supplierId;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public OrderStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Order{id=" + orderId +
                ", supplierId=" + supplierId +
                ", date=" + orderDate +
                ", items=" + items +
                ", status=" + status + '}';
    }
}