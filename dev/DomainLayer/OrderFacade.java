package DomainLayer;

import DomainLayer.Classes.Order;
import DomainLayer.Classes.Supplier;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

public class OrderFacade {

    private final Map<UUID, Order> orders;
    private final SupplierFacade supplierFacade; // קישור ל-SupplierFacade

    public OrderFacade(SupplierFacade supplierFacade) {
        this.orders = new HashMap<>();
        this.supplierFacade = supplierFacade;
    }

    public Order createOrder(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> tempMap = mapper.readValue(json, Map.class);
            String supplierIdStr = (String) tempMap.get("supplierId");
            String contactPhone = (String) tempMap.get("contactPhone");
            String orderItemsStr = (String) tempMap.get("orderItems");

            if (supplierIdStr == null) {
                throw new IllegalArgumentException("Supplier ID is missing from the order JSON.");
            }
            UUID supplierId = UUID.fromString(supplierIdStr);
            Supplier supplier = supplierFacade.getSupplier(supplierIdStr);
            String newJson = String.format(
                    "{\"supplierId\":\"%s\",\"supplierName\":\"%s\",\"supplierAddress\":\"%s\",\"contactPhone\":\"%s\",\"orderItems\":%s}",
                    supplier.getSupplierId().toString(),
                    supplier.getName(),
                    supplier.getAddress(),
                    contactPhone,
                    orderItemsStr);
            Order order = mapper.readValue(newJson, Order.class);
            orders.put(order.getOrderId(), order);
            return order;

        } catch (Exception e) {
            throw new RuntimeException("Failed to create order: " + e.getMessage(), e);
        }

    }

    public Order getOrder(UUID orderId) {
        Order order = orders.get(orderId);
        if (order == null) {
            throw new NoSuchElementException("Order not found with id: " + orderId);
        }
        return order;
    }

    public List<Order> listOrders() {
        return new ArrayList<>(orders.values());
    }

    public void deleteOrder(UUID orderId) {
        if (!orders.containsKey(orderId)) {
            throw new NoSuchElementException("Order not found to delete with id: " + orderId);
        }
        orders.remove(orderId);
    }

    public Order updateOrder(Order updatedOrder) {
        if (!orders.containsKey(updatedOrder.getOrderId())) {
            throw new NoSuchElementException("Order not found to update with id: " + updatedOrder.getOrderId());
        }
        orders.put(updatedOrder.getOrderId(), updatedOrder);
        return updatedOrder;
    }
}
