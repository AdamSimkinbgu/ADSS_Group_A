package Domain;


import java.util.HashMap;
import java.util.List;

public class MainDomain {
    private HashMap<Integer, ProductDomain> prodMap;
    private List<DiscountDomain> activeDisLst;
    private List<DiscountDomain> pastDisLst;
    private List<SaleDomain> saleLst;
    private List<CategoryDomain> categoryLst;
}
