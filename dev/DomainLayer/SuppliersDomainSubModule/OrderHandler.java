package DomainLayer.SuppliersDomainSubModule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import DTOs.SuppliersModuleDTOs.AddressDTO;
import DTOs.SuppliersModuleDTOs.AgreementDTO;
import DTOs.SuppliersModuleDTOs.BillofQuantitiesItemDTO;
import DTOs.SuppliersModuleDTOs.CatalogProductDTO;
import DTOs.SuppliersModuleDTOs.OrderDTO;
import DTOs.SuppliersModuleDTOs.OrderInfoDTO;
import DTOs.SuppliersModuleDTOs.OrderItemLineDTO;
import DTOs.SuppliersModuleDTOs.OrderResultDTO;
import DTOs.SuppliersModuleDTOs.PeriodicOrderDTO;
import DTOs.SuppliersModuleDTOs.ProductOrderFailureDTO;
import DTOs.SuppliersModuleDTOs.ProductOrderSuccessDTO;
import DTOs.SuppliersModuleDTOs.SupplierDTO;
import DTOs.SuppliersModuleDTOs.SupplierProductDTO;
import DTOs.SuppliersModuleDTOs.Enums.InitializeState;
import DTOs.SuppliersModuleDTOs.Enums.OrderCatagory;
import DTOs.SuppliersModuleDTOs.Enums.OrderStatus;
import DomainLayer.SuppliersDomainSubModule.Classes.ContactInfo;
import DomainLayer.SuppliersDomainSubModule.Classes.Supplier;
import DomainLayer.SuppliersDomainSubModule.Repositories.OrdersRepositoryImpl;
import DomainLayer.SuppliersDomainSubModule.Repositories.SuppliersAgreementsRepositoryImpl;
import DomainLayer.SuppliersDomainSubModule.Repositories.RepositoryIntefaces.OrdersRepositoryInterface;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

public class OrderHandler {
   private static final Logger LOGGER = LoggerFactory.getLogger(OrderHandler.class);

   private final OrdersRepositoryInterface ordersRepository;
   private final SupplierFacade supplierFacade;

   public OrderHandler(SupplierFacade supplierFacade, InitializeState initializeState) {
      this.supplierFacade = supplierFacade;
      this.ordersRepository = new OrdersRepositoryImpl();
   }

   public OrderDTO addOrder(OrderDTO orderDTO) {
      if (orderDTO == null) {
         throw new IllegalArgumentException("OrderDTO cannot be null");
      }
      OrderDTO createdOrder = ordersRepository.createRegularOrder(orderDTO);
      if (createdOrder == null) {
         throw new RuntimeException("Failed to create order");
      }
      return createdOrder;
   }

   /**
    * Entry point for creating orders
    */
   public OrderResultDTO createOrder(OrderInfoDTO infoDTO) {
      if (infoDTO == null) {
         throw new IllegalArgumentException("OrderInfoDTO cannot be null");
      }

      // Combine quantities for any repeated product entries
      Map<Integer, Integer> mergedProducts = mergeProductQuantities(infoDTO.getProducts());
      if (mergedProducts.isEmpty()) {
         throw new IllegalArgumentException("No products to order after merging");
      }

      LocalDate creationDate = infoDTO.getCreationDate();
      LocalDate requestDate = infoDTO.getOrderDate();

      // FIND FEASIBLE SUPPLIERS
      List<Integer> failedProductIds = new ArrayList<>();
      Map<Integer, List<Supplier>> feasibleSuppliers = findFeasibleSuppliers(
            mergedProducts, creationDate, requestDate, failedProductIds);

      // FILTER OUT UNFULFILLABLE PRODUCTS
      Map<Integer, Integer> filteredProducts = filterFulfillableProducts(mergedProducts, failedProductIds);

      // SELECT BEST SUPPLIERS WITH FULL DATA
      Map<Integer, SupplierSelection> selections = chooseBestSuppliers(
            feasibleSuppliers, filteredProducts, creationDate);

      // GROUP PRODUCTS BY SUPPLIER FOR PERSISTENCE
      Map<Supplier, Map<Integer, Integer>> grouped = groupProductsBySupplier(selections, filteredProducts);

      // PERSIST ORDERS TO DB
      try {
         persistOrders(grouped, requestDate, creationDate, OrderCatagory.REGULAR);
      } catch (Exception ex) {
         LOGGER.error("Failed to persist orders", ex);
         throw new RuntimeException("Error persisting orders", ex);
      }

      // BUILD AND RETURN RESULT DTO
      return buildResult(mergedProducts, selections, failedProductIds);
   }

   /**
    * Merges duplicate products by summing their quantities
    */
   private Map<Integer, Integer> mergeProductQuantities(Map<Integer, Integer> products) {
      Map<Integer, Integer> mergedProducts = new HashMap<>();
      for (Map.Entry<Integer, Integer> entry : products.entrySet()) {
         mergedProducts.merge(entry.getKey(), entry.getValue(), Integer::sum);
      }
      return mergedProducts;
   }

   /**
    * Filters out products that have no feasible suppliers
    */
   private Map<Integer, Integer> filterFulfillableProducts(
         Map<Integer, Integer> allProducts, List<Integer> failedProductIds) {
      Map<Integer, Integer> filteredProducts = new HashMap<>(allProducts);
      for (Integer pid : failedProductIds) {
         filteredProducts.remove(pid);
      }
      return filteredProducts;
   }

   /**
    * For each product, retrieves all suppliers and filters those
    * who can deliver on or before the requested date.
    * Products without feasible suppliers are added to failedProductIds.
    */
   private Map<Integer, List<Supplier>> findFeasibleSuppliers(
         Map<Integer, Integer> products,
         LocalDate creationDate,
         LocalDate requestDate,
         List<Integer> failedProductIds) {

      Map<Integer, List<Supplier>> result = new HashMap<>();

      for (var entry : products.entrySet()) {
         int pid = entry.getKey();
         List<Supplier> suppliers = supplierFacade.getAllSuppliersForProduct(pid);
         List<Supplier> feasible = new ArrayList<>();

         for (Supplier s : suppliers) {
            LocalDate delivery = getEarliestDelivery(s, creationDate);
            if (!delivery.isAfter(requestDate)) {
               feasible.add(s);
            }
         }

         if (feasible.isEmpty()) {
            failedProductIds.add(pid);
         } else {
            result.put(pid, feasible);
         }
      }
      return result;
   }

