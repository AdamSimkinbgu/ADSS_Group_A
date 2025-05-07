package DomainLayer.Classes;

import DomainLayer.Enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

public class Order implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID orderId;
    private UUID supplierId;
    private LocalDate orderDate;
    private List<OrderItem> items;
    private EnumSet<OrderStatus> status;

    public Order() {
        this.items = new ArrayList<>();
        this.status = EnumSet.noneOf(OrderStatus.class);
    }

    @JsonCreator
    public Order(
            @JsonProperty(value = "orderId", required = false) UUID orderId,
            @JsonProperty(value = "supplierId", required = true) UUID supplierId,
            @JsonProperty(value = "orderDate", required = true) LocalDate orderDate,
            @JsonProperty(value = "items", required = true) List<OrderItem> items,
            @JsonProperty(value = "status", required = true) EnumSet<OrderStatus> status) {
        this.orderId = (orderId != null)
                ? orderId
                : UUID.randomUUID();
        setSupplierId(supplierId);
        setOrderDate(orderDate);
        setItems(items);
        setStatus(status);
    }

    @JsonProperty("supplierId")
    public void setSupplierId(UUID supplierId) {
        if (supplierId == null) {
            throw new IllegalArgumentException("supplierId must not be null");
        }
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
    public void setStatus(EnumSet<OrderStatus> status) {
        if (status == null) {
            this.status = EnumSet.noneOf(OrderStatus.class);
        } else {
            this.status = EnumSet.copyOf(status);
        }
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

    public EnumSet<OrderStatus> getStatus() {
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