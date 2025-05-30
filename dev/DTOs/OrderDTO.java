package DTOs;

import DTOs.Enums.OrderStatus;
import DomainLayer.Classes.Address;
import DomainLayer.Classes.Order;
import DomainLayer.Classes.OrderItemLine;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrderDTO {
    private int orderId;
    private int supplierId;  // internal? or taxnum ?
    private String supplierName;
    private LocalDate orderDate;
    private LocalDate creationDate;
    private Address address ;
    private String contactPhoneNumber ;
    private List<OrderItemLineDTO> items;
    private OrderStatus status;

    public OrderDTO() {}


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


    public OrderDTO(Order order) {
        this.orderId = order.getOrderId();
        this.supplierId = order.getSupplierId();
        this.supplierName = order.getSupplierName();
        this.orderDate = order.getOrderDate();
        this.creationDate = order.getCreationDate();
        this.address = order.getAddress();
        this.contactPhoneNumber = order.getContactPhoneNumber();
        this.status = order.getStatus();

        if (order.getAllItems().size() <= 0) {
            this.items = new ArrayList<OrderItemLineDTO>();
        }
        else {
            if (order.getAllItems().isEmpty()) {
                this.items = new ArrayList<OrderItemLineDTO>();
            }
            else {
                for (OrderItemLine item : order.getAllItems()) {
                    this.items.add(new OrderItemLineDTO(item));
                }
            }
        }
    }
}
