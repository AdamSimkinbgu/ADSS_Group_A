package Inventory.Service;

import Inventory.Domain.SupplyDomain;

import java.time.chrono.ChronoLocalDate;
import java.util.Date;

public class SupplyService {
    int supplyID;
    int productID;
    int quantityWarehouse;
    int quantityStore;
    ChronoLocalDate expireDate;
    int quantityBad;

    public int getSupplyID() {
        return supplyID;
    }

    public int getProductID() {
        return productID;
    }

    public int getQuantityWarehouse() {
        return quantityWarehouse;
    }

    public int getQuantityStore() {
        return quantityStore;
    }

    public int getQuantityBad() {
        return quantityBad;
    }

    public ChronoLocalDate getExpireDate() {
        return expireDate;
    }

    public SupplyService() {
    }

    public SupplyService(int id, int qW, ChronoLocalDate ex) {
        supplyID = id;
        productID = 0;
        quantityWarehouse = qW;
        quantityStore = 0;
        quantityBad = 0;
        expireDate = ex;
    }

    public SupplyService(SupplyDomain other, int otherId) {
        supplyID = other.getId();
        productID = otherId;
        quantityWarehouse = other.getQuantityWarehouse();
        quantityStore = other.getQuantityStore();
        quantityBad = other.getQuantityBad();
        expireDate = other.getExpierDate();
    }

}
