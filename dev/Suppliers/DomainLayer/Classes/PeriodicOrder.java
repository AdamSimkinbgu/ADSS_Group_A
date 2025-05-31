package Suppliers.DomainLayer.Classes;

import Suppliers.DTOs.PeriodicOrderDTO;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.util.HashMap;

/**
 * Represents a periodic order template requested by the warehouse.
 * This order is executed automatically a day before the specified delivery day,
 * creating a regular order with the best matching supplier.
 */
public class PeriodicOrder implements Serializable {
    private int periodicOrderID;
    private DayOfWeek deliveryDay;
    private HashMap<Integer, Integer> productsInOrder;
    private boolean isActive;

    /**
     * Constructor that initializes a periodic order with an ID and delivery day,
     * and creates an empty product list.
     * The order is set to active by default.
     *
     * @param periodicOrderID the ID of the periodic order
     * @param arrivalDate     the day of the week when the warehouse wants delivery
     */
    public PeriodicOrder(int periodicOrderID, DayOfWeek arrivalDate) {
        this.periodicOrderID = periodicOrderID;
        this.deliveryDay = arrivalDate;
        this.productsInOrder = new HashMap<>();
        this.isActive = true;
    }

    /**
     * Full constructor.
     *
     * @param periodicOrderID the ID of the periodic order
     * @param arrivalDate     the day of the week when the warehouse wants delivery
     * @param productsInOrder map of product IDs to their quantities
     */
    public PeriodicOrder(int periodicOrderID, DayOfWeek arrivalDate, HashMap<Integer, Integer> productsInOrder) {
        this.periodicOrderID = periodicOrderID;
        this.deliveryDay = arrivalDate;
        this.productsInOrder = productsInOrder;
        this.isActive = true;
    }

    /**
     * Constructs a periodic order from a DTO (Data Transfer Object).
     *
     * @param periodicOrderDTO the DTO containing the periodic order's data
     */
    public PeriodicOrder(PeriodicOrderDTO periodicOrderDTO) {
        this.periodicOrderID = periodicOrderDTO.getPeriodicOrderID();
        this.deliveryDay = periodicOrderDTO.getDeliveryDay();
        this.productsInOrder = periodicOrderDTO.getProductsInOrder();
        this.isActive = true;
    }

    /**
     * Returns the ID of the periodic order.
     *
     * @return the order ID
     */
    public int getPeriodicOrderID() {
        return periodicOrderID;
    }

    /**
     * Sets the ID of the periodic order.
     *
     * @param periodicOrderID the new order ID
     */
    public void setPeriodicOrderID(int periodicOrderID) {
        this.periodicOrderID = periodicOrderID;
    }

    /**
     * Returns the list of products and quantities in the periodic order.
     *
     * @return map of product IDs to quantities
     */
    public HashMap<Integer, Integer> getProductsInOrder() {
        return productsInOrder;
    }

    /**
     * Sets the list of products and quantities for the periodic order.
     *
     * @param productsInOrder map of product IDs to quantities
     */
    public void setProductsInOrder(HashMap<Integer, Integer> productsInOrder) {
        this.productsInOrder = productsInOrder;
    }

    /**
     * Returns the day of the week the warehouse wants the order delivered.
     *
     * @return the delivery day
     */
    public DayOfWeek getDeliveryDay() {
        return deliveryDay;
    }

    /**
     * Sets the day of the week the warehouse wants the order delivered.
     *
     * @param deliveryDay the new delivery day
     */
    public void setDeliveryDay(DayOfWeek deliveryDay) {
        this.deliveryDay = deliveryDay;
    }

    /**
     * Returns whether the periodic order is currently active.
     *
     * @return true if the order is active, false otherwise
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Sets the active status of the periodic order.
     *
     * @param active true to activate the order, false to deactivate
     */
    public void setActive(boolean active) {
        isActive = active;
    }
}
