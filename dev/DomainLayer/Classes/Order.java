package DomainLayer.Classes;

import DomainLayer.Enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Order {

    private UUID orderId;
    private String supplierName;
    private UUID supplierId;
    private String supplierAddress;
    private LocalDateTime orderDate;
    private String contactPhone;
    private OrderStatus orderStatus;
    private List<OrderItem> orderItems;

    public Order() {
        this.orderId = UUID.randomUUID();
        this.supplierName = "";
        this.supplierId = null;
        this.supplierAddress = "";
        this.orderDate = LocalDateTime.now();
        this.contactPhone = "";
        this.orderStatus = OrderStatus.PENDING;
        this.orderItems = new ArrayList<>();
    }

    @JsonCreator
    public Order(
            @JsonProperty("supplierName") String supplierName,
            @JsonProperty("supplierId") UUID supplierId,
            @JsonProperty("supplierAddress") String supplierAddress,
            @JsonProperty("orderDate") LocalDateTime orderDate,
            @JsonProperty("contactPhone") String contactPhone,
            @JsonProperty("orderStatus") OrderStatus orderStatus,
            @JsonProperty("orderItems") List<OrderItem> orderItems
    ) {
        this.orderId = UUID.randomUUID();
        this.supplierName = supplierName;
        this.supplierId = supplierId;
        this.supplierAddress = supplierAddress;
        this.orderDate = (orderDate != null) ? orderDate : LocalDateTime.now();
        this.contactPhone = contactPhone;
        this.orderStatus = (orderStatus != null) ? orderStatus : OrderStatus.PENDING;
        this.orderItems = (orderItems != null) ? orderItems : new ArrayList<>();
    }

    // Getters and setters

    public UUID getOrderId() {
        return orderId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public UUID getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(UUID supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierAddress() {
        return supplierAddress;
    }

    public void setSupplierAddress(String supplierAddress) {
        this.supplierAddress = supplierAddress;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public void addOrderItem(OrderItem item) {
        if (orderItems == null) {
            orderItems = new ArrayList<>();
        }
        orderItems.add(item);
    }
}