package DTOs.InventoryModuleDTOs;

import java.util.ArrayList;
import java.util.List;

import DomainLayer.InventoryDomainSubModule.CategoryDomain;

public class CategoryDTO {
    private String name;
    private List<Integer> product;
    private List<CategoryDTO> subs;

    // Getters
    public String getName() {
        return name;
    }

    public List<CategoryDTO> getSubs() {
        return subs;
    }

    public List<Integer> getProduct() {
        return product;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setProduct(List<Integer> product) {
        this.product = product;
    }

    public void setSubs(List<CategoryDTO> subs) {
        this.subs = subs;
    }

    public CategoryDTO() {
    }

    public CategoryDTO(String name) {
        this.name = name;
        this.product = new ArrayList<>();
        this.subs = new ArrayList<>();
    }

    public CategoryDTO(CategoryDomain other) {
        name = other.getName();
        this.product = other.getProductLs();
        this.subs = new ArrayList<>();
        for (CategoryDomain c : other.getSubCategoryLs()) {
            this.subs.add(new CategoryDTO(c));
        }
    }

    public void AddSubcategory(CategoryDTO sub) {
        this.subs.add(sub);
    }

    public void AddProduct(int productId) {
        this.product.add(productId);
    }
}
