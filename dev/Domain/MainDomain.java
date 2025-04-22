package Domain;


import type.Position;

import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainDomain {
    private int productCounter;
    private int supplyCounter;
    private HashMap<Integer, ProductDomain> prodMap;
    private List<DiscountDomain> activeDisLst;
    private List<DiscountDomain> pastDisLst;
    private List<SaleDomain> saleLst;
    private List<CategoryDomain> categoryLst;

    public MainDomain() {
        productCounter = 0;
        supplyCounter = 0;
        prodMap = new HashMap<>();
        activeDisLst = new ArrayList<>();
        pastDisLst = new ArrayList<>();
        saleLst = new ArrayList<>();
        categoryLst = new ArrayList<>();
    }

    public void InventoryInitialization(){
        //todo
    }

    public void UpdateInventoryRestock(int pId, int quantity, ChronoLocalDate ex){
        SupplyDomain sd = new SupplyDomain(supplyCounter++,quantity,ex);
        prodMap.get(pId).AddSupply(sd);
        //todo
    }

    public void UpdateInventorySale(SaleDomain sld){
        //todo
    }

    public String GetMissingReport(){
        StringBuilder ret = new StringBuilder("=====Missing/Bad Report=====\n");

        for (ProductDomain p : prodMap.values()) {
            int badnum = p.GetBads();
            if (badnum > 0) {
                ret.append(p.getproductName())
                        .append(": ")
                        .append(badnum)
                        .append(" \n");
            }
        }
        return ret.toString();
        //todo check if working
    }

    /*
    * report a bad product
    *
    * */
    public void AddBadProduct(int pId,int quantity){
        //todo
    }

    public String GetBadReport(){
        return "";
        //todo
    }

    public String GetCurrentInventoryReport(){
        return "";
        //todo
    }

    /*
    * Move Product to a new shelf
    *
    * @param pId the product id number
    * @param SOrW set to true if you want to change self in the store and false for change in the warehouse
    * @param newP the new shelf
    * */
    public void MoveProduct(int pId, boolean SOrW, Position newP){
        //todo
    }

    /*
    * Search by Product id number
    * */
    public String Search(int pId){
        return "";
        //todo
    }

    public String Search(String name){
        return "";
        //todo
    }



    public void AddDiscount(){
        //todo
    }

    public void AddCategory(){
        //todo
    }
}
