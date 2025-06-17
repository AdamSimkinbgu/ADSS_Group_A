package Inventory.Service;

import Inventory.Domain.ProductDomain;
import com.fasterxml.jackson.annotation.JsonIgnore;
import Inventory.type.Position;

public class ProductService {
    int productId;
    String productName;
    String manufacturerName;
    int minimalAmoutStore;
    int minimalAmoutStock;
    int quantity;
    int badQuantity;
    float productPrice;
    // @JsonIgnore
    int storeShelf;
    int storeLine;
    // @JsonIgnore
    int wareHouseShelf;
    int wareHouseLane;
    // @JsonIgnore

    public int getproductId() {
        return productId;
    }

    public String getproductName() {
        return productName;
    }

    public String getmanufacturerName() {
        return manufacturerName;
    }

    public int getminimalAmoutStore() {
        return minimalAmoutStore;
    }

    public int getminimalAmoutStock() {
        return minimalAmoutStock;
    }

    public float getproductPrice() {
        return productPrice;
    }

    // public Position getstoreShalf() { return storeShelf;}
    // public Position getwareHouseShelf() { return wareHouseShelf;}
    public int getQuantity() {
        return quantity;
    }

    public int getBadQuantity() {
        return badQuantity;
    }

    public ProductService() {
    }

    public ProductService(String pName, String MfName, int MAStore, int MAStock, float PPrice, int SShalf, int SLine,
            int WHShelf, int WHLine) {
        productId = 0;
        productName = pName;
        manufacturerName = MfName;
        minimalAmoutStore = MAStore;
        minimalAmoutStock = MAStock;
        productPrice = PPrice;
        storeShelf = SShalf;
        storeLine = SLine;
        wareHouseShelf = WHShelf;
        wareHouseLane = WHLine;
    }

    public ProductService(ProductDomain other) {
        productId = other.getproductID();
        productName = other.getproductName();
        productPrice = other.getproductPrice();
        manufacturerName = other.getmanufactuerName();
        minimalAmoutStock = other.getminimalAmountStock();
        minimalAmoutStore = other.getminimalAmountStore();
        // storeShelf = getstoreShalf();
        // wareHouseShelf = getwareHouseShelf();
        quantity = other.getQuantity();
        badQuantity = other.getBadQantity();
    }

}
