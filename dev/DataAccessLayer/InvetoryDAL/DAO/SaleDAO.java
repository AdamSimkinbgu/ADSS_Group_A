package DataAccessLayer.InvetoryDAL.DAO;

import java.util.List;

import DTOs.InventoryModuleDTOs.SaleDTO;

public interface SaleDAO {
    public List<SaleDTO> GetAll();

    public SaleDTO Add(SaleDTO s);

    public void Delete(int id);

    public void DeleteAll();

}
