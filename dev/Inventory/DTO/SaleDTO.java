package Inventory.DTO;

import Inventory.Domain.SaleDomain;

import java.time.LocalDate;
import java.util.HashMap;

public class SaleDTO {
    int id;
    float salePrice;
    LocalDate date;
    HashMap<Integer, Integer> products; //// hashmap(ID,saleQuantity) ////

    // getters
    public float getSalePrice() {
        return salePrice;
    }

    public HashMap<Integer, Integer> getProducts() {
        return products;
    }

    public int getId() {
        return id;
    }

    public LocalDate getdate() {
        return date;
    }

    // setters
    public void setId(int id) {
        this.id = id;
    }

    public void setsalePrice(float salePrice) {
        this.salePrice = salePrice;
    }

    public void setdate(LocalDate date) {
        this.date = date;
    }

    public void setproducts(HashMap<Integer, Integer> products) {
        this.products = new HashMap<>();
        for (Integer i : products.keySet())
            this.products.put(i, products.get(i));
    }

    public SaleDTO() {
    }

    public SaleDTO(HashMap<Integer, Integer> prod) {
        id = -1;
        salePrice = 0;
        date = LocalDate.now();
        products = new HashMap<>(prod);
    }

    public SaleDTO(SaleDomain other) {
        salePrice = other.getPrice();
        date = other.getSaleTime();
        id = other.getSaleID();

        products = new HashMap<>();
        for (Integer i : other.getItemLs().keySet())
            products.put(i, other.getItemLs().get(i));
    }
}
