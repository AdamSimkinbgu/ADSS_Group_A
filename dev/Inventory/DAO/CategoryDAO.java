package Inventory.DAO;

import Inventory.DTO.CategoryDTO;

import java.util.List;

public interface CategoryDAO {
    public CategoryDTO get(String name);

    public List<CategoryDTO> getAll();

    public void delete(String name);

    public CategoryDTO addCategory(String name);

    public void addToCategory(String cat, int pid);

    public void addToCategory(String cat, String sub);
}
