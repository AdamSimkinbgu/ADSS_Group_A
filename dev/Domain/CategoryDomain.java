package Domain;

import java.util.ArrayList;
import java.util.List;

public class CategoryDomain {
    private final String name;
    private List<Integer> productLs; //// <product id>
    private List<CategoryDomain> subCategoryLs;

    public String getName() {
        return name;
    }

    public CategoryDomain(String catName){
        name = catName;
        productLs = new ArrayList<>();
        subCategoryLs = new ArrayList<>();
    }
}