   /**
    * Chooses the best supplier for each product based on cost and delivery time
    */
   private Map<Integer, SupplierSelection> chooseBestSuppliers(
         Map<Integer, List<Supplier>> feasibleSuppliers,
         Map<Integer, Integer> products,
         LocalDate creationDate) {

      Map<Integer, SupplierSelection> result = new HashMap<>();

      for (var entry : products.entrySet()) {
         int pid = entry.getKey();
         int qty = entry.getValue();
         List<Supplier> options = feasibleSuppliers.get(pid);

         if (options == null || options.isEmpty())
            continue;

         Supplier bestSupplier = null;
         BigDecimal bestCost = null;
         LocalDate bestDelivery = null;

         for (Supplier supplier : options) {
            BigDecimal cost = calculateTotalCostWithTieredDiscounts(supplier, pid, qty);
            LocalDate delivery = getEarliestDelivery(supplier, creationDate);

            if (cost == null)
               continue;

            boolean isBetter = bestSupplier == null
                  || cost.compareTo(bestCost) < 0
                  || (cost.compareTo(bestCost) == 0 && delivery.isBefore(bestDelivery));

            if (isBetter) {
               bestSupplier = supplier;
               bestCost = cost;
               bestDelivery = delivery;
            }
         }

         if (bestSupplier != null) {
            result.put(pid, new SupplierSelection(bestSupplier, bestCost, bestDelivery));
         }
      }

      return result;
   }

   /**
    * Groups products by their selected suppliers for order persistence
    */
   private Map<Supplier, Map<Integer, Integer>> groupProductsBySupplier(
         Map<Integer, SupplierSelection> selections,
         Map<Integer, Integer> products) {

      Map<Supplier, Map<Integer, Integer>> grouped = new HashMap<>();

      for (Map.Entry<Integer, Integer> entry : products.entrySet()) {
         int pid = entry.getKey();
         int qty = entry.getValue();
         SupplierSelection selection = selections.get(pid);
         if (selection != null) {
            Supplier supplier = selection.supplier();
            grouped.computeIfAbsent(supplier, sup -> new HashMap<>()).put(pid, qty);
         }
      }

      return grouped;
   }

   /**
    * Calculates total cost with tiered discounts based on quantity
    */
   private BigDecimal calculateTotalCostWithTieredDiscounts(
         Supplier supplier,
         int productId,
         int quantity) {

      // 1. Fetch base unit price
      BigDecimal unitPrice = supplierFacade
            .getSupplierProductById(supplier.getSupplierId(), productId)
            .map(SupplierProductDTO::getPrice)
            .orElse(BigDecimal.ZERO);

      if (unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
         return null;
      }

      // 2. Gather and filter BoQ items for this product
      List<BillofQuantitiesItemDTO> tiers = new ArrayList<>();
      for (AgreementDTO agreement : supplierFacade.getAgreementsForSupplier(supplier.getSupplierId())) {
         for (BillofQuantitiesItemDTO item : supplierFacade.getBoQItemsForAgreement(agreement.getAgreementId())) {
            if (item.getProductId() == productId) {
               tiers.add(item);
            }
         }
      }

      // 3. Sort descending by min-quantity threshold
      tiers.sort(Comparator.comparingInt(BillofQuantitiesItemDTO::getQuantity).reversed());

      BigDecimal totalCost = BigDecimal.ZERO;
      int remaining = quantity;

      // 4. Apply each tier progressively
      for (BillofQuantitiesItemDTO tier : tiers) {
         int threshold = tier.getQuantity();
         BigDecimal discount = tier.getDiscountPercent() != null
               ? BigDecimal.valueOf(1).subtract(tier.getDiscountPercent())
               : BigDecimal.ZERO;

         if (remaining >= threshold) {
            int applyQty = remaining - (threshold - 1);
            BigDecimal partialCost = unitPrice
                  .multiply(BigDecimal.valueOf(applyQty))
                  .multiply(BigDecimal.ONE.subtract(discount));
            totalCost = totalCost.add(partialCost);
            remaining -= applyQty;
         }
         if (remaining <= 0)
            break;
      }

      // 5. Add remaining units at full price
      if (remaining > 0) {
         totalCost = totalCost.add(unitPrice.multiply(BigDecimal.valueOf(remaining)));
      }

      return totalCost;
   }

   /**
    * Calculates the earliest delivery date for a given supplier,
    * based on the order creation date, the supplier's lead time,
    * and the supplier's available delivery days.
    */
   private LocalDate getEarliestDelivery(Supplier supplier, LocalDate creationDate) {
      LocalDate earliestDate = creationDate.plusDays(supplier.getLeadSupplyDays());

      // If supplier has no fixed delivery days, return date after lead time
      if (supplier.getSupplyDays() == null || supplier.getSupplyDays().isEmpty()) {
         return earliestDate;
      }

      // Otherwise, find the first matching day-of-week in the supplier's supplyDays
      while (!supplier.getSupplyDays().contains(earliestDate.getDayOfWeek())) {
         earliestDate = earliestDate.plusDays(1);
      }

      return earliestDate;
   }

   /**
    * Builds the final result DTO with successful and failed product orders
    */
   private OrderResultDTO buildResult(
         Map<Integer, Integer> allProducts,
         Map<Integer, SupplierSelection> selections,
         List<Integer> failedProductIds) {

      List<ProductOrderSuccessDTO> successful = new ArrayList<>();
      List<ProductOrderFailureDTO> failed = new ArrayList<>();

      for (var entry : allProducts.entrySet()) {
         int pid = entry.getKey();
         int qty = entry.getValue();

         String productName = supplierFacade.getProductName(pid);
         if (productName == null || productName.isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty for product ID: " + pid);
         }

         if (failedProductIds.contains(pid)) {
            failed.add(new ProductOrderFailureDTO(pid, productName, qty));
         } else {
            SupplierSelection selection = selections.get(pid);
            if (selection != null) {
               Supplier supplier = selection.supplier();
               successful.add(new ProductOrderSuccessDTO(
                     pid, productName, qty, supplier.getSupplierId(), supplier.getName()));
            } else {
               // Fallback - shouldn't happen if logic is correct
               failed.add(new ProductOrderFailureDTO(pid, productName, qty));
            }
         }
      }

      return new OrderResultDTO(successful, failed);
   }

