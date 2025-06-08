package Suppliers.DomainLayer;

import Suppliers.DTOs.*;
import Suppliers.DomainLayer.Classes.Order;
import Suppliers.DomainLayer.Classes.PeriodicOrder;
import Suppliers.DomainLayer.Repositories.OrdersRepositoryImpl;
import Suppliers.DomainLayer.Repositories.RepositoryIntefaces.OrdersRepositoryInterface;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import Suppliers.DomainLayer.Classes.Supplier;
import Suppliers.DomainLayer.Repositories.SuppliersAgreementsRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderHandler {
   private static final Logger LOGGER = LoggerFactory.getLogger(OrderFacade.class);

   private  final OrdersRepositoryInterface ordersRepository ;
   private final SupplierFacade supplierFacade;

   public OrderHandler(SupplierFacade supplierFacade) {
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

   public OrderResultDTO createOrder(OrderInfoDTO infoDTO)  {
      Map<Integer, Integer> products = infoDTO.getProducts();
      LocalDate creationDate = infoDTO.getCreationDate();
      LocalDate requestDate = infoDTO.getOrderDate();

      Map<Integer, List<Supplier>> feasible = findFeasibleSuppliers(products, creationDate, requestDate);
      Map<Integer, Supplier> chosen = chooseBestSuppliers(feasible, products, creationDate);
      Map<Supplier, Map<Integer, Integer>> grouped = groupBySupplier(chosen, products);
      persistOrders(grouped, requestDate, creationDate);

      return buildResult(products, chosen);
   }

   private Map<Integer, List<Supplier>> findFeasibleSuppliers(Map<Integer, Integer> products,
                                                              LocalDate creationDate,
                                                              LocalDate requestDate) {
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
         result.put(pid, feasible);
      }
      return result;
   }
   private Map<Integer, Supplier> chooseBestSuppliers(Map<Integer, List<Supplier>> feasible,
                                                      Map<Integer, Integer> products,
                                                      LocalDate creationDate) {
      Map<Integer, Supplier> chosen = new HashMap<>();
      for (var entry : products.entrySet()) {
         int pid = entry.getKey();
         int qty = entry.getValue();
         List<Supplier> options = feasible.get(pid);
         if (options != null && !options.isEmpty()) {
            chosen.put(pid, selectBestSupplier(options, pid, qty, creationDate));
         }
      }
      return chosen;
   }
   private Supplier selectBestSupplier(List<Supplier> list, int pid, int qty, LocalDate creationDate) {
      Supplier best = null;
      BigDecimal bestPrice = null;
      LocalDate bestDate = null;

      for (Supplier s : list) {
         BigDecimal price = calculateTotalPrice(s, pid, qty);
         LocalDate date = getEarliestDelivery(s, creationDate);

         if (best == null || price.compareTo(bestPrice) < 0 ||
                 (price.compareTo(bestPrice) == 0 && date.isBefore(bestDate))) {
            best = s;
            bestPrice = price;
            bestDate = date;
         }
      }
      return best;
   }
   private BigDecimal calculateTotalPrice(Supplier s, int productId, int quantity) {
      BigDecimal unitPrice = getUnitPrice(s, productId);
      BigDecimal discount = findBestDiscount(s.getSupplierId(), productId, quantity);
      BigDecimal qty = BigDecimal.valueOf(quantity);
      return unitPrice.multiply(qty).multiply(BigDecimal.ONE.subtract(discount));
   }

   private BigDecimal getUnitPrice(Supplier s, int productId) {
      return supplierFacade.getSupplierProductById(s.getSupplierId(), productId)
              .map(SupplierProductDTO::getPrice)
              .orElse(BigDecimal.ZERO);
   }
   private BigDecimal findBestDiscount(int supplierId, int productId, int quantity) {
      BigDecimal best = BigDecimal.ZERO;
      for (AgreementDTO ag : supplierFacade.getAgreementsForSupplier(supplierId)) {
         for (BillofQuantitiesItemDTO item : supplierFacade.getBoQItemsForAgreement(ag.getAgreementId())) {
            if (item.getProductId() == productId && quantity >= item.getQuantity()) {
               best = best.max(item.getDiscountPercent());
            }
         }
      }
      return best;
   }
   private LocalDate getEarliestDelivery(Supplier s, LocalDate creationDate) {
      LocalDate date = creationDate.plusDays(s.getLeadSupplyDays());
      while (!s.getSupplyDays().contains(date.getDayOfWeek())) {
         date = date.plusDays(1);
      }
      return date;
   }
   private Map<Supplier, Map<Integer, Integer>> groupBySupplier(Map<Integer, Supplier> chosen,
                                                                Map<Integer, Integer> products) {
      Map<Supplier, Map<Integer, Integer>> result = new HashMap<>();
      for (var entry : chosen.entrySet()) {
         Supplier s = entry.getValue();
         result.computeIfAbsent(s, k -> new HashMap<>())
                 .put(entry.getKey(), products.get(entry.getKey()));
      }
      return result;
   }
   private void persistOrders(Map<Supplier, Map<Integer, Integer>> grouped,
                              LocalDate requestDate,
                              LocalDate creationDate) throws SQLException {
      for (var entry : grouped.entrySet()) {
         Supplier s = entry.getKey();
         Order o = new Order();
         o.setSupplierId(s.getSupplierId());
         o.setSupplierName(s.getName());
         o.setOrderDate(requestDate);
         o.setCreationDate(creationDate);
         o.setAddress(s.getAddress());
         o.setContactPhoneNumber(s.getPhoneNumber());
         int orderId = orderRepository.save(o);
         for (var item : entry.getValue().entrySet()) {
            int productId = item.getKey();
            int quantity = item.getValue();
            SupplierProduct sp = s.getProduct(productId);
            if (sp == null)
               throw new IllegalStateException("Supplier " + s.getSupplierId() + " does not offer product " + productId);

            BigDecimal unitPrice = sp.getPrice();
            int catalogNumber = sp.getCatalogNumber();
            String productName = sp.getProductName();

            o.addItem(productId, quantity, unitPrice, catalogNumber, productName);
         }
         OrdersRepositoryImpl.update(o);
      }
   }


//   private void persistOrders(Map<Supplier, Map<Integer, Integer>> grouped,
//                              LocalDate requestDate,
//                              LocalDate creationDate) throws SQLException {
//      for (var entry : grouped.entrySet()) {
//         Supplier s = entry.getKey();
//         Order o = new Order(s.getSupplierId(), requestDate, creationDate);
//         for (var item : entry.getValue().entrySet()) {
//            o.addItem(item.getKey(), item.getValue());
//         }
//         OrdersRepositoryImpl.save(o);
//      }
//   }
   private OrderResultDTO buildResult(Map<Integer, Integer> products, Map<Integer, Supplier> chosen) {
      List<ProductOrderSuccessDTO> success = new ArrayList<>();
      List<ProductOrderFailureDTO> failure = new ArrayList<>();

      for (var entry : products.entrySet()) {
         int pid = entry.getKey();
         int qty = entry.getValue();

         Supplier s = chosen.get(pid);
         String name = supplierFacade.getSupplierProductById(s != null ? s.getSupplierId() : -1, pid)
                 .map(SupplierProductDTO::getName)
                 .orElse("Unknown");

         if (s != null) {
            success.add(new ProductOrderSuccessDTO(pid, name, qty, s.getSupplierId(), s.getName()));
         } else {
            failure.add(new ProductOrderFailureDTO(pid, name, qty));
         }
      }

      return new OrderResultDTO(success, failure);
   }

   public  OrderResultDTO createOrderByShortage (Map<Integer, Integer> pOrder)
   {
      return null; //TODO
   }
    public OrderDTO getOrderById(int orderID) {
      return null ; //TODO
    }

   public List<OrderDTO> getAllOrders() {
      return null; //TODO 
   }

   public OrderDTO updateOrderInfo(OrderDTO updatedOrder) {
      return null; //TODO 
   }

   public OrderDTO updateProductsInOrder(int orderID, HashMap<Integer, Integer> productsToAdd) {
   }

   public List<OrderDTO> getOrdersBySupplier(int supplierID) {
   }

   public HashMap<Integer, OrderDTO> getOrdersForToday() {
   }

   public OrderDTO markOrderAsCollected(int orderID) {
   }

   public OrderDTO removeProductsFromOrder(int orderID, ArrayList<Integer> productsToRemove) {
   }

   public List<OrderResultDTO> executePeriodicOrdersForDay(DayOfWeek day ,List<PeriodicOrder> periodicOrders) {


   }

   //#######################################################################################################################
//                                        Help functions
//#######################################################################################################################

   private Map<Integer, Integer> filterProductsThatDontHaveSupplier (Map < Integer, Integer > productsAndAmount){
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

   private List<OrderItemLineDTO> filterItemsThatSupplierDoesntHave (List < OrderItemLineDTO > items,int supplierId)
   {
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
      OrderDTO order = orderController.addOrder(orderDTO);
      if (order == null) {
         throw new RuntimeException("Failed to add order");
      }
      return order;
   }
}
