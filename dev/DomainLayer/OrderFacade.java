package DomainLayer;

import DomainLayer.Classes.Order;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import java.util.*;

public class OrderFacade extends BaseFacade {

    private final Map<UUID, Order> orders;
    private final SupplierFacade supplierFacade; // קישור ל-SupplierFacade

    public OrderFacade(SupplierFacade supplierFacade) {
        this.orders = new HashMap<>();
        this.supplierFacade = supplierFacade;
    }

    public Order addOrder(String json) {
        try {
            Order o = mapper.readValue(json, Order.class);
            orders.put(o.getOrderId(), o);
            return o;
        } catch (MismatchedInputException e) {
            System.err.println("JSON parse error: " + e.getOriginalMessage());
            System.err.println("  at: " + e.getPathReference());
            throw new RuntimeException("Order JSON parse failed", e);
        } catch (Exception e) {
            throw new RuntimeException("Order creation failed", e);
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
