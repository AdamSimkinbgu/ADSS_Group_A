package DomainLayer.Classes;

import DTOs.Enums.OrderStatus;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Order implements Serializable {
    private static int nextOrderID = 1;
    private int orderId;
    private int supplierId;
    private LocalDate orderDate;
    private List<OrderItemLine> items;
    private OrderStatus status;

    // load constructor
    public Order(
            int orderId,
            int supplierId,
            LocalDate orderDate,
            List<OrderItemLine> items,
            OrderStatus status) {
        this.orderId = orderId;
        this.supplierId = supplierId;
        this.orderDate = orderDate;
        this.items = items;
        this.status = status;
    }

    // create constructor
    public Order(
            int supplierId,
            LocalDate orderDate,
            List<OrderItemLine> items,
            OrderStatus status) {
        this.orderId = nextOrderID++;
        this.supplierId = supplierId;
        this.orderDate = orderDate;
        this.items = items;
        this.status = status;
    }

    // create constructor with default status
    public Order(
            int supplierId,
            LocalDate orderDate,
            List<OrderItemLine> items) {
        this.orderId = nextOrderID++;
        this.supplierId = supplierId;
        this.orderDate = orderDate;
        this.items = items;
        this.status = OrderStatus.SENT;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public void setItems(List<OrderItemLine> items) {
        if (items == null) {
            this.items = new ArrayList<>();
        } else {
            this.items = new ArrayList<>(items);
        }
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public List<OrderItemLine> getItems() {
        return items;
    }

    public OrderStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "{\n" +
                "   \"orderId\": " + orderId + ",\n" +
                "   \"supplierId\": " + supplierId + ",\n" +
                "   \"orderDate\": \"" + orderDate + "\",\n" +
                "   \"items\": " + items + ",\n" +
                "   \"status\": \"" + status + "\"\n" +
                "}";
    }
}