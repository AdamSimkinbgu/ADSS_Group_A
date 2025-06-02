package Inventory.DTO;


import Inventory.Domain.ProductDomain;
import Inventory.type.Position;

public class ProductDTO {

    private int productId;
    private String productName;
    private String manufacturerName;
    private float productPrice;
    private int minimalAmountStore;
    private int minimalAmountStock;
    private Position wareHouseShelf;
    private Position storeShelf;

    // getters
    public int getproductId() {
        return productId;
    }

    public String getproductName() {
        return productName;
    }

    public String getmanufacturerName() {
        return manufacturerName;
    }

    public float getproductPrice() {
        return productPrice;
    }

    public int getminimalAmountStore() {
        return minimalAmountStore;
    }

    public int getminimalAmountStock() {
        return minimalAmountStock;
    }

    public Position getwareHouseShelf() {
        return wareHouseShelf;
    }

    public Position getstoreShelf() {
        return storeShelf;
    }

    // Setters
    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    public void setProductPrice(float productPrice) {
        this.productPrice = productPrice;
    }

    public void setMinimalAmountStore(int minimalAmountStore) {
        this.minimalAmountStore = minimalAmountStore;
    }

    public void setMinimalAmountStock(int minimalAmountStock) {
        this.minimalAmountStock = minimalAmountStock;
    }

    public void setWareHouseShelf(Position wareHouseShelf) {
        this.wareHouseShelf = wareHouseShelf;
    }

    public void setStoreShelf(Position storeShelf) {
        this.storeShelf = storeShelf;
    }

    public ProductDTO() {
    }

    public ProductDTO(int pId, String pName, String MfName) {
        productId = pId;
        productName = pName;
        manufacturerName = MfName;

    }

    public ProductDTO(int pId, String pName, String MfName, int minAStore, int minAStock, float PPrice, Position s,
            Position w) {
        productId = pId;
        productName = pName;
        manufacturerName = MfName;
        productPrice = PPrice;
        this.minimalAmountStock = minAStock;
        this.minimalAmountStore = minAStore;
        this.storeShelf = s;
        this.wareHouseShelf = w;
    }

    public ProductDTO(ProductDomain other) {
        productId = other.getproductID();
        productName = other.getproductName();
        productPrice = other.getproductPrice();
        manufacturerName = other.getmanufactuerName();
        minimalAmountStore = other.getminimalAmountStore();
        minimalAmountStock = other.getminimalAmountStock();
        wareHouseShelf = other.getwareHouseShelf();
        storeShelf = other.getstoreShelf();

    }

}
