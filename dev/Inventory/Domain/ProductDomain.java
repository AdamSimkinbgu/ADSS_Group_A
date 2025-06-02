package Inventory.Domain;

import Inventory.DAO.SupplyDAO;
import Inventory.DAO.SupplyDTO_SQL;
import Inventory.DTO.ProductDTO;
import Inventory.DTO.SupplyDTO;
import Inventory.type.Position;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

public class ProductDomain {
    private int productID;
    private String productName;
    private String manufactuerName;
    private int minimalAmountStore;
    private int minimalAmountStock;
    private float productPrice1unit;
    private Position wareHouseShelf;
    private Position storeShelf;
    private DiscountDomain discount;
    private List<SupplyDomain> supplyList; //// list of supplies ////

    private SupplyDAO SPdao;

    public int getproductID() {
        return productID;
    }

    public String getproductName() {
        return productName;
    }

    public String getmanufactuerName() {
        return manufactuerName;
    }

    public int getminimalAmountStore() {
        return minimalAmountStore;
    }

    public int getminimalAmountStock() {
        return minimalAmountStock;
    }

    public float getproductPrice() {
        return productPrice1unit;
    }

    public Position getstoreShelf() {
        return storeShelf;
    }

    public Position getwareHouseShelf() {
        return wareHouseShelf;
    }

    public DiscountDomain getDiscount() {
        return discount;
    }

    public ProductDomain(int pID, String pName, String MfName, int MAStore, int MAStock, float PPrice, Position SShalf,
            Position WHShelf) {
        productID = pID;
        productName = pName;
        manufactuerName = MfName;
        minimalAmountStore = MAStore;
        minimalAmountStock = MAStock;
        productPrice1unit = PPrice;
        storeShelf = SShalf;
        wareHouseShelf = WHShelf;
        supplyList = new ArrayList<>();
    }

    // todo check
    public ProductDomain(ProductDTO other) {
        productID = other.getproductId();
        productName = other.getproductName();
        manufactuerName = other.getmanufacturerName();
        minimalAmountStore = other.getminimalAmountStore();
        minimalAmountStock = other.getminimalAmountStock();
        productPrice1unit = other.getproductPrice();
        storeShelf = other.getstoreShelf();
        wareHouseShelf = other.getwareHouseShelf();
        supplyList = new ArrayList<>();
        SPdao = new SupplyDTO_SQL();


        // up lode from database
        List<SupplyDTO> ls = SPdao.GetAll(this.productID);
        for (SupplyDTO s : ls) {
            supplyList.add(new SupplyDomain(s));
        }

        for (SupplyDomain supply : supplyList) {
            if (supply.IsEx()) {
                SPdao.Set(new SupplyDTO(supply, productID));
                supplyList.remove(supply);
            }
        }

    }

    // setters
    public void setwareHouseShelf(Position wareHouseShelf) {
        this.wareHouseShelf = wareHouseShelf;
    }

    public void setstoreShelf(Position storeShelf) {
        this.storeShelf = storeShelf;
    }

    public void setminimalAmountStore(int minimalAmountStore) {
        this.minimalAmountStore = minimalAmountStore;
    }

    public void setminimalAmountStock(int minimalAmountStock) {
        this.minimalAmountStock = minimalAmountStock;
    }

    //////////////////////////////////////////////////////////////////////////////////

    // todo check
    public void AddSupply(SupplyDTO s) {
        // add to database
        s = SPdao.Add(s);

        supplyList.add(new SupplyDomain(s));
        supplyList.sort(Comparator.comparing(SupplyDomain::getExpierDate));
        reStockStore();
    }

    // todo check
    public void reStockStore() {
        int all_supp_instore = 0;
        for (SupplyDomain supply : supplyList) {
            all_supp_instore += supply.getQuantityStore();
        }
        if (all_supp_instore < minimalAmountStore) {
            int quant = 2 * minimalAmountStore - all_supp_instore;
            for (SupplyDomain supp : supplyList) {
                quant = supp.restock(quant);
                // add to database
                SPdao.Set(new SupplyDTO(supp, productID));
                if (quant == 0)
                    break;
            }
        }
    }

    public void moveProduct(boolean sOrW, Position newP) {
        if (sOrW)
            storeShelf = newP;
        else
            wareHouseShelf = newP;
    }

    public int GetMissing() {
        int TotalInStore = 0;
        int ret = 0;
        for (SupplyDomain supply : supplyList) {
            TotalInStore += supply.getQuantityStore();
            TotalInStore += supply.getQuantityWarehouse();
        }
        if (TotalInStore < minimalAmountStock) {
            ret = minimalAmountStock - TotalInStore;
        }

        reStockStore();
        return ret;

    }

    public int GetBads() {
        int Totalbad = 0;
        for (SupplyDomain supply : supplyList) {
            Totalbad += supply.getQuantityBad();
        }
        reStockStore();
        return Totalbad;
    }

    public void AddDiscount(DiscountDomain d) {
        discount = d;
    }

    public float getproductPrice1unit(float dis) {
        if (discount != null && discount.getdiscountEnd().isAfter(LocalDate.now()))
            dis += discount.getpercent();
        return productPrice1unit * (1 - dis);
    }

    // todo check
    public void Buy(int quantity) {
        if (getQuantity() < quantity)
            throw new IllegalArgumentException("not enough product to complete the sale");

        for (SupplyDomain s : supplyList) {
            if (quantity == 0)
                break;
            // add to database
            SupplyDTO sdto = new SupplyDTO(s, productID);
            if (sdto.getquantityS() > quantity)
                sdto.setQuantityS(sdto.getquantityS() - quantity);
            else
                sdto.setQuantityS(0);
            SPdao.Set(sdto);

            quantity = s.Buy(quantity);
        }
        reStockStore();
    }

    public int ReportBad(int quantity) {
        for (SupplyDomain s : supplyList) {
            if (quantity == 0)
                break;
            quantity = s.Report(quantity);
            SPdao.Set(new SupplyDTO(s, productID));
        }
        return GetMissing();
    }

    public String GetCurrentInventory() {
        int totalInStore = 0;
        int totalInWarehouse = 0;
        for (SupplyDomain supply : supplyList) {
            totalInStore += supply.getQuantityStore();
            totalInWarehouse += supply.getQuantityWarehouse();
        }
        return "Product: " + getproductName() + " (ID: " + getproductID() + ")\n"
                + "In Store: " + totalInStore + "\n"
                + "In Warehouse: " + totalInWarehouse + "\n"
                + "Bad Units: " + GetBads() + "\n"
                + "Missing from Store: " + GetMissing() + "\n";
    }

    public int getQuantity() {
        int ret = 0;
        for (SupplyDomain s : supplyList) {
            ret += s.getQuantityStore();
            ret += s.getQuantityWarehouse();
        }
        return ret;
    }

    public int getBadQantity() {
        int ret = 0;
        for (SupplyDomain s : supplyList)
            ret += s.getQuantityBad();
        return ret;
    }
}