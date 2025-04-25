package Domain;

import type.Position;

import java.util.ArrayList;
import java.util.List;

import java.util.Comparator;

public class ProductDomain {
    private int productID;
    private String productName;
    private String manufactuerName;
    private int minimalAmountStore;
    private int minimalAmountStock;
    private float productPrice1unit;
    private Position wareHouseShelf;
    private Position storeShelf;
    private List<SupplyDomain> supplyList; //// list of supplies ////


    public int getproductID() {
        return productID;
    }
    public String getproductName() {
        return productName;
    }
    public String getmanufactuerName() {
        return manufactuerName;
    }
    public int getminimalAmountStore() {
        return minimalAmountStore;
    }
    public int getminimalAmountStock() {
        return minimalAmountStock;
    }
    public float getproductPrice1unit() {
        return productPrice1unit;
    }
    public Position getstoreShelf() {
        return storeShelf;
    }
    public Position getwareHouseShelf() {
        return wareHouseShelf;
    }


    public ProductDomain(int pID, String pName, String MfName, int MAStore, int MAStock, float PPrice, Position SShalf, Position WHShelf) {
        productID = pID;
        productName = pName;
        manufactuerName = MfName;
        minimalAmountStore = MAStore;
        minimalAmountStock = MAStock;
        productPrice1unit = PPrice;
        storeShelf = SShalf;
        wareHouseShelf = WHShelf;
        supplyList = new ArrayList<>();
    }

    //setters 
    public void setwareHouseShelf(Position wareHouseShelf) {
        this.wareHouseShelf = wareHouseShelf;
    }
    public void setstoreShelf(Position storeShelf) {
        this.storeShelf = storeShelf;
    }
    public void setminimalAmountStore(int minimalAmountStore){
        this.minimalAmountStore = minimalAmountStore;
    }
    public void setminimalAmountStock(int minimalAmountStock){
        this.minimalAmountStock = minimalAmountStock;
    }


    //////////////////////////////////////////////////////////////////////////////////


    public void AddSupply(SupplyDomain supply){
        supplyList.add(supply);
        supplyList.sort(Comparator.comparing(SupplyDomain::getExpierDate));
    }

    public void reStockStore(){
        int all_supp_instore = 0;
        for (SupplyDomain supply: supplyList){
            supply.IsEx();
            all_supp_instore += supply.getQuantityStore();
        }
        if (all_supp_instore < minimalAmountStore){
            int quant = 2*minimalAmountStore - all_supp_instore;
            for(SupplyDomain supp : supplyList){
                quant = supp.restock(quant);
                if(quant == 0)break;
            }
        }
    }

    public void moveProudct(boolean sOrW, Position newP){
        if(sOrW)storeShelf = newP;
        else wareHouseShelf = newP;
    }

    public int GetMissing(){
        int TotalInStore = 0;
        int ret = 0;
        for (SupplyDomain supply: supplyList){
            TotalInStore += supply.getQuantityStore();
            TotalInStore += supply.getQuantityWarehouse();
            }
        if(TotalInStore < minimalAmountStock){
            ret = minimalAmountStock - TotalInStore;
        }

        reStockStore();
        return ret;
        //todo
    }

    public int GetBads(){
        int Totalbad = 0;
        for (SupplyDomain supply: supplyList){
            Totalbad += supply.getQuantityBad();
            }
        reStockStore();
        return Totalbad;
        }
    

    public String GetCurrentInventory(){
        int totalInStore = 0;
        int totalInWarehouse = 0;
        for (SupplyDomain supply : supplyList) {
            totalInStore += supply.getQuantityStore();
            totalInWarehouse += supply.getQuantityWarehouse();
        }
        return "Product: " + getproductName() + " (ID: " + getproductID() + ")\n"
                + "In Store: " + totalInStore + "\n"
                + "In Warehouse: " + totalInWarehouse + "\n"
                + "Bad Units: " + GetBads() + "\n"
                + "Missing from Store: " + GetMissing() + "\n";
    }
}