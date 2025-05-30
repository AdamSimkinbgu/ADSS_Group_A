package DomainLayer.Classes;

import DTOs.Enums.OrderStatus;
import DTOs.OrderDTO;
import DTOs.OrderItemLineDTO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Order implements Serializable {

    private int orderId;
    private int supplierId;  // internal? or taxnum ?
    private String supplierName;
    private LocalDate orderDate;
    private LocalDate creationDate;
    private Address address ;
    private String contactPhoneNumber ;
    private List<OrderItemLine> items;
    private OrderStatus status;

    public Order() {}

    public Order( int id , OrderDTO dto) {
        this.orderId = id;
        this.supplierId = dto.getSupplierId();
        this.supplierName = dto.getSupplierName();
        this.orderDate = dto.getOrderDate();
        this.creationDate = dto.getCreationDate();
        this.address = dto.getAddress();
        this.contactPhoneNumber = dto.getContactPhoneNumber();
        this.status = dto.getStatus();
        this.items = new ArrayList<>();
        if (dto.getItems() != null) {
            for (OrderItemLineDTO itemDto : dto.getItems()) {
                this.items.add(new OrderItemLine(
                        id,
                        itemDto.getOrderItemLineID(),
                        itemDto.getProductId(),
                        itemDto.getQuantity(),
                        itemDto.getUnitPrice(),
                        itemDto.getSupplierProductCatalogNumber(),
                        itemDto.getProductName(),
                        itemDto.getDiscount()
                ));
            }
        }
    }

    public Order(int orderId, int supplierId, String supplierName, LocalDate orderDate, LocalDate creationDate, Address address, String contactPhoneNumber, List<OrderItemLine> items, OrderStatus status) {
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

    public void addItem(int productId ,int quantity , BigDecimal unitPrice, int supplierProductCatalogNumber ,String productName) { items.add(new OrderItemLine( this.getOrderId(), (items.size() + 1 ), productId, quantity,unitPrice, supplierProductCatalogNumber, productName)) ; }
    public void removeItem(int lineId) {
        items.remove(lineId);
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
    public String displayOrderDetails() {
        StringBuilder sb = new StringBuilder();

        sb.append("=========== Order Details ===========\n");

        sb.append("Supplier Name: ").append(supplierName != null ? supplierName : "Unavailable").append(" | ");
        sb.append("Address: ").append(address != null ? address.toString() : "Unavailable").append(" | ");
        sb.append("Order ID: ").append(orderId).append("\n");

        sb.append("Supplier ID: ").append(supplierId).append(" | ");
        sb.append("Order Date: ").append(orderDate != null ? orderDate : "Unavailable").append(" | ");
        sb.append("Contact Phone: ").append(contactPhoneNumber != null ? contactPhoneNumber : "Unavailable").append("\n");

        sb.append("\nItems:\n");
        sb.append("--------------------------------------------------------------------------------------\n");
        sb.append(String.format("| %-10s | %-20s | %-8s | %-10s | %-8s | %-11s |\n",
                "Product ID", "Product Name", "Quantity", "Unit Price", "Discount", "Final Price"));
        sb.append("--------------------------------------------------------------------------------------\n");

        for (OrderItemLine item : items) {
            sb.append(String.format("| %-10d | %-20s | %-8d | %-10.2f | %-8.2f | %-11.2f |\n",
                    item.getProductId(),
                    item.getProductName(),
                    item.getQuantity(),
                    item.getUnitPrice(),
                    item.getDiscount(),
                    item.getFinalPrice()));
        }

        sb.append("--------------------------------------------------------------------------------------\n");

        return sb.toString();
    }


}