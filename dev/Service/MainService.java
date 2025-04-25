package Service;

import Domain.MainDomain;
import Domain.ProductDomain;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import type.Position;

import java.time.chrono.ChronoLocalDate;

public class MainService {
    private MainDomain md;
    private ObjectMapper om;

    public MainService(){
        om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        md = new MainDomain();
        md.InventoryInitialization();
    }

    public String AddProduct(String message ){
        try{
            ProductService p = om.readValue(message,ProductService.class);
            md.AddProduct(p.productName,p.manufacturerName,p.minimalAmoutStore,p.minimalAmoutStock,p.productPrice,p.storeShelf,p.wareHouseShelf);
            return "Product added successfully.";
        }
        catch (IllegalArgumentException e){
            return e.getMessage();
        }
        catch (Exception e){
            return "Error converting JSON to product at service: " + e.getMessage();
        }
    }

    public String AddSupply(int pId , int quantity , ChronoLocalDate ex){
        try{
            //SupplyService sup = om.readValue(json,SupplyService.class);
            md.UpdateInventoryRestock(pId, quantity, ex);
            return "Supply added successfully.";
        }
        catch (IllegalArgumentException e){
            return e.getMessage();
        }
        catch (Exception e){
            return "Error converting JSON to product at service: " + e.getMessage();
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
}
