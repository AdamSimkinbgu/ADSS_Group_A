package DomainLayer;

import DomainLayer.Classes.Order;

import java.util.*;

public class OrderFacade extends BaseFacade {

    private final Map<Integer, Order> orders;
    private final SupplierFacade supplierFacade;

    public OrderFacade(SupplierFacade supplierFacade) {
        this.orders = new HashMap<>();
        this.supplierFacade = supplierFacade;
    }

    public Order addOrder(String json) {
        return null; // TODO: Implement this method
    }

    public Order getOrder(int orderID) {
        return null; // TODO: Implement this method

    }

    public List<Order> listOrders() {
        return null; // TODO: Implement this method
    }

    public void deleteOrder(int orderID) {
        // TODO: Implement this method
    }

    public Order updateOrder(Order updatedOrder) {
        return null; // TODO: Implement this method
    }
}
