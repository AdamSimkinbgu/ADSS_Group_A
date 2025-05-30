package DomainLayer.Classes;

import DTOs.Enums.OrderStatus;
import DTOs.OrderDTO;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Order implements Serializable {
    private static int nextOrderID = 1;
    private int orderId;
    private int supplierId;
    private String supplierName;
    private LocalDate orderDate;
    private LocalDate creationDate;
    private Address address ;
    private String contactPhoneNumber ;
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

    public Order (OrderDTO orderDTO){

    }


//// Get and Set
    public OrderStatus getStatus() {
        return status;
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

    public List<OrderItemLine> getAllItems() {
        return items;
    }

    public String getSupplierName() { return supplierName; }
    public LocalDate getCreationDate() { return creationDate; }
    public Address getAddress() { return address; }


    public void setAddress(Address address) { this.address = address; }

    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }

    public void setCreationDate(LocalDate creationDate) { this.creationDate = creationDate; }

    protected void setOrderId(int orderId) { this.orderId = orderId; }

    public void setStatus(OrderStatus status) {this.status = status; }

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

    public void setContactPhoneNumber(String contactPhoneNumber) { this.contactPhoneNumber = contactPhoneNumber; }
    public String getContactPhoneNumber() { return contactPhoneNumber; }

    //  don't think its correct
    public void addItem(OrderItemLine orderItemLine) { items.add(orderItemLine); }
    public void removeItem(OrderItemLine orderItemLine) { items.remove(orderItemLine); }


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