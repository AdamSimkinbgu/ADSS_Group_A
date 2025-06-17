package Suppliers.DomainLayer.Classes;

import Suppliers.DTOs.Enums.OrderStatus;
import Suppliers.DTOs.AddressDTO;
import Suppliers.DTOs.OrderDTO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a purchase order, containing supplier details,
 * delivery information, status, and associated line items.
 */
public class Order implements Serializable {

    private int orderId;
    private int supplierId;
    private String supplierName;
    private LocalDate orderDate;
    private LocalDate creationDate;
    private AddressDTO address;
    private String contactPhoneNumber;
    private List<OrderItemLine> items;
    private OrderStatus status;

    /**
     * Default constructor.
     * Initializes items list and sets default status to SENT.
     */
    public Order() {
        this.items = new ArrayList<>();
        this.status = OrderStatus.SENT;
    }

    /**
     * Constructs an Order from a DTO.
     * Converts each OrderItemLineDTO into an OrderItemLine.
     *
     * @param id  the ID to assign to this order
     * @param dto the data transfer object containing order data
     */
    public Order(int id, OrderDTO dto) {
        this.orderId = id;
        this.supplierId = dto.getSupplierId();
        this.supplierName = dto.getSupplierName();
        this.orderDate = dto.getOrderDate();
        this.creationDate = dto.getCreationDate();
        this.address = dto.getAddress();
        this.contactPhoneNumber = dto.getContactPhoneNumber();
        this.status = dto.getStatus();
        this.items = dto.getItems() != null
                ? new ArrayList<>()
                : dto.getItems().stream()
                        .map(item -> new OrderItemLine(item))
                        .toList();
    }

    /**
     * Full constructor for loading or creating an order with all fields.
     *
     * @param orderId            the unique order identifier
     * @param supplierId         the supplier's internal ID
     * @param supplierName       the name of the supplier
     * @param orderDate          the expected delivery date
     * @param creationDate       the date the order was created
     * @param address            the delivery address
     * @param contactPhoneNumber the contact phone for the order
     * @param items              the list of order line items
     * @param status             the current status of the order
     */
    public Order(int orderId,
            int supplierId,
            String supplierName,
            LocalDate orderDate,
            LocalDate creationDate,
            AddressDTO address,
            String contactPhoneNumber,
            List<OrderItemLine> items,
            OrderStatus status) {
        this.orderId = orderId;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.orderDate = orderDate;
        this.creationDate = creationDate;
        this.address = address;
        this.contactPhoneNumber = contactPhoneNumber;
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
        this.status = status;
    }

    /**
     * @return the current status of the order
     */
    public OrderStatus getStatus() {
        return status;
    }

    /**
     * Sets the current status of the order.
     *
     * @param status the status to set
     */
    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    /**
     * @return the unique identifier of the order
     */
    public int getOrderId() {
        return orderId;
    }

    /**
     * @return the supplier's internal identifier
     */
    public int getSupplierId() {
        return supplierId;
    }

    /**
     * @return the name of the supplier
     */
    public String getSupplierName() {
        return supplierName;
    }

    /**
     * Sets the name of the supplier.
     *
     * @param supplierName the supplier name to set
     */
    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    /**
     * @return the expected delivery date of the order
     */
    public LocalDate getOrderDate() {
        return orderDate;
    }

    /**
     * Sets the expected delivery date of the order.
     *
     * @param orderDate the delivery date to set
     */
    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    /**
     * @return the date the order was created
     */
    public LocalDate getCreationDate() {
        return creationDate;
    }

    /**
     * Sets the creation date of the order.
     *
     * @param creationDate the creation date to set
     */
    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * @return the delivery address for the order
     */
    public AddressDTO getAddress() {
        return address;
    }

    /**
     * Sets the delivery address for the order.
     *
     * @param address the address to set
     */
    public void setAddress(AddressDTO address) {
        this.address = address;
    }