   /**
    * Helper method to get product name
    */
   private String getProductName(int productId) {
      return supplierFacade.getSupplierProductById(-1, productId)
            .map(SupplierProductDTO::getName)
            .orElse("Unknown Product");
   }

   /**
    * Persists orders to the database by creating OrderDTO objects
    * and delegating to the OrdersRepositoryImpl
    */
   private void persistOrders(
         Map<Supplier, Map<Integer, Integer>> groupedOrders,
         LocalDate requestDate,
         LocalDate creationDate,
         OrderCatagory orderCategory) {

      for (Map.Entry<Supplier, Map<Integer, Integer>> entry : groupedOrders.entrySet()) {
         Supplier supplier = entry.getKey();
         Map<Integer, Integer> products = entry.getValue();

         // Create OrderDTO for this supplier
         OrderDTO orderDTO = new OrderDTO();
         orderDTO.setSupplierId(supplier.getSupplierId());
         orderDTO.setSupplierName(supplier.getName());
         orderDTO.setOrderDate(requestDate);
         orderDTO.setCreationDate(creationDate);
         orderDTO.setStatus(OrderStatus.PENDING); // או OrderStatus.CREATED לפי הצורך

         // Set supplier contact details if available
         if (supplier.getContacts() != null && !supplier.getContacts().isEmpty()) {
            // Take the first contact's phone number as default
            ContactInfo primaryContact = supplier.getContacts().get(0);
            if (primaryContact.getPhone() != null) {
               orderDTO.setContactPhoneNumber(primaryContact.getPhone());
            }
         }
         if (supplier.getAddress() != null) {
            orderDTO.setAddress(supplier.getAddress());
         }

         // Create OrderItemLineDTO objects for each product
         List<OrderItemLineDTO> items = new ArrayList<>();

         for (Map.Entry<Integer, Integer> productEntry : products.entrySet()) {
            int productId = productEntry.getKey();
            int quantity = productEntry.getValue();

            // Get product details from supplier facade
            Optional<SupplierProductDTO> productOpt = supplierFacade
                  .getSupplierProductById(supplier.getSupplierId(), productId);

            if (productOpt.isPresent()) {
               SupplierProductDTO product = productOpt.get();

               // Calculate cost and discount for this product with this supplier
               BigDecimal baseUnitPrice = product.getPrice();
               BigDecimal totalCostWithDiscount = calculateTotalCostWithTieredDiscounts(
                     supplier, productId, quantity);

               if (totalCostWithDiscount != null && baseUnitPrice != null) {

                  // Calculate total discount amount
                  BigDecimal baseTotalPrice = baseUnitPrice.multiply(BigDecimal.valueOf(quantity));
                  BigDecimal discountAmount = totalCostWithDiscount.divide(baseTotalPrice, 4, BigDecimal.ROUND_HALF_UP);

                  OrderItemLineDTO item = new OrderItemLineDTO();
                  item.setProductId(productId);
                  item.setProductName(product.getName());
                  item.setSupplierProductCatalogNumber(product.getSupplierCatalogNumber());
                  item.setQuantity(quantity);
                  item.setUnitPrice(baseUnitPrice);
                  item.setDiscount(discountAmount);

                  items.add(item);
               }
            } else {
               LOGGER.warn("Product {} not found for supplier {}", productId, supplier.getSupplierId());
            }
         }

         orderDTO.setItems(items);

         // Persist the order using the repository
         try {
            orderDTO.setOrderCatagory(orderCategory);
            OrderDTO createdOrder = ordersRepository.createRegularOrder(orderDTO);
            LOGGER.info("Successfully created order {} for supplier {} with {} items",
                  createdOrder.getOrderId(), supplier.getName(), items.size());
         } catch (Exception e) {
            LOGGER.error("Failed to persist order for supplier {}: {}",
                  supplier.getName(), e.getMessage(), e);
            throw new RuntimeException("Failed to persist order for supplier: " + supplier.getName(), e);
         }
      }
   }

   public OrderResultDTO createOrderByShortage(OrderInfoDTO infoDTO) {
      // similar to createOrder but first looking for delivery time and then cost
      if (infoDTO == null) {
         throw new IllegalArgumentException("OrderInfoDTO cannot be null");
      }

      // Combine quantities for any repeated product entries
      Map<Integer, Integer> mergedProducts = mergeProductQuantities(infoDTO.getProducts());
      if (mergedProducts.isEmpty()) {
         throw new IllegalArgumentException("No products to order after merging");
      }

      LocalDate creationDate = infoDTO.getCreationDate();
      LocalDate requestDate = infoDTO.getOrderDate();

      // FIND FEASIBLE SUPPLIERS
      List<Integer> failedProductIds = new ArrayList<>();
      Map<Integer, List<Supplier>> feasibleSuppliers = findFeasibleSuppliers(
            mergedProducts, creationDate, requestDate, failedProductIds);

      // FILTER OUT UNFULFILLABLE PRODUCTS
      Map<Integer, Integer> filteredProducts = filterFulfillableProducts(mergedProducts, failedProductIds);

      // SELECT BEST SUPPLIERS WITH FULL DATA
      Map<Integer, SupplierSelection> selections = chooseBestSuppliers(
            feasibleSuppliers, filteredProducts, creationDate);

      // GROUP PRODUCTS BY SUPPLIER FOR PERSISTENCE
      Map<Supplier, Map<Integer, Integer>> grouped = groupProductsBySupplier(selections, filteredProducts);

      // PERSIST ORDERS TO DB
      try {
         persistOrders(grouped, requestDate, creationDate, OrderCatagory.REGULAR);
      } catch (Exception ex) {
         LOGGER.error("Failed to persist orders", ex);
         throw new RuntimeException("Error persisting orders", ex);
      }

      // BUILD AND RETURN RESULT DTO
      return buildResult(mergedProducts, selections, failedProductIds);
   }

