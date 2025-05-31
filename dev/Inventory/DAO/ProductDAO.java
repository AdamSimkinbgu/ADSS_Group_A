package Inventory.DAO;

import Inventory.DTO.ProductDTO;

import java.util.List;

public interface ProductDAO {
    public void Add(ProductDTO p);

    public List<ProductDTO> GetAll();

    public void Set(ProductDTO p);

    public void Delete(ProductDTO p);
}
