package Suppliers.DomainLayer;

import Suppliers.DTOs.BillofQuantitiesItemDTO;
import Suppliers.DTOs.CatalogProductDTO;
import Suppliers.DTOs.OrderDTO;
import Suppliers.DTOs.OrderItemLineDTO;
import Suppliers.DTOs.PeriodicOrderDTO;
import Suppliers.DTOs.SupplierProductDTO;
import Suppliers.DTOs.Enums.InitializeState;
import Suppliers.DomainLayer.Repositories.SuppliersAgreementsRepositoryImpl;

import java.math.BigDecimal;
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
    public PeriodicOrderDTO createPeriodicOrder(DayOfWeek fixedDay,
            HashMap<Integer, Integer> productsAndAmount) {
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

}
