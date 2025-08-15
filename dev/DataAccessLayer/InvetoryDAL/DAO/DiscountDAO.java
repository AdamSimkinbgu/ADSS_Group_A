package DataAccessLayer.InvetoryDAL.DAO;

import java.util.List;

import DTOs.InventoryModuleDTOs.DiscountDTO;

public interface DiscountDAO {
    public DiscountDTO add(DiscountDTO d);

    public List<DiscountDTO> getAll();

    public void delete(DiscountDTO d);

    public void deleteAll();
}
