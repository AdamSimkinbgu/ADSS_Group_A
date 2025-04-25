package Service;

import Domain.MainDomain;
import Domain.ProductDomain;
import com.fasterxml.jackson.databind.ObjectMapper;
import type.Position;

public class MainService {
    private MainDomain md;
    private ObjectMapper om;

    public MainService(){
        om = new ObjectMapper();
        md = new MainDomain();
        md.InventoryInitialization();
    }

    public String AddProduct(String message ){
        try{
            ProductService p = om.readValue(message,ProductService.class);
            md.AddProduct(p.productName,p.manufacturerName,p.minimalAmoutStore,p.minimalAmoutStock,p.productPrice,p.storeShalf,p.wareHouseShelf);
            return "Product added successfully.";
        }
        catch (IllegalArgumentException e){
            return e.getMessage();
        }
        catch (Exception e){
            return "Error converting JSON to product: " + e.getMessage();
        }
    }
}
