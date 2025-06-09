package Suppliers.DomainLayer;

import Suppliers.DTOs.*;
import Suppliers.DTOs.Enums.InitializeState;
import Suppliers.DTOs.Enums.OrderStatus;
import Suppliers.DomainLayer.Classes.PeriodicOrder;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.util.*;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Inventory.DTO.SupplyDTO;

public class OrderFacade extends BaseFacade {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderFacade.class);

    private final PeriodicOrderHandler periodicOrderHandler;
    private final OrderHandler orderHandler;
    private final SupplierFacade supplierFacade;

    public OrderFacade(InitializeState initializeState, SupplierFacade supplierFacade) {

        this.orderHandler = new OrderHandler(supplierFacade, initializeState);
        this.periodicOrderHandler = new PeriodicOrderHandler();
        this.supplierFacade = supplierFacade;
    }

    // ##################################################################################################################
    // Periodic Order
    // ##################################################################################################################
    public PeriodicOrderDTO createPeriodicOrder(PeriodicOrderDTO periodicOrderDTO) {
        LOGGER.info("Facade: createPeriodicOrder called for day: {} with {} items",
                periodicOrderDTO.getDeliveryDay(),
                periodicOrderDTO.getProductsInOrder() != null ? periodicOrderDTO.getProductsInOrder().size() : 0);

        PeriodicOrderDTO result = periodicOrderHandler.createPeriodicOrder(periodicOrderDTO);

        LOGGER.info("Facade: createPeriodicOrder result: {}", result);
        return result;
    }

    public List<OrderResultDTO> executePeriodicOrdersForDay(DayOfWeek day) {
        LOGGER.info("Facade: executePeriodicOrdersForDay called for day: {}", day);
        List<PeriodicOrder> periodicOrders = periodicOrderHandler.getAllActivePeriodicOrdersByDay(day);
        List<OrderResultDTO> results = orderHandler.executePeriodicOrdersForDay(day, periodicOrders);
        LOGGER.info("Facade: executePeriodicOrdersForDay returned {} results", results == null ? 0 : results.size());
        return results;
    }

    public boolean deletePeriodicOrder(int periodicOrderId) {
        LOGGER.info("Facade: deletePeriodicOrder called for ID: {}", periodicOrderId);
        boolean deleted = periodicOrderHandler.deletePeriodicOrder(periodicOrderId);
        LOGGER.info("Facade: deletePeriodicOrder result: {}", deleted);
        return deleted;
    }

    public PeriodicOrderDTO getPeriodicOrder(int periodicOrderId) {
        LOGGER.info("Facade: getPeriodicOrder called for ID: {}", periodicOrderId);
        PeriodicOrderDTO dto = periodicOrderHandler.getPeriodicOrder(periodicOrderId);
        LOGGER.info("Facade: getPeriodicOrder result: {}", dto);
        return dto;
    }

    public List<PeriodicOrderDTO> getAllPeriodicOrders() {
        LOGGER.info("Facade: getAllPeriodicOrders called");
        List<PeriodicOrderDTO> list = periodicOrderHandler.getAllPeriodicOrders();
        int size = list == null ? 0 : list.size();
        LOGGER.info("Facade: getAllPeriodicOrders returned {} entries", size);
        return size == 0 ? Collections.emptyList() : list;
    }

    public PeriodicOrderDTO updatePeriodicOrder(PeriodicOrderDTO dto) {
        LOGGER.info("Facade: updatePeriodicOrder called with DTO: {}", dto);
        PeriodicOrderDTO updated = periodicOrderHandler.updatePeriodicOrder(dto);
        LOGGER.info("Facade: updatePeriodicOrder result: {}", updated);
        return updated;
    }
    // #######################################################################################################################
    // Order
    // #######################################################################################################################

    public OrderDTO addOrderManually(OrderDTO orderDTO) {
        LOGGER.info("Facade: addOrderManually called with DTO: {}", orderDTO);
        OrderDTO result = orderHandler.addOrderManually(orderDTO);
        LOGGER.info("Facade: addOrderManually result: {}", result);
        return result;
    }

    public OrderResultDTO createOrder(OrderInfoDTO infoDTO) {
        LOGGER.info("Facade: createOrder called with InfoDTO: {}", infoDTO);
        OrderResultDTO result = orderHandler.createOrder(infoDTO);
        LOGGER.info("Facade: createOrder result: {}", result);
        return result;
    }

    public OrderResultDTO createOrderByShortage(OrderInfoDTO pOrder) {
        LOGGER.info("Facade: createOrderByShortage called with InfoDTO: {}", pOrder);
        OrderResultDTO result = orderHandler.createOrderByShortage(pOrder);
        LOGGER.info("Facade: createOrderByShortage result: {}", result);
        return result;
    }

    public OrderDTO getOrderById(int orderID) {
        return orderHandler.getOrderById(orderID);
    }

    public List<OrderDTO> listOrders() {
        return orderHandler.getAllOrders();
    }

    public boolean updateOrderInfo(OrderDTO updatedOrder) {
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

    public boolean markOrderAsCollected(int orderID) {
        return orderHandler.markOrderAsCompleted(orderID);
    }

    public OrderDTO updateOrder(OrderInfoDTO updatedDto) {
        return null;
    }

    public boolean deleteOrder(int orderId) {
        LOGGER.info("Facade: deleteOrder called for ID: {}", orderId);
        boolean deleted = orderHandler.deleteOrder(orderId);
        if (deleted) {
            LOGGER.info("Facade: deleteOrder successful for ID: {}", orderId);
        } else {
            LOGGER.warn("Facade: deleteOrder failed for ID: {}", orderId);
        }
        return deleted;
    }

    public List<OrderDTO> getAllOrders() {
        LOGGER.info("Facade: getAllOrders called");
        List<OrderDTO> orders = orderHandler.getAllOrders();
        int size = orders == null ? 0 : orders.size();
        LOGGER.info("Facade: getAllOrders returned {} entries", size);
        return size == 0 ? Collections.emptyList() : orders;
    }

    public List<SupplyDTO> getSupplyDTOFromOrder(OrderDTO order) {
        // we need to get the experation date for each product from the supplier
        // products
        if (order == null || order.getItems() == null || order.getItems().isEmpty()) {
            LOGGER.warn("Order or items are null or empty, returning empty supply list");
            return Collections.emptyList();
        }
        List<SupplyDTO> supplyList = new ArrayList<>();
        for (OrderItemLineDTO item : order.getItems()) {
            if (item.getProductId() <= 0 || item.getQuantity() <= 0) {
                LOGGER.warn("Invalid product ID or quantity in order item: {}", item);
                continue;
            }
            Integer expiresInDayNum = supplierFacade.getProductExperationInDays(item.getProductId(),
                    order.getSupplierId());
            SupplyDTO supply = new SupplyDTO(item.getProductId(), item.getQuantity(),
                    order.getDeliveryDate().plusDays(expiresInDayNum));
            supplyList.add(supply);
        }
        LOGGER.info("Facade: getSupplyDTOFromOrder created {} supply items from order ID: {}", supplyList.size(),
                order.getOrderId());
        return supplyList;
    }

    public List<OrderDTO> getOrdersInDeliveredStatus() {
        LOGGER.info("Facade: getOrdersInDeliveredStatus called");
        List<OrderDTO> deliveredOrders = orderHandler.getOrdersInDeliveredStatus();
        int size = deliveredOrders == null ? 0 : deliveredOrders.size();
        LOGGER.info("Facade: getOrdersInDeliveredStatus returned {} entries", size);
        return size == 0 ? Collections.emptyList() : deliveredOrders;
    }

    public void advanceOrderStatus(int orderId, OrderStatus status) {
        LOGGER.info("Facade: advanceOrderStatus called for order ID: {} with status: {}", orderId, status);
        if (orderId <= 0 || status == null) {
            throw new IllegalArgumentException("Invalid order ID or status");
        }
        OrderDTO order = orderHandler.getOrderById(orderId);
        if (order == null) {
            LOGGER.warn("No order found with ID: {}", orderId);
            return;
        }
        order.setStatus(status);
        orderHandler.updateOrderInfo(order);
        LOGGER.info("Facade: advanceOrderStatus updated order ID: {} to status: {}", orderId, status);
    }
}