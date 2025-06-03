package Suppliers.DomainLayer;

import Suppliers.DTOs.OrderDTO;
import Suppliers.DTOs.PeriodicOrderDTO;
import Suppliers.DTOs.Enums.InitializeState;
import Suppliers.DomainLayer.Classes.Order;
import Suppliers.DomainLayer.Repositories.SuppliersAgreementsRepositoryImpl;

import java.time.DayOfWeek;
import java.util.*;
import java.util.function.Supplier;

public class OrderFacade extends BaseFacade {

    // private final SupplierController supplierFacade;

    private PeriodicOrderController periodicOrderController;
    private OrderController orderController;
    private SuppliersAgreementsRepositoryImpl suppliersAgreementsRepository;

    public OrderFacade(InitializeState initializeState) {
        this.orderController = new OrderController();
        this.suppliersAgreementsRepository = SuppliersAgreementsRepositoryImpl.getInstance();
        this.periodicOrderController = new PeriodicOrderController();

    }

    // ##################################################################################################################
    // Periodic Order
    // ##################################################################################################################
    public PeriodicOrderDTO createPeriodicOrder(int supplierID, int branchID, DayOfWeek fixedDay,
            HashMap<Integer, Integer> productsAndAmount) {
        // return periodicOrderController.createPeriodicOrder(supplierID, branchID,
        // fixedDay, productsAndAmount);
        return null;
    }

    // ##################################################################################################################
    // Order
    // ##################################################################################################################
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

        return null; // TODO: Implement this method
    }
}
