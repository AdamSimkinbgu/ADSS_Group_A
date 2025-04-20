package Service;

import java.util.Date;
import java.util.HashMap;

public class SaleService {
    int saleID;
    int price;
    Date saleTime;
    HashMap<String,Integer> products; //// hashmap(saleID,saleQuantity) ////

    public int getSaleID() {
        return saleID;
    }
    public int getPrice() {
        return price;
    }
    public Date getSaleTime() {
        return saleTime;
    }
    public HashMap<String, Integer> getProducts() {
        return products;
    }
}
