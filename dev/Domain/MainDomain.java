package Domain;


import DAO.*;
import DTO.CategoryDTO;
import DTO.DiscountDTO;
import DTO.ProductDTO;
import DTO.SupplyDTO;
import type.Position;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainDomain {
    private int productCounter;
    private int supplyCounter;
    private int saleCounter;

    //Data Acsis Object
    private ProductDAO Pdao;
    private OrderDeliverdDAO ODdao;
    private DiscountDAO Ddao;
    private CategoryDAO Cdao;
    private SaleDAO Sdao;

    private HashMap<Integer, ProductDomain> prodMap;
    private List<DiscountDomain> disLst;
    private List<SaleDomain> saleLst;
    private List<CategoryDomain> categoryLst;
    private List<OrderDeliverdDomian> orders;

    public MainDomain() {
        productCounter = 0;
        supplyCounter = 0;
        saleCounter = 0;
        prodMap = new HashMap<>();
        disLst = new ArrayList<>();
        saleLst = new ArrayList<>();
        categoryLst = new ArrayList<>();
        orders = new ArrayList<>();
    }

    //todo all
    public void InventoryInitialization(){

        //uplode product
        List<ProductDTO> pls = Pdao.GetAll();
        for(ProductDTO p : pls){
            prodMap.put(p.getproductId(),new ProductDomain(p));
        }

        //uplode orders
        orders = ODdao.GetAll();

        //uplode category
        List<CategoryDTO> cls = Cdao.getAll();
        for(CategoryDTO c: cls){
            categoryLst.add(new CategoryDomain(c));
        }

        List<DiscountDTO> dls = Ddao.getAll();
        for(DiscountDTO d :dls){
            DiscountDomain dis = new DiscountDomain(d);
            if(d.getpId() != -1){
                prodMap.get(d.getpId()).AddDiscount(dis);
            }
            else {
                for(CategoryDomain c: categoryLst){
                    if(c.Isin(d.getCatName())){
                        //todo add to the database
                        c.AddDiscount(dis, d.getCatName());
                        break;
                    }
                }
            }
            disLst.add(dis);
        }


        //todo
    }

    //VVVVVV
    public int AddProduct(String pName,String MfName, int MAStore, int MAStock, float Price,Position SShalf,Position WHShelf){
        for(ProductDomain p: prodMap.values()){
            if(p.getproductName().equals(pName))throw new IllegalArgumentException("Product name alredy in stock");
        }

        //todo add to database

        prodMap.put(productCounter,new ProductDomain(productCounter,pName,MfName,MAStore,MAStock,Price,SShalf,WHShelf));
        productCounter++;
        return productCounter - 1;
    }

    //todo check
    public void UpdateInventoryRestock(){
        SupplyDomain s;
        for(OrderDeliverdDomian o : orders){
            //todo add to database
            s = new SupplyDomain(supplyCounter++,o.getQuantity(),o.getExDate());
            prodMap.get(o.getpId()).AddSupply(s);
        }
    }

    //VVVVVV
    public SaleDomain UpdateInventorySale(SaleDomain s){
        for(Integer pIg : s.getItemLs().keySet()){
            if(!prodMap.containsKey(pIg))throw new IllegalArgumentException("invalid product id");
        }


        float discount;
        float price = 0;
        for(Integer pIg : s.getItemLs().keySet()){
            discount = 0;
            for(CategoryDomain c: categoryLst)discount += c.getDiscount(pIg);
            //price += product price after discount * number of sed product
            price += prodMap.get(pIg).getproductPrice1unit(discount) * s.getItemLs().get(pIg);
            prodMap.get(pIg).Buy(s.getItemLs().get(pIg));
        }

        s.setSalePrice(price);
        s.setSaleID(saleCounter++);
        saleLst.add(s);
        return s;
    }

    //VVVVVV
    public String GetMissingReport(){
        StringBuilder ret = new StringBuilder("=====Missing Report=====\n");
        int missNum = 0;
        for (ProductDomain p : prodMap.values()){
            missNum = p.GetMissing();
            if (missNum > 0) {
                ret.append(p.getproductID())
                        .append(", ")
                        .append(p.getmanufactuerName())
                        .append(": ")
                        .append(missNum)
                        .append(" \n");
            }
        }
        return ret.toString();
    }

    //VVVVVV
    /*
    * report a bad product
    *
    * */
    public String AddBadProduct(int pId,int quantity){
        if(!prodMap.containsKey(pId))throw new IllegalArgumentException("invalid product id");

        int missing = prodMap.get(pId).ReportBad(quantity);
        if(missing > 0){
            return "Bad Product Reported \nWarning!!! minimal amount retched";
        }
        else return "Bad Product Reported";
    }

    //VVVVVV
    public String GetBadReport(){
        StringBuilder ret = new StringBuilder("=====Bad Report=====\n");
        int badNum = 0;
        for (ProductDomain p : prodMap.values()) {
            badNum = p.GetBads();
            if (badNum > 0) {
                ret.append(p.getproductName())
                        .append(", ")
                        .append(p.getmanufactuerName())
                        .append(": ")
                        .append(badNum)
                        .append(" \n");
            }
        }
        return ret.toString();

    }

    public String GetCurrentInventoryReport(){
        StringBuilder table = new StringBuilder();


        table.append("=====Current Inventory Report=====\n");
        table.append(String.format("%-15s %-10s %-10s%n", "Id", "Name", "Quantity"));
        table.append("----------------------------------------\n");
        for (ProductDomain p:prodMap.values()){
            table.append(String.format("%-15d %-10s %-10d%n", p.getproductID(), p.getproductName(), p.getQuantity()));
        }
        return table.toString();

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

        prodMap.get(pId).moveProduct(SOrW, newP);

    }

    //VVVVVV
    /*
    * Search by Product id number
    * */
    public ProductDomain Search(int pId){
        if(!prodMap.containsKey(pId))throw new IllegalArgumentException("Invalid product id");

        return prodMap.get(pId);
        //todo
    }

    //VVVVVV
    public void AddDiscount(DiscountDTO dis){
        if(dis.getpId()!=-1) {

            if (!prodMap.containsKey(dis.getpId())) throw new IllegalArgumentException("pId invalid");

            DiscountDomain d = new DiscountDomain(dis.getPercent(),dis.getDiscountEnd());

            //todo save to the data base

            prodMap.get(dis.getpId()).AddDiscount(d);
            disLst.add(d);
        }
        else {
            DiscountDomain d = new DiscountDomain(dis.getPercent(),dis.getDiscountEnd());

            for(CategoryDomain c: categoryLst){
                if(c.Isin(dis.getCatName())){
                    //todo add to the database
                    c.AddDiscount(d, dis.getCatName());
                    disLst.add(d);
                    return;
                }
            }
            throw new IllegalArgumentException("No category by that name");
        }

    }

    //VVVVVV
    public void AddCategory(String newName){
        for(CategoryDomain c : categoryLst){
            if(c.getName().equals(newName))throw new IllegalArgumentException("Name already used.");
        }
        categoryLst.add(new CategoryDomain(newName));
        int i =0;
    }

    //VVVVVV
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

    //VVVVVV
    public void AddToCategory(String catName,String subCat){
        boolean flag = false;
        CategoryDomain sub = new CategoryDomain("");
        for(CategoryDomain c : categoryLst){
            if(c.Isin(subCat)){
                flag = true;
                if(c.getName().equals(subCat)){
                    sub = c;
                    categoryLst.remove(c);
                }else {
                    sub = c.remove(subCat);
                }
                break;
            }
        }
        if(!flag)throw new IllegalArgumentException("There is no category by that name");


        for(CategoryDomain c : categoryLst){
            if(c.Isin(catName)){
                c.InsertSub(catName,sub);
                return;
            }
        }
        throw new IllegalArgumentException("There is no category by that name");
    }



    //todo
    public List<SupplyDTO> AddMissingOrder(){
        int missingA;
        List<SupplyDTO> ls = new ArrayList<>();
        for(ProductDomain p:prodMap.values()){
            missingA = p.GetMissing();
            if(missingA > 0){
                ls.add(new SupplyDTO(p.getproductID(),missingA,LocalDate.now()));
            }
        }

        return ls;

    }

    public void DeliverOrder(List<SupplyDTO> ls){
        for(SupplyDTO s:ls){
            orders.add(new OrderDeliverdDomian(s));
        }
    }

    public ArrayList<ProductDTO> cleanCatalog(ArrayList<ProductDTO> ls){
        for (ProductDTO p : ls){
            if(prodMap.containsKey(p.getproductId())){
                ls.remove(p);
            }
        }

        return ls;
    }

    public boolean DoesProdExist(int pid){
        return prodMap.containsKey(pid);
    }
}
