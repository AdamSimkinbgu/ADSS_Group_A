package Inventory.DAO;

import Inventory.DTO.SupplyDTO;

import java.util.List;

public interface SupplyDAO {

    public SupplyDTO Get(int id);

    public List<SupplyDTO> GetAll();

    public SupplyDTO Add(SupplyDTO s);

    public void Delete(int id);

    public void Set(SupplyDTO s);

    public void DeleteAll();

}
