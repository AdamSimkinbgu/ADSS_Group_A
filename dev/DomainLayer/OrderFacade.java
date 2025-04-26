package DomainLayer;

import DomainLayer.Classes.Order;
import DomainLayer.SupplierFacade;

import java.util.*;

public class OrderFacade {

    private final Map<UUID, Order> orders;
    private final SupplierFacade supplierFacade; // קישור ל-SupplierFacade

    public OrderFacade(SupplierFacade supplierFacade) {
        this.orders = new HashMap<>();
        this.supplierFacade = supplierFacade;
    }

    public Order createOrder(Order order) {
        // לוודא שהספק קיים ב-SupplierFacade
        if (supplierFacade.getSupplier("{\"supplierId\":\"" + order.getSupplierId() + "\"}").isEmpty()) {
            throw new IllegalArgumentException("Supplier not found with id: " + order.getSupplierId());
        }

        orders.put(order.getOrderId(), order);
        return order;
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
