package Inventory.Domain;

import Inventory.DAO.*;
import Inventory.DTO.*;
import Inventory.Service.ProductService;
import Inventory.type.Position;
import Suppliers.DTOs.Enums.InitializeState;
import Suppliers.DTOs.OrderPackageDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainDomain {

    // Data Acsis Object
    private final ProductDAO Pdao;
    private final OrderDeliverdDAO ODdao;
    private final DiscountDAO Ddao;
    private final CategoryDAO Cdao;
    private final SaleDAO Sdao;
    private final SupplyDAO SPdao;

    private final HashMap<Integer, ProductDomain> prodMap;
    private final List<DiscountDomain> disLst;
    private final List<SaleDomain> saleLst;
    private final List<CategoryDomain> categoryLst;
    private  List<OrderPackageDTO> orders;

    // todo assign DAOs
    public MainDomain() {

        prodMap = new HashMap<>();
        disLst = new ArrayList<>();
        saleLst = new ArrayList<>();
        categoryLst = new ArrayList<>();
        orders = new ArrayList<>();

        Pdao = new ProductDAO_SQL();
        ODdao = new OrderDeliverdDAO_SQL();
        Ddao = new DiscountDAO_SQL();
        Cdao = new CategoryDAO_SQL();
        Sdao = new SaleDTO_SQL();
        SPdao = new SupplyDTO_SQL();
    }

    // todo check
    public void InventoryInitialization(InitializeState input) {

        if(input == InitializeState.CURRENT_STATE) {

            // uplode product
            List<ProductDTO> pls = Pdao.GetAll();
            for (ProductDTO p : pls) {
                prodMap.put(p.getproductId(), new ProductDomain(p));
            }

            // uplode orders
            orders = ODdao.GetAll();

            // uplode category
            List<CategoryDTO> cls = Cdao.getAll();
            for (CategoryDTO c : cls) {
                categoryLst.add(new CategoryDomain(c));
            }

            // uplode discount
            List<DiscountDTO> dls = Ddao.getAll();
            for (DiscountDTO d : dls) {
                DiscountDomain dis = new DiscountDomain(d);
                if (d.getpId() != -1) {
                    prodMap.get(d.getpId()).AddDiscount(dis);
                } else {
                    //todo : fix this
                    for (CategoryDomain c : categoryLst) {
                        if (c.Isin(d.getCatName())) {
                            c.AddDiscount(dis, d.getCatName());
                            break;
                        }
                    }
                }
                disLst.add(dis);
            }

            // uplode sales
            List<SaleDTO> sls = Sdao.GetAll();
            for (SaleDTO s : sls) {
                saleLst.add(new SaleDomain(s));

            }


        }
        else {
            Sdao.DeleteAll();
            Ddao.deleteAll();
            Cdao.deleteAll();
            ODdao.DeleteAll();
            SPdao.DeleteAll();
            Pdao.DeleteAll();
            if (input == InitializeState.DEFAULT_STATE) {
                ProductDTO pdto = new ProductDTO( 1,"Milk 3%", "Yotvata", 25,75, (float)4.6, new Position(1, 1), new Position(2, 1));
                AddProduct(pdto);
                pdto = new ProductDTO( 2,"Cornflacks Cariot", "Telma", 10,50, (float)1.5, new Position(1, 2), new Position(2, 2));
                AddProduct(pdto);
                pdto = new ProductDTO( 3,"Cottage Cheese", "Tnuva", 20,100, (float)2.5, new Position(1, 3), new Position(2, 3));
                AddProduct(pdto);
                pdto = new ProductDTO( 4,"Pastrami Sandwich", "DeliMeat", 50,200, (float)15.0, new Position(1, 4), new Position(2, 4));
                AddProduct(pdto);
                pdto = new ProductDTO( 5,"Milk 3%", "Tnuva", 30,75, (float)4.7, new Position(1, 5), new Position(2, 5));
                AddProduct(pdto);


                SupplyDTO sdto = new SupplyDTO(1, 100, LocalDate.now());
                prodMap.get(1).AddSupply(sdto);
                sdto = new SupplyDTO(2, 50, LocalDate.now());
                prodMap.get(2).AddSupply(sdto);
                sdto = new SupplyDTO(3, 200, LocalDate.now());
                prodMap.get(3).AddSupply(sdto);
                sdto = new SupplyDTO(4, 300, LocalDate.now());
                prodMap.get(4).AddSupply(sdto);
                sdto = new SupplyDTO(5, 100, LocalDate.now());
                prodMap.get(5).AddSupply(sdto);

                // add categories
                AddCategory("milk");
                AddCategory("milk products");
                AddCategory("food");
                AddToCategory("milk", 1);
                AddToCategory("milk", 5);
                AddToCategory("milk products", 3);
                AddToCategory("food", 4);
                AddToCategory("food", "milk");
                AddToCategory("milk", "milk products");

            }
        }
    }

    // todo
    public int AddProduct(ProductDTO pdto) {
        for (ProductDomain p : prodMap.values()) {
            if (p.getproductID() == pdto.getproductId())
                throw new IllegalArgumentException("Product already in stock");
        }

        Pdao.Add(pdto);

        prodMap.put(pdto.getproductId(), new ProductDomain(pdto));
        return 0;
    }

    // todo check
    public List<Integer> UpdateInventoryRestock() {
        List<Integer> ret = new ArrayList<>();
        for (OrderPackageDTO o : orders) {
            for(SupplyDTO sdto: o.getSupplies()) {
                if(!prodMap.containsKey(sdto.getProductID()))
                    throw new IllegalArgumentException("no product with this id: " + sdto.getProductID());
                sdto = SPdao.Add(sdto);
                prodMap.get(sdto.getProductID()).AddSupply(sdto);
            }
            ret.add(o.getOrderId());
            // add to database
            ODdao.Remove(o);
        }
        return ret;
    }

    // todo change database
    public SaleDTO UpdateInventorySale(SaleDTO sdto) {
        // check if all products are in stock
        for (Integer pIg : sdto.getProducts().keySet()) {
            if (!prodMap.containsKey(pIg))
                throw new IllegalArgumentException("invalid product id");
        }

        // calculate sale price
        float discount;
        float price = 0;
        for (Integer pIg : sdto.getProducts().keySet()) {
            discount = 0;
            for (CategoryDomain c : categoryLst)
                discount += c.getDiscount(pIg);
            // price += product price after discount * number of sed product
            price += prodMap.get(pIg).getproductPrice1unit(discount) * sdto.getProducts().get(pIg);
            prodMap.get(pIg).Buy(sdto.getProducts().get(pIg));
        }
        sdto.setsalePrice(price);

        // add to database
        sdto = Sdao.Add(sdto);

        // add to sales
        saleLst.add(new SaleDomain(sdto));
        return sdto;
    }

    // todo change
    public String GetMissingReport() {
        StringBuilder ret = new StringBuilder("=====Missing Report=====\n");
        int missNum ;
        for (ProductDomain p : prodMap.values()) {
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

    // todo change database
    /*
     * report a bad product
     *
     */
    public String AddBadProduct(int pId, int quantity) {
        if (!prodMap.containsKey(pId))
            throw new IllegalArgumentException("invalid product id");

        int missing = prodMap.get(pId).ReportBad(quantity);
        if (missing > 0) {
            return "Bad Product Reported \nWarning!!! minimal amount retched";
        } else
            return "Bad Product Reported";
    }

    // VVVVVV
    public String GetBadReport() {
        int badNum;
        StringBuilder ret = new StringBuilder();
        ret.append("=====Bad Report=====\n");
        ret.append("------------------------------------------\n");
        ret.append(String.format("| %-15s | %-15s | %-6s |%n",
                "Product Name", "Manufacturer", "Bad Qty"));
        ret.append("------------------------------------------\n");

        for (ProductDomain p : prodMap.values()) {
            badNum = p.GetBads();
            if (badNum > 0) {
                ret.append(String.format("| %-15s | %-15s | %6d |%n",
                        p.getproductName(),
                        p.getmanufactuerName(),
                        badNum));
            }
        }
        ret.append("------------------------------------------\n");
        return ret.toString();

    }

    public String GetCurrentInventoryReport() {
        StringBuilder table = new StringBuilder();

        table.append("=====Current Inventory Report=====\n");
        table.append(String.format("%-15s %-20s %-20s %-10s%n", "Id", "Name", "Manufacturer", "Quantity"));
        table.append("---------------------------------------------------------------------\n");
        for (ProductDomain p : prodMap.values()) {
            table.append(String.format("%-15d %-20s %-20s %-10d%n",
                    p.getproductID(),
                    p.getproductName(),
                    p.getmanufactuerName(),
                    p.getQuantity()));
        }
        return table.toString();

    }

    // VVVVVV
    /*
     * Move Product to a new shelf
     *
     * @param pId the product id number
     *
     * @param SOrW set to true if you want to change self in the store and false for
     * change in the warehouse
     *
     * @param newP the new shelf
     */
    public void MoveProduct(int pId, boolean SOrW, Position newP) {
        if (!prodMap.containsKey(pId))
            throw new IllegalArgumentException("pId invalid");

        // change database
        ProductDTO pdto = new ProductDTO(prodMap.get(pId));
        if (SOrW)
            pdto.setStoreShelf(newP);
        else
            pdto.setWareHouseShelf(newP);
        Pdao.Set(pdto);

        prodMap.get(pId).moveProduct(SOrW, newP);

    }

    // VVVVVV
    /*
     * Search by Product id number
     */
    public ProductDomain Search(int pId) {
        if (!prodMap.containsKey(pId))
            throw new IllegalArgumentException("Invalid product id");

        return prodMap.get(pId);
        // todo
    }

    public List<ProductService> Search(String name) {
        List<ProductService> ret = new ArrayList<>();
        CategoryDomain cat = null;
        for(CategoryDomain c: categoryLst){
            if(c.Isin(name)){
                cat = c.getSub(name);
                break;
            }
        }


        for (int pId : cat.getProductLs()) {
            ret.add(new ProductService(prodMap.get(pId)));
        }
        return ret;
    }

    // todo check
    public void AddDiscount(DiscountDTO dis) {
        DiscountDomain d = new DiscountDomain(dis);

        // discount for product
        if (dis.getpId() != -1) {

            if (!prodMap.containsKey(dis.getpId()))
                throw new IllegalArgumentException("pId invalid");

            // Add to database
            if( prodMap.get(dis.getpId()).getDiscount() != null) {
                // delete old discount
                Ddao.delete(new DiscountDTO(prodMap.get(dis.getpId()).getDiscount(), dis.getpId()));
            }
            Ddao.add(dis);

            // add to product
            prodMap.get(dis.getpId()).AddDiscount(d);
            disLst.add(d);
        }
        // discount for category
        else {
            for (CategoryDomain c : categoryLst) {
                if (c.Isin(dis.getCatName())) {
                    // Add to database
                    if( c.getDisDom() != null) {
                        // delete old discount
                        Ddao.delete(new DiscountDTO(c.getDisDom(), dis.getCatName()));
                    }

                    Ddao.add(dis);

                    // add to category
                    c.AddDiscount(d, dis.getCatName());
                    disLst.add(d);
                    return;
                }
            }
            throw new IllegalArgumentException("No category by that name");
        }
    }

    // todo check
    public void AddCategory(String newName) {
        for (CategoryDomain c : categoryLst) {
            if (c.getName().equals(newName))
                throw new IllegalArgumentException("Name already used.");
        }

        // add to database
        Cdao.addCategory(newName);

        // add a category
        categoryLst.add(new CategoryDomain(newName));
    }

    // todo check
    public void AddToCategory(String catName, int pId) {

        if (!prodMap.containsKey(pId))
            throw new IllegalArgumentException("invalid product id");

        for (CategoryDomain c : categoryLst) {
            if (c.Isin(catName)) {
                // add to database
                Cdao.addToCategory(catName, pId);
                // add to category
                c.InsertPID(catName, pId);
                return;
            }
        }
        throw new IllegalArgumentException("There is no category by that name");

    }

    // todo check
    public void AddToCategory(String catName, String subCat) {
        boolean flag1 = false, flag2 = false;
        CategoryDomain sub = new CategoryDomain("");

        for (CategoryDomain c : categoryLst) {
            if (c.Isin(catName))
                flag1 = true;
            if (c.Isin(subCat))
                flag2 = true;
        }
        if (!flag1 || !flag2)
            throw new IllegalArgumentException("There is no category by that name");

        // remove subCat
        for (CategoryDomain c : categoryLst) {
            if (c.Isin(subCat)) {
                if (c.getName().equals(subCat)) {
                    sub = c;
                    categoryLst.remove(c);
                } else {
                    sub = c.remove(subCat);
                }
                break;
            }
        }

        // add to catName
        for (CategoryDomain c : categoryLst) {
            if (c.Isin(catName)) {
                // add to database
                Cdao.addToCategory(catName, subCat);
                // add to category
                c.InsertSub(catName, sub);
                return;
            }
        }

    }

    // todo
    public List<SupplyDTO> AddMissingOrder() {
        int missingA;
        List<SupplyDTO> ls = new ArrayList<>();
        for (ProductDomain p : prodMap.values()) {
            missingA = p.GetMissing();
            if (missingA > 0) {
                ls.add(new SupplyDTO(p.getproductID(), 2*missingA, LocalDate.now()));
            }
        }

        return ls;

    }

    // todo check
    public void DeliverOrder(OrderPackageDTO order) {
        //add to database
        ODdao.Add(order);
            // add to orders
        orders.add(order);
    }

    // todo check
    // remove from catalog all product already in system
    public ArrayList<ProductDTO> cleanCatalog(ArrayList<ProductDTO> ls) {
        for (ProductDTO p : ls) {
            if (prodMap.containsKey(p.getproductId())) {
                ls.remove(p);
            }
        }

        return ls;
    }

    // todo check
    public boolean DoesProdExist(int pid) {
        return prodMap.containsKey(pid);
    }

}
