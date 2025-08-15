package DomainLayer.InventoryDomainSubModule;

import java.time.LocalDate;

import DTOs.InventoryModuleDTOs.SupplyDTO;

public class OrderDeliverdDomian {
    private int pId;
    private int quantity;
    private LocalDate exDate;

    public int getpId() {
        return pId;
    }

    public int getQuantity() {
        return quantity;
    }

    public LocalDate getExDate() {
        return exDate;
    }

    public OrderDeliverdDomian(SupplyDTO s) {
        exDate = s.getExpireDate();
        pId = s.getProductID();
        quantity = s.getQuantityWH();
    }
}