   public OrderDTO getOrderById(int orderID) {
      if (orderID <= 0) {
         throw new IllegalArgumentException("Order ID must be greater than 0");
      }
      OrderDTO order = ordersRepository.getRegularOrderById(orderID);
      if (order == null) {
         LOGGER.warn("Order not found for ID: {}", orderID);
         return null;
      }

      // Set product names and categories for order items
      List<OrderItemLineDTO> itemLines = supplierFacade
            .setProductNameAndCategoryForOrderItems(order.getItems(), order.getSupplierId());
      order.setItems(itemLines);

      // Set supplier details
      SupplierDTO supplierDTO = supplierFacade.getSupplierDTO(order.getSupplierId());
      order.setSupplierName(
            supplierDTO != null ? supplierDTO.getName() : "Unknown Supplier");
      order.setContactPhoneNumber(
            supplierFacade.getSupplierContactPhoneNumber(order.getSupplierId()));
      order.setAddress(supplierDTO != null ? supplierDTO.getAddress() : new AddressDTO());

      LOGGER.info("Retrieved order with ID: {}", orderID);
      return order;
   }

   public List<OrderDTO> getAllOrders() {
      List<OrderDTO> orders = ordersRepository.getAllRegularOrders();
      if (orders == null || orders.isEmpty()) {
         LOGGER.info("No orders found");
         return Collections.emptyList();
      }
      for (OrderDTO order : orders) {
         List<OrderItemLineDTO> itemLines = supplierFacade
               .setProductNameAndCategoryForOrderItems(order.getItems(), order.getSupplierId());
         order.setItems(itemLines);
         SupplierDTO supplierDTO = supplierFacade.getSupplierDTO(order.getSupplierId());
         order.setSupplierName(
               supplierDTO != null ? supplierDTO.getName() : "Unknown Supplier");
         order.setContactPhoneNumber(
               supplierFacade.getSupplierContactPhoneNumber(order.getSupplierId()));
         order.setAddress(supplierDTO != null ? supplierDTO.getAddress() : new AddressDTO());
      }
      LOGGER.info("Retrieved {} orders", orders.size());
      return orders;
   }

   public boolean updateOrderInfo(OrderDTO updatedOrder) {
      if (updatedOrder == null || updatedOrder.getOrderId() <= 0) {
         throw new IllegalArgumentException("Invalid OrderDTO provided for update");
      }
      OrderDTO existingOrder = ordersRepository.getRegularOrderById(updatedOrder.getOrderId());
      if (existingOrder == null) {
         throw new RuntimeException("Order not found for ID: " + updatedOrder.getOrderId());
      }

      // Update order details
      existingOrder.setSupplierId(updatedOrder.getSupplierId());
      existingOrder.setSupplierName(updatedOrder.getSupplierName());
      existingOrder.setContactPhoneNumber(updatedOrder.getContactPhoneNumber());
      existingOrder.setAddress(updatedOrder.getAddress());
      existingOrder.setItems(updatedOrder.getItems());
      existingOrder.setStatus(updatedOrder.getStatus());

      // Save updated order
      return ordersRepository.updateRegularOrder(existingOrder);
   }

   public OrderDTO updateProductsInOrder(int orderID, HashMap<Integer, Integer> productsToAdd) {
      return null; // TODO
   }

   public List<OrderDTO> getOrdersBySupplier(int supplierID) {
      return null; // TODO
   }

   public HashMap<Integer, OrderDTO> getOrdersForToday() {
      return null; // TODO
   }

   public boolean markOrderAsCompleted(int orderID) {
      if (orderID <= 0) {
         throw new IllegalArgumentException("Order ID must be greater than 0");
      }
      OrderDTO order = ordersRepository.getRegularOrderById(orderID);
      if (order == null) {
         LOGGER.warn("Order not found for ID: {}", orderID);
         return false;
      }
      if (order.getStatus() != OrderStatus.DELIVERED) {
         LOGGER.warn("Order ID {} is not in DELIVERED status, cannot mark as COMPLETED", orderID);
         return false;
      }
      order.setStatus(OrderStatus.COMPLETED);
      boolean updated = ordersRepository.updateRegularOrder(order);
      if (updated) {
         LOGGER.info("Order ID {} marked as collected successfully", orderID);
      } else {
         LOGGER.error("Failed to mark order ID {} as collected", orderID);
      }
      return updated;
   }

   public OrderDTO removeProductsFromOrder(int orderID, ArrayList<Integer> productsToRemove) {
      return null; // TODO
   }

   public List<OrderResultDTO> executePeriodicOrdersForDay(DayOfWeek dayOfWeek, List<PeriodicOrderDTO> periodicOrders) {
      if (dayOfWeek == null || periodicOrders == null || periodicOrders.isEmpty()) {
         throw new IllegalArgumentException("Day of week and periodic orders cannot be null or empty");
      }

      List<OrderResultDTO> results = new ArrayList<>();
      for (PeriodicOrderDTO periodicOrder : periodicOrders) {
         if (periodicOrder.getDeliveryDay() == dayOfWeek && periodicOrder.isActive()) {
            // Combine quantities for any repeated product entries
            Map<Integer, Integer> mergedProducts = mergeProductQuantities(periodicOrder.getProductsInOrder());
            if (mergedProducts.isEmpty()) {
               throw new IllegalArgumentException("No products to order after merging");
            }

            LocalDate creationDate = LocalDate.now();
            LocalDate requestDate = periodicOrder.getNextDeliveryDate();

            // FIND FEASIBLE SUPPLIERS
            List<Integer> failedProductIds = new ArrayList<>();
            Map<Integer, List<Supplier>> feasibleSuppliers = findFeasibleSuppliers(
                  mergedProducts, creationDate, requestDate, failedProductIds);

            // FILTER OUT UNFULFILLABLE PRODUCTS
            Map<Integer, Integer> filteredProducts = filterFulfillableProducts(mergedProducts, failedProductIds);

            // SELECT BEST SUPPLIERS WITH FULL DATA
            Map<Integer, SupplierSelection> selections = chooseBestSuppliers(
                  feasibleSuppliers, filteredProducts, creationDate);

            // GROUP PRODUCTS BY SUPPLIER FOR PERSISTENCE
            Map<Supplier, Map<Integer, Integer>> grouped = groupProductsBySupplier(selections, filteredProducts);

            // PERSIST ORDERS TO DB
            try {

               persistOrders(grouped, requestDate, creationDate, OrderCatagory.PERIODIC);
            } catch (Exception ex) {
               LOGGER.error("Failed to persist orders", ex);
               throw new RuntimeException("Error persisting orders", ex);
            }

            // BUILD AND RETURN RESULT DTO
            OrderResultDTO res = buildResult(mergedProducts, selections, failedProductIds);
            results.add(res);
         }
      }
      return results;
   }

