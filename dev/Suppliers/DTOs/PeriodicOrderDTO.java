package Suppliers.DTOs;

import Suppliers.DomainLayer.Classes.PeriodicOrder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;

public class PeriodicOrderDTO {
    private int periodicOrderID;
    private DayOfWeek deliveryDay;
    private HashMap<Integer, Integer> productsInOrder;
    private boolean isActive;

    public PeriodicOrderDTO() {
    }

    public PeriodicOrderDTO(int periodicOrderID, DayOfWeek arrivalDate, HashMap<Integer, Integer> productsInOrder,
            boolean isActive) {
        this.periodicOrderID = periodicOrderID;
        this.deliveryDay = arrivalDate;
        this.productsInOrder = productsInOrder;
        this.isActive = isActive;

    }

    public PeriodicOrderDTO(PeriodicOrder periodicOrder) {
        this.periodicOrderID = periodicOrder.getPeriodicOrderID();
        this.deliveryDay = periodicOrder.getDeliveryDay();
        this.productsInOrder = periodicOrder.getProductsInOrder();
        this.isActive = periodicOrder.isActive();
    }

    public int getPeriodicOrderID() {
        return periodicOrderID;
    }

    public void setPeriodicOrderID(int periodicOrderID) {
        this.periodicOrderID = periodicOrderID;
    }

    public DayOfWeek getDeliveryDay() {
        return deliveryDay;
    }

    public void setDeliveryDay(DayOfWeek deliveryDay) {
        this.deliveryDay = deliveryDay;
    }

    public HashMap<Integer, Integer> getProductsInOrder() {
        return productsInOrder;
    }

    public void setProductsInOrder(HashMap<Integer, Integer> productsInOrder) {
        this.productsInOrder = productsInOrder;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getDeliveryDayMask() {
        // this is a 7 character string representing the days of the week with '1' for
        // active days and '0' for inactive days
        StringBuilder mask = new StringBuilder("0000000");
        if (deliveryDay != null) {
            // we want sunday to be the first character, so we need to adjust the index
            int dayIndex = (deliveryDay.getValue() % 7); // DayOfWeek.SUNDAY is 7, we want it to be 0
            mask.setCharAt(dayIndex, '1');
        }
        return mask.toString();
    }
}
