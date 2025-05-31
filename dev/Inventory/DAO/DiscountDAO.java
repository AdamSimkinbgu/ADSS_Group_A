package Inventory.DAO;

import Inventory.DTO.DiscountDTO;

import java.util.List;

public interface DiscountDAO {
    public void add(DiscountDTO d);

    public List<DiscountDTO> getAll();
}
