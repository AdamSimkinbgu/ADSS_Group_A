package Service;

import Domain.ProductDomain;
import type.Position;

public class ProductService {
    final int productId;
    String productName;
    String manufacturerName;
    int minimalAmoutStore;
    int minimalAmoutStock;
    float productPrice;
    Position storeShalf;
    Position wareHouseShelf;


    public int getproductId() { return productId;}
    public String getproductName() { return productName;}
    public String getmanufacturerName() { return manufacturerName;}
    public int getminimalAmoutStore() { return minimalAmoutStore;}
    public int getminimalAmoutStock() { return minimalAmoutStock;}
    public float getproductPrice() { return productPrice;}
    public Position getstoreShalf() { return storeShalf;}
    public Position getwareHouseShelf() { return wareHouseShelf;}

    public ProductService(String pName,String MfName, int MAStore, int MAStock, float PPrice,Position SShalf,Position WHShelf){
        productId = 0;
        productName = pName;
        manufacturerName = MfName;
        minimalAmoutStore = MAStore;
        minimalAmoutStock = MAStock;
        productPrice = PPrice;
        storeShalf = SShalf;
        wareHouseShelf = WHShelf;
    }
    public ProductService(ProductDomain other){
        productId = other.getproductID();
        productName = other.getproductName();
        productPrice = other.getproductPrice1unit();
        manufacturerName = other.getmanufactuerName();
        minimalAmoutStock = other.getminimalAmountStock();
        minimalAmoutStore = other.getminimalAmountStore();

        storeShalf = getstoreShalf();
        wareHouseShelf = getwareHouseShelf();
    }

}
