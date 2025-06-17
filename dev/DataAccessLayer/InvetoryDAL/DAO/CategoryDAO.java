package DataAccessLayer.InvetoryDAL.DAO;

import java.util.List;

import DTOs.InventoryModuleDTOs.CategoryDTO;

public interface CategoryDAO {
    public CategoryDTO get(String name);

    public List<CategoryDTO> getAll();

    public void delete(String name);

    public void deleteAll();

    public CategoryDTO addCategory(String name);

    public void addToCategory(String cat, int pid);

    public void addToCategory(String cat, String sub);
}