   // #######################################################################################################################
   // Help functions
   // #######################################################################################################################

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

   public OrderDTO addOrderManually(OrderDTO orderDTO) {
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
      OrderDTO order = ordersRepository.createRegularOrder(orderDTO);
      if (order == null) {
         throw new RuntimeException("Failed to add order");
      }
      return order;
   }

   // Inner class moved to the correct location
   public static class SupplierSelection {
      private final Supplier supplier;
      private final BigDecimal totalCost;
      private final LocalDate deliveryDate;

      public SupplierSelection(Supplier supplier, BigDecimal totalCost, LocalDate deliveryDate) {
         this.supplier = supplier;
         this.totalCost = totalCost;
         this.deliveryDate = deliveryDate;
      }

      public Supplier supplier() {
         return supplier;
      }

      public BigDecimal totalCost() {
         return totalCost;
      }

      public LocalDate deliveryDate() {
         return deliveryDate;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o)
            return true;
         if (o == null || getClass() != o.getClass())
            return false;
         SupplierSelection that = (SupplierSelection) o;
         return Objects.equals(supplier, that.supplier) &&
               Objects.equals(totalCost, that.totalCost) &&
               Objects.equals(deliveryDate, that.deliveryDate);
      }

      @Override
      public int hashCode() {
         return Objects.hash(supplier, totalCost, deliveryDate);
      }

      @Override
      public String toString() {
         return "SupplierSelection{" +
               "supplier=" + supplier +
               ", totalCost=" + totalCost +
               ", deliveryDate=" + deliveryDate +
               '}';
      }
   }

   public boolean deleteOrder(int orderId) {
      LOGGER.info("Handler: deleteOrder called for ID: {}", orderId);
      boolean deleted = ordersRepository.deleteRegularOrder(orderId);
      if (deleted) {
         LOGGER.info("Handler: deleteOrder successful for ID: {}", orderId);
      } else {
         LOGGER.warn("Handler: deleteOrder failed for ID: {}", orderId);
      }
      return deleted;
   }

   public List<OrderDTO> getOrdersInDeliveredStatus() {
      List<OrderDTO> orders = ordersRepository.getOrdersByStatus(OrderStatus.DELIVERED);
      if (orders == null || orders.isEmpty()) {
         LOGGER.info("No delivered orders found");
         return Collections.emptyList();
      }
      for (OrderDTO order : orders) {
         List<OrderItemLineDTO> itemLines = supplierFacade
               .setProductNameAndCategoryForOrderItems(order.getItems(), order.getSupplierId());
         order.setItems(itemLines);
         SupplierDTO supplierDTO = supplierFacade.getSupplierDTO(order.getSupplierId());
         order.setSupplierName(
               supplierDTO != null ? supplierDTO.getName() : "Unknown Supplier");
         order.setContactPhoneNumber(
               supplierFacade.getSupplierContactPhoneNumber(order.getSupplierId()));
         order.setAddress(supplierDTO != null ? supplierDTO.getAddress() : new AddressDTO());
      }
      LOGGER.info("Retrieved {} delivered orders", orders.size());
      return orders;
   }
}

