package DTO;

import Domain.SupplyDomain;

public class SupplyDTO {

    int productID;
    LocalDate expireDate;
    int quantity;


    public int getProductID() {
        return productID;
    }
    public int getQuantity() {
        return quantity;
    }
    public LocalDate getExpireDate() {
        return expireDate;
    }

    public SupplyDTO(){}

    public SupplyDTO(int id, int quantity, LocalDate ex){
        productID = id;
        quantity = quantity;
        expireDate = ex;
    }
    public SupplyDTO(SupplyDomain other , int otherId){
        productID = otherId;
        quantity = other.getQuantityBad();
        expireDate = other.getExpierDate();
    }


}
