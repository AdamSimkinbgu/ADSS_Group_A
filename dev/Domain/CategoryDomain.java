package Domain;

import java.util.List;

public class CategoryDomain {
    private String name;
    private List<Integer> productLs; //// <product id>
    private List<CategoryDomain> subCategoryLs;

    public String getName() {
        return name;
    }
}
