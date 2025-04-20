package Service;

import Domain.SupplyDomain;

import java.util.Date;


public class SupplyService {
    int supplyID;
    final int productID;
    int quantityWarehouse;
    int quantityStore;
    final Date expireDate;
    int quantityBad;

    public int getSupplyID() {
        return supplyID;
    }
    public int getProductID() {
        return productID;
    }
    public int getQuantityWarehouse() {
        return quantityWarehouse;
    }
    public int getQuantityStore() {
        return quantityStore;
    }
    public int getQuantityBad() {
        return quantityBad;
    }
    public Date getExpireDate() {
        return expireDate;
    }

    public SupplyService(int id,int pId,int qW, int qS,int qB,Date ex){
        supplyID = id;
        productID = pId;
        quantityWarehouse = qW;
        quantityStore = qS;
        quantityBad = qB;
        expireDate = ex;
    }

    public SupplyService(SupplyDomain other , int otherId){
        supplyID = other.getId();
        productID = otherId;
        quantityWarehouse = other.getQuantityWarehouse();
        quantityStore = other.getQuantityStore();
        quantityBad = other.getQuantityBad();
        expireDate = other.getExpierDate();
    }


}
