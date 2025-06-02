package Inventory.Domain;

import Inventory.DTO.CategoryDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CategoryDomain {
    private final String name;
    private List<Integer> productLs; //// <product id>
    private List<CategoryDomain> subCategoryLs;
    private DiscountDomain disDom;

    public String getName() {
        return name;
    }

    public float getDiscount(Integer pId) {
        float dis = 0;
        if (!this.Isin(pId))
            return dis;
        if (productLs.contains(pId) && disDom != null && disDom.getdiscountEnd().isAfter(LocalDate.now()))
            dis += disDom.getpercent();
        for (CategoryDomain c : subCategoryLs)
            dis += c.getDiscount(pId);
        return dis;
    }

    public List<CategoryDomain> getSubCategoryLs() {
        return subCategoryLs;
    }

    public List<Integer> getProductLs() {
        List<Integer> productLs = new ArrayList<>(this.productLs);
        for (CategoryDomain c : subCategoryLs) {
            productLs.addAll(c.getProductLs());
        }
        return productLs;
    }

    public CategoryDomain(String catName) {
        name = catName;
        productLs = new ArrayList<>();
        subCategoryLs = new ArrayList<>();
    }

    public CategoryDomain(CategoryDTO other) {
        name = other.getName();
        productLs = other.getProduct();
        subCategoryLs = new ArrayList<>();
        for (CategoryDTO c : other.getSubs()) {
            subCategoryLs.add(new CategoryDomain(c));
        }
    }

    public boolean Isin(String item) {
        if (item.equals(name)) {
            return true;
        } else {
            for (CategoryDomain c : subCategoryLs) {
                if (c.Isin(item))
                    return true;
            }
            return false;
        }
        // todo check
    }

    public boolean Isin(int itemID) {
        for (Integer pd : productLs) {
            if (pd == itemID)
                return true;
        }
        for (CategoryDomain c : subCategoryLs) {
            if (c.Isin(itemID))
                return true;
        }
        return false;
        // todo check
    }

    public void InsertPID(String catName, int pid) {
        if (catName.equals(name))
            productLs.add(pid);
        else {
            for (CategoryDomain c : subCategoryLs) {
                if (c.Isin(catName)) {
                    c.InsertPID(catName, pid);
                    return;
                }
            }
        }
    }

    public void InsertSub(String catName, CategoryDomain subCat) {
        if (catName.equals(name))
            subCategoryLs.add(subCat);
        else {
            for (CategoryDomain c : subCategoryLs) {
                if (c.Isin(catName)) {
                    c.InsertSub(catName, subCat);
                    return;
                }
            }
        }
    }

    public CategoryDomain remove(String subName) {
        for (CategoryDomain c : subCategoryLs) {
            if (c.getName().equals(subName)) {
                subCategoryLs.remove(c);
                return c;
            } else if (c.Isin(subName))
                return c.remove(subName);
        }
        throw new IllegalArgumentException("There is no category by that name");
    }

    public void AddDiscount(DiscountDomain d, String catName) {
        if (name.equals(catName)) {
            disDom = d;
            return;
        }
        for (CategoryDomain c : subCategoryLs) {
            if (c.Isin(catName)) {
                c.AddDiscount(d, catName);
                return;
            }
        }
        return;

    }

    public CategoryDomain getSub(String subName) {
        if( subName.equals(name)) {
            return this;
        }
        for (CategoryDomain c : subCategoryLs) {
            if(c.Isin(subName)) {
                return c.getSub(subName);
            }
        }
        throw new IllegalArgumentException("There is no category by that name");
    }

    public DiscountDomain getDisDom() {
        return disDom;
    }

}