// package Suppliers.DomainLayer;
//
// import Suppliers.DTOs.*;
// import Suppliers.DTOs.Enums.InitializeState;
// import Suppliers.DTOs.Enums.OrderStatus;
// import Suppliers.DomainLayer.Classes.ContactInfo;
// import Suppliers.DomainLayer.Classes.PeriodicOrder;
// import Suppliers.DomainLayer.Classes.Supplier;
// import Suppliers.DomainLayer.Repositories.OrdersRepositoryImpl;
// import
// Suppliers.DomainLayer.Repositories.RepositoryIntefaces.OrdersRepositoryInterface;
// import Suppliers.DomainLayer.Repositories.SuppliersAgreementsRepositoryImpl;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
//
// import java.math.BigDecimal;
// import java.time.DayOfWeek;
// import java.time.LocalDate;
// import java.util.*;
//
// public class OrderHandler {
// private static final Logger LOGGER =
// LoggerFactory.getLogger(OrderHandler.class);
//
//
// private final OrdersRepositoryInterface ordersRepository;
// private final SupplierFacade supplierFacade;
//
// public OrderHandler(SupplierFacade supplierFacade, InitializeState
// initializeState) {
// this.supplierFacade = supplierFacade;
// this.ordersRepository = new OrdersRepositoryImpl();
// }
//
// public OrderDTO addOrder(OrderDTO orderDTO) {
// if (orderDTO == null) {
// throw new IllegalArgumentException("OrderDTO cannot be null");
// }
// OrderDTO createdOrder = ordersRepository.createRegularOrder(orderDTO);
// if (createdOrder == null) {
// throw new RuntimeException("Failed to create order");
// }
// return createdOrder;
// }
//
//
// /**
// * Entry point for creating orders
// */
// public OrderResultDTO createOrder(OrderInfoDTO infoDTO) {
// if (infoDTO == null) {
// throw new IllegalArgumentException("OrderInfoDTO cannot be null");
// }
//
// // Combine quantities for any repeated product entries
// Map<Integer, Integer> mergedProducts =
// mergeProductQuantities(infoDTO.getProducts());
// if (mergedProducts.isEmpty()) {
// throw new IllegalArgumentException("No products to order after merging");
// }
//
// LocalDate creationDate = infoDTO.getCreationDate();
// LocalDate requestDate = infoDTO.getOrderDate();
//
// // FIND FEASIBLE SUPPLIERS
// List<Integer> failedProductIds = new ArrayList<>();
// Map<Integer, List<Supplier>> feasibleSuppliers = findFeasibleSuppliers(
// mergedProducts, creationDate, requestDate, failedProductIds);
//
// // FILTER OUT UNFULFILLABLE PRODUCTS
// Map<Integer, Integer> filteredProducts =
// filterFulfillableProducts(mergedProducts, failedProductIds);
//
// // SELECT BEST SUPPLIERS WITH FULL DATA
// Map<Integer, SupplierSelection> selections = chooseBestSuppliers(
// feasibleSuppliers, filteredProducts, creationDate);
//
// // GROUP PRODUCTS BY SUPPLIER FOR PERSISTENCE
// Map<Supplier, Map<Integer, Integer>> grouped =
// groupProductsBySupplier(selections, filteredProducts);
//
// // PERSIST ORDERS TO DB
// try {
// persistOrders(grouped, requestDate, creationDate);
// } catch (Exception ex) {
// LOGGER.error("Failed to persist orders", ex);
// throw new RuntimeException("Error persisting orders", ex);
// }
//
// // BUILD AND RETURN RESULT DTO
// return buildResult(mergedProducts, selections, failedProductIds);
// }
//
// /**
// * Merges duplicate products by summing their quantities
// */
// private Map<Integer, Integer> mergeProductQuantities(Map<Integer, Integer>
// products) {
// Map<Integer, Integer> mergedProducts = new HashMap<>();
// for (Map.Entry<Integer, Integer> entry : products.entrySet()) {
// mergedProducts.merge(entry.getKey(), entry.getValue(), Integer::sum);
// }
// return mergedProducts;
// }
//
// /**
// * Filters out products that have no feasible suppliers
// */
// private Map<Integer, Integer> filterFulfillableProducts(
// Map<Integer, Integer> allProducts, List<Integer> failedProductIds) {
// Map<Integer, Integer> filteredProducts = new HashMap<>(allProducts);
// for (Integer pid : failedProductIds) {
// filteredProducts.remove(pid);
// }
// return filteredProducts;
// }
//
// /**
// * For each product, retrieves all suppliers and filters those
// * who can deliver on or before the requested date.
// * Products without feasible suppliers are added to failedProductIds.
// */
// private Map<Integer, List<Supplier>> findFeasibleSuppliers(
// Map<Integer, Integer> products,
// LocalDate creationDate,
// LocalDate requestDate,
// List<Integer> failedProductIds) {
//
// Map<Integer, List<Supplier>> result = new HashMap<>();
//
// for (var entry : products.entrySet()) {
// int pid = entry.getKey();
// List<Supplier> suppliers = supplierFacade.getAllSuppliersForProduct(pid);
// List<Supplier> feasible = new ArrayList<>();
//
// for (Supplier s : suppliers) {
// LocalDate delivery = getEarliestDelivery(s, creationDate);
// if (!delivery.isAfter(requestDate)) {
// feasible.add(s);
// }
// }
//
// if (feasible.isEmpty()) {
// failedProductIds.add(pid);
// } else {
// result.put(pid, feasible);
// }
// }
// return result;
// }
//
// /**
// * Chooses the best supplier for each product based on cost and delivery time
// */
// private Map<Integer, SupplierSelection> chooseBestSuppliers(
// Map<Integer, List<Supplier>> feasibleSuppliers,
// Map<Integer, Integer> products,
// LocalDate creationDate) {
//
// Map<Integer, SupplierSelection> result = new HashMap<>();
//
// for (var entry : products.entrySet()) {
// int pid = entry.getKey();
// int qty = entry.getValue();
// List<Supplier> options = feasibleSuppliers.get(pid);
//
// if (options == null || options.isEmpty()) continue;
//
// Supplier bestSupplier = null;
// BigDecimal bestCost = null;
// LocalDate bestDelivery = null;
//
// for (Supplier supplier : options) {
// BigDecimal cost = calculateTotalCostWithTieredDiscounts(supplier, pid, qty);
// LocalDate delivery = getEarliestDelivery(supplier, creationDate);
//
// if (cost == null) continue;
//
// boolean isBetter = bestSupplier == null
// || cost.compareTo(bestCost) < 0
// || (cost.compareTo(bestCost) == 0 && delivery.isBefore(bestDelivery));
//
// if (isBetter) {
// bestSupplier = supplier;
// bestCost = cost;
// bestDelivery = delivery;
// }
// }
//
// if (bestSupplier != null) {
// result.put(pid, new SupplierSelection(bestSupplier, bestCost, bestDelivery));
// }
// }
//
// return result;
// }
//
// /**
// * Groups products by their selected suppliers for order persistence
// */
// private Map<Supplier, Map<Integer, Integer>> groupProductsBySupplier(
// Map<Integer, SupplierSelection> selections,
// Map<Integer, Integer> products) {
//
// Map<Supplier, Map<Integer, Integer>> grouped = new HashMap<>();
//
// for (Map.Entry<Integer, Integer> entry : products.entrySet()) {
// int pid = entry.getKey();
// int qty = entry.getValue();
// Supplier supplier = selections.get(pid).supplier();
// grouped.computeIfAbsent(supplier, sup -> new HashMap<>()).put(pid, qty);
// }
//
// return grouped;
// }
//
// private Supplier selectBestSupplier(List<Supplier> list, int pid, int qty,
// LocalDate creationDate) {
// Supplier best = null;
// BigDecimal bestPrice = null;
// LocalDate bestDate = null;
//
// /**
// * Calculates total cost with tiered discounts based on quantity
// */
// private BigDecimal calculateTotalCostWithTieredDiscounts (
// Supplier supplier,
// int productId,
// int quantity){
//
// // 1. Fetch base unit price
// BigDecimal unitPrice = supplierFacade
// .getSupplierProductById(supplier.getSupplierId(), productId)
// .map(SupplierProductDTO::getPrice)
// .orElse(BigDecimal.ZERO);
//
// if (unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
// return null;
// }
//
// // 2. Gather and filter BoQ items for this product
// List<BillofQuantitiesItemDTO> tiers = new ArrayList<>();
// for (AgreementDTO agreement :
// supplierFacade.getAgreementsForSupplier(supplier.getSupplierId())) {
// for (BillofQuantitiesItemDTO item :
// supplierFacade.getBoQItemsForAgreement(agreement.getAgreementId())) {
// if (item.getProductId() == productId) {
// tiers.add(item);
// }
// }
// }
//
// // 3. Sort descending by min-quantity threshold
// tiers.sort(Comparator.comparingInt(BillofQuantitiesItemDTO::getQuantity).reversed());
//
// BigDecimal totalCost = BigDecimal.ZERO;
// int remaining = quantity;
//
// // 4. Apply each tier progressively
// for (BillofQuantitiesItemDTO tier : tiers) {
// int threshold = tier.getQuantity();
// BigDecimal discount = tier.getDiscountPercent() != null
// ? tier.getDiscountPercent()
// : BigDecimal.ZERO;
//
// if (remaining >= threshold) {
// int applyQty = remaining - (threshold - 1);
// BigDecimal partialCost = unitPrice
// .multiply(BigDecimal.valueOf(applyQty))
// .multiply(BigDecimal.ONE.subtract(discount));
// totalCost = totalCost.add(partialCost);
// remaining -= applyQty;
// }
// if (remaining <= 0) break;
// }
//
// // 5. Add remaining units at full price
// if (remaining > 0) {
// totalCost = totalCost.add(unitPrice.multiply(BigDecimal.valueOf(remaining)));
// }
//
// return totalCost;
// }
//
// /**
// * Calculates the earliest delivery date for a given supplier,
// * based on the order creation date, the supplier's lead time,
// * and the supplier's available delivery days.
// */
// private LocalDate getEarliestDelivery (Supplier supplier, LocalDate
// creationDate){
// LocalDate earliestDate = creationDate.plusDays(supplier.getLeadSupplyDays());
//
// // If supplier has no fixed delivery days, return date after lead time
// if (supplier.getSupplyDays() == null || supplier.getSupplyDays().isEmpty()) {
// return earliestDate;
// }
//
// // Otherwise, find the first matching day-of-week in the supplier's
// supplyDays
// while (!supplier.getSupplyDays().contains(earliestDate.getDayOfWeek())) {
// earliestDate = earliestDate.plusDays(1);
// }
//
// return earliestDate;
// }
//
// /**
// * Builds the final result DTO with successful and failed product orders
// */
// private OrderResultDTO buildResult (
// Map < Integer, Integer > allProducts,
// Map < Integer, SupplierSelection > selections,
// List < Integer > failedProductIds){
//
// List<ProductOrderSuccessDTO> successful = new ArrayList<>();
// List<ProductOrderFailureDTO> failed = new ArrayList<>();
//
// for (var entry : allProducts.entrySet()) {
// int pid = entry.getKey();
// int qty = entry.getValue();
//
// String productName = getProductName(pid);
//
// if (failedProductIds.contains(pid)) {
// failed.add(new ProductOrderFailureDTO(pid, productName, qty));
// } else {
// SupplierSelection selection = selections.get(pid);
// if (selection != null) {
// Supplier supplier = selection.supplier();
// successful.add(new ProductOrderSuccessDTO(
// pid, productName, qty, supplier.getSupplierId(), supplier.getName()));
// } else {
// // Fallback - shouldn't happen if logic is correct
// failed.add(new ProductOrderFailureDTO(pid, productName, qty));
// }
// }
// }
//
// return new OrderResultDTO(successful, failed);
// }
//
// /**
// * Helper method to get product name
// */
// private String getProductName ( int productId){
// return supplierFacade.getSupplierProductById(-1, productId)
// .map(SupplierProductDTO::getName)
// .orElse("Unknown Product");
// }
// /**
// * Persists orders to the database by creating OrderDTO objects
// * and delegating to the OrdersRepositoryImpl
// */
// private void persistOrders (
// Map < Supplier, Map < Integer, Integer >> groupedOrders,
// LocalDate requestDate,
// LocalDate creationDate){
//
// for (Map.Entry<Supplier, Map<Integer, Integer>> entry :
// groupedOrders.entrySet()) {
// Supplier supplier = entry.getKey();
// Map<Integer, Integer> products = entry.getValue();
//
// // Create OrderDTO for this supplier
// OrderDTO orderDTO = new OrderDTO();
// orderDTO.setSupplierId(supplier.getSupplierId());
// orderDTO.setSupplierName(supplier.getName());
// orderDTO.setOrderDate(requestDate);
// orderDTO.setCreationDate(creationDate);
// orderDTO.setStatus(OrderStatus.PENDING); // או OrderStatus.CREATED לפי הצורך
//
// // Set supplier contact details if available
// if (supplier.getContacts() != null && !supplier.getContacts().isEmpty()) {
// // Take the first contact's phone number as default
// ContactInfo primaryContact = supplier.getContacts().get(0);
// if (primaryContact.getPhone() != null) {
// orderDTO.setContactPhoneNumber(((ContactInfo) primaryContact).getPhone());
// }
// }
// if (supplier.getAddress() != null) {
// orderDTO.setAddress(supplier.getAddress());
// }
//
// // Create OrderItemLineDTO objects for each product
// List<OrderItemLineDTO> items = new ArrayList<>();
//
// for (Map.Entry<Integer, Integer> productEntry : products.entrySet()) {
// int productId = productEntry.getKey();
// int quantity = productEntry.getValue();
//
// // Get product details from supplier facade
// Optional<SupplierProductDTO> productOpt = supplierFacade
// .getSupplierProductById(supplier.getSupplierId(), productId);
//
// if (productOpt.isPresent()) {
// SupplierProductDTO product = productOpt.get();
//
// // Calculate cost and discount for this product with this supplier
// BigDecimal baseUnitPrice = product.getPrice();
// BigDecimal totalCostWithDiscount = calculateTotalCostWithTieredDiscounts(
// supplier, productId, quantity);
//
// if (totalCostWithDiscount != null && baseUnitPrice != null) {
// // Calculate effective unit price after discounts
// BigDecimal effectiveUnitPrice = totalCostWithDiscount
// .divide(BigDecimal.valueOf(quantity), 2, BigDecimal.ROUND_HALF_UP);
//
// // Calculate total discount amount
// BigDecimal baseTotalPrice =
// baseUnitPrice.multiply(BigDecimal.valueOf(quantity));
// BigDecimal discountAmount = baseTotalPrice.subtract(totalCostWithDiscount);
//
// OrderItemLineDTO item = new OrderItemLineDTO();
// item.setProductId(productId);
// item.setProductName(product.getName());
// item.setSupplierProductCatalogNumber(product.getSupplierCatalogNumber());
// item.setQuantity(quantity);
// item.setUnitPrice(effectiveUnitPrice);
// item.setDiscount(discountAmount);
//
// items.add(item);
// }
// } else {
// LOGGER.warn("Product {} not found for supplier {}", productId,
// supplier.getSupplierId());
// }
// }
//
// orderDTO.setItems(items);
//
// // Persist the order using the repository
// try {
// OrderDTO createdOrder = ordersRepository.createRegularOrder(orderDTO);
// LOGGER.info("Successfully created order {} for supplier {} with {} items",
// createdOrder.getOrderId(), supplier.getName(), items.size());
// } catch (Exception e) {
// LOGGER.error("Failed to persist order for supplier {}: {}",
// supplier.getName(), e.getMessage(), e);
// throw new RuntimeException("Failed to persist order for supplier: " +
// supplier.getName(), e);
// }
// }
// }
//
// public OrderResultDTO createOrderByShortage (Map < Integer, Integer > pOrder)
// {
// return null; //TODO
// }
// public OrderDTO getOrderById ( int orderID){
// return null; //TODO
// }
//
// public List<OrderDTO> getAllOrders () {
// return null; //TODO
// }
//
// public OrderDTO updateOrderInfo (OrderDTO updatedOrder){
// return null; //TODO
// }
//
// public OrderDTO updateProductsInOrder ( int orderID, HashMap<Integer, Integer
// > productsToAdd){
// return null; //TODO
//
// }
//
// public List<OrderDTO> getOrdersBySupplier ( int supplierID){
// return null; //TODO
//
// }
//
// public HashMap<Integer, OrderDTO> getOrdersForToday () {
// return null; //TODO
//
// }
//
// public OrderDTO markOrderAsCollected ( int orderID){
// return null; //TODO
//
// }
//
// public OrderDTO removeProductsFromOrder ( int orderID, ArrayList<Integer >
// productsToRemove){
// return null; //TODO
//
// }
//
// public List<OrderResultDTO> executePeriodicOrdersForDay (DayOfWeek day, List
// < PeriodicOrder > periodicOrders){
// return null; //TODO
//
//
// }
//
// //#######################################################################################################################
//// Help functions
//// #######################################################################################################################
//
// private Map<Integer, Integer> filterProductsThatDontHaveSupplier (Map <
// Integer, Integer > productsAndAmount){
// if (productsAndAmount == null || productsAndAmount.isEmpty()) {
// throw new IllegalArgumentException("Products and amount cannot be null or
// empty");
// }
// List<CatalogProductDTO> catalogProducts =
// SuppliersAgreementsRepositoryImpl.getInstance().getCatalogProducts();
// Map<Integer, Integer> filteredProducts = new HashMap<>();
// for (Map.Entry<Integer, Integer> entry : productsAndAmount.entrySet()) {
// int productId = entry.getKey();
// if (catalogProducts.stream().anyMatch(p -> p.getProductId() == productId)) {
// filteredProducts.put(productId, entry.getValue());
// } else {
// LOGGER.warn("Product ID {} not found in catalog, skipping", productId);
// }
// }
// return filteredProducts;
// }
//
// private List<OrderItemLineDTO> filterItemsThatSupplierDoesntHave (List <
// OrderItemLineDTO > items,int supplierId)
// {
// if (items == null || items.isEmpty()) {
// throw new IllegalArgumentException("Items cannot be null or empty");
// }
// List<Integer> supplierProducts = SuppliersAgreementsRepositoryImpl
// .getInstance().getAllProductsForSupplierId(supplierId);
// if (supplierProducts == null || supplierProducts.isEmpty()) {
// throw new IllegalArgumentException("No products found for supplier ID: " +
// supplierId);
// }
// List<OrderItemLineDTO> filteredItems = new ArrayList<>();
// for (OrderItemLineDTO item : items) {
// if (supplierProducts.contains(item.getProductId())) {
// filteredItems.add(item);
// } else {
// LOGGER.warn("Product ID {} not found for supplier ID {}, removing from
// order", item.getProductId(),
// supplierId);
// }
// }
// return filteredItems;
// }
//
//
// public OrderDTO addOrderManually (OrderDTO orderDTO){
// if (orderDTO == null) {
// throw new IllegalArgumentException("OrderDTO cannot be null");
// }
// // Validate that all products exist in the supplier's catalog
// List<OrderItemLineDTO> filteredProducts =
// filterItemsThatSupplierDoesntHave(orderDTO.getItems(),
// orderDTO.getSupplierId());
// if (filteredProducts.isEmpty()) {
// LOGGER.warn("No valid products found for the order. Please check the product
// IDs.");
// return null;
// }
//
// filteredProducts = supplierFacade
// .setProductNameAndCategoryForOrderItems(filteredProducts,
// orderDTO.getSupplierId());
// filteredProducts = supplierFacade
// .setSupplierPricesAndDiscountsByBestPrice(filteredProducts,
// orderDTO.getSupplierId());
// orderDTO.setItems(filteredProducts);
// orderDTO.setSupplierName(
// supplierFacade.getSupplierDTO(orderDTO.getSupplierId()).getName());
// OrderDTO order = ordersRepository.createRegularOrder(orderDTO);
// if (order == null) {
// throw new RuntimeException("Failed to add order");
// }
// return order;
// }
//
//
//
// public static class SupplierSelection {
// private final Supplier supplier;
// private final BigDecimal totalCost;
// private final LocalDate deliveryDate;
//
// public SupplierSelection(Supplier supplier, BigDecimal totalCost, LocalDate
// deliveryDate) {
// this.supplier = supplier;
// this.totalCost = totalCost;
// this.deliveryDate = deliveryDate;
// }
//
// public Supplier supplier() {
// return supplier;
// }
//
// public BigDecimal totalCost() {
// return totalCost;
// }
//
// public LocalDate deliveryDate() {
// return deliveryDate;
// }
//
// @Override
// public boolean equals(Object o) {
// if (this == o) return true;
// if (o == null || getClass() != o.getClass()) return false;
// SupplierSelection that = (SupplierSelection) o;
// return Objects.equals(supplier, that.supplier) &&
// Objects.equals(totalCost, that.totalCost) &&
// Objects.equals(deliveryDate, that.deliveryDate);
// }
//
// @Override
// public int hashCode() {
// return Objects.hash(supplier, totalCost, deliveryDate);
// }
//
// @Override
// public String toString() {
// return "SupplierSelection{" +
// "supplier=" + supplier +
// ", totalCost=" + totalCost +
// ", deliveryDate=" + deliveryDate +
// '}';
// }
// }
//
// }
// }
