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
        if(disDom ==)
            return 0;
        return disDom.getpercent();
    }

    public CategoryDomain(String catName){
        name = catName;
        productLs = new ArrayList<>();
        subCategoryLs = new ArrayList<>();
    }

    public boolean Isin(String item){
        if(item == name){
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



}
