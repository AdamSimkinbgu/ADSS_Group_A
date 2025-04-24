package Domain;

import javax.lang.model.type.NullType;
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
                if(c.Isin(item) == true)return true;
            }
            return false;
        }
        //todo
    }

    public boolean Isin(int itemID){
        for(){

        }
        //todo
    }



}
