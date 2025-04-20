package Domain;

import java.util.List;

public class ProductDomain {
    private int productID;
    private String productName;
    private String manufactuerName;
    private int minimalAmountStore;
    private int minimalAmountStock;
    private float productPrice1unit;
    private String wareHouseShelf;
    private String storeShelf;
    private List<SupplyDomain> supplyList; //// list of supplies ////


    public int getproductID() { return productID;}
    public String getproductName() { return productName;}
    public String getmanufactuerName() { return manufactuerName;}
    public int getminimalAmountStore() { return minimalAmountStore;}
    public int getminimalAmountStock() { return minimalAmountStock;}
    public float getproductPrice1unit() { return productPrice1unit;}
    public String getstoreShelf() { return storeShelf;}
    public String getwareHouseShelf() { return wareHouseShelf;}

    public ProductDomain(int pID,String pName,String MfName, int MAStore, int MAStock, float PPrice,String SShalf,String WHShelf){
        productID = pID;
        productName = pName;
        manufactuerName = MfName;
        minimalAmountStore = MAStore;
        minimalAmountStock = MAStock;
        productPrice1unit = PPrice;
        storeShelf = SShalf;
        wareHouseShelf = WHShelf;

}
