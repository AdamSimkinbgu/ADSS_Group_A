package DAO;

import Domain.OrderDeliverdDomian;

import java.util.List;

public interface OrderDeliverdDAO {
    public List<OrderDeliverdDomian> GetAll();
    public void Add(OrderDeliverdDomian o);
    public void Remove(OrderDeliverdDomian o);

}
