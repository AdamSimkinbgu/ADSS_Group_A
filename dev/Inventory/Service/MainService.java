package Inventory.Service;

import Inventory.DTO.*;
import Inventory.Domain.MainDomain;

import Suppliers.DTOs.OrderPackageDTO;
import Suppliers.DTOs.PeriodicOrderDTO;
import Suppliers.DTOs.Enums.InitializeState;
import Suppliers.ServiceLayer.IntegrationService;
import Suppliers.ServiceLayer.Interfaces_and_Abstracts.ServiceResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import Suppliers.DTOs.CatalogProductDTO;

import Inventory.type.Position;

import java.time.DayOfWeek;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainService {
    private MainDomain md;
    private ObjectMapper om;
    private IntegrationService is;

    private static MainService mainServiceInstance;

    private MainService() {
        om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        md = new MainDomain();
    }

    public void Initialize(InitializeState input) {
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
            md.AddProduct(p);

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
        if (!response.isSuccess()) {
            return response.getErrors().toString();
        } else if (response.getValue() == null) {
            return "No products in catalog";
        }
        List<CatalogProductDTO> catalog = (List<CatalogProductDTO>) response.getValue();

        // convert CatalogProductDTO to ProductDTO
        for (CatalogProductDTO cp : catalog) {
            ProductDTO p = new ProductDTO(cp.productId(), cp.name(), cp.manufacturerName());
            ls.add(p);
        }

        ls = md.cleanCatalog(ls);

        StringBuilder sb = new StringBuilder();
        int counter = 1;
        sb.append("Product List:\n");
        sb.append("──────────────────────────────────────\n");
        for (ProductDTO p : ls) {
            sb.append(String.format("%d. %s\n", counter++, p.toString()));
        }
        return sb.toString();
    }

    public String AddRecurringOrder(HashMap<Integer, Integer> order, DayOfWeek day) {

        for (Integer pId : order.keySet()) {
            if (!md.DoesProdExist(pId)) {
                return "Product ID " + pId + " dosent exist";
            }
        }

        // call supply func
        ServiceResponse<?> response = is.createPeriodicOrder(order, day);
        if (response.isSuccess())
            return "Order successfuly build";
        else
            return response.getErrors().toString();
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
        if (response.isSuccess())
            return "Order successfuly build";
        else
            return response.getErrors().toString();

    }

    // Get called by Supplier Domain
    public String DeliverOrder(OrderPackageDTO order) {

        md.DeliverOrder(order);

        return "done";
    }

    public String DeleteRecurringOrder(int orderId) {
        ServiceResponse<?> response = is.requestDeletePeriodicOrder(orderId);

        if (response.isSuccess()) {
            return "Order deleted successfully";
        } else {
            return response.getErrors().toString();
        }
    }

    public String GetRecurringOrders() {
        ServiceResponse<List<PeriodicOrderDTO>> response = (ServiceResponse<List<PeriodicOrderDTO>>) is
                .viewPeriodicOrders();
        if (response.isSuccess()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Periodic Orders:\n");
            int counter = 1;
            for (PeriodicOrderDTO po : response.getValue()) {
                sb.append(String.format("%d. %s\n", counter++, po.toString()));
            }
            return sb.toString();
        } else {
            return response.getErrors().toString();
        }
    }
}
