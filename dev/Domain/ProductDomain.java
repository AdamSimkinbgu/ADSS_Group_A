package Domain;

import type.Position;

import java.util.List;

import java.time.LocalDate;

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
    }

    public void reStock(Position ItemPosition){
        int all_supp_instore = 0;
        for (SupplyDomain supply: supplyList){
            suplly.IsEx();
            all_supp_instore += suplly.getquantityStore();
        }
        if (all_supp_instore < minimalAmountStore){
            suplly.setQuantityWarehouse((-(2*minimalAmountStore)))
            supplly.setQuantityStore(2*minimalAmountStore)

            }
            
            

        }
    }

    

    public int GetMissing(){
        int TotalInStore = 0;
        for (SupplyDomain supply: supplyList){
            TotalInStore += supply.getQuantityStore();
            TotalInStore += supply.getQuantityWarehouse();
            }
        if(TotalInStore < minimalAmountStock){
            return minimalAmountStore - TotalInStore;
        }
        else return 0;
        //todo
    }

    public int GetBads(){
        int Totalbad = 0;
        for (SupplyDomain supply: supplyList){
            Totalbad += supply.getQuantityBad();
            }
        return Totalbad;
        //todo restock
        }
    

    public String GetCurrentInventory(){
        int totalInStore = 0;
        int totalInWarehouse = 0;
        String all_curr_inv = "";
        for (SupplyDomain supply : supplyList) {
            totalInStore += supply.getQuantityStore();
            totalInWarehouse += supply.getQuantityWarehouse();
            all_curr_inv += "Product: " + getproductName() + " (ID: " + getproductID() + ")\n"
                + "In Store: " + totalInStore + "\n"
                + "In Warehouse: " + totalInWarehouse + "\n"
                + "Bad Units: " + GetBads() + "\n"
                + "Missing from Store: " + GetMissing() + "\n";
        }
        return all_curr_inv;

    

}