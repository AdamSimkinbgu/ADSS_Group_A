package Inventory.Service;

import Inventory.DTO.*;
import Inventory.Domain.MainDomain;
import Inventory.Domain.SaleDomain;
import Suppliers.DTOs.Enums.InitializeState;
import Suppliers.DTOs.OrderPackageDTO;
import Suppliers.ServiceLayer.IntegrationService;
import Suppliers.ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import Suppliers.DTOs.CatalogProductDTO;

import Inventory.type.Position;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainService {
    private MainDomain md;
    private ObjectMapper om;
    private IntegrationService is;

    private static MainService mainServiceInstance;

    private MainService() {
        om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        md = new MainDomain();
        md.InventoryInitialization(0);
    }


    public  void Initialize(InitializeState input) {
        //todo
        md.InventoryInitialization(input);
    }

    public static MainService GetInstance() {
        if (mainServiceInstance == null)
            mainServiceInstance = new MainService();
        return mainServiceInstance;
    }

    public boolean SetIntegrationService() {
        if (is != null)
            return false;
        is = IntegrationService.getIntegrationServiceInstance();
        return true;
    }

    // ???
    public String AddProduct(String message) {
        try {
            ProductDTO p = om.readValue(message, ProductDTO.class);
            int pId = md.AddProduct(p);

            // order the product
            HashMap<Integer, Integer> order = new HashMap<>();
            order.put(p.getproductId(), 2 * p.getminimalAmountStock());
            is.createRegularOrder(order);

            return "Product added successfully.";
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        } catch (Exception e) {
            return "Error converting JSON to product at service: " + e.getMessage();
        }
    }

    public String MoveOrder() {
        try {
            List<Integer> ls = md.UpdateInventoryRestock();
            for (Integer i : ls) {
                is.completeOrder(i);
            }
            return "Orders Moved successfully.";
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        } catch (Exception e) {
            return "Error converting JSON to product at service: " + e.getMessage();
        }
    }

    public String AddSale(String json) {
        try {
            SaleDTO sdto = om.readValue(json, SaleDTO.class);
            sdto = md.UpdateInventorySale(sdto);
            return om.writeValueAsString(sdto);
        } catch (IllegalArgumentException e) {
            return "Problem with the list: " + e.getMessage();
        } catch (Exception e) {
            return "Json problem: " + e.getMessage();
        }
    }

    public String AddDiscount(String json) {
        int pid = 0;
        try {
            DiscountDTO d = om.readValue(json, DiscountDTO.class);
            md.AddDiscount(d);
            return "Discount Added successfully";
        } catch (Exception e) {
            return "Unable to add discount: " + e.getMessage();
        }
    }

    public String AddNewCategory(String name) {
        try {
            md.AddCategory(name);
            return "category crated successfully";
        } catch (IllegalArgumentException e) {
            return "Failed to crate category:" + e.getMessage();
        }
    }

    public String AddToCategory(String catName, int pId) {
        try {
            md.AddToCategory(catName, pId);
            return "Item added to category" + catName;
        } catch (IllegalArgumentException e) {
            return "Failed to add item to the category: " + e.getMessage();
        }
        // todo
    }

    public String AddToCategory(String catName, String subCat) {
        try {
            md.AddToCategory(catName, subCat);
            return "sub-category added to category" + catName;
        } catch (IllegalArgumentException e) {
            return "Failed to add item to the category: " + e.getMessage();
        }
        // todo
    }

    public String MoveProduct(int pid, boolean SOW, Position p) {
        try {
            md.MoveProduct(pid, SOW, p);
            return "Product moved successfully";
        } catch (IllegalArgumentException e) {
            return "Failed to move product: " + e.getMessage();
        }
    }

    public String AddBadProduct(int pId, int quantity) {
        try {
            return md.AddBadProduct(pId, quantity);
        } catch (IllegalArgumentException e) {
            return "Failed to report bad product: " + e.getMessage();
        }
    }

    public String MissingReport() {
        return md.GetMissingReport();
    }

    public String BadReport() {
        return md.GetBadReport();
    }

    public String Search(String name) {
        try {
            List<ProductService> ls = md.Search(name);
            return om.writeValueAsString(ls);
        } catch (IllegalArgumentException e) {
            return "Search failed: " + e.getMessage();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String Search(int pId) {
        try {
            ProductService p = new ProductService(md.Search(pId));
            return om.writeValueAsString(p);
        } catch (IllegalArgumentException e) {
            return "Search failed: " + e.getMessage();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String GetCurrentReport() {
        return md.GetCurrentInventoryReport();
    }

    public String GetProductLst() {
        ArrayList<ProductDTO> ls = new ArrayList<>();
        // todo call the get Catalog func
        ServiceResponse<?> response = is.getCatalog();
        if(!response.isSuccess()){
            return response.getErrors().toString();
        }
        List<CatalogProductDTO> catalog = (List<CatalogProductDTO>) response.getValue();

        // convert CatalogProductDTO to ProductDTO
        for(CatalogProductDTO cp : catalog){
            ProductDTO p = new ProductDTO(cp.productId(), cp.name(), cp.manufacturerName());
            ls.add(p);
        }


        ls = md.cleanCatalog(ls);

        try {
            return om.writeValueAsString(ls);
        } catch (Exception e) {
            return "Error";
        }
    }

    public String AddRecurringOrder(HashMap<Integer, Integer> order, int day) {

        for (Integer pId : order.keySet()) {
            if (!md.DoesProdExist(pId)) {
                return "Product ID " + pId + " dosent exist";
            }
        }

<<<<<<< HEAD
<<<<<<< HEAD
        // call supply func
        ServiceResponse<?> response = is.createPeriodicOrder(order, day);
        if (response.isSuccess())
            return "Order successfuly build";
        else
            return response.getErrors().toString();
=======
=======
>>>>>>> sup-inv-merge

        //call supply func
        ServiceResponse<?> response = is.createPeriodicOrder(order,day);
        if(response.isSuccess())return "Order successfuly build";
        else return response.getErrors().toString();

<<<<<<< HEAD
>>>>>>> 49c80a7 (init fix)
=======
>>>>>>> sup-inv-merge
    }

    // todo check
    public String AddMissingOrder() {
        List<SupplyDTO> Orders = md.AddMissingOrder();

        HashMap<Integer, Integer> order = new HashMap<>();

        for (SupplyDTO s : Orders) {
            order.put(s.getProductID(), s.getQuantityWH());
        }

        // call supply func
        ServiceResponse<?> response = is.createShortageOrder(order);
<<<<<<< HEAD
<<<<<<< HEAD
        if (response.isSuccess())
            return "Order successfuly build";
        else
            return response.getErrors().toString();
=======
=======
>>>>>>> sup-inv-merge

        if(response.isSuccess())return "Order successfuly build";
        else return response.getErrors().toString();

<<<<<<< HEAD
>>>>>>> 49c80a7 (init fix)
=======
>>>>>>> sup-inv-merge

    }

    // Get called by Supplier Domain
    public String DeliverOrder(OrderPackageDTO order) {

        md.DeliverOrder(order);

        return "done";
    }

<<<<<<< HEAD
<<<<<<< HEAD
=======

>>>>>>> 49c80a7 (init fix)
=======

>>>>>>> sup-inv-merge
    public String DeleteRecurringOrder(int orderId) {
        ServiceResponse<?> response = is.requestDeletePeriodicOrder(orderId);

        if (response.isSuccess()) {
            return "Order deleted successfully";
        } else {
            return response.getErrors().toString();
        }
    }
}
