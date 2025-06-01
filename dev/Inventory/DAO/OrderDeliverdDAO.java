package Inventory.DAO;

import Inventory.Domain.OrderDeliverdDomian;
import Suppliers.DTOs.OrderPackageDTO;

import java.util.List;

public interface OrderDeliverdDAO {
    public List<OrderPackageDTO> GetAll();

    public void Add(OrderPackageDTO o);

    public void Remove(OrderPackageDTO o);

}
