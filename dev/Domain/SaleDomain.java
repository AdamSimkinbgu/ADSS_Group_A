package Domain;

import java.util.Date;
import java.util.HashMap;

public class SaleDomain {
    private HashMap<Integer,Integer> itemLs;   ////items<product id , quantity>
    int saleID;
    private int salePrice;
    private Date saleTime;

    public int getSaleID() {
        return saleID;
    }
    public int getPrice() {
        return salePrice;
    }
    public Date getSaleTime() {
        return saleTime;
    }
    public HashMap<Integer, Integer> getItemLs() {
        return itemLs;
    }

    public SaleDomain(int id,int price,Date time,HashMap<Integer,Integer> itms){
        saleID = id;
        salePrice = price;
        saleTime = time;
        itemLs = new HashMap<>(itms);
    }
}

