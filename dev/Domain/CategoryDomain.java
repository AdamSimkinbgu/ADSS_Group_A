package Domain;


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
    public float getDiscunt(){
        //if(disDom ==)
            return 0;
        //return disDom.getpercent();
    }

    public CategoryDomain(String catName){
        name = catName;
        productLs = new ArrayList<>();
        subCategoryLs = new ArrayList<>();
    }

    public boolean Isin(String item){
        if(item.equals(name)){
            return true;
        }
        else{
            for(CategoryDomain c : subCategoryLs){
                if(c.Isin(item))return true;
            }
            return false;
        }
        //todo check
    }

    public boolean Isin(int itemID){
        for(Integer pd: productLs){
            if(pd == itemID)return true;
        }
        for(CategoryDomain c : subCategoryLs){
            if(c.Isin(itemID))return true;
        }
        return false;
        //todo check
    }

    public void InsertPID(String catName, int pid){
        if(catName.equals(name))productLs.add(pid);
        else {
            for(CategoryDomain c :subCategoryLs){
                if(c.Isin(catName)){
                    c.InsertPID(catName,pid);
                    return;
                }
            }
        }
    }

    public void InsertSub(String catName,CategoryDomain subCat){
        if(catName.equals(name))subCategoryLs.add(subCat);
        else {
            for(CategoryDomain c :subCategoryLs){
                if(c.Isin(catName)){
                    c.InsertSub(catName,subCat);
                    return;
                }
            }
        }
    }

}
