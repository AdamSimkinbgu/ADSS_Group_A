package DataAccessLayer.InvetoryDAL.DAO;

import java.util.List;

import DTOs.SuppliersModuleDTOs.OrderPackageDTO;

public interface OrderDeliverdDAO {
    public List<OrderPackageDTO> GetAll();

    public void Add(OrderPackageDTO o);

    public void Remove(OrderPackageDTO o);

    public void DeleteAll();
}
