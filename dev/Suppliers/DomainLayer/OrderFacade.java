package Suppliers.DomainLayer;

import Suppliers.DTOs.CatalogProductDTO;
import Suppliers.DTOs.OrderDTO;
import Suppliers.DTOs.OrderItemLineDTO;
import Suppliers.DTOs.PeriodicOrderDTO;
import Suppliers.DTOs.PeriodicOrderItemLineDTO;
import Suppliers.DTOs.Enums.InitializeState;
import Suppliers.DomainLayer.Repositories.SuppliersAgreementsRepositoryImpl;

import java.time.DayOfWeek;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderFacade extends BaseFacade {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderFacade.class);
    private final SupplierFacade supplierFacade;

    private PeriodicOrderHandler periodicOrderController;
    private OrderController orderController;

    public OrderFacade(InitializeState initializeState, SupplierFacade supplierFacade) {
        this.supplierFacade = supplierFacade;
        this.orderController = new OrderController();
        this.periodicOrderController = new PeriodicOrderHandler();
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
    public PeriodicOrderDTO createPeriodicOrder(PeriodicOrderDTO periodicOrderDTO) {
        if (periodicOrderDTO == null) {
            throw new IllegalArgumentException("PeriodicOrderDTO cannot be null");
        }
        // Validate that all products exist in the supplier's catalog
        Map<Integer, Integer> filteredProducts = filterProductsThatDontHaveSupplier(
                periodicOrderDTO.getProductsInOrder());
        if (filteredProducts.isEmpty()) {
            LOGGER.warn("No valid products found for the periodic order. Please check the product IDs.");
            return null;
        }
        periodicOrderDTO.setProductsInOrder(new HashMap<>(filteredProducts));
        // Create the periodic order
        periodicOrderDTO = periodicOrderController.createPeriodicOrder(periodicOrderDTO);
        if (periodicOrderDTO.getPeriodicOrderID() == -1) {
            LOGGER.error("Failed to create periodic order for day: {}", periodicOrderDTO.getDeliveryDay());
            throw new RuntimeException("Failed to create periodic order");
        }
        LOGGER.info("Periodic order created successfully for day: {}", periodicOrderDTO.getDeliveryDay());
        return periodicOrderDTO;

    }

    // ##################################################################################################################
    // Order
    // ##################################################################################################################
    public OrderDTO addOrder(OrderDTO orderDTO) {
        if (orderDTO == null) {
            throw new IllegalArgumentException("OrderDTO cannot be null");
        }
        // Validate that all products exist in the supplier's catalog
        List<OrderItemLineDTO> filteredProducts = filterItemsThatSupplierDoesntHave(orderDTO.getItems(),
                orderDTO.getSupplierId());
        if (filteredProducts.isEmpty()) {
            LOGGER.warn("No valid products found for the order. Please check the product IDs.");
            return null;
        }

        filteredProducts = supplierFacade
                .setProductNameAndCategoryForOrderItems(filteredProducts, orderDTO.getSupplierId());
        filteredProducts = supplierFacade
                .setSupplierPricesAndDiscountsByBestPrice(filteredProducts, orderDTO.getSupplierId());
        orderDTO.setItems(filteredProducts);
        orderDTO.setSupplierName(
                supplierFacade.getSupplierDTO(orderDTO.getSupplierId()).getName());
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

    private Map<Integer, Integer> filterProductsThatDontHaveSupplier(
            Map<Integer, Integer> productsAndAmount) {
        if (productsAndAmount == null || productsAndAmount.isEmpty()) {
            throw new IllegalArgumentException("Products and amount cannot be null or empty");
        }
        List<CatalogProductDTO> catalogProducts = SuppliersAgreementsRepositoryImpl
                .getInstance().getCatalogProducts();
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

    private List<OrderItemLineDTO> filterItemsThatSupplierDoesntHave(
            List<OrderItemLineDTO> items, int supplierId) {
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

    public boolean deletePeriodicOrder(int periodicOrderId) {
        if (periodicOrderId <= 0) {
            LOGGER.error("Invalid periodic order ID: {}", periodicOrderId);
            throw new IllegalArgumentException("Periodic order ID must be greater than 0");
        }
        boolean deleted = periodicOrderController.deletePeriodicOrder(periodicOrderId);
        if (!deleted) {
            LOGGER.warn("Periodic order with ID {} not found or could not be deleted.", periodicOrderId);
        } else {
            LOGGER.info("Periodic order with ID {} deleted successfully.", periodicOrderId);
        }
        return deleted;
    }

    public PeriodicOrderDTO getPeriodicOrder(int periodicOrderId) {
        if (periodicOrderId <= 0) {
            LOGGER.error("Invalid periodic order ID: {}", periodicOrderId);
            throw new IllegalArgumentException("Periodic order ID must be greater than 0");
        }
        PeriodicOrderDTO periodicOrder = periodicOrderController.getPeriodicOrder(periodicOrderId);
        if (periodicOrder == null) {
            LOGGER.warn("Periodic order with ID {} not found.", periodicOrderId);
            return null;
        }
        LOGGER.info("Fetched periodic order with ID {} successfully.", periodicOrderId);
        return periodicOrder;
    }

    public List<PeriodicOrderDTO> getAllPeriodicOrders() {
        List<PeriodicOrderDTO> periodicOrders = periodicOrderController.getAllPeriodicOrders();
        if (periodicOrders == null || periodicOrders.isEmpty()) {
            LOGGER.info("No periodic orders found.");
            return Collections.emptyList();
        }
        LOGGER.info("Fetched {} periodic orders.", periodicOrders.size());
        return periodicOrders;
    }

    public PeriodicOrderDTO updatePeriodicOrder(PeriodicOrderDTO updatedDto) {
        if (updatedDto == null || updatedDto.getPeriodicOrderID() <= 0) {
            LOGGER.error("Invalid periodic order DTO: {}", updatedDto);
            throw new IllegalArgumentException("Periodic order DTO cannot be null and must have a valid ID");
        }
        PeriodicOrderDTO updatedOrder = periodicOrderController.updatePeriodicOrder(updatedDto);
        if (updatedOrder == null) {
            LOGGER.error("Failed to update periodic order with ID: {}", updatedDto.getPeriodicOrderID());
            throw new RuntimeException("Failed to update periodic order");
        }
        LOGGER.info("Periodic order with ID {} updated successfully.", updatedDto.getPeriodicOrderID());
        return updatedOrder;
    }

}
