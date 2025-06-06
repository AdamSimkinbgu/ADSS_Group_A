package Suppliers.DomainLayer;

import Suppliers.DTOs.OrderDTO;
import Suppliers.DomainLayer.Classes.Order;

import java.time.DayOfWeek;
import java.util.*;

public class OrderFacade extends BaseFacade {

    private final Map<Integer, Order> orders;
    private final SupplierController supplierFacade;
    private static int nextOrderID = 1;
    private PeriodicOrderController periodicOrderController;
    private OrderController orderController;


    public OrderFacade() {
        orders = new HashMap<>();
        this.supplierFacade = new SupplierController();

    }

    public OrderFacade(SupplierController supplierFacade) {
        this.orders = new HashMap<>();
        this.supplierFacade = supplierFacade;
        this.periodicOrderController = new PeriodicOrderController();
        this.orderController = new OrderController();

    }

    //##################################################################################################################
    //                                        Periodic Order
    //##################################################################################################################
    public periodiceOrderDTO createPeriodicOrder(int supplierID, int branchID, DayOfWeek fixedDay, HashMap<Integer, Integer> productsAndAmount) {


    //##################################################################################################################
    //                                        Order
    //##################################################################################################################
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

    public OrderDTO createOrderByShortage(int branchId, HashMap<Integer, Integer> shortage) {

        return 0 ;
    }

