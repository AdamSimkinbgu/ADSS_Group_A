package Suppliers.DomainLayer;

import Suppliers.DTOs.*;
import Suppliers.DTOs.Enums.InitializeState;
import Suppliers.DomainLayer.Classes.Supplier;
import Suppliers.DomainLayer.Repositories.SuppliersAgreementsRepositoryImpl;

import java.time.DayOfWeek;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderFacade extends BaseFacade {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderFacade.class);
    private final SupplierFacade supplierFacade;
    private PeriodicOrderHandler periodicOrderController;
    private OrderHandler orderHandler;

    public OrderFacade(InitializeState initializeState, SupplierFacade supplierFacade) {
        this.supplierFacade = supplierFacade;
        this.orderHandler = new OrderHandler();
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
        filteredProducts = supplierFacade.setProductNameAndCategoryForOrderItems(filteredProducts, orderDTO.getSupplierId());
        filteredProducts = supplierFacade.setSupplierPricesAndDiscountsByBestPrice(filteredProducts, orderDTO.getSupplierId());
        orderDTO.setItems(filteredProducts);
        orderDTO.setSupplierName(supplierFacade.getSupplierDTO(orderDTO.getSupplierId()).getName());
        OrderDTO order = orderHandler.addOrder(orderDTO);
        if (order == null) {
            throw new RuntimeException("Failed to add order");
        }
        return order;
    }


    public OrderDTO createOrder(OrderInfoDTO infoDTO) {
        if (infoDTO == null) {
            throw new IllegalArgumentException("OrderDTO cannot be null");
        }
        // Validate that there are products to order
        if (infoDTO.getProducts() == null || infoDTO.getProducts().isEmpty()) {
            throw new IllegalArgumentException("OrderDTO.products cannot be null or empty");
        }
        Map<Integer, Integer> products = infoDTO.getProducts(); // productId -> quantity


        return null ;
    }

    public OrderDTO createOrderByShortage(int branchId, HashMap<Integer, Integer> shortage) {
        return null; // TODO: Implement this method

    }

    public OrderDTO getOrder(int orderID) {
        return null; // TODO: Implement this method
    }



    public List<OrderDTO> listOrders() {
            return null; // TODO: Implement this method 
    }
    
    public OrderDTO updateOrder(OrderDTO updatedOrder) {
        return null; // TODO: Implement this method
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

    /**
     * Retrieves, for each productId in the input map, a list of Supplier objects
     * who carry that product (without checking quantity), sorted by base unit price
     * and then by delivery time.
     *
     * @param products Map of productId -> quantity (quantity is not used for filtering here)
     * @return Map of productId -> List<Supplier> sorted by unit price and delivery time
     */
    public Map<Integer, List<Supplier>> getSuppliersForProductsSortedByPriceAndDelivery(Map<Integer, Integer> products) {
        {
            Map<Integer, List<Supplier>> result = new HashMap<>();

            for (Integer productId : products.keySet()) {
                List<Integer> supplierIds = getAllSuppliersForProductId(productId);

                if (supplierIds == null || supplierIds.isEmpty()) {
                    LOGGER.warn("No suppliers found in the database for product ID: {}", productId);
                    result.put(productId, Collections.emptyList());
                    continue;
                }
                List<Supplier> suppliers = new ArrayList<>();
                for (Integer supplierId : supplierIds) {

                }


            }

        }
    }
}
