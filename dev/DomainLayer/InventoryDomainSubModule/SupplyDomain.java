package DomainLayer.InventoryDomainSubModule;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;

import DTOs.InventoryModuleDTOs.SupplyDTO;

public class SupplyDomain {

    private int id;
    private int quantityWarehouse;
    private int quantityStore;
    private LocalDate expierDate;
    private int quantityBad;

    public int getId() {
        return id;
    }

    public int getQuantityWarehouse() {
        return quantityWarehouse;
    }

    public int getQuantityStore() {
        return quantityStore;
    }

    public int getQuantityBad() {
        IsEx();
        return quantityBad;
    }

    public LocalDate getExpierDate() {
        return expierDate;
    }

    public SupplyDomain() {
    }

    public SupplyDomain(int Id, int qw, LocalDate ex) {
        id = Id;
        quantityWarehouse = qw;
        quantityStore = 0;
        quantityBad = 0;
        expierDate = ex;
    }

    public SupplyDomain(SupplyDTO other) {
        id = other.getsId();
        quantityWarehouse = other.getQuantityWH();
        quantityStore = other.getquantityS();
        quantityBad = other.getquantityB();
        expierDate = other.getExpireDate();
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setQuantityWarehouse(int quantityWarehouse) {
        this.quantityWarehouse = quantityWarehouse;
    }

    public void setQuantityStore(int quantityStore) {
        this.quantityStore = quantityStore;
    }

    public void setQuantityBad(int quantityBad) {
        this.quantityBad = quantityBad;
    }

    public void setExpierDate(LocalDate expierDate) {
        this.expierDate = expierDate;
    }

    /*
     * check if the expaer date is due and move all supply to bad if it does
     */
    public boolean IsEx() {
        if (LocalDate.now().isAfter(expierDate)) {
            quantityBad += quantityWarehouse;
            quantityBad += quantityStore;
            quantityWarehouse = 0;
            quantityStore = 0;
        }
        if (quantityWarehouse == 0 && quantityStore == 0)
            return true;
        return false;
    }

    public int Buy(int quantity) {
        if (quantity < quantityStore) {
            quantityStore -= quantity;
            return 0;
        } else {
            quantity -= quantityStore;
            quantityStore = 0;
            return quantity;
        }
    }

    public int restock(int quantity) {
        if (quantity <= quantityWarehouse) {
            quantityWarehouse -= quantity;
            quantityStore += quantity;
            return 0;
        } else {
            quantity -= quantityWarehouse;
            quantityStore += quantityWarehouse;
            quantityWarehouse = 0;
            return quantity;
        }
    }

    public int Report(int quantity) {
        if (quantity < quantityWarehouse) {
            quantityWarehouse -= quantity;
            quantityBad += quantity;
            return 0;
        } else {
            quantity -= quantityWarehouse;
            quantityBad += quantityWarehouse;
            quantityWarehouse = 0;
            if (quantity < quantityStore) {
                quantityStore -= quantity;
                quantityBad += quantity;
                return 0;
            } else {
                quantity -= quantityStore;
                quantityBad += quantityStore;
                quantityStore = 0;
                return quantity;
            }
        }
    }
}
