package Suppliers.DomainLayer;

import Suppliers.DTOs.OrderDTO;
import Suppliers.DTOs.PeriodicOrderDTO;
import Suppliers.DTOs.Enums.InitializeState;
import Suppliers.DomainLayer.Repositories.OrdersRepositoryImpl;
import Suppliers.DomainLayer.Repositories.RepositoryIntefaces.OrdersRepositoryInterface;

import java.time.DayOfWeek;
import java.util.*;

public class OrderFacade extends BaseFacade {

    // private final SupplierController supplierFacade;

    private PeriodicOrderController periodicOrderController;
    private OrderController orderController;
    private OrdersRepositoryInterface ordersRepository;

    public OrderFacade(InitializeState initializeState) {

        this.orderController = new OrderController();
        this.periodicOrderController = new PeriodicOrderController();
        this.ordersRepository = new OrdersRepositoryImpl();
        initialize(initializeState);
    }

    private void initialize(InitializeState initializeState) {
        if (initializeState == InitializeState.CURRENT_STATE) {
        } else if (initializeState == InitializeState.DEFAULT_STATE) {
        } else if (initializeState == InitializeState.NO_DATA_STATE) {
        } else {
            throw new IllegalArgumentException("Invalid InitializeState: " + initializeState);
        }
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
    public OrderDTO addOrder(OrderDTO orderDTO) {
        if (orderDTO == null) {
            throw new IllegalArgumentException("OrderDTO cannot be null");
        }
        OrderDTO order = orderController.addOrder(orderDTO);
        if (order == null) {
            throw new RuntimeException("Failed to add order");
        }
        return order;
    }

    public OrderDTO getOrder(int orderID) {
        return null; // TODO: Implement this method

    }

    public List<OrderDTO> listOrders() {
        return null; // TODO: Implement this method
    }

    public void deleteOrder(int orderID) {
        // TODO: Implement this method
    }

    public OrderDTO updateOrder(OrderDTO updatedOrder) {
        return null; // TODO: Implement this method
    }

    public OrderDTO createOrderByShortage(int branchId, HashMap<Integer, Integer> shortage) {

        return null; // TODO: Implement this method
    }
}
