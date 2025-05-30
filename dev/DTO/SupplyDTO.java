package DTO;


import Domain.SupplyDomain;

import java.time.LocalDate;
public class SupplyDTO {
    int sId;
    int productID;
    LocalDate expireDate;
    int quantityWH;
    int quantityS;
    int quantityB;


    public int getProductID() {
        return productID;
    }
    public int getQuantityWH() {
        return quantityWH;
    }
    public LocalDate getExpireDate() {
        return expireDate;
    }

    public SupplyDTO(){}

    public SupplyDTO(int id, int quantity, LocalDate ex){
        productID = id;
        quantityWH = quantity;
        expireDate = ex;
        quantityS = 0;
        quantityB = 0;
    }
    public SupplyDTO(SupplyDomain other , int otherId){
        sId = other.getId();
        productID = otherId;
        quantityB = other.getQuantityBad();
        quantityS = other.getQuantityStore();
        quantityWH = other.getQuantityWarehouse();
        expireDate = other.getExpierDate();
    }


}
