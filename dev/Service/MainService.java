package Service;

import DTO.*;
import Domain.MainDomain;
import Domain.OrderDeliverdDomian;
import Domain.ProductDomain;
import Domain.SaleDomain;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import type.Position;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.List;

public class MainService {
    private MainDomain md;
    private ObjectMapper om;

    public MainService(){
        om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        md = new MainDomain();
        md.InventoryInitialization();
    }


    //???
    public String AddProduct(String message ){
        try{
            ProductService p = om.readValue(message,ProductService.class);
            Position sS = new Position(p.storeLine,p.storeShelf);
            Position whS = new Position(p.wareHouseLane,p.wareHouseShelf);
            int pId = md.AddProduct(p.productName,p.manufacturerName,p.minimalAmoutStore,p.minimalAmoutStock,p.productPrice,sS,whS);
            return "Product added successfully. \nproduct id:"+pId;
        }
        catch (IllegalArgumentException e){
            return e.getMessage();
        }
        catch (Exception e){
            return "Error converting JSON to product at service: " + e.getMessage();
        }
    }

    public String MoveOrder(){
        try{
            md.UpdateInventoryRestock();
            return "Orders Moved successfully.";
        }
        catch (IllegalArgumentException e){
            return e.getMessage();
        }
        catch (Exception e){
            return "Error converting JSON to product at service: " + e.getMessage();
        }
    }

    public String AddSale(String json){
        try {
            SaleService sS = om.readValue(json, SaleService.class);

            SaleDomain sD = new SaleDomain(sS.getProducts());
            sD = md.UpdateInventorySale(sD);
            sS = new SaleService(sD);
            return om.writeValueAsString(sS);
        }catch (IllegalArgumentException e){
            return "Problem with the list: " + e.getMessage();
        }catch (Exception e){
            return "Json problem: " + e.getMessage();
        }
    }

    public String AddDiscount(String json){
        int pid = 0;
        try{
            DiscountDTO d = om.readValue(json,DiscountDTO.class);
            md.AddDiscount(d);
            return "Discount Added successfully";
        }
        catch (Exception e){
            return "Unable to add discount: " + e.getMessage();
        }
    }

    public String AddNewCategory(String name){
        try{
            md.AddCategory(name);
            return "category crated successfully";
        }catch (IllegalArgumentException e){
            return "Failed to crate category:" + e.getMessage();
        }
    }

    public String AddToCategory(String catName, int pId){
        try{
            md.AddToCategory(catName,pId);
            return "Item added to category" + catName;
        }catch (IllegalArgumentException e){
            return "Failed to add item to the category: " + e.getMessage();
        }
        //todo
    }

    public String AddToCategory(String catName, String subCat){
        try{
            md.AddToCategory(catName,subCat);
            return "sub-category added to category" + catName;
        }catch (IllegalArgumentException e){
            return "Failed to add item to the category: " + e.getMessage();
        }
        //todo
    }

    public String MoveProduct(int pid , boolean SOW, Position p){
        try{
            md.MoveProduct(pid,SOW,p);
            return "Product moved successfully";
        }catch (IllegalArgumentException e){
            return "Failed to move product: " + e.getMessage();
        }
    }

    public String AddBadProduct(int pId, int quantity){
        try{
            return md.AddBadProduct(pId,quantity);
        }catch (IllegalArgumentException e){
            return "Failed to report bad product: " + e.getMessage();
        }
    }

    public String MissingReport(){
        return md.GetMissingReport();
    }

    public String BadReport(){
        return md.GetBadReport();
    }

    public String Search(int pId){
        try {
            ProductService p = new ProductService(md.Search(pId));
            return om.writeValueAsString(p);
        }catch (IllegalArgumentException e){
            return "Search failed: " + e.getMessage();
        }catch (Exception e){
            return e.getMessage();
        }
    }

    public String GetCurrentReport(){
        return md.GetCurrentInventoryReport();
    }



    public String GetProductLst(){
        ArrayList<ProductDTO> ls = new ArrayList<>();
        //todo call the get Catalog func

        ls = md.cleanCatalog(ls);

        try{
            return om.writeValueAsString(ls);
        }
        catch (Exception e){
            return "Error";
        }
    }


    public String AddRecurringOrder(int pId, int quantity,int day){

        //todo call supply func
        return "done?";
    }

    //todo
    public String AddMissingOrder(){
        List<SupplyDTO> Orders = md.AddMissingOrder();

        //todo call supply func with orders

        return "done?";

    }


    //Get called by Supplier Domain
    public String DeliverOrder(List<SupplyDTO> ls){
        //todo ?? build ls

        md.DeliverOrder(ls);

        return "done";
    }
}
