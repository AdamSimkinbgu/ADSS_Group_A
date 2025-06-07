package Suppliers.DomainLayer;

import Suppliers.DTOs.*;
import Suppliers.DTOs.Enums.InitializeState;
import Suppliers.DomainLayer.Classes.PeriodicOrder;
import Suppliers.DomainLayer.Repositories.SuppliersAgreementsRepositoryImpl;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderFacade extends BaseFacade {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderFacade.class);

    private PeriodicOrderHandler periodicOrderController;
    private OrderHandler orderHandler;

    public OrderFacade(InitializeState initializeState, SupplierFacade supplierFacade) {

        this.orderHandler = new OrderHandler(supplierFacade);
        this.periodicOrderController = new PeriodicOrderHandler();
        initialize(initializeState);
    }

    private void initialize(InitializeState initializeState) {
        if (initializeState == InitializeState.CURRENT_STATE) {
        }
        else if (initializeState == InitializeState.DEFAULT_STATE) {
        }
        else if (initializeState == InitializeState.NO_DATA_STATE) {
        }
        else {
            throw new IllegalArgumentException("Invalid InitializeState: " + initializeState);
        }
    }


//#######################################################################################################################################################################################################################
//                                       Periodic Order
//#######################################################################################################################################################################################################################
public PeriodicOrderDTO createPeriodicOrder(DayOfWeek fixedDay, HashMap<Integer, Integer> productsAndAmount) {
    if (productsAndAmount == null || productsAndAmount.isEmpty()) {
        throw new IllegalArgumentException("Products and amount cannot be null or empty");
    }
    // Validate that all products exist in the supplier's catalog
    Map<Integer, Integer> filteredProducts = filterProductsThatDontHaveSupplier(productsAndAmount);
    if (filteredProducts.isEmpty()) {
        LOGGER.warn("No valid products found for the periodic order. Please check the product IDs.");
        return null;
    }
    PeriodicOrderDTO periodicOrderDTO = periodicOrderController
            .createPeriodicOrder(fixedDay, filteredProducts);
    if (periodicOrderDTO == null) {
        LOGGER.error("Failed to create periodic order for day: {}", fixedDay);
        throw new RuntimeException("Failed to create periodic order");
    }
    LOGGER.info("Periodic order created successfully for day: {}", fixedDay);
    return periodicOrderDTO;
}

    public List<OrderResultDTO> executePeriodicOrdersForDay(DayOfWeek day) {
        List<PeriodicOrder> periodicOrders = PeriodicOrderHandler.getAllActivePeriodicOrdersByDay(day);

        return orderHandler.executePeriodicOrdersForDay(day);
    }
//#######################################################################################################################
//                                        Order
//#######################################################################################################################

    public OrderDTO addOrderManually(OrderDTO orderDTO) {
        if (orderDTO == null) {
            throw new IllegalArgumentException("OrderDTO cannot be null");
        }
        // Validate that all products exist in the supplier's catalog
        List<OrderItemLineDTO> filteredProducts = filterItemsThatSupplierDoesntHave(orderDTO.getItems(), orderDTO.getSupplierId()); // why Suplieer id ?? becuse it manulay? ifwe dont have this ?
        if (filteredProducts.isEmpty()) {
            LOGGER.warn("No valid products found for the order. Please check the product IDs.");
            return null;
        }
    }





    public OrderResultDTO createOrder(OrderInfoDTO infoDTO) throws SQLException {
        return orderHandler.handleOrder(infoDTO);
    }

    public OrderDTO createOrderByShortage(OrderInfoDTO infoDTO) throws SQLException {
            if (infoDTO == null || infoDTO.isEmpty()) {
                throw new IllegalArgumentException("Shortage map cannot be null or empty");
            }
            OrderInfoDTO infoDTO = new OrderInfoDTO(branchId, shortage);
            try {
                return orderHandler.handleOrder(infoDTO).getCreatedOrder();
            } catch (SQLException e) {
                LOGGER.error("Error creating order by shortage: {}", e.getMessage());
                throw new RuntimeException("Error creating order by shortage", e);
            }
    }

    public OrderDTO getOrderById(int orderID) {
        return orderHandler.getOrderById(orderID);
    }

    public List<OrderDTO> listOrders() {
        return orderHandler.getAllOrders();
    }

    public OrderDTO updateOrderInfo(OrderDTO updatedOrder) {
        if (updatedOrder == null) {
            throw new IllegalArgumentException("Updated order cannot be null");
        }
        return orderHandler.updateOrderInfo(updatedOrder);
    }

    public OrderDTO removeProductsFromOrder(int orderID, ArrayList<Integer> productsToRemove) {
        if (productsToRemove == null || productsToRemove.isEmpty()) {
            throw new IllegalArgumentException("Products to remove cannot be null or empty");
        }
        return orderHandler.removeProductsFromOrder(orderID, productsToRemove);
    }
    public OrderDTO updateProductsInOrder(int orderID, HashMap<Integer, Integer> productsToAdd) {
        if (productsToAdd == null || productsToAdd.isEmpty()) {
            throw new IllegalArgumentException("Products to add cannot be null or empty");
        }
        return orderHandler.updateProductsInOrder(orderID, productsToAdd);
    }

    public void printOrder(int supplierID) {
        List<OrderDTO> orders = orderHandler.getOrdersBySupplier(supplierID);
        orders.forEach(order -> LOGGER.info(order.toString()));
    }
    public void printOrders() {
        List<OrderDTO> orders = orderHandler.getAllOrders();
        orders.forEach(order -> LOGGER.info(order.toString()));
    }

    public HashMap<Integer, OrderDTO> getAllOrderForToday() {
        return orderHandler.getOrdersForToday();
    }

    public OrderDTO markOrderAsCollected(int orderID) {
        return orderHandler.markOrderAsCollected(orderID);
    }
//#######################################################################################################################
//                                        Help functions
//#######################################################################################################################

    private Map<Integer, Integer> filterProductsThatDontHaveSupplier(Map<Integer, Integer> productsAndAmount) {
        if (productsAndAmount == null || productsAndAmount.isEmpty()) {
            throw new IllegalArgumentException("Products and amount cannot be null or empty");
        }
        List<CatalogProductDTO> catalogProducts = SuppliersAgreementsRepositoryImpl.getInstance().getCatalogProducts();
        Map<Integer, Integer> filteredProducts = new HashMap<>();
        for (Map.Entry<Integer, Integer> entry : productsAndAmount.entrySet()) {
            int productId = entry.getKey();
            if (catalogProducts.stream().anyMatch(p -> p.getProductId() == productId)) {
                filteredProducts.put(productId, entry.getValue());
            } else {
                LOGGER.warn("Product ID {} not found in catalog, skipping", productId);
            }
        }
        return filteredProducts;
    }

    private List<OrderItemLineDTO> filterItemsThatSupplierDoesntHave(List<OrderItemLineDTO> items, int supplierId) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Items cannot be null or empty");
        }
        List<Integer> supplierProducts = SuppliersAgreementsRepositoryImpl
                .getInstance().getAllProductsForSupplierId(supplierId);
        if (supplierProducts == null || supplierProducts.isEmpty()) {
            throw new IllegalArgumentException("No products found for supplier ID: " + supplierId);
        }
        List<OrderItemLineDTO> filteredItems = new ArrayList<>();
        for (OrderItemLineDTO item : items) {
            if (supplierProducts.contains(item.getProductId())) {
                filteredItems.add(item);
            } else {
                LOGGER.warn("Product ID {} not found for supplier ID {}, removing from order", item.getProductId(),
                        supplierId);
            }
        }
        return filteredItems;
    }

}
