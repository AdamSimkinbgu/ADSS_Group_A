package Suppliers.DTOs;

import Suppliers.DomainLayer.Classes.PeriodicOrder;

import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.Map;

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
    public PeriodicOrderDTO (DayOfWeek arrivalDate, HashMap<Integer, Integer> productsInOrder, boolean isActive) {
        this.periodicOrderID = 0;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(
                "PeriodicOrder [%4d]  DeliveryDay: %-8s  Active: %s%n",
                periodicOrderID,
                (deliveryDay != null ? deliveryDay.name() : "N/A"),
                isActive ? "Yes" : "No"));
        sb.append("  ─────────────────────────────────────\n");

        if (productsInOrder == null || productsInOrder.isEmpty()) {
            sb.append("  [No products in periodic order]\n");
        } else {
            sb.append("  Products & Quantities:\n");
            for (Map.Entry<Integer, Integer> entry : productsInOrder.entrySet()) {
                sb.append(String.format(
                        "    ProdID: %4d  Qty: %3d%n",
                        entry.getKey(),
                        entry.getValue()));
            }
            sb.append(String.format(
                    "  (%d product%s total)%n",
                    productsInOrder.size(),
                    productsInOrder.size() == 1 ? "" : "s"));
        }

        sb.append("==============================================\n");
        return sb.toString();
    }
}
