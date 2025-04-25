package Domain;


import type.Position;

import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
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
        ProductDomain p = new ProductDomain(productCounter,"Milk","Tara",8,20,(float)7.5,new Position(3,7),new Position(4,5));
        prodMap.put(productCounter,p);
        productCounter++;
        //todo
    }

    //VVVVVV
    public void AddProduct(String pName,String MfName, int MAStore, int MAStock, float Price,Position SShalf,Position WHShelf){
        for(ProductDomain p: prodMap.values()){
            if(p.getproductName().equals(pName))throw new IllegalArgumentException("Product name alredy in stock");
        }
        prodMap.put(productCounter,new ProductDomain(productCounter,pName,MfName,MAStore,MAStock,Price,SShalf,WHShelf));

    }
    //VVVVVV
    public void UpdateInventoryRestock(int pId, int quantity, ChronoLocalDate ex){
        if(!prodMap.containsKey(pId)){
            throw new IllegalArgumentException("no product with this ip");
        }
        SupplyDomain sd = new SupplyDomain(supplyCounter++,quantity,ex);
        prodMap.get(pId).AddSupply(sd);
        //todo
    }

    public void UpdateInventorySale(SaleDomain sld){
        //todo
    }

    //v
    public String GetMissingReport(){
        StringBuilder ret = new StringBuilder("=====Missing Report=====\n");
        int missNum = 0;
        for (ProductDomain p : prodMap.values()) {
            missNum = p.GetMissing();
            if (missNum > 0) {
                ret.append(p.getproductName())
                        .append(": ")
                        .append(missNum)
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

    //v
    public String GetBadReport(){
        StringBuilder ret = new StringBuilder("=====Bad Report=====\n");
        int badNum = 0;
        for (ProductDomain p : prodMap.values()) {
            badNum = p.GetBads();
            if (badNum > 0) {
                ret.append(p.getproductName())
                        .append(": ")
                        .append(badNum)
                        .append(" \n");
            }
        }
        return ret.toString();
        //todo check if working
    }

    public String GetCurrentInventoryReport(){
        return "";
        //todo
    }

    //VVVVVV
    /*
    * Move Product to a new shelf
    *
    * @param pId the product id number
    * @param SOrW set to true if you want to change self in the store and false for change in the warehouse
    * @param newP the new shelf
    * */
    public void MoveProduct(int pId, boolean SOrW, Position newP){
        if(!prodMap.containsKey(pId))throw new IllegalArgumentException("pId invalid");

        prodMap.get(pId).moveProudct(SOrW, newP);

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

    //VVVVVV
    public void AddCategory(String newName){
        for(CategoryDomain c : categoryLst){
            if(c.getName().equals(newName))throw new IllegalArgumentException("Name already used.");
        }
        categoryLst.add(new CategoryDomain(newName));
        int i =0;
    }

    public void AddToCategory(String catName,int pId){

        if(!prodMap.containsKey(pId))throw new IllegalArgumentException("invalid product id");

        for(CategoryDomain c : categoryLst){
            if(c.Isin(catName)){
                c.InsertPID(catName,pId);
                return;
            }
        }
        throw new IllegalArgumentException("There is no category by that name");

    }

    public void AddToCategory(String catName,String subCat){

    }
}
