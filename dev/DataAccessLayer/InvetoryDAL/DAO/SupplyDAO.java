package DataAccessLayer.InvetoryDAL.DAO;

import java.util.List;

import DTOs.InventoryModuleDTOs.SupplyDTO;

public interface SupplyDAO {

    public SupplyDTO Get(int id);

    public List<SupplyDTO> GetAll(int pId);

    public SupplyDTO Add(SupplyDTO s);

    public void Delete(int id);

    public void Set(SupplyDTO s);

    public void DeleteAll();

}