    /**
     * @return the contact phone number for the order
     */
    public String getContactPhoneNumber() {
        return contactPhoneNumber;
    }

    /**
     * Sets the contact phone number for the order.
     *
     * @param contactPhoneNumber the phone number to set
     */
    public void setContactPhoneNumber(String contactPhoneNumber) {
        this.contactPhoneNumber = contactPhoneNumber;
    }

    /**
     * @return the list of line items in the order
     */
    public List<OrderItemLine> getAllItems() {
        return items;
    }

    /**
     * Sets the list of line items for the order.
     *
     * @param items the items to set
     */
    public void setItems(List<OrderItemLine> items) {
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
    }

    /**
     * Adds a new line item to the order.
     *
     * @param orderItemLine the line item to add
     */
    public void addItem(OrderItemLine orderItemLine) {
        items.add(orderItemLine);
    }

    /**
     * Removes a line item from the order.
     *
     * @param orderItemLine the line item to remove
     */
    public void removeItem(OrderItemLine orderItemLine) {
        items.remove(orderItemLine);
    }

    /**
     * Adds a new item to the order by specifying its properties.
     *
     * @param productId                    the product ID
     * @param quantity                     the quantity ordered
     * @param unitPrice                    the price per unit
     * @param supplierProductCatalogNumber the catalog number from supplier
     * @param productName                  the name of the product
     */
    public void addItem(int productId,
            int quantity,
            BigDecimal unitPrice,
            int supplierProductCatalogNumber,
            String productName) {
        // items.add(new OrderItemLine(
        // this.getOrderId(),
        // (items.size() + 1),
        // productId,
        // quantity,
        // unitPrice,
        // supplierProductCatalogNumber,
        // productName));
    }

    /**
     * Removes the line item at the specified index.
     *
     * @param lineId the index of the line to remove
     */
    public void removeItem(int lineId) {
        if (lineId >= 0 && lineId < items.size()) {
            items.remove(lineId);
        }
    }

    /**
     * /**
     * 
     * @return a concise string representation of this order
     */
    @Override
    public String toString() {
        return "Order{id=" + orderId + ", supplierId=" + supplierId + ", status=" + status + "}";
    }

    /**
     * Provides a detailed tabular view of the order.
     *
     * @return formatted order details
     */
    public String displayOrderDetails() {
        StringBuilder sb = new StringBuilder();

        sb.append("=========== Order Details =========== ");

        sb.append("Supplier Name: ")
                .append(supplierName != null ? supplierName : "Unavailable")
                .append(" | ");
        sb.append("Address: ")
                .append(address != null ? address.toString() : "Unavailable")
                .append(" | ");
        sb.append("Order ID: ").append(orderId).append(" ");

        sb.append("Supplier ID: ").append(supplierId).append(" | ");
        sb.append("Order Date: ")
                .append(orderDate != null ? orderDate : "Unavailable")
                .append(" | ");
        sb.append("Contact Phone: ")
                .append(contactPhoneNumber != null ? contactPhoneNumber : "Unavailable")
                .append(" Items: ");

        sb.append("-------------------------------------------------------------------------------------- ");
        sb.append(String.format("| %-10s | %-20s | %-8s | %-10s | %-8s | %-11s | ",
                "Product ID",
                "Product Name",
                "Quantity",
                "Unit Price",
                "Discount",
                "Final Price"));
        sb.append("-------------------------------------------------------------------------------------- ");

        for (OrderItemLine item : items) {
            sb.append(String.format("| %-10d | %-20s | %-8d | %-10.2f | %-8.2f | %-11.2f | ",
                    item.getProductId(),
                    item.getProductName(),
                    item.getQuantity(),
                    item.getUnitPrice(),
                    item.getDiscount(),
                    item.getFinalPrice()));
        }

        sb.append("-------------------------------------------------------------------------------------- ");

        return sb.toString();
    }

}
