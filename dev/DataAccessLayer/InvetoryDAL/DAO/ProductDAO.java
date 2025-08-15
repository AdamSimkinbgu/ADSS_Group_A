package DataAccessLayer.InvetoryDAL.DAO;

import java.util.List;

import DTOs.InventoryModuleDTOs.ProductDTO;

public interface ProductDAO {
    public void Add(ProductDTO p);

    public List<ProductDTO> GetAll();

    public void Set(ProductDTO p);

    public void Delete(ProductDTO p);

    public void DeleteAll();
}
