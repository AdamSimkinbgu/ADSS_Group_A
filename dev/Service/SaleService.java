package Service;

import java.util.Date;
import java.util.HashMap;

public class SaleService {
    int saleID;
    int salePrice;
    final Date saleTime;
    HashMap<Integer,Integer> products; //// hashmap(saleID,saleQuantity) ////

    public int getSaleID() {
        return saleID;
    }
    public int getSalePrice() {
        return salePrice;
    }
    public Date getSaleTime() {
        return saleTime;
    }
    public HashMap<Integer, Integer> getProducts() {
        return products;
    }

    public SaleService(int id, int price, Date saleT, HashMap<Integer,Integer> prod){
        saleID = id;
        salePrice = price;
        saleTime = saleT;
        products = new HashMap<>(prod);
    }
}
