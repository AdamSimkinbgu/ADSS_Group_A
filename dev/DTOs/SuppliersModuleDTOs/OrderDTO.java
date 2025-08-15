package DTOs.SuppliersModuleDTOs;

import DTOs.SuppliersModuleDTOs.Enums.OrderCatagory;
import DTOs.SuppliersModuleDTOs.Enums.OrderStatus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderDTO {
    private int orderId;
    private int supplierId;
    private String supplierName;
    private LocalDate orderDate;
    private LocalDate creationDate;
    private LocalDate deliveryDate;
    private AddressDTO address;
    private String contactPhoneNumber;
    private List<OrderItemLineDTO> items;
    private OrderStatus status;
    private OrderCatagory orderCatagory;

    public OrderDTO() {
    }

    public OrderDTO(int supplierId, LocalDate orderDate, AddressDTO address, String contactPhoneNumber,
            List<OrderItemLineDTO> items) {
        this.orderId = -1;
        this.supplierId = supplierId;
        this.supplierName = "";
        this.orderDate = orderDate;
        this.creationDate = LocalDate.now();
        this.address = address;
        this.contactPhoneNumber = contactPhoneNumber;
        this.items = items;
        this.status = OrderStatus.PENDING;
        this.orderCatagory = OrderCatagory.REGULAR; // Default to REGULAR
    }

    public OrderDTO(OrderDTO order) {
        this.orderId = order.getOrderId();
        this.supplierId = order.getSupplierId();
        this.supplierName = order.getSupplierName();
        this.orderDate = order.getOrderDate();
        this.creationDate = order.getCreationDate();
        this.address = order.getAddress();
        this.contactPhoneNumber = order.getContactPhoneNumber();
        this.status = order.getStatus();

        if (order.getItems() != null) {
            this.items = new ArrayList<>();
            for (OrderItemLineDTO item : order.getItems()) {
                this.items.add(new OrderItemLineDTO(item));
            }
        } else {
            this.items = new ArrayList<>();
        }
        this.deliveryDate = order.getDeliveryDate() != null ? order.getDeliveryDate() : LocalDate.now();
        this.orderCatagory = order.getOrderCatagory();
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

    public AddressDTO getAddress() {
        return address;
    }

    public void setAddress(AddressDTO address) {
        this.address = address;
    }

    public String getContactPhoneNumber() {
        return contactPhoneNumber;
    }

    public void setDeliveryDate() {
        this.deliveryDate = LocalDate.now();
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
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

    public Map<Integer, Integer> getProductsAndQuantities() {
        Map<Integer, Integer> productAndPrices = new HashMap<>();
        if (items != null) {
            for (OrderItemLineDTO item : items) {
                productAndPrices.put(item.getProductId(), item.getQuantity());
            }
        }
        return productAndPrices;
    }

    public void addItem(OrderItemLineDTO item) {
        if (this.items == null) {
            this.items = new ArrayList<>();
        }
        this.items.add(item);
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
        if (status == OrderStatus.DELIVERED) {
            setDeliveryDate();
        }
    }

    private final int MAX_SUPPLIER_NAME = 20;

    private String truncate(String s, int maxLen) {
        if (s == null)
            return "";
        if (s.length() <= maxLen)
            return s;
        return s.substring(0, maxLen - 3) + "...";
    }

    @Override
    public String toString() {
        // Helper to truncate very long strings (if necessary) to a max length:

        // Format dates as YYYY-MM-DD
        DateTimeFormatter df = DateTimeFormatter.ISO_LOCAL_DATE;
        String orderDateStr = orderDate != null ? orderDate.format(df) : "N/A";
        String creationDateStr = creationDate != null ? creationDate.format(df) : "N/A";

        // Build the “header” lines:
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(
                "Order [%4d]  Supplier: %-20s  (ID=%d)  Status: %s%n",
                orderId,
                truncate(supplierName, MAX_SUPPLIER_NAME),
                supplierId,
                status != null ? status.name() : "N/A"));
        sb.append(String.format(
                "  Order Date:    %s    Created On: %s%n",
                orderDateStr,
                creationDateStr));

        // Ship-to address (AddressDTO assumed to have its own toString, or build
        // inline):
        if (address != null) {
            // e.g. AddressDTO.toString() might be “123 Main St, Springfield” already
            sb.append(String.format("  Ship To:       %s%n", address.toString()));
        } else {
            sb.append("  Ship To:       [no address]\n");
        }

        // Contact phone
        sb.append(String.format("  Contact Phone: %s%n",
                contactPhoneNumber != null ? contactPhoneNumber : "N/A"));

        sb.append("  --------------------------------------------------\n");

        // Items section
        if (items == null || items.isEmpty()) {
            sb.append("  [No items in this order]\n");
        } else {
            sb.append("  Items:\n");
            for (OrderItemLineDTO line : items) {
                // Indent each line’s toString by two spaces
                sb.append("    ").append(line.toString()).append("\n");
            }
            sb.append(String.format("  (%d item%s total)%n",
                    items.size(),
                    items.size() == 1 ? "" : "s"));
        }

        sb.append("======================================================");
        return sb.toString();
    }

    public OrderCatagory getOrderCatagory() {
        return orderCatagory;
    }

    public void setOrderCatagory(OrderCatagory orderCatagory) {
        this.orderCatagory = orderCatagory;
    }
}
