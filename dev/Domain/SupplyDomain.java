package Domain;

import java.util.Date;

public class SupplyDomain {

    private int id;
    private int quantityWarehouse;
    private int quantityStore;
    private Date expierDate;
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
        return quantityBad;
    }
    public Date getExpierDate() {
        return expierDate;
    }
}
