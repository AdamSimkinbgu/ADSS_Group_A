package Domain;

import type.Position;

import java.util.List;

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
        //todo
    }

    public void MoveSupply(Position ItemPosition){
        //todo
    }

    public String GetMissing(){
        return ""
        //todo
    }

    public String GetBads(){
        return ""
        //todo
    }

    public String GetCurrentInventory(){
        return ""
        //todo
    }

    
    
}