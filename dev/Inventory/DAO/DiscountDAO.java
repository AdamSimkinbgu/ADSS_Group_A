package Inventory.DAO;

import Inventory.DTO.DiscountDTO;

import java.util.List;

public interface DiscountDAO {
    public DiscountDTO add(DiscountDTO d);

    public List<DiscountDTO> getAll();

    public void delete(DiscountDTO d);
}
