package Domain;

import java.util.Date;
import java.util.HashMap;

public class SaleDomain {
    private HashMap<Integer,Integer> itemLs;   ////items<product id , quantity>
    int saleID;
    private int price;
    private Date saleTime;

    public int getSaleID() {
        return saleID;
    }
    public int getPrice() {
        return price;
    }
    public Date getSaleTime() {
        return saleTime;
    }
    public HashMap<Integer, Integer> getItemLs() {
        return itemLs;
    }
}

