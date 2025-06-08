package Suppliers.DTOs;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Data Transfer Object representing order information,
 * including order dates and a mapping of product IDs to their quantities.
 */

public class OrderInfoDTO {

    private LocalDate orderDate; // The date when the order was placed.
    private LocalDate creationDate; // The date when this order record was created in the system.
    private HashMap<Integer, Integer> products; // A map where the key is the product ID and the value is the quantity
                                                // ordered.

    public OrderInfoDTO(LocalDate orderDate, HashMap<Integer, Integer> items) {
        this.orderDate = orderDate;
        this.creationDate = LocalDate.now();
        this.products = items;
    }

    public OrderInfoDTO(OrderDTO existingOrder) {
        this.orderDate = existingOrder.getOrderDate();
        this.creationDate = existingOrder.getCreationDate();
        this.products = new HashMap<>();
        for (OrderItemLineDTO item : existingOrder.getItems()) {
            this.products.put(item.getProductId(), item.getQuantity());
        }
    }

    public HashMap<Integer, Integer> getProducts() {
        return products;
    }

    public void setProducts(HashMap<Integer, Integer> products) {
        this.products = products;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    @Override
    public String toString() {
        return String.format(
                "OrderInfo[orderDate=%s, created=%s, items=%s]",
                orderDate,
                creationDate,
                products.entrySet()
                        .stream()
                        .map(e -> e.getKey() + "×" + e.getValue())
                        .collect(Collectors.joining(", ", "{", "}")));
    }
}