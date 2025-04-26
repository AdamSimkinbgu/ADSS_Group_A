package Service;

import Domain.SaleDomain;

import java.util.Date;
import java.util.HashMap;

public class SaleService {
    int saleID;
    float salePrice;

    HashMap<Integer,Integer> products; //// hashmap(saleID,saleQuantity) ////

    public int getSaleID() {
        return saleID;
    }
    public float getSalePrice() {
        return salePrice;
    }
    public HashMap<Integer, Integer> getProducts() {
        return products;
    }

    public SaleService(){}

    public SaleService(HashMap<Integer,Integer> prod){
        saleID = 0;
        salePrice = 0;
        products = new HashMap<>(prod);
    }

    public SaleService(SaleDomain other){
        saleID = other.getSaleID();
        salePrice = other.getPrice();
        products = new HashMap<>();

        for(Integer i : other.getItemLs().keySet())products.put(i,other.getItemLs().get(i));
    }
}
