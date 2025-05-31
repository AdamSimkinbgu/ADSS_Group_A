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

    //getters 

    public int getProductID() {
        return productID;
    }
    public int getQuantityWH() {
        return quantityWH;
    }
    public LocalDate getExpireDate() {
        return expireDate;
    }

    public int getsId(){
        return sId;
    }

    public int getquantityS(){
        return quantityS;
    }

    public int getquantityB(){
        return quantityB;
    }

    //setters

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public void setQuantityWH(int quantityWH) {
        this.quantityWH = quantityWH;
    }

    public void setExpireDate(LocalDate expireDate) {
        this.expireDate = expireDate;
    }

    public void setSId(int sId) {
        this.sId = sId;
    }

    public void setQuantityS(int quantityS) {
        this.quantityS = quantityS;
    }

    public void setQuantityB(int quantityB) {
        this.quantityB = quantityB;
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
