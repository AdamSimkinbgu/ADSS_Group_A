package DAO;

import DTO.SaleDTO;

import java.util.List;

public interface SaleDAO {
    public List<SaleDTO> GetAll();

    public void Add(SaleDTO s);

}
