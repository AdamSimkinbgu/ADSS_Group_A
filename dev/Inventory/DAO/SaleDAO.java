package Inventory.DAO;

import Inventory.DTO.SaleDTO;

import java.util.List;

public interface SaleDAO {
    public List<SaleDTO> GetAll();

    public SaleDTO Add(SaleDTO s);

    public void Delete(int id);

}
