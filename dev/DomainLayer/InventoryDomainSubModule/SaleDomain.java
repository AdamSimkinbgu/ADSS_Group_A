package DomainLayer.InventoryDomainSubModule;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;

import DTOs.InventoryModuleDTOs.SaleDTO;

public class SaleDomain {
    private HashMap<Integer, Integer> itemLs;
    //// items<product id , quantity>
    int saleID;
    private float salePrice;
    private LocalDate saleTime;

    public int getSaleID() {
        return saleID;
    }

    public float getPrice() {
        return salePrice;
    }

    public LocalDate getSaleTime() {
        return saleTime;
    }

    public HashMap<Integer, Integer> getItemLs() {
        return itemLs;
    }

    public SaleDomain() {
    }

    public SaleDomain(HashMap<Integer, Integer> items) {
        saleID = 0;
        salePrice = 0;
        saleTime = LocalDate.now();
        itemLs = new HashMap<>(items);
    }

    public SaleDomain(SaleDTO other) {
        salePrice = other.getSalePrice();
        saleTime = other.getdate();
        itemLs = other.getProducts();
    }

    public void setSaleID(int saleID) {
        this.saleID = saleID;
    }

    public void setSalePrice(float salePrice) {
        this.salePrice = salePrice;
    }
}
