package Suppliers.DTOs;

import Suppliers.DTOs.Enums.OrderStatus;
import Suppliers.DomainLayer.Classes.Address;
import Suppliers.DomainLayer.Classes.OrderItemLine;
import Suppliers.DomainLayer.Classes.Order;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrderDTO {
    private int orderId;
    private int supplierId; // internal? or taxnum ?
    private String supplierName;
    private LocalDate orderDate;
    private LocalDate creationDate;
    private Address address;
    private String contactPhoneNumber;
    private List<OrderItemLineDTO> items;
    private OrderStatus status;

    public OrderDTO() {
    }

    public OrderDTO(Integer orderId, Integer supplierId, String supplierName,
            LocalDate orderDate, LocalDate creationDate,
            Address address, String contactPhoneNumber,
            List<OrderItemLineDTO> items, OrderStatus status) {
        this.orderId = orderId;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.orderDate = orderDate;
        this.creationDate = creationDate;
        this.address = address;
        this.contactPhoneNumber = contactPhoneNumber;
        this.items = items;
        this.status = status;
    }

    public OrderDTO(Integer orderId, Integer supplierId, String supplierName,
            LocalDate orderDate, LocalDate creationDate,
            Address address, String contactPhoneNumber,
            List<OrderItemLineDTO> items) {
        this.orderId = orderId;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.orderDate = orderDate;
        this.creationDate = creationDate;
        this.address = address;
        this.contactPhoneNumber = contactPhoneNumber;
        this.items = items;
        this.status = OrderStatus.SENT;
    }

    public OrderDTO(Order order) {
        this.orderId = order.getOrderId();
        this.supplierId = order.getSupplierId();
        this.supplierName = order.getSupplierName();
        this.orderDate = order.getOrderDate();
        this.creationDate = order.getCreationDate();
        this.address = order.getAddress();
        this.contactPhoneNumber = order.getContactPhoneNumber();
        this.status = order.getStatus();

        if (order.getAllItems().isEmpty()) {
            this.items = new ArrayList<OrderItemLineDTO>();
        } else {
            for (OrderItemLine item : order.getAllItems()) {
                this.items.add(new OrderItemLineDTO(item));
            }
        }
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getContactPhoneNumber() {
        return contactPhoneNumber;
    }

    public void setContactPhoneNumber(String contactPhoneNumber) {
        this.contactPhoneNumber = contactPhoneNumber;
    }

    public List<OrderItemLineDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemLineDTO> items) {
        this.items = items;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
