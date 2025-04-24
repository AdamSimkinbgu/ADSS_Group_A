package Domain;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;


public class SupplyDomain {

    private int id;
    private int quantityWarehouse;
    private int quantityStore;
    private ChronoLocalDate expierDate;
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
    public ChronoLocalDate getExpierDate() {
        return expierDate;
    }

    public SupplyDomain(int Id,int qw, ChronoLocalDate ex){
        id=Id;
        quantityWarehouse = qw;
        quantityStore = 0;
        quantityBad = 0;
        expierDate = ex;
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

    public void setExpierDate(ChronoLocalDate expierDate) {
        this.expierDate = expierDate;
    }

    /*
    * check if the expaer date is due and move all supply to bad if it does
    * */
    public boolean IsEx(){
        if(LocalDate.now().isAfter(expierDate)) {
            quantityBad += quantityWarehouse;
            quantityBad += quantityStore;
            quantityWarehouse = 0;
            quantityStore = 0;
        }
        if(quantityWarehouse == 0 && quantityStore ==0)return true;
        return false;
    }

    public int restock(int quantity){
        if(quantity <= quantityWarehouse){
            quantityWarehouse -= quantity;
            quantityStore +=quantity;
            return 0;
        }
        else{
            quantity -=quantityWarehouse;
            quantityStore += quantityWarehouse;
            quantityWarehouse = 0;
            return quantity;
        }
    }
}
